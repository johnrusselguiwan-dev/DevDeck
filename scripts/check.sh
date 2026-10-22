#!/usr/bin/env bash
set -euo pipefail

repo_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_dir"

all_variants=false
browser=false
ios=false

while (($#)); do
  case "$1" in
    --all-variants) all_variants=true ;;
    --browser) browser=true ;;
    --ios)
      if [[ "$(uname -s)" != Darwin ]]; then
        echo "iOS checks require macOS and Xcode." >&2
        exit 2
      fi
      ios=true
      ;;
    --help)
      echo "Usage: bash scripts/check.sh [--all-variants] [--browser] [--ios] [-- Gradle arguments]"
      echo "Default: devDebug lint, assembly, unit tests, and Detekt for both modules."
      echo "--all-variants adds devRelease, prodDebug, prodRelease, and shared release lint."
      echo "--browser adds web assembly, browser tests, and analytics/text-portfolio tests (Node.js 20+)."
      echo "Browser tests need Chrome/CHROME_BIN; iOS tests need macOS/Xcode."
      exit 0
      ;;
    --) shift; break ;;
    *) echo "Unknown option: $1" >&2; exit 2 ;;
  esac
  shift
done

variants=(DevDebug)
if "$all_variants"; then
  variants+=(ProdDebug)
fi

gradle_tasks=(:androidApp:detekt :shared:detekt :shared:lintDebug :shared:testDebugUnitTest)
for variant in "${variants[@]}"; do
  gradle_tasks+=(":androidApp:lint${variant}" ":androidApp:assemble${variant}" ":androidApp:test${variant}UnitTest")
done
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
if "$browser"; then
  node --test scripts/analytics.test.mjs scripts/portfolio-overview.test.mjs || check_status=1
fi
"$gradle_wrapper" "${gradle_tasks[@]}" --max-workers=1 --continue "$@" --no-daemon || check_status=1
if "$all_variants"; then
  release_tasks=(:shared:lintRelease)
  for variant in DevRelease ProdRelease; do
    release_tasks+=(":androidApp:lint${variant}" ":androidApp:assemble${variant}")
  done
  "$gradle_wrapper" "${release_tasks[@]}" --max-workers=1 --continue "$@" --no-daemon || check_status=1
fi
if ((check_status != 0)); then
  exit "$check_status"
fi
echo "All requested checks passed."
