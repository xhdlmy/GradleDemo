package com.bruce.gradle;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;

/**
 * Created by Bruce on 2019/6/20.
 */

public class GApplication extends Application {

    protected static int sVersionCode;
    protected static String sVersionName;
    protected static String sPackageName;
    protected static String sChannelId;

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this.getApplicationContext();
        initAppInfo();

        // MTA 分析
        // [可选]设置是否打开debug输出，上线时请关闭，Logcat标签为"MtaSDK"
        boolean apkInDebug = isApkInDebug(this);
        StatConfig.setDebugEnable(apkInDebug);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this);
    }

    private void initAppInfoByBuildConfig() {
        sVersionCode = BuildConfig.VERSION_CODE;
        sVersionName = BuildConfig.VERSION_NAME;
        sPackageName = BuildConfig.APPLICATION_ID;
        sChannelId = BuildConfig.FLAVOR;
    }

    private void initAppInfo() {
        PackageManager packageManager = sContext.getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(sContext.getPackageName(),0);
            sVersionCode = packInfo.versionCode;
            sVersionName = packInfo.versionName;
            sPackageName = packInfo.packageName;
            ApplicationInfo appInfo = packageManager.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            sChannelId = appInfo.metaData.getString("CHANNEL_ID");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            sVersionCode = -1;
            sVersionName = "package not found!";
            sPackageName = sContext.getPackageName();
            sChannelId = "debug";
        }
    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

}
