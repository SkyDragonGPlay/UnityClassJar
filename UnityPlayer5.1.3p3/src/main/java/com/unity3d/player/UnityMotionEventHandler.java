package com.unity3d.player;

import java.util.*;
import android.app.*;
import android.content.*;
import java.util.concurrent.*;
import android.view.*;

public final class UnityMotionEventHandler implements IUnityMotionEventHandler
{
    private final Queue<MotionEvent> mQueue;
    private final Activity mActivity;
    private Runnable c;
    
    public UnityMotionEventHandler(final ContextWrapper contextWrapper) {
        this.mQueue = new ConcurrentLinkedQueue();
        this.c = new Runnable() {
            private void dispatchGenericMotionEvent(final View view, final MotionEvent motionEvent) {
                if (UnityConstants.IS_SDK_VERSION_GE_12) {
                    UnityConstants.DISPATCH_GENERIC_MOTION_EVENT_CALLBACK.dispatchGenericMotionEvent(view, motionEvent);
                }
            }
            
            @Override
            public final void run() {
                MotionEvent motionEvent;
                while ((motionEvent = mQueue.poll()) != null) {
                    final View decorView = UnityMotionEventHandler.this.mActivity.getWindow().getDecorView();
                    final int source;
                    if (((source = motionEvent.getSource()) & 0x2) != 0x0) {
                        switch (motionEvent.getAction() & 0xFF) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6: {
                                decorView.dispatchTouchEvent(motionEvent);
                                continue;
                            }
                            default: {
                                dispatchGenericMotionEvent(decorView, motionEvent);
                                continue;
                            }
                        }
                    }
                    else if ((source & 0x4) != 0x0) {
                        decorView.dispatchTrackballEvent(motionEvent);
                    }
                    else {
                        dispatchGenericMotionEvent(decorView, motionEvent);
                    }
                }
            }
        };
        this.mActivity = (Activity)contextWrapper;
    }
    
    private static MotionEvent.PointerCoords[] getPointerCoords(final int n, final float[] array) {
        final MotionEvent.PointerCoords[] array2 = new MotionEvent.PointerCoords[n];
        a(array2, array, 0);
        return array2;
    }
    
    private static int a(final MotionEvent.PointerCoords[] pointerCoordses, final float[] array2, int n) {
        for (int i = 0; i < pointerCoordses.length; ++i) {
            final MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
            pointerCoordses[i] = pointerCoords;
            final MotionEvent.PointerCoords pointerCoords2 = pointerCoords;
            pointerCoords.orientation = array2[n++];
            pointerCoords2.pressure = array2[n++];
            pointerCoords2.size = array2[n++];
            pointerCoords2.toolMajor = array2[n++];
            pointerCoords2.toolMinor = array2[n++];
            pointerCoords2.touchMajor = array2[n++];
            pointerCoords2.touchMinor = array2[n++];
            pointerCoords2.x = array2[n++];
            pointerCoords2.y = array2[n++];
        }
        return n;
    }
    
    @Override
    public final void forwardMotionEventToDalvik(final long downTime, final long eventTime, final int action, final int pointerCount, final int[] pointerIds,
                                                 final float[] array2, final int metaState, final float xPrecision, final float yPrecision, final int deviceId,
                                                 final int edgeFlags, final int source, final int flags, final int n12, final long[] array3, final float[] array4)
    {
        if (this.mActivity != null) {
//            pointerCoords
            final MotionEvent obtain = MotionEvent.obtain(downTime, eventTime, action, pointerCount,
                    pointerIds, getPointerCoords(pointerCount, array2), metaState, xPrecision, yPrecision, deviceId,
                    edgeFlags, source, flags);

            int a = 0;
            for (int i = 0; i < n12; ++i) {
                final MotionEvent.PointerCoords[] pointerCoordses = new MotionEvent.PointerCoords[pointerCount];
                a = a(pointerCoordses, array4, a);
                obtain.addBatch(array3[i], pointerCoordses, metaState);
            }
            this.mQueue.add(obtain);
            this.mActivity.runOnUiThread(this.c);
        }
    }
}
