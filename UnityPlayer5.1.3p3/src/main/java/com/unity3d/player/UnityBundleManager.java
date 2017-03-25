package com.unity3d.player;

import android.os.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;

public final class UnityBundleManager
{
    private final Bundle mBundle;
    
    public UnityBundleManager(Activity activity) {
        Bundle bundle = Bundle.EMPTY;
        final PackageManager packageManager = activity.getPackageManager();
        ComponentName componentName = activity.getComponentName();
        try {
            final ActivityInfo activityInfo;
            if ((activityInfo = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)) != null && activityInfo.metaData != null) {
                bundle = activityInfo.metaData;
            }
        }
        catch (PackageManager.NameNotFoundException ex) {
            UnityLog.Log(6, "Unable to retreive meta data for activity '" + componentName + "'");
        }
        this.mBundle = new Bundle(bundle);
    }
    
    public final boolean isForwardNativeEventsToDalvik() {
        return this.mBundle.getBoolean(getBundleKey("ForwardNativeEventsToDalvik"));
    }
    
    private static String getBundleKey(final String s) {
        return String.format("%s.%s", "unityplayer", s);
    }
}
