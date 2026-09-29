#!/bin/bash
set -e

# Detect OS
OS="$(uname -s)"
case "$OS" in
  Linux*|Darwin*)   PLATFORM="unix" ;;
  CYGWIN*|MINGW*|MSYS*) PLATFORM="windows" ;;
  *)                PLATFORM="unknown" ;;
esac

echo "▶ Detected platform: $PLATFORM"

# Set gradlew command based on OS
if [ "$PLATFORM" = "windows" ]; then
  GRADLEW="./gradlew.bat"
else
  GRADLEW="./gradlew"
  chmod +x gradlew
fi

echo "▶ Running Android Lint..."
$GRADLEW lint --warning-mode all

echo "▶ Running Detekt..."
$GRADLEW detekt --warning-mode all

echo "▶ Running Unit Tests..."
$GRADLEW test --warning-mode all

echo "✅ All checks passed!"
