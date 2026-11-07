import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

MODULE = Path(__file__).resolve().parents[1] / "package-release.py"
spec = importlib.util.spec_from_file_location("package_release", MODULE)
release = importlib.util.module_from_spec(spec)
spec.loader.exec_module(release)


class PackageReleaseTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name)
        self.metadata = self.root / "output-metadata.json"
        self.version = self.root / "version.properties"
        self.version.write_text("VERSION_CODE=1\nVERSION_NAME=1.0\n")
        (self.root / "app.apk").write_bytes(b"APK fixture")
        self.data = {
            "applicationId": "com.example.devdeck",
            "variantName": "prodRelease",
            "elements": [{"outputFile": "app.apk", "versionCode": 1, "versionName": "1.0"}]
        }

    def package(self):
        self.metadata.write_text(json.dumps(self.data))
        return release.package_apk(self.metadata, self.version, self.root / "artifacts")

    def test_packages_named_apk_and_checksum(self):
        apk = self.package()
        self.assertEqual(apk.name, "DevDeck-1.0-code1-prod-release.apk")
        self.assertEqual(apk.read_bytes(), b"APK fixture")
        self.assertIn(apk.name, apk.with_suffix(".apk.sha256").read_text())

    def test_rejects_wrong_package_variant_version_and_multiple_outputs(self):
        for key, value in [("applicationId", "com.example.devdeck.dev"), ("variantName", "prodDebug")]:
            previous = self.data[key]
            self.data[key] = value
            with self.subTest(key=key), self.assertRaises(ValueError):
                self.package()
            self.data[key] = previous
        for key, value in [("versionCode", 2), ("versionName", "2.0")]:
            previous = self.data["elements"][0][key]
            self.data["elements"][0][key] = value
            with self.subTest(key=key), self.assertRaises(ValueError):
                self.package()
            self.data["elements"][0][key] = previous
        self.data["elements"] *= 2
        with self.assertRaises(ValueError):
            self.package()

    def test_rejects_unsafe_missing_and_non_apk_paths(self):
        for filename in ["../outside.apk", "/tmp/outside.apk", "missing.apk", "output-metadata.json"]:
            self.data["elements"][0]["outputFile"] = filename
            with self.subTest(filename=filename), self.assertRaises(ValueError):
                self.package()

    def test_rejects_unsafe_version_names_and_nonpositive_codes(self):
        for properties in ["VERSION_CODE=0\nVERSION_NAME=1.0\n", "VERSION_CODE=1\nVERSION_NAME=../1.0\n"]:
            self.version.write_text(properties)
            with self.subTest(properties=properties), self.assertRaises(ValueError):
                self.package()


if __name__ == "__main__":
    unittest.main()
