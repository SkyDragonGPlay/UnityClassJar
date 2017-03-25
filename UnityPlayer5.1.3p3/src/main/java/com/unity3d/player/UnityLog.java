package com.unity3d.player;

import android.util.*;

final class UnityLog
{
    protected static boolean bShowLog;
    
    protected static void Log(final int n, final String s) {
        if (UnityLog.bShowLog) {
            return;
        }
        if (n == 6) {
            Log.e("Unity", s);
        }
        if (n == 5) {
            Log.w("Unity", s);
        }
    }
    
    static {
        UnityLog.bShowLog = false;
    }
}
