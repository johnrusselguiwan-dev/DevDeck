#!/usr/bin/env bash
set -euo pipefail

repo_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_dir"

all_variants=false
browser=false
ios=false
web_only=false
quiet=false

while (($#)); do
  case "$1" in
    --all-variants) all_variants=true ;;
    --browser) browser=true ;;
    --web-only) web_only=true; browser=true ;;
    --quiet) quiet=true ;;
    --ios)
      if [[ "$(uname -s)" != Darwin ]]; then
        echo "iOS checks require macOS and Xcode." >&2
        exit 2
      fi
      ios=true
      ;;
    --help)
      echo "Usage: bash scripts/check.sh [--all-variants] [--browser | --web-only] [--ios] [--quiet] [-- Gradle arguments]"
      echo "Default: release-packaging tests, devDebug lint, assembly, unit tests, and Detekt for both modules."
      echo "--all-variants checks all four app variants; shared Android-KMP lint runs once."
      echo "--browser adds web assembly, browser tests, and platform/analytics/text-portfolio tests (Node.js 20+)."
      echo "--web-only runs web checks and shared Detekt, without Android builds/lint."
      echo "--quiet keeps full output in a temporary log and shows the error tail on failure."
      echo "Browser tests need Chrome/CHROME_BIN; iOS tests need macOS/Xcode."
      exit 0
      ;;
    --) shift; break ;;
    *) echo "Unknown option: $1" >&2; exit 2 ;;
  esac
  shift
done

if "$web_only" && { "$all_variants" || "$ios"; }; then
  echo "--web-only cannot be combined with --all-variants or --ios." >&2
  exit 2
fi

if "$quiet"; then
  check_log_file=$(mktemp "${TMPDIR:-/tmp}/devdeck-check.XXXXXX.log")
  exec 3>&1
  exec > "$check_log_file" 2>&1
  trap '
    check_exit=$?
    if ((check_exit == 0)); then
      echo "Checks passed. Log: $check_log_file" >&3
    else
      echo "Checks failed. Log: $check_log_file" >&3
      tail -n 60 "$check_log_file" >&3
    fi
  ' EXIT
fi

variants=(DevDebug)
if "$all_variants"; then
  variants+=(ProdDebug)
fi

gradle_tasks=(:shared:detekt)
if ! "$web_only"; then
  gradle_tasks+=(:androidApp:detekt :shared:lintAndroidMain :shared:testAndroidHostTest)
  for variant in "${variants[@]}"; do
    gradle_tasks+=(":androidApp:lint${variant}" ":androidApp:assemble${variant}" ":androidApp:test${variant}UnitTest")
  done
fi
if "$all_variants"; then
  gradle_tasks+=(:shared:assembleAndroidDeviceTest)
fi
if "$browser"; then
  gradle_tasks+=(:shared:wasmJsBrowserTest :shared:wasmJsBrowserDistribution)
fi
if "$ios"; then
  gradle_tasks+=(:shared:iosSimulatorArm64Test)
fi

case "$(uname -s)" in
  CYGWIN*|MINGW*|MSYS*) gradle_wrapper=./gradlew.bat ;;
  *) gradle_wrapper=./gradlew ;;
esac

check_status=0
python3 -m unittest discover -s scripts/tests -v || check_status=1
if "$browser"; then
  node --test scripts/analytics.test.mjs scripts/portfolio-overview.test.mjs scripts/platform.test.mjs scripts/bootstrap.test.mjs || check_status=1
fi
"$gradle_wrapper" "${gradle_tasks[@]}" --max-workers=1 --continue "$@" --no-daemon || check_status=1
if "$all_variants"; then
  release_tasks=()
  for variant in DevRelease ProdRelease; do
    release_tasks+=(":androidApp:lint${variant}" ":androidApp:assemble${variant}")
  done
  "$gradle_wrapper" "${release_tasks[@]}" --max-workers=1 --continue "$@" --no-daemon || check_status=1
fi
if ((check_status != 0)); then
  exit "$check_status"
fi
echo "All requested checks passed."
