package android.app;

import java.util.List;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(AppOpsManager.class)
public class AppOpsManagerHidden {
    public static final int OP_MOCK_LOCATION = 58;

    public List<PackageOps> getPackagesForOps(int[] ops) {
        throw new RuntimeException("Stub!");
    }

    public void setMode(int code, int uid, String packageName, int mode) {
        throw new RuntimeException("Stub!");
    }

    public static class OpEntry {
        public int getMode() {
            throw new RuntimeException("Stub!");
        }
    }

    public static class PackageOps {
        public String getPackageName() {
            throw new RuntimeException("Stub!");
        }

        public List<OpEntry> getOps() {
            throw new RuntimeException("Stub!");
        }
    }
}
