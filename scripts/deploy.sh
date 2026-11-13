#!/usr/bin/env bash
set -euo pipefail

repo_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_dir"

publish=false
target="release"
channel="dev"
explicit_target=false

while (($#)); do
  case "$1" in
    --dry-run) publish=false ;;
    --publish) publish=true ;;
    --release) target="release"; explicit_target=true ;;
    --debug) target="debug"; explicit_target=true ;;
    --channel=*)
      target="debug"
      explicit_target=true
      channel="${1#*=}"
      ;;
    --help)
      echo "Usage: bash scripts/deploy.sh [--debug | --release] [--dry-run | --publish] [--channel=<name>]"
      echo "Default: check/build web for release (dry-run) and print the deployment command without uploading."
      echo "Options:"
      echo "  --release        Target official live production site (johnrusselguiwan.web.app)."
      echo "  --debug          Target QA/staging preview channel (devdeck-607ea--<channel>-*.web.app)."
      echo "  --channel=<name> Custom preview channel name for debug/QA (default: dev)."
      echo "  --dry-run        Check and build only, without deploying (default)."
      echo "  --publish        Execute the deployment to Firebase Hosting."
      exit 0
      ;;
    *) echo "Unknown option: $1" >&2; exit 2 ;;
  esac
  shift
done

if [ -t 0 ] && [ -t 1 ] && [ "$explicit_target" = false ] && [ -z "${DEVDECK_COMMAND_TRACE:-}" ]; then
  echo "Select web deployment target:"
  echo "  1) Release / Production Live (johnrusselguiwan.web.app)"
  echo "  2) Debug / QA Preview Channel (devdeck-607ea--${channel}-*.web.app)"
  read -r -p "Enter choice [1/2] (default: 1): " choice || choice="1"
  case "$choice" in
    2|debug|dev) target="debug" ;;
    *) target="release" ;;
  esac
fi

if "$publish" && ! command -v firebase >/dev/null 2>&1; then
  echo "Firebase CLI is required for --publish. Install it and log in before retrying." >&2
  exit 1
fi

echo "=> [1/3] Validating Firebase Hosting configuration..."
python3 - <<'PY'
import json
from pathlib import Path
hosting = json.loads(Path('firebase.json').read_text())['hosting']
project = json.loads(Path('.firebaserc').read_text())['projects']['default']
if project != 'devdeck-607ea' or hosting.get('site') != 'johnrusselguiwan':
    raise SystemExit('Hosting destination changed. Review the deployment configuration first.')
if hosting.get('public') != 'shared/build/dist/wasmJs/productionExecutable':
    raise SystemExit('Hosting output directory changed. Review the deployment configuration first.')
PY

echo "=> [2/3] Running web checks, Detekt, browser tests, and compiling Wasm production bundle..."
if [ -t 1 ] && [ -z "${DEVDECK_COMMAND_TRACE:-}" ]; then
  bash scripts/check.sh --web-only --quiet &
  check_pid=$!
  frames=('⠋' '⠙' '⠹' '⠸' '⠼' '⠴' '⠦' '⠧' '⠇' '⠏')
  start_time=$SECONDS
  i=0
  while kill -0 "$check_pid" 2>/dev/null; do
    elapsed=$((SECONDS - start_time))
    printf "\r   %s Building & verifying web bundle... (%ds elapsed)" "${frames[i % ${#frames[@]}]}" "$elapsed"
    i=$((i + 1))
    sleep 0.2
  done
  printf "\r\033[K"
  wait "$check_pid"
else
  bash scripts/check.sh --web-only --quiet
fi

echo "=> [3/3] Verifying compiled web assets..."
for asset in index.html devdeck.js platform.js bootstrap.js; do
  test -f "shared/build/dist/wasmJs/productionExecutable/$asset" || {
    echo "Missing web output: $asset" >&2
    exit 1
  }
done

if [[ "$target" == "release" ]]; then
  if "$publish"; then
    firebase deploy --only hosting --project devdeck-607ea --config "$repo_dir/firebase.json" --non-interactive
  else
    echo "Dry run complete (Target: Release / Production). Web output is ready; nothing was uploaded."
    echo "To publish: bash scripts/deploy.sh --release --publish"
    echo "Command: firebase deploy --only hosting --project devdeck-607ea --config firebase.json --non-interactive"
  fi
else
  if "$publish"; then
    firebase hosting:channel:deploy "$channel" --project devdeck-607ea --config "$repo_dir/firebase.json" --non-interactive
  else
    echo "Dry run complete (Target: Debug / QA Preview Channel: '$channel'). Web output is ready; nothing was uploaded."
    echo "To publish to preview channel: bash scripts/deploy.sh --debug --channel=$channel --publish"
    echo "Command: firebase hosting:channel:deploy $channel --project devdeck-607ea --config firebase.json --non-interactive"
  fi
fi
