# Rin Android

Rin Android is a Jetpack Compose client for the [Rin](https://github.com/openRin/Rin) blog platform. It focuses on mobile publishing: writing posts, managing drafts, browsing articles, and uploading images from an Android device.

## Features

- Connect to a self-hosted Rin blog instance
- Password login with encrypted local token storage
- Browse published, draft, and unlisted posts
- Write and update Markdown articles
- Insert uploaded images into the editor as Markdown
- Save local drafts with Room
- Search posts
- Manage profile information
- Switch between Rin server storage and external image hosting
- English and Chinese UI resources

## Compatible Projects

- Blog backend/frontend: [openRin/Rin](https://github.com/openRin/Rin)
- External image hosting: [0-RTT/telegraph](https://github.com/0-RTT/telegraph)

External image hosting is configured inside the app. No upload endpoint, username, or password is bundled in this repository.

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Hilt
- Retrofit + OkHttp
- kotlinx.serialization
- Room
- EncryptedSharedPreferences
- Coil
- CommonMark

## Requirements

- Android Studio with JDK 17 or newer
- Android SDK 36
- Gradle wrapper included in this repository
- Minimum Android SDK: 26

## Build

```powershell
.\gradlew.bat assembleDebug
```

The debug APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Setup

1. Start the app.
2. Enter the Rin blog base URL.
3. Sign in with a Rin account.
4. Optional: open Profile and configure external image hosting.

For Telegraph-compatible image hosting, use the service upload endpoint and credentials provided by your own deployment. The app sends multipart form data with the field name `file` and expects this JSON response:

```json
{
  "data": "https://example.com/path/to/image.jpg"
}
```

## Privacy

This repository does not include personal blog URLs, image hosting URLs, usernames, passwords, API keys, keystores, or local Android SDK paths.

Runtime secrets are stored locally on the device using encrypted preferences.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
