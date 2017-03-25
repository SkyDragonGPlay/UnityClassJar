package com.unity3d.player;

import android.app.*;
import android.hardware.display.*;
import android.content.*;
import android.os.*;
import android.view.*;

public final class UnityPresentation implements IUnityPresentation
{
    private Object lockObject;
    private Presentation mPresentation;
    private DisplayManager.DisplayListener mDisplayListener;
    
    public UnityPresentation() {
        this.lockObject = new Object[0];
    }
    
    @Override
    public final void registerDisplayListener(final UnityPlayer unityPlayer, final Context context) {
        final DisplayManager displayManager;
        if ((displayManager = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE)) == null) {
            return;
        }
        displayManager.registerDisplayListener(mDisplayListener = (DisplayManager.DisplayListener)new DisplayManager.DisplayListener() {
            public final void onDisplayAdded(final int n) {
                unityPlayer.displayChanged(-1, null);
            }
            
            public final void onDisplayRemoved(final int n) {
                unityPlayer.displayChanged(-1, null);
            }
            
            public final void onDisplayChanged(final int n) {
            }
        }, (Handler)null);
    }
    
    @Override
    public final void unregisterDisplayListener(final Context context) {
        if (this.mDisplayListener == null) {
            return;
        }
        final DisplayManager displayManager;
        if ((displayManager = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE)) == null) {
            return;
        }
        displayManager.unregisterDisplayListener(this.mDisplayListener);
    }
    
    @Override
    public final boolean installPresentationDisplay(final UnityPlayer unityPlayer, final Context context, final int n) {
        synchronized (this.lockObject) {
            final Display display;
            if (this.mPresentation != null && this.mPresentation.isShowing() && (display = this.mPresentation.getDisplay()) != null && display.getDisplayId() == n) {
                return true;
            }
            final DisplayManager displayManager;
            if ((displayManager = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE)) == null) {
                return false;
            }
            final Display display2;
            if ((display2 = displayManager.getDisplay(n)) == null) {
                return false;
            }
            unityPlayer.runOnUiThread(new Runnable() {
                @Override
                public final void run() {
                    synchronized (UnityPresentation.this.lockObject) {
                        if (UnityPresentation.this.mPresentation != null) {
                            UnityPresentation.this.mPresentation.dismiss();
                        }
                        UnityPresentation.this.mPresentation = new Presentation(context, display2) {
                            protected final void onCreate(final Bundle bundle) {
                                final SurfaceView contentView;
                                (contentView = new SurfaceView(context)).getHolder().addCallback((SurfaceHolder.Callback)new SurfaceHolder.Callback() {
                                    public final void surfaceCreated(final SurfaceHolder surfaceHolder) {
                                        unityPlayer.displayChanged(1, surfaceHolder.getSurface());
                                    }
                                    
                                    public final void surfaceChanged(final SurfaceHolder surfaceHolder, final int n, final int n2, final int n3) {
                                        unityPlayer.displayChanged(1, surfaceHolder.getSurface());
                                    }
                                    
                                    public final void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
                                        unityPlayer.displayChanged(1, null);
                                    }
                                });
                                this.setContentView((View)contentView);
                            }
                            
                            public final void onDisplayRemoved() {
                                this.dismiss();
                                synchronized (UnityPresentation.this.lockObject) {
                                    UnityPresentation.this.mPresentation = null;
                                }
                            }
                        };
                        UnityPresentation.this.mPresentation.show();
                    }
                }
            });
            return true;
        }
    }
}
