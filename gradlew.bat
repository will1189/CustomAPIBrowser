@echo off
REM Fallback Gradle wrapper script.
REM This project uses Gradle 8.7 configured in .github/workflows/build.yml.
REM If the full wrapper is missing, make sure Gradle 8.7 is installed locally.

if exist "gradle\wrapper\gradle-wrapper.jar" (
    java -jar gradle\wrapper\gradle-wrapper.jar %*
) else (
    gradle %*
)
