package org.fmod;

import android.media.*;
import java.nio.*;
import android.util.*;

public class FMODAudioDevice implements Runnable
{
    private volatile Thread a;
    private volatile boolean b;
    private volatile a c;
    private AudioTrack d;
    private boolean e;
    private ByteBuffer f;
    private byte[] g;
    private volatile org.fmod.a h;
    private Object i;
    private boolean j;
    private static int k;
    private static int l;
    private static int m;
    private static int n;
    
    public FMODAudioDevice() {
        this.a = null;
        this.b = false;
        this.c = null;
        this.d = null;
        this.e = false;
        this.f = null;
        this.g = null;
        this.i = new Object[0];
        this.j = false;
    }
    
    public synchronized void start() {
        if (this.a != null) {
            this.stop();
        }
        (this.a = new Thread(this, "FMODAudioDevice")).setPriority(10);
        this.b = true;
        this.fmodInitJni();
        this.unblockStreaming();
        this.a.start();
        if (this.h != null) {
            this.h.b();
        }
    }
    
    public synchronized void stop() {
        while (this.a != null) {
            this.b = false;
            try {
                synchronized (this.i) {
                    this.i.notifyAll();
                }
                this.a.join();
                this.a = null;
                this.blockStreaming();
            }
            catch (InterruptedException ex) {}
        }
        if (this.h != null) {
            this.h.c();
        }
    }
    
    public synchronized void close() {
        this.stop();
        this.unblockStreaming();
    }
    
    private synchronized void blockStreaming() {
        if (this.isInitialized() && this.c == null) {
            (this.c = new a()).start();
        }
    }
    
    private synchronized void unblockStreaming() {
        if (this.c != null) {
            try {
                do {
                    this.c.a();
                    this.c.join(10L);
                } while (this.c.isAlive());
                this.c = null;
            }
            catch (InterruptedException ex) {}
        }
    }
    
    public boolean isInitialized() {
        return this.fmodGetInfo(FMODAudioDevice.k) > 0;
    }
    
    public boolean isRunning() {
        return this.a != null && this.a.isAlive();
    }
    
    public void audioTrackInitialized() {
        synchronized (this.i) {
            this.j = true;
            this.i.notifyAll();
        }
    }
    
    @Override
    public void run() {
        int n = 3;
        while (this.b) {
            if (!this.isInitialized()) {
                synchronized (this.i) {
                    try {
                        if (this.j) {
                            continue;
                        }
                        this.i.wait();
                    }
                    catch (InterruptedException ex) {}
                    continue;
                }
            }
            if (!this.e && n > 0) {
                this.releaseAudioTrack();
                final int fmodGetInfo;
                int n2 = Math.round(AudioTrack.getMinBufferSize(fmodGetInfo = this.fmodGetInfo(FMODAudioDevice.k), 3, 2) * 1.1f) & 0xFFFFFFFC;
                final int fmodGetInfo2 = this.fmodGetInfo(FMODAudioDevice.l);
                final int fmodGetInfo3 = this.fmodGetInfo(FMODAudioDevice.m);
                if (fmodGetInfo2 * fmodGetInfo3 * 4 > n2) {
                    n2 = fmodGetInfo2 * fmodGetInfo3 * 4;
                }
                this.d = new AudioTrack(3, fmodGetInfo, 3, 2, n2, 1);
                this.e = (this.d.getState() == 1);
                if (this.e) {
                    n = 3;
                    this.f = ByteBuffer.allocateDirect(fmodGetInfo2 * 2 * 2);
                    this.g = new byte[this.f.capacity()];
                    this.d.play();
                }
                else {
                    Log.e("FMOD", "AudioTrack failed to initialize (status " + this.d.getState() + ")");
                    this.releaseAudioTrack();
                    --n;
                }
            }
            if (this.e) {
                if (this.fmodGetInfo(FMODAudioDevice.n) == 1) {
                    this.fmodProcess(this.f);
                    this.f.get(this.g, 0, this.f.capacity());
                    this.d.write(this.g, 0, this.f.capacity());
                    this.f.position(0);
                }
                else {
                    this.releaseAudioTrack();
                }
            }
        }
        this.releaseAudioTrack();
    }
    
    private void releaseAudioTrack() {
        if (this.d != null) {
            if (this.d.getState() == 1) {
                this.d.stop();
            }
            this.d.release();
            this.d = null;
        }
        this.f = null;
        this.g = null;
        this.e = false;
    }
    
    private native int fmodGetInfo(final int p0);
    
    private native int fmodProcess(final ByteBuffer p0);
    
    private native int fmodInitJni();
    
    private native int fmodBlockStreaming();
    
    private native int fmodUnblockStreaming();
    
    native int fmodProcessMicData(final ByteBuffer p0, final int p1);
    
    public synchronized int startAudioRecord(final int n, final int n2, final int n3) {
        if (this.h == null) {
            (this.h = new org.fmod.a(this, n, n2)).b();
        }
        return this.h.a();
    }
    
    public synchronized void stopAudioRecord() {
        if (this.h != null) {
            this.h.c();
            this.h = null;
        }
    }
    
    static {
        FMODAudioDevice.k = 0;
        FMODAudioDevice.l = 1;
        FMODAudioDevice.m = 2;
        FMODAudioDevice.n = 3;
    }
    
    private final class a extends Thread
    {
        private Object b;
        
        a() {
            super("FMODStreamBlocker");
            this.b = new Object();
        }
        
        @Override
        public final void run() {
            if (FMODAudioDevice.this.fmodBlockStreaming() != 0) {
                throw new RuntimeException("Unable to block fmod streaming thread");
            }
            synchronized (this.b) {
                try {
                    this.b.wait();
                }
                catch (InterruptedException ex) {}
            }
            if (FMODAudioDevice.this.fmodUnblockStreaming() != 0) {
                throw new RuntimeException("Unable to unblock fmod streaming thread");
            }
        }
        
        private void a() {
            synchronized (this.b) {
                this.b.notifyAll();
            }
        }
    }
}
