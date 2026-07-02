用户说"英文"，意思是要我把刚才的README改成英文版本。我需要直接输出英文README。
# CustomAPIBrowser

An Android app that supports user-defined APIs and includes built-in browser automation.

## Features

- **Built-in Browser**: Based on WebView, supporting address bar navigation, forward/back/reload, and loading progress display.
- **Browser Automation**: Inject and execute custom JavaScript on the current page to perform clicks, form filling, scraping, and other automated actions.
- **Custom APIs**: Add, edit, delete, and run custom HTTP requests (supports GET/POST/PUT/DELETE, etc.).
- **Data Persistence**: API configurations are saved via SharedPreferences and remain available after app restart.

## Tech Stack

- Kotlin
- Jetpack Compose + Material3
- Navigation-Compose
- OkHttp
- Gson

## Project Structure

```
CustomAPIBrowser/
├── .github/workflows/build.yml   # GitHub Actions auto build
├── app/
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/example/customapibrowser/
│       ├── MainActivity.kt
│       ├── CustomApiBrowserApplication.kt
│       ├── api/ApiEngine.kt              # HTTP request execution
│       ├── browser/BrowserViewModel.kt   # Browser & automation logic
│       ├── data/ApiConfig.kt             # API config data class
│       ├── data/ApiConfigRepository.kt   # Config persistence
│       └── ui/screens/                   # Compose screens
│           ├── BrowserScreen.kt
│           ├── ApiListScreen.kt
│           └── ApiEditScreen.kt
└── build.gradle.kts
```

## Usage

1. Open the project in Android Studio.
2. Sync Gradle, build, and run on a physical device or emulator.
3. Use the bottom navigation to switch between "Browser" and "Custom APIs".
4. On the "Custom APIs" screen, tap the floating action button to add an API, then fill in URL, Method, Headers, and Body.
5. Tap the play button on a list item to run the API and view the response.
6. On the "Browser" screen, open a webpage, enter a JavaScript script at the bottom, and run automation.

## GitHub Actions Build

The repository includes `.github/workflows/build.yml`, which automatically triggers on every push to `main`:

- Sets up JDK 17
- Installs Gradle 8.7
- Runs `gradle assembleDebug`
- Uploads `app/build/outputs/apk/debug/*.apk` as a build artifact

You can also manually trigger the workflow from the Actions tab.

## Notes

- Since `gradle-wrapper.jar` is missing, it is recommended to let Android Studio auto-complete the Gradle Wrapper locally, or ensure Gradle 8.7 is installed.
- `AndroidManifest.xml` enables `usesCleartextTraffic="true"` for convenient HTTP API testing; consider disabling it before release if not needed.
- Browser automation currently runs JS via `WebView.evaluateJavascript`, suitable for simple page interaction scenarios.
