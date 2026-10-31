# Manager Android App

Complete native Android application built with Kotlin, Jetpack Compose, Material 3, Room, and DataStore.

## Features
- **UI Exact Parity:** Approved Forest Green + Warm Cream theme with floating bottom capsule navigation bar.
- **Daily Earnings:** 5-day interactive selector, 7dp stroke circular progress rings, real-time reactive income/expense/net calculation.
- **Goals Screen:** Daily, Weekly, and Monthly goal progress tracking with period calculations and targets.
- **Loans & Lending:** Tracking of lent/borrowed amounts, multi-loan support, repayments, and outstanding balances.
- **Settings:** Avatar picker, 3 theme presets, custom color pickers, and complete English/Hindi localization.

## Building and Installing
### Option 1: GitHub Actions (Direct APK Download)
1. Push this project to `https://github.com/zaidshah000001-cell/Manager.app`.
2. The GitHub Action `.github/workflows/android.yml` will automatically run tests and compile the release/debug APK.
3. Download the compiled `manager-debug-apk` artifact directly to your Android device from the Actions tab.

### Option 2: Local Android Studio
1. Open the project folder in Android Studio.
2. Ensure JDK 17 is configured.
3. Sync Gradle and run `./gradlew assembleDebug`.
