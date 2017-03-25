package com.unity3d.player;

import android.app.*;
import android.view.*;

abstract class UnityCameraSurface implements SurfaceHolder.Callback
{
    private final Activity mActivity;
    private final int mType;
    private SurfaceView mSurfaceView;
    
    UnityCameraSurface(final int n) {
        this.mActivity = (Activity) UnitySurfaceViewManager.Instance.getContext();
        this.mType = 3;
    }
    
    final void a() {
        this.mActivity.runOnUiThread((Runnable)new Runnable() {
            @Override
            public final void run() {
                if (UnityCameraSurface.this.mSurfaceView == null) {
                    UnityCameraSurface.this.mSurfaceView = new SurfaceView(UnitySurfaceViewManager.Instance.getContext());
                    UnityCameraSurface.this.mSurfaceView.getHolder().setType(UnityCameraSurface.this.mType);
                    UnityCameraSurface.this.mSurfaceView.getHolder().addCallback((SurfaceHolder.Callback)UnityCameraSurface.this);
                    UnitySurfaceViewManager.Instance.a((View)UnityCameraSurface.this.mSurfaceView);
                    UnityCameraSurface.this.mSurfaceView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    
    final void close() {
        this.mActivity.runOnUiThread((Runnable)new Runnable() {
            @Override
            public final void run() {
                if (UnityCameraSurface.this.mSurfaceView != null) {
                    UnitySurfaceViewManager.Instance.removeCameraSurface((View)UnityCameraSurface.this.mSurfaceView);
                }
                UnityCameraSurface.this.mSurfaceView = null;
            }
        });
    }
    
    public void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
    }
    
    public void surfaceChanged(final SurfaceHolder surfaceHolder, final int n, final int n2, final int n3) {
    }
}
