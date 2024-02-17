package io.github.auag0.hidemocklocation;

import android.content.Context;

interface IMockLocationService {
    void writeMockLocation(String mockLocationAppName);
    String getCurrentMockLocationApp();
    void removeAllMockLocations();
    void removeMockLocationForApp(String appName);
    List<PackageInfo> getMockLocationApps();
}