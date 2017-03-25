package org.fmod;

import java.nio.*;
import android.media.*;
import android.util.*;

final class a implements Runnable
{
    private final FMODAudioDevice a;
    private final ByteBuffer b;
    private final int c;
    private final int d;
    private final int e;
    private volatile Thread f;
    private volatile boolean g;
    private AudioRecord h;
    private boolean i;
    
    a(final FMODAudioDevice a, final int c, final int d) {
        this.a = a;
        this.c = c;
        this.d = d;
        this.e = 2;
        this.b = ByteBuffer.allocateDirect(AudioRecord.getMinBufferSize(c, d, 2));
    }
    
    public final int a() {
        return this.b.capacity();
    }
    
    public final void b() {
        if (this.f != null) {
            this.c();
        }
        this.g = true;
        (this.f = new Thread(this)).start();
    }
    
    public final void c() {
        while (this.f != null) {
            this.g = false;
            try {
                this.f.join();
                this.f = null;
            }
            catch (InterruptedException ex) {}
        }
    }
    
    @Override
    public final void run() {
        int n = 3;
        while (this.g) {
            if (!this.a.isInitialized()) {
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException ex) {
                    this.g = false;
                }
            }
            else {
                if (!this.i && n > 0) {
                    this.d();
                    this.h = new AudioRecord(1, this.c, this.d, this.e, this.b.capacity());
                    this.i = (this.h.getState() == 1);
                    if (this.i) {
                        n = 3;
                        this.b.position(0);
                        this.h.startRecording();
                    }
                    else {
                        Log.e("FMOD", "AudioRecord failed to initialize (status " + this.h.getState() + ")");
                        --n;
                        this.d();
                    }
                }
                if (!this.i || this.h.getRecordingState() != 3) {
                    continue;
                }
                this.a.fmodProcessMicData(this.b, this.h.read(this.b, this.b.capacity()));
                this.b.position(0);
            }
        }
        this.d();
    }
    
    private void d() {
        if (this.h != null) {
            if (this.h.getState() == 1) {
                this.h.stop();
            }
            this.h.release();
            this.h = null;
        }
        this.b.position(0);
        this.i = false;
    }
}
