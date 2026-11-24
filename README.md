# Hide Mock Location

> [!WARNING]
> ### 🛑 Project Discontinued / Archived
> This project is no longer actively maintained. 
> 
> **Important notice regarding soft-reboots ([Issue #29](https://github.com/auag0/HideMockLocation/issues/29)):**
> Scoping this module to **System Framework (`android`)** can cause a fatal `system_server` crash (`IllegalStateException: Preconditions.checkState` in `LocationEventLog$AggregateStats.markRequestBackground`), resulting in repeated **soft-reboots** during app foreground/background transitions.
> 
> * **Cause:** Hooking `android.location.Location` methods inside `system_server` desynchronizes the internal state tracking of `LocationProviderManager` and `LocationEventLog$AggregateStats`, tripping assertion checks when request counters desync.
> * **Workaround:** **Do NOT select "System Framework" in your module scope.** The module still **works normally** if you only scope the specific target apps you want to hide mock locations from, along with **Settings Storage**.

---

**Prevents detection of mock location**  
**Supports Android 6–16**  
**Test App**: **[MockLocationDetector](https://github.com/auag0/MockLocationDetector)**

## Compatibility Notes
- For **Xposed API 100 or lower**: Please use **[v1.2.2](https://github.com/auag0/HideMockLocation/releases/tag/v1.2.2) or below**.
- For **Xposed API 101 or higher**: **[v2.0.0 or above](https://github.com/auag0/HideMockLocation/releases)** is recommended.

## Usage
1. Download the APK from the [latest releases](https://github.com/auag0/HideMockLocation/releases/latest) and install it on your device.
2. Enable the module in your Xposed manager.
3. Select the target apps from which you want to hide the mock location.
4. **Scope Recommendations:**
   * **Settings Storage** (`com.android.providers.settings`) — *Recommended*
   * Your target applications (e.g. games, navigation, work apps) — *Recommended*
   * ⚠️ **Do NOT select "System Framework" (`android`)**, as it causes bootloops/soft-reboots due to [Issue #29](https://github.com/auag0/HideMockLocation/issues/29).
5. Reboot your device (if required by your Xposed manager) and you're all set!

## Hooked Methods
- `android.location.Location`
  - `isFromMockProvider()`
  - `isMock()`
  - `setIsFromMockProvider()`
  - `setMock()`
  - `getExtras()`
  - `setExtras()`
  - `getProvider()`
  - `set()`
- `android.provider.Settings`
  - `Secure.getStringForUser()`
- `android.app.AppOpsManager`
  - `checkOp()`
  - `checkOpNoThrow()`
  - `unsafeCheckOp()`
  - `unsafeCheckOpNoThrow()`
- `com.android.providers.settings.SettingsProvider`
  - `call()`
- `com.android.server.appop.AppOpsService`
- `com.android.server.AppOpsService`
  - `checkOperationImpl()`
  - `checkOperation()`

## How to set Mock Location app via ADB ([Stack Overflow](https://stackoverflow.com/questions/40414011/how-to-set-the-android-6-0-mock-location-app-from-adb/43747384#43747384))

### Granting Permission
`adb shell appops set <MOCK_LOCATION_APP_PKG> android:mock_location allow`

### Revoking Permission
`adb shell appops set <MOCK_LOCATION_APP_PKG> android:mock_location deny`
> **Note:** For rooted devices using a terminal emulator on the device itself, omit the `adb shell` prefix.

## Disclaimer
**Use this module at your own risk.** The developer assumes no responsibility for any damage, data loss, or issues caused by the use of this software.

## Credits & References
[ThePieMonster#HideMockLocation](https://github.com/ThePieMonster/HideMockLocation)
