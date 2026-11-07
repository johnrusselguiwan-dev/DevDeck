import os
from pathlib import Path
import shutil
import shlex
import sys
import subprocess
import tempfile
import unittest


class WorkflowCommandsTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix="devdeck-command-tests-")
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name)
        self.bin = self.root / "bin"
        self.bin.mkdir()
        scripts = self.root / "scripts"
        (scripts / "tests").mkdir(parents=True)
        source = Path(__file__).resolve().parents[1]
        shutil.copy2(source / "check.sh", scripts / "check.sh")
        deploy = source / "deploy.sh"
        if deploy.exists():
            shutil.copy2(deploy, scripts / "deploy.sh")
        self.trace = self.root / "commands.txt"
        self.env = dict(os.environ, PATH=str(self.bin) + os.pathsep + os.environ["PATH"],
                        DEVDECK_COMMAND_TRACE=str(self.trace), TMPDIR=str(self.root))
        self.executable(self.root / "gradlew", '''#!/usr/bin/env bash
printf 'gradle %s\\n' "$*" >> "$DEVDECK_COMMAND_TRACE"
if [[ "${DEVDECK_FAIL_GRADLE:-0}" == 1 ]]; then
  echo "Intentional Gradle failure" >&2
  exit 7
fi
mkdir -p shared/build/dist/wasmJs/productionExecutable
for asset in index.html devdeck.js platform.js bootstrap.js; do
  touch "shared/build/dist/wasmJs/productionExecutable/$asset"
done
''')
        self.executable(self.bin / "python3", '#!/usr/bin/env bash\nif [[ "$1" == "-m" ]]; then exit 0; fi\nexec ' + shlex.quote(sys.executable) + ' "$@"\n')
        for tool in ["node", "firebase"]:
            self.executable(self.bin / tool, f'#!/usr/bin/env bash\nprintf "{tool} %s\\n" "$*" >> "$DEVDECK_COMMAND_TRACE"\n')
        (self.root / "firebase.json").write_text('{"hosting":{"site":"johnrusselguiwan","public":"shared/build/dist/wasmJs/productionExecutable"}}')
        (self.root / ".firebaserc").write_text('{"projects":{"default":"devdeck-607ea"}}')

    def executable(self, path, content):
        path.write_text(content)
        path.chmod(0o755)

    def run_script(self, script, *arguments, **environment):
        return subprocess.run(["bash", str(self.root / "scripts" / script), *arguments],
                              cwd=self.root, env=dict(self.env, **environment), text=True, capture_output=True)

    def commands(self):
        return self.trace.read_text() if self.trace.exists() else ""

    def test_web_only_checks_avoid_android_builds_and_quiet_retains_full_log(self):
        result = self.run_script("check.sh", "--web-only", "--quiet")
        self.assertEqual(result.returncode, 0, result.stderr + result.stdout)
        commands = self.commands()
        self.assertIn(":shared:detekt", commands)
        self.assertIn(":shared:wasmJsBrowserTest", commands)
        self.assertIn(":shared:wasmJsBrowserDistribution", commands)
        self.assertNotIn(":androidApp:", commands)
        self.assertNotIn(":shared:lintAndroidMain", commands)
        self.assertIn("Log:", result.stdout)
        self.assertTrue(list(self.root.glob("devdeck-check.*.log")))

    def test_quiet_failures_are_nonzero_and_show_the_error(self):
        result = self.run_script("check.sh", "--web-only", "--quiet", DEVDECK_FAIL_GRADLE="1")
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("Intentional Gradle failure", result.stdout + result.stderr)

    def test_default_deployment_only_checks_and_prepares(self):
        result = self.run_script("deploy.sh")
        self.assertEqual(result.returncode, 0, result.stderr + result.stdout)
        self.assertNotIn("firebase deploy", self.commands())
        self.assertIn("--publish", result.stdout)

    def test_publish_uses_the_explicit_hosting_project(self):
        result = self.run_script("deploy.sh", "--publish")
        self.assertEqual(result.returncode, 0, result.stderr + result.stdout)
        self.assertIn("firebase deploy --only hosting --project devdeck-607ea", self.commands())

    def test_failed_checks_prevent_publish(self):
        result = self.run_script("deploy.sh", "--publish", DEVDECK_FAIL_GRADLE="1")
        self.assertNotEqual(result.returncode, 0)
        self.assertNotIn("firebase deploy", self.commands())

    def test_changed_destination_prevents_publish_before_building(self):
        (self.root / ".firebaserc").write_text('{"projects":{"default":"another-project"}}')
        result = self.run_script("deploy.sh", "--publish")
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(self.commands(), "")

    def test_default_checks_still_include_android_lint_and_detekt(self):
        result = self.run_script("check.sh", "--quiet")
        self.assertEqual(result.returncode, 0, result.stderr + result.stdout)
        commands = self.commands()
        self.assertIn(":androidApp:lintDevDebug", commands)
        self.assertIn(":androidApp:detekt", commands)
        self.assertIn(":shared:testAndroidHostTest", commands)
        self.assertIn(":shared:lintAndroidMain", commands)
        self.assertNotIn(":shared:wasmJsBrowserTest", commands)

    def test_full_gate_compiles_the_migrated_device_tests(self):
        result = self.run_script("check.sh", "--all-variants", "--quiet")
        self.assertEqual(result.returncode, 0, result.stderr + result.stdout)
        self.assertIn(":shared:assembleAndroidDeviceTest", self.commands())
        self.assertIn(":androidApp:assembleDevDebugAndroidTest", self.commands())
        self.assertIn(":androidApp:lintProdRelease", self.commands())

    def test_web_only_rejects_incompatible_flags(self):
        result = self.run_script("check.sh", "--web-only", "--all-variants")
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(self.commands(), "")
