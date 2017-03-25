package com.unity3d.player;

import android.content.*;

public interface IUnityPresentation
{
    void registerDisplayListener(final UnityPlayer p0, final Context p1);
    
    void unregisterDisplayListener(final Context p0);
    
    boolean installPresentationDisplay(final UnityPlayer p0, final Context p1, final int p2);
}
