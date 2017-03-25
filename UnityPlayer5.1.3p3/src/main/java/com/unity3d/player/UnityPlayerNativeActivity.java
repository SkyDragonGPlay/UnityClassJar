package com.unity3d.player;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.res.*;
import android.view.*;

public class UnityPlayerNativeActivity extends NativeActivity
{
    protected UnityPlayer mUnityPlayer;
    
    protected void onCreate(final Bundle bundle) {
        this.requestWindowFeature(1);
        super.onCreate(bundle);
        this.getWindow().takeSurface((SurfaceHolder.Callback2)null);
        this.getWindow().setFormat(2);
        this.mUnityPlayer = new UnityPlayer((ContextWrapper)this);
        if (this.mUnityPlayer.getSettings().getBoolean("hide_status_bar", true)) {
            this.getWindow().setFlags(1024, 1024);
        }
        this.setContentView((View)this.mUnityPlayer);
        this.mUnityPlayer.requestFocus();
    }
    
    protected void onDestroy() {
        this.mUnityPlayer.quit();
        super.onDestroy();
    }
    
    protected void onPause() {
        super.onPause();
        this.mUnityPlayer.pause();
    }
    
    protected void onResume() {
        super.onResume();
        this.mUnityPlayer.resume();
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mUnityPlayer.configurationChanged(configuration);
    }
    
    public void onWindowFocusChanged(final boolean b) {
        super.onWindowFocusChanged(b);
        this.mUnityPlayer.windowFocusChanged(b);
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        if (keyEvent.getAction() == 2) {
            return this.mUnityPlayer.injectEvent((InputEvent)keyEvent);
        }
        return super.dispatchKeyEvent(keyEvent);
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        return this.mUnityPlayer.injectEvent((InputEvent)keyEvent);
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        return this.mUnityPlayer.injectEvent((InputEvent)keyEvent);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return this.mUnityPlayer.injectEvent((InputEvent)motionEvent);
    }
    
    public boolean onGenericMotionEvent(final MotionEvent motionEvent) {
        return this.mUnityPlayer.injectEvent((InputEvent)motionEvent);
    }
}
