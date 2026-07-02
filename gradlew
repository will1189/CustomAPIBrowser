#!/bin/sh

# Fallback Gradle wrapper script.
# This project uses the Gradle version configured in .github/workflows/build.yml (8.7).
# If the full wrapper (gradle-wrapper.jar) is missing, make sure Gradle 8.7 is installed locally.

if [ -f "./gradle/wrapper/gradle-wrapper.jar" ]; then
    exec java -jar ./gradle/wrapper/gradle-wrapper.jar "$@"
else
    exec gradle "$@"
fi
