package com.unity3d.player;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.res.*;
import android.view.*;

public class UnityPlayerActivity extends Activity
{
    protected UnityPlayer a;
    
    protected void onCreate(final Bundle bundle) {
        this.requestWindowFeature(1);
        super.onCreate(bundle);
        this.getWindow().setFormat(2);
        this.a = new UnityPlayer((ContextWrapper)this);
        if (this.a.getSettings().getBoolean("hide_status_bar", true)) {
            this.getWindow().setFlags(1024, 1024);
        }
        this.setContentView((View)this.a);
        this.a.requestFocus();
    }
    
    protected void onDestroy() {
        this.a.quit();
        super.onDestroy();
    }
    
    protected void onPause() {
        super.onPause();
        this.a.pause();
    }
    
    protected void onResume() {
        super.onResume();
        this.a.resume();
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.a.configurationChanged(configuration);
    }
    
    public void onWindowFocusChanged(final boolean b) {
        super.onWindowFocusChanged(b);
        this.a.windowFocusChanged(b);
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        if (keyEvent.getAction() == 2) {
            return this.a.injectEvent((InputEvent)keyEvent);
        }
        return super.dispatchKeyEvent(keyEvent);
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        return this.a.injectEvent((InputEvent)keyEvent);
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        return this.a.injectEvent((InputEvent)keyEvent);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return this.a.injectEvent((InputEvent)motionEvent);
    }
    
    public boolean onGenericMotionEvent(final MotionEvent motionEvent) {
        return this.a.injectEvent((InputEvent)motionEvent);
    }
}
