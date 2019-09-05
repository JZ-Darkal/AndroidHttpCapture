package cn.darkal.networkdiagnosis.Utils.NetInfo;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * Created by rock on 16-2-25.
 */
public class SystemBasicInfo {
    public static String getBuildInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("Product:" + Build.PRODUCT);
        builder.append("\nTags:" + Build.TAGS);
        builder.append("\nCPU_ABI:" + Build.CPU_ABI);
        builder.append("\nVERSION_CODES.BASE:" + Build.VERSION_CODES.BASE);
        builder.append("\nMODEL:" + Build.MODEL);
        builder.append("\nSDK:" + Build.VERSION.SDK);
        builder.append("\nVERSION.RELEASE:" + Build.VERSION.RELEASE);
        builder.append("\nDEVICE:" + Build.DEVICE);
        builder.append("\nBrand:" + Build.BRAND);
        builder.append("\nBoard:" + Build.BOARD);
        builder.append("\nFINGERPRINT:" + Build.FINGERPRINT);
        builder.append("\nID:" + Build.ID);
        builder.append("\nManufacturer:" + Build.MANUFACTURER);
        builder.append("\nUser:" + Build.USER);

        return builder.toString();
    }

    /**
     * 手机UUID
     */
    public static String getUUID(Context context) {
        try {
            String androidId = getAndroidId(context);
            String deviceId = getDeviceId(context) == null ? "null" : getDeviceId(context);
            UUID uuid = new UUID(androidId.hashCode(), ((long) deviceId.hashCode() << 32) | deviceId.hashCode());
            return uuid.toString();
        } catch (Exception e) {
            return "null";
        }
    }

    /**
     * 安卓Id
     */
    public static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            return "null";
        }
    }


    public static String getDeviceId(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Exception e) {
            return "null";
        }
    }
}
