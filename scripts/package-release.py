"""Package one prodRelease APK from Gradle metadata; signing is checked by the caller."""

import hashlib
import json
from pathlib import Path
import re
import shutil
import sys


def package_apk(metadata_path: Path, version_path: Path, output_dir: Path) -> Path:
    properties = dict(
        line.strip().split("=", 1)
        for line in version_path.read_text().splitlines()
        if line.strip() and not line.lstrip().startswith("#")
    )
    code = int(properties["VERSION_CODE"])
    name = properties["VERSION_NAME"]
    if code <= 0 or not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9._-]*", name):
        raise ValueError("Version must have a positive code and a filename-safe name")
    metadata = json.loads(metadata_path.read_text())
    if metadata.get("applicationId") != "com.example.devdeck" or metadata.get("variantName") != "prodRelease":
        raise ValueError("Expected the production release package and variant")
    elements = metadata.get("elements", [])
    if len(elements) != 1:
        raise ValueError("Expected exactly one APK output")
    element = elements[0]
    if element.get("versionCode") != code or element.get("versionName") != name:
        raise ValueError("APK metadata does not match version.properties")
    source = (metadata_path.parent / element["outputFile"]).resolve()
    if not source.is_relative_to(metadata_path.parent.resolve()) or source.suffix != ".apk" or not source.is_file():
        raise ValueError("APK must exist within the selected build output directory")
    output_dir.mkdir(parents=True, exist_ok=True)
    destination = output_dir / f"DevDeck-{name}-code{code}-prod-release.apk"
    shutil.copyfile(source, destination)
    checksum = hashlib.sha256(destination.read_bytes()).hexdigest()
    destination.with_suffix(".apk.sha256").write_text(f"{checksum}  {destination.name}\n")
    return destination


if __name__ == "__main__":
    if len(sys.argv) != 4:
        sys.exit("Usage: python3 scripts/package-release.py output-metadata.json version.properties output-directory")
    try:
        print(package_apk(*(Path(argument) for argument in sys.argv[1:])))
    except (KeyError, TypeError, ValueError, OSError) as error:
        sys.exit(f"Release packaging failed: {error}")
