# Hide Mock Location
**Prevents detection of mock location**  
**Supported Android 6~14**  
Testapp: **[MockLocationDetector](https://github.com/auag0/MockLocationDetector)**
## usage
- Download the APK([here](https://github.com/auag0/HideMockLocation/releases/latest)) and install APK on device
- Enable the module in xposed
- if use lsposed, choose the app from which to hide mock location
- owari!

## hooked methods
- android.location.Location
  - isFromMockProvider()
  - isMock()
  - getExtras().getBoolean(key="mockLocation")
- android.provider.Settings
  - Secure.getStringForUser(name="mock_location")
  - System.getStringForUser(name="mock_location")
  - Global.getStringForUser(name="mock_location")
  - NameValueCache.getStringForUser(name="mock_location")

## how to set MockLocation app from Adb [(stackoverflow)](https://stackoverflow.com/questions/40414011/how-to-set-the-android-6-0-mock-location-app-from-adb/43747384#43747384)
### Allowing app for mock locaiton
`adb shell appops set <MOCK_LOCATION_APP_PKG> android:mock_location allow`
### Removing app for mock location
`adb shell appops set <MOCK_LOCATION_APP_PKG> android:mock_location deny`
#### for root device, remove `adb shell` in terminal and run

## super thanks (reference)
[ThePieMonster#HideMockLocation](https://github.com/ThePieMonster/HideMockLocation)
