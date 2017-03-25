package com.unity3d.player;

import android.os.*;

public final class UnityConstants
{
    static final boolean IS_SDK_VERSION_GE_11 = (Build.VERSION.SDK_INT >= 11);;
    static final boolean IS_SDK_VERSION_GE_12 = (Build.VERSION.SDK_INT >= 12);
    static final boolean IS_SDK_VERSION_GE_14 = (Build.VERSION.SDK_INT >= 14);
    static final boolean IS_SDK_VERSION_GE_16 = (Build.VERSION.SDK_INT >= 16);
    static final boolean IS_SDK_VERSION_GE_17 = (Build.VERSION.SDK_INT >= 17);
    static final boolean IS_SDK_VERSION_GE_19 = (Build.VERSION.SDK_INT >= 19);
    static final boolean IS_SDK_VERSION_GE_21 = (Build.VERSION.SDK_INT >= 21);
    static final f h = (UnityConstants.IS_SDK_VERSION_GE_11 ? new UnitySurfaceTexture() : null);
    static final IDispatchGenericMotionEventCallback DISPATCH_GENERIC_MOTION_EVENT_CALLBACK = (UnityConstants.IS_SDK_VERSION_GE_12 ? new DispatchGenericMotionEventCallback() : null);
    static final h j = (UnityConstants.IS_SDK_VERSION_GE_16 ? new k() : null);
    static final IUnityPresentation UNITY_PRESENTATION = (UnityConstants.IS_SDK_VERSION_GE_17 ? new UnityPresentation() : null);
}
