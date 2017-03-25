package com.unity3d.player;

import android.view.*;
import java.util.concurrent.locks.*;

public final class k implements h
{
    private Choreographer a;
    private long b;
    private Choreographer.FrameCallback c;
    private Lock d;
    
    public k() {
        this.a = null;
        this.b = 0L;
        this.d = new ReentrantLock();
    }
    
    @Override
    public final void a(final UnityPlayer unityPlayer) {
        this.d.lock();
        if (this.a == null) {
            this.a = Choreographer.getInstance();
            if (this.a != null) {
                this.c = (Choreographer.FrameCallback)new Choreographer.FrameCallback() {
                    public final void doFrame(final long n) {
                        UnityPlayer.lockNativeAccess();
                        if (UnityEnviroment.getIsNativeLibraryLoaded()) {
                            unityPlayer.nativeAddVSyncTime(n);
                        }
                        UnityPlayer.unlockNativeAccess();
                        k.this.d.lock();
                        if (k.this.a != null) {
                            k.this.a.postFrameCallback(k.this.c);
                        }
                        k.this.d.unlock();
                    }
                };
                this.a.postFrameCallback(this.c);
            }
        }
        this.d.unlock();
    }
    
    @Override
    public final void a() {
        this.d.lock();
        if (this.a != null) {
            this.a.removeFrameCallback(this.c);
        }
        this.a = null;
        this.d.unlock();
    }
}
