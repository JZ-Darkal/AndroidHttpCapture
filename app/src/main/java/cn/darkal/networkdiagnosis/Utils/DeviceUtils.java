package cn.darkal.networkdiagnosis.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.proxy.dns.AdvancedHostResolver;

import cn.darkal.networkdiagnosis.SysApplication;

/**
 * Created by Darkal on 2016/9/21.
 */

public class DeviceUtils {
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //dip To  px
    public static int dip2px(Context context, int dp) {
        //dp和px的转换关系
        float density = context.getResources().getDisplayMetrics().density;
        //2*1.5+0.5  2*0.75 = 1.5+0.5
        return (int)(dp*density+0.5);
    }


    public static void changeHost(BrowserMobProxy browserMobProxy,String newValue){
        AdvancedHostResolver advancedHostResolver = browserMobProxy.getHostNameResolver();
        advancedHostResolver.clearHostRemappings();
        for (String temp : newValue.split("\\n")) {
            if (temp.split(" ").length == 2) {
                advancedHostResolver.remapHost(temp.split(" ")[1], temp.split(" ")[0]);
                Log.e("~~~~remapHost ", temp.split(" ")[1] + " " + temp.split(" ")[0]);
            }
        }
        browserMobProxy.setHostNameResolver(advancedHostResolver);
    }
}
