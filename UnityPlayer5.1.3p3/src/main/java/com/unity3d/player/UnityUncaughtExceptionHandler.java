package com.unity3d.player;

import android.os.*;

final class UnityUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private volatile Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;
    
    final synchronized boolean init() {
        final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
        if ((defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()) == this) {
            return false;
        }
        this.mUncaughtExceptionHandler = defaultUncaughtExceptionHandler;
        Thread.setDefaultUncaughtExceptionHandler(this);
        return true;
    }
    
    @Override
    public final synchronized void uncaughtException(final Thread thread, final Throwable t) {
        try {
            final Error error;
            (error = new Error(String.format("FATAL EXCEPTION [%s]\n", thread.getName()) + String.format("Unity version     : %s\n", "5.1.3p3") + String.format("Device model      : %s %s\n", Build.MANUFACTURER, Build.MODEL) + String.format("Device fingerprint: %s\n", Build.FINGERPRINT))).setStackTrace(new StackTraceElement[0]);
            error.initCause(t);
            this.mUncaughtExceptionHandler.uncaughtException(thread, error);
        }
        catch (Throwable t2) {
            this.mUncaughtExceptionHandler.uncaughtException(thread, t);
        }
    }
}
