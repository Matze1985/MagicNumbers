<table>
  <tr>
    <td width="200">
      <img src="art/icon.png" width="210" alt="Magic Numbers Logo">
    </td>
    <td>      <h1>Magic Numbers</h1>
      <p>Magic Numbers is an ad-free, offline, and open-source Android app that generates spiritual numerology messages based on the "rhythm of the moment". It combines clean Material 3 design with mystical insights.</p>
      <p>You can <strong><a href="https://github.com/Matze1985/MagicNumbers/releases">download the latest release</a></strong> directly from GitHub.</p>
    </td>
  </tr>
</table>

## ✨ Features

### 🔢 Rhythm of the Moment – Number Generation
- The number is generated **on demand**.
- The random generator is seeded with the **exact millisecond of the interaction** (`System.currentTimeMillis()`).
- This creates a unique “Rhythm of the Moment” energy signature.

---

### 🔢 Flexible Message Numbers
- Generates **6-digit numbers** (`000000 – 999999`) including leading zeros (e.g. `082009`).
- **4-digit numbers** are fully supported and receive their own resonance patterns.
- The digit sequence itself is always the foundation of the interpretation.

---

### 🧮 Deep Numerology Analysis
- Calculates the **digit sum (cross sum)** as the numerological base.
- **Master & Special Numbers** are detected **before reduction**:
    - 11, 22, 33, 44, 55, 66, 77, 88, 99
- These numbers retain their **high vibration** and are not automatically reduced to a single digit.
- **Karmic details and lessons** (e.g. 10, 13, 14, 16, 19, 23) are identified and added as deeper context.

---

### 🕊️ Angel Numbers – Precise & Complete
- Angel numbers are detected **only through adjacent repetitions**:
    - Examples:
        - `44`, `55`, `11`
        - `333`, `4444`, `000`
- **Multiple angel numbers can appear at the same time**:
    - Example: `441155` → 44, 11, and 55
- **No false positives**:
    - A number like `612643` will **not** produce “66” because there is no adjacent repetition.
- All detected angel numbers are **always displayed** and never overridden.

---

### 🔮 Resonance Pattern Recognition
The app detects **visual number patterns** as an independent energetic layer.

#### ✔ 6-digit Resonance Patterns
- Monotype (AAAAAA)
- Transition (AAABBB)
- Stepwise Process (AABBCC)
- Alternation / Pendulum (ABABAB)
- Cycle (ABCABC)
- Mirror (Palindrome)
- Free Mix (fallback)

#### ✔ 4-digit Resonance Patterns
- Monotype (AAAA)
- Two Phases (AABB)
- Alternation (ABAB)
- Mirror (ABBA)
- Even Distribution (ABCD)
- Free Mix (fallback)

Each resonance provides:
- A title
- A short meaning
- A focus hint (start / center / end / frame)
- An optional **frequency boost**

---

### 🧠 Intelligent Prioritization
- **Angel numbers and resonance information are always shown as prefixes**.
- Special combinations override **only the core summary**, never:
    - Angel numbers
    - Resonance information
- This ensures that **all energetic layers remain visible**.

---

### 🧬 Detailed Interpretation
- **Digit breakdown**: Explains the vibration of every digit (0–9) present.
- **Amplification**: Repeated digits are highlighted and explained.
- **Mathematical transparency**: The cross sum calculation is shown (e.g. `0 + 8 + 2 + 0 + 0 + 9 = 19`).

---

### 🌀 Vibration Frequency Meter
- Visual frequency indicator (percentage).
- Influenced by:
    - Average digit vibration
    - Master & special numbers
    - Resonance boosts
- The frequency score is **never distorted by a single rule**.

---

### 🧪 Debug-Only Information
- Resonance IDs (e.g. `R6_ABABAB`, `R4_AABB`) are displayed **only in debug builds**.
- To achieve this without relying on the now optional `BuildConfig.DEBUG` flag (as of AGP 8.0+), the app uses a more robust method: `kotlin val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0`
    - This ensures a clean separation between debug and release builds at runtime and improves build performance by default.

---

## 🛠 Technical Details

*   **Language**: Kotlin
    ***UI Framework**: Jetpack Compose (Material 3)
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