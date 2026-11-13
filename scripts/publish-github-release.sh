#!/usr/bin/env bash
set -euo pipefail

repo_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_dir"

publish=false
explicit_mode=false
bump_auto=false
tag=""
title=""
apk_path=""
ios_zip_path=""
arg_version_code=""
arg_version_name=""

# Read version.properties
version_code=$(grep -E '^VERSION_CODE=' version.properties | cut -d= -f2 | tr -d '[:space:]' || true)
version_name=$(grep -E '^VERSION_NAME=' version.properties | cut -d= -f2 | tr -d '[:space:]' || true)

if [[ -z "$version_code" || -z "$version_name" ]]; then
  echo "Error: Unable to parse VERSION_CODE or VERSION_NAME from version.properties" >&2
  exit 1
fi

while (($#)); do
  case "$1" in
    --publish) publish=true; explicit_mode=true ;;
    --dry-run) publish=false; explicit_mode=true ;;
    --bump) bump_auto=true ;;
    --version-code=*) arg_version_code="${1#*=}" ;;
    --version-name=*) arg_version_name="${1#*=}" ;;
    --tag=*) tag="${1#*=}" ;;
    --title=*) title="${1#*=}" ;;
    --apk=*) apk_path="${1#*=}" ;;
    --ios-zip=*) ios_zip_path="${1#*=}" ;;
    --help)
      echo "Usage: bash scripts/publish-github-release.sh [--dry-run | --publish] [--bump] [--version-code=<int>] [--version-name=<ver>] [--tag=<tag>] [--title=<title>] [--apk=<path>] [--ios-zip=<path>]"
      echo "Default: Interactively bump version, prepare release notes, and publish/preview GitHub release."
      echo "Options:"
      echo "  --dry-run              Validate artifacts and print the release command without publishing."
      echo "  --publish              Create/update the GitHub release and upload assets using GitHub CLI (gh)."
      echo "  --bump                 Automatically increment VERSION_CODE without prompting."
      echo "  --version-code=<code>  Explicitly set VERSION_CODE in version.properties."
      echo "  --version-name=<name>  Explicitly set VERSION_NAME in version.properties."
      echo "  --tag=<name>           Release tag name (default: v<VERSION_NAME>)."
      echo "  --title=<title>        Release title (default: DevDeck v<VERSION_NAME>)."
      echo "  --apk=<path>           Path to signed prod-release APK."
      echo "  --ios-zip=<path>       Path to iOS simulator Appetize zip."
      exit 0
      ;;
    *) echo "Unknown option: $1" >&2; exit 2 ;;
  esac
  shift
done

# Compute suggested auto-incremented VERSION_CODE (keep VERSION_NAME unless explicitly entered)
suggested_code=$((version_code + 1))

new_version_code="$version_code"
new_version_name="$version_name"

if [[ -n "$arg_version_code" || -n "$arg_version_name" ]]; then
  new_version_code="${arg_version_code:-$version_code}"
  new_version_name="${arg_version_name:-$version_name}"
elif "$bump_auto"; then
  new_version_code="$suggested_code"
  new_version_name="$version_name"
elif [ -t 0 ] && [ -t 1 ]; then
  echo "================================================================="
  echo "App Version Configuration"
  echo "Current version.properties: VERSION_NAME=${version_name}, VERSION_CODE=${version_code}"
  echo "================================================================="
  read -r -p "Enter VERSION_CODE [press Enter to bump to ${suggested_code}, or type ${version_code} to keep]: " input_code || input_code=""
  new_version_code="${input_code:-$suggested_code}"

  read -r -p "Enter VERSION_NAME [press Enter to keep ${version_name}, or type new version]: " input_name || input_name=""
  new_version_name="${input_name:-$version_name}"
  echo ""
fi

# Validate chosen version values
if ! [[ "$new_version_code" =~ ^[1-9][0-9]*$ ]]; then
  echo "Error: VERSION_CODE must be a positive integer (got: '$new_version_code')" >&2
  exit 1
fi
if ! [[ "$new_version_name" =~ ^[A-Za-z0-9][A-Za-z0-9._-]*$ ]]; then
  echo "Error: VERSION_NAME must be filename-safe (got: '$new_version_name')" >&2
  exit 1
fi

# Update version.properties if changed
if [[ "$new_version_code" != "$version_code" || "$new_version_name" != "$version_name" ]]; then
  cat <<EOF > version.properties
VERSION_CODE=${new_version_code}
VERSION_NAME=${new_version_name}
EOF
  echo "=> Updated version.properties: VERSION_CODE=${new_version_code}, VERSION_NAME=${new_version_name}"
  version_code="$new_version_code"
  version_name="$new_version_name"
fi

tag="${tag:-v${version_name}}"
title="${title:-DevDeck v${version_name}}"

# Locate Android APK for the target version
expected_apk="release-artifacts/DevDeck-${version_name}-code${version_code}-prod-release.apk"
if [[ -z "$apk_path" ]]; then
  if [[ -f "$expected_apk" ]]; then
    apk_path="$expected_apk"
  elif [ -t 0 ] && [ -t 1 ]; then
    echo "=> Signed APK for v${version_name} (code ${version_code}) not found at '${expected_apk}'."
    read -r -p "Build and package signed prodRelease APK for v${version_name} (code ${version_code}) now? [Y/n]: " build_choice || build_choice="y"
    case "$build_choice" in
      n|N|no|NO)
        echo "=> Skipping APK build."
        ;;
      *)
        echo "=> Building signed prodRelease APK..."
        ./gradlew :androidApp:assembleProdRelease -PrequireReleaseSigning=true --max-workers=1 --no-daemon
        apk_path=$(python3 scripts/package-release.py androidApp/build/outputs/apk/prod/release/output-metadata.json version.properties release-artifacts)
        echo "=> Packaged APK: $apk_path"
        ;;
    esac
  fi
fi

# Locate iOS Appetize simulator zip
if [[ -z "$ios_zip_path" ]]; then
  default_ios_zip="release-artifacts/DevDeck-ios-simulator.zip"
  if [[ -f "$default_ios_zip" ]]; then
    ios_zip_path="$default_ios_zip"
  elif [[ -f "DevDeck-ios-simulator.zip" ]]; then
    ios_zip_path="DevDeck-ios-simulator.zip"
  elif command -v gh >/dev/null 2>&1 && gh auth status >/dev/null 2>&1; then
    echo "=> Checking GitHub Actions for 'DevDeck-Appetize' iOS simulator artifact..."
    mkdir -p release-artifacts
    if gh run download --name DevDeck-Appetize --dir release-artifacts/ 2>/dev/null && [[ -f "$default_ios_zip" ]]; then
      echo "=> Downloaded DevDeck-ios-simulator.zip from GitHub Actions!"
      ios_zip_path="$default_ios_zip"
    fi
  fi
fi

# Prepare assets array
assets=()

if [[ -n "$apk_path" && -f "$apk_path" ]]; then
  assets+=("$apk_path")
  sha_file="${apk_path}.sha256"
  if [[ -f "$sha_file" ]]; then
    assets+=("$sha_file")
  fi
else
  echo "Warning: No production Android APK found for v${version_name} (code ${version_code}) at '${expected_apk}'." >&2
fi

if [[ -n "$ios_zip_path" && -f "$ios_zip_path" ]]; then
  assets+=("$ios_zip_path")
  ios_sha="${ios_zip_path}.sha256"
  if [[ ! -f "$ios_sha" ]]; then
    (cd "$(dirname "$ios_zip_path")" && sha256sum "$(basename "$ios_zip_path")" > "$(basename "$ios_sha")")
  fi
  assets+=("$ios_sha")
else
  echo "Note: No iOS simulator Appetize zip found at '${ios_zip_path:-release-artifacts/DevDeck-ios-simulator.zip}'."
  echo "      To build it, run the GitHub Actions workflow 'Export iOS simulator for Appetize'"
  echo "      (gh workflow run ios-simulator-export.yml) and download the 'DevDeck-Appetize' artifact."
fi

# Generate release notes
notes_file=$(mktemp "${TMPDIR:-/tmp}/devdeck-release-notes.XXXXXX.md")
trap 'rm -f "$notes_file"' EXIT

cat <<EOF > "$notes_file"
## DevDeck ${version_name} (Build ${version_code})

Cross-platform developer portfolio built with **Kotlin Multiplatform** and **Compose Multiplatform**, showcasing Android, iOS, and Web (Wasm) responsive UI architectures.

---

### 📱 Android Installation (.apk)
1. Download \`DevDeck-${version_name}-code${version_code}-prod-release.apk\` below.
2. Open the downloaded file on your Android device (Android 7.0 / API 24+).
3. If prompted by your device, allow installation from unknown sources.
4. Verify checksum integrity:
   \`\`\`bash
   sha256sum --check DevDeck-${version_name}-code${version_code}-prod-release.apk.sha256
   \`\`\`

---

### 🍎 iOS Testing via Appetize.io (.zip)
The iOS version is packaged as a simulator build ready for instant browser-based testing via **Appetize.io**—no Mac or physical iPhone required:

1. Download **\`DevDeck-ios-simulator.zip\`** below.
2. Visit **[https://appetize.io/upload](https://appetize.io/upload)** (or your Appetize dashboard).
3. Upload the \`DevDeck-ios-simulator.zip\` file.
4. Select your preferred device configuration (e.g., **iPhone 15 / iPhone 16** running iOS 17 or later).
5. Click **Play / Tap to Play** to run and test the interactive iOS Compose Multiplatform UI directly in your web browser.

---

### 🌐 Live Web Portfolio
Visit the production website running Kotlin/Wasm:
👉 **[https://johnrusselguiwan.web.app](https://johnrusselguiwan.web.app)**
EOF

echo "================================================================="
echo "Prepared Release: $title (Tag: $tag)"
echo "================================================================="
echo "Assets to attach:"
if ((${#assets[@]} > 0)); then
  for asset in "${assets[@]}"; do
    echo "  - $asset ($(du -h "$asset" | cut -f1))"
  done
else
  echo "  (None found yet)"
fi
echo ""
echo "Release Notes:"
echo "-----------------------------------------------------------------"
cat "$notes_file"
echo "-----------------------------------------------------------------"

if [ -t 0 ] && [ -t 1 ] && [ "$explicit_mode" = false ]; then
  echo ""
  echo "Select GitHub release action:"
  echo "  1) Dry run only (do not upload)"
  echo "  2) Publish / Update release '$tag' on GitHub now"
  read -r -p "Enter choice [1/2] (default: 1): " choice || choice="1"
  case "$choice" in
    2|publish|yes|y) publish=true ;;
    *) publish=false ;;
  esac
fi

if "$publish"; then
  if ! command -v gh >/dev/null 2>&1; then
    echo "Error: GitHub CLI (gh) is required to publish releases. Please install it." >&2
    exit 1
  fi

  if ! gh auth status >/dev/null 2>&1; then
    echo "Error: GitHub CLI is not authenticated. Run 'gh auth login' before retrying." >&2
    exit 1
  fi

  if ((${#assets[@]} == 0)); then
    echo "Error: Cannot publish a release with zero assets." >&2
    exit 1
  fi

  if gh release view "$tag" >/dev/null 2>&1; then
    echo "=> Release '$tag' already exists on GitHub. Updating release notes and uploading assets..."
    gh release edit "$tag" \
      --title "$title" \
      --notes-file "$notes_file"
    gh release upload "$tag" "${assets[@]}" --clobber
    echo "=> Release $tag updated successfully!"
  else
    echo "=> Creating new release '$tag' on GitHub..."
    gh release create "$tag" "${assets[@]}" \
      --title "$title" \
      --notes-file "$notes_file"
    echo "=> Release $tag published successfully!"
  fi
else
  echo ""
  echo "Dry run complete. Nothing was uploaded."
  echo "To publish or update this release on GitHub:"
  if ((${#assets[@]} > 0)); then
    echo "  Run: ./scripts/publish-github-release.sh --publish"
  else
    echo "  Ensure Android APK and iOS Appetize zip are built first, then run with --publish."
  fi
fi
