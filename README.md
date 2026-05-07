# 资产管家 (Zichan)

A refined personal asset management app for Android. Track everything you own — from electronics to furniture to digital subscriptions — in one elegant, local-first app.

## Features

- **Asset Tracking** — Add, edit, and organize your belongings with rich metadata (brand, model, price, serial number, purchase channel, photos)
- **Smart Categories** — 10 built-in categories (electronics, furniture, collectibles, software, subscriptions, etc.)
- **Lending Management** — Track who borrowed what, with expected return dates and history
- **Contact Book** — Manage people you lend items to
- **Statistics Dashboard** — Visual breakdown of your assets by category, status, and value
- **Depreciation Calculator** — Three depreciation strategies (general, electronics, collectibles)
- **Expiry Alerts** — Get notified when virtual assets or subscriptions are about to expire
- **Dark & Light Themes** — Follows system appearance with a refined amber-on-charcoal palette
- **Biometric Lock** — Protect your data with fingerprint or face recognition
- **Local Backup** — Export all data as JSON to your device storage
- **100% Local** — All data stored on-device via Room/SQLite. No server, no account, no cloud.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Database | Room (SQLite) |
| DI | Hilt |
| Async | Coroutines + Flow |
| Backups | Gson JSON export |
| Biometrics | AndroidX BiometricPrompt |

## Requirements

- Android 12 (API 31) or later
- Works on HyperOS, MIUI, and stock Android

## Build

Open the project in Android Studio, sync Gradle, and run.

```bash
./gradlew assembleDebug
```

## License

MIT
