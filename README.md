# ✨ Magic Numbers

![App Icon](art/icon.png)

**Magic Numbers** is an ad-free, offline, and open-source Android app that generates spiritual numerology messages based on the "rhythm of the moment". It combines clean Material 3 design with mystical insights.

## ✨ Features

*   **Rhythm of the Moment Generation**: The number is generated on-demand using a random generator seeded with the exact millisecond of your interaction (`System.currentTimeMillis()`), capturing the unique “energy” of that moment.
*   **6-Digit Message Number**: Generates a number between **100000 and 999999**.
*   **Deep Numerology Analysis**:
    *   Calculates the digit sum (cross sum / Quersumme) as the numerological base.
    *   Adds **special meanings** when the *sum before reduction* matches master/special numbers (11, 22, 33, 44, 55, 66, 77, 88, 99).
    *   Assigns **karmic details** (and lessons for select sums) based on the *sum before reduction* (e.g., 10, 11, 13, 14, 16, 19, 22, 33 and more supported values in-app).
*   **Cross Sum (Quersumme)**: Automatically calculates and displays the detailed equation (e.g., `1 + 2 + 3 = 6`) as part of the message.
*   **Vibration Frequency Meter** 🌀: A visual bar indicates the energetic frequency score (percentage) of the generated number.
*   **Detailed Interpretations**:
    *   Shows the **keyword vibration** for each distinct digit (0–9) contained in the number.
    *   Creates per-digit **message intros** and marks repeated digits with an occurrence note (e.g., “appears X times, amplified”).
    *   **Special combination summaries & energy flows** for certain patterns (e.g., `99` + `1`, `1` + `9` (without `99`), `8` + `55`, `4` + `8`, `11` + `22`, `77` + `7`, `33` + `3`).
*   **Privacy First**:
    *   100% Offline.
    *   No tracking, no analytics, no ads.
    *   No internet permission required (except for opening the optional donate link in a browser).
*   **User Friendly**:
    *   Modern Material 3 Design (Dark Mode).
    *   Copy functionality for sharing messages (copies the full formatted result to the clipboard).
    *   **Donate Button** 🤝: Support the developer directly via PayPal.

## 🛠 Technical Details

*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Material 3)
*   **Architecture**: Single Activity, State Management via `rememberSaveable` (screen rotation support).
*   **Minimum SDK**: 24 (Android 7.0)
*   **Target SDK**: 35+
*   **License**: MIT License

## 🌍 Localization

The app is fully translated into:

*   🇸🇦 Arabic (العربية)
*   🇧🇬 Bulgarian (Български)
*   🇨🇳 Chinese Simplified (简体中文)
*   🇭🇷 Croatian (Hrvatski)
*   🇨🇿 Czech (Čeština)
*   🇩🇰 Danish (Dansk)
*   🇳🇱 Dutch (Nederlands)
*   🇬🇧 English (UK)
*   🇺🇸 English (US)
*   🇪🇪 Estonian (Eesti)
*   🇫🇮 Finnish (Suomi)
*   🇫🇷 French (Français)
*   🇩🇪 German (Deutsch)
*   🇬🇷 Greek (Ελληνικά)
*   🇮🇱 Hebrew (עברית)
*   🇮🇳 Hindi (हिन्दी)
*   🇭🇺 Hungarian (Magyar)
*   🇮🇩 Indonesian (Bahasa Indonesia)
*   🇮🇹 Italian (Italiano)
*   🇯🇵 Japanese (日本語)
*   🇰🇷 Korean (한국어)
*   🇱🇻 Latvian (Latviešu)
*   🇱🇹 Lithuanian (Lietuvių)
*   🇳🇴 Norwegian (Norsk)
*   🇵🇱 Polish (Polski)
*   🇵🇹 Portuguese (Português)
*   🇷🇴 Romanian (Română)
*   🇷🇺 Russian (Русский)
*   🇷🇸 Serbian (Српски)
*   🇸🇰 Slovak (Slovenčina)
*   🇸🇮 Slovenian (Slovenščina)
*   🇪🇸 Spanish (Español)
*   🇸🇪 Swedish (Svenska)
*   🇹🇭 Thai (ไทย)
*   🇹🇷 Turkish (Türkçe)
*   🇺🇦 Ukrainian (Українська)
*   🇻🇳 Vietnamese (Tiếng Việt)

## 📱 Screenshots

|                   Welcome Screen                   |                    Message View                    |
|:--------------------------------------------------:|:--------------------------------------------------:|
| <img src="art/screenshot_welcome.jpg" width="250"> | <img src="art/screenshot_message.jpg" width="250"> |

*(To display these images, create an `art` folder in the project's root directory and place your app's icon and screenshots there.)*

## 🛠️ Built With

-   [Kotlin](https://kotlinlang.org/): As the primary programming language.
-   [Jetpack Compose](https://developer.android.com/jetpack/compose): For the declarative and modern UI toolkit.
-   [Material Design 3](https://m3.material.io/): For design components and theming.
-   [Android Studio](https://developer.android.com/studio): As the Integrated Development Environment (IDE).

## 🚀 Getting Started

You can download the app from the Google Play Store (link will be added here once available) or build the project yourself.

### Building from Source

1.  Clone this repository:
2.  Open the project in the latest version of [Android Studio](https://developer.android.com/studio).
3.  Let Gradle sync the project dependencies.
4.  To sign the app for release, you will need to create your own `keystore.properties` file or modify the `signingConfigs` block in the `app/build.gradle` file.
5.  Run the app on an emulator or a physical device.

## 🤝 Contributing

Contributions are welcome!
1.  Fork the project.
2.  Create your feature branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.