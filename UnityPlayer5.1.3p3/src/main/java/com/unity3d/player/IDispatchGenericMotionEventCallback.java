package com.unity3d.player;

import android.view.*;

public interface IDispatchGenericMotionEventCallback
{
    boolean dispatchGenericMotionEvent(final View view, final MotionEvent motionEvent);
}
