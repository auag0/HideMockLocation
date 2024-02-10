# Hide Mock Location

<img src="./app/src/main/ic_launcher-playstore.png" width="100">

## usage
- Download the APK[(here)](https://github.com/auag0/HideMockLocation/releases/latest) and install APK on device
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

## super thanks (reference)
[ThePieMonster#HideMockLocation](https://github.com/ThePieMonster/HideMockLocation)