package com.unity3d.player;

import android.view.*;

public final class DispatchGenericMotionEventCallback implements IDispatchGenericMotionEventCallback
{
    @Override
    public final boolean dispatchGenericMotionEvent(final View view, final MotionEvent motionEvent) {
        return view.dispatchGenericMotionEvent(motionEvent);
    }
}
