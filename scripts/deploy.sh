#!/usr/bin/env bash
set -euo pipefail

repo_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_dir"

publish=false
if (($# > 1)); then
  echo "Usage: bash scripts/deploy.sh [--dry-run | --publish]" >&2
  exit 2
fi
case "${1:---dry-run}" in
  --dry-run) ;;
  --publish) publish=true ;;
  --help)
    echo "Usage: bash scripts/deploy.sh [--dry-run | --publish]"
    echo "Default: check/build web and print the deployment command without uploading."
    exit 0
    ;;
  *) echo "Unknown option: $1" >&2; exit 2 ;;
esac

if "$publish" && ! command -v firebase >/dev/null 2>&1; then
  echo "Firebase CLI is required for --publish. Install it and log in before retrying." >&2
  exit 1
fi

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

bash scripts/check.sh --web-only --quiet
for asset in index.html devdeck.js platform.js bootstrap.js; do
  test -f "shared/build/dist/wasmJs/productionExecutable/$asset" || {
    echo "Missing web output: $asset" >&2
    exit 1
  }
done

if "$publish"; then
  firebase deploy --only hosting --project devdeck-607ea --config "$repo_dir/firebase.json" --non-interactive
else
  echo "Dry run complete. Web output is ready; nothing was uploaded."
  echo "To publish: bash scripts/deploy.sh --publish"
  echo "Command: firebase deploy --only hosting --project devdeck-607ea --config firebase.json --non-interactive"
fi
