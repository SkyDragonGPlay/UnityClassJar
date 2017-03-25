package com.unity3d.player;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.*;
import android.os.*;

public final class UnitySurfaceTexture implements f
{
    private static final SurfaceTexture _instance;
    private int b;
    private volatile boolean c;
    
    public UnitySurfaceTexture() {
        this.b = 1;
    }
    
    @Override
    public final boolean a(final Camera camera) {
        try {
            camera.setPreviewTexture(UnitySurfaceTexture._instance);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    public final void a() {
        this.b = (UnityConstants.IS_SDK_VERSION_GE_19 ? 5894 : 1);
    }
    
    @Override
    public final void setSystemUiVisibility(final View view, final boolean c) {
        this.c = c;
        view.setSystemUiVisibility(this.c ? (view.getSystemUiVisibility() | this.b) : (view.getSystemUiVisibility() & ~this.b));
    }
    
    @Override
    public final void a(final View view) {
        view.setOnSystemUiVisibilityChangeListener((View.OnSystemUiVisibilityChangeListener)new View.OnSystemUiVisibilityChangeListener() {
            public final void onSystemUiVisibilityChange(final int n) {
                UnitySurfaceTexture.a(UnitySurfaceTexture.this, view);
            }
        });
    }
    
    @Override
    public final void b(final View view) {
        if (UnityConstants.IS_SDK_VERSION_GE_19) {
            this.a(view, 500);
            return;
        }
        if (this.c) {
            this.setSystemUiVisibility(view, false);
            this.c = true;
            this.a(view, 500);
        }
    }
    
    private void a(final View view, final int n) {
        final Handler handler;
        if ((handler = view.getHandler()) == null) {
            this.setSystemUiVisibility(view, this.c);
            return;
        }
        handler.postDelayed((Runnable)new Runnable() {
            @Override
            public final void run() {
                UnitySurfaceTexture.this.setSystemUiVisibility(view, UnitySurfaceTexture.this.c);
            }
        }, (long)n);
    }
    
    static /* synthetic */ void a(final UnitySurfaceTexture d, final View view) {
        d.a(view, 1000);
    }
    
    static {
        _instance = new SurfaceTexture(-1);
    }
}
