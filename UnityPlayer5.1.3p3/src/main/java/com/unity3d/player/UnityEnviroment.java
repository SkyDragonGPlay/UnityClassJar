package com.unity3d.player;

final class UnityEnviroment
{
    private static boolean mIsLoadNativeLibrary;
    private boolean mIsWindowFocus;
    private boolean mIsPlaying;
    private boolean mIsPause;
    
    UnityEnviroment() {
        this.mIsWindowFocus = false;
        this.mIsPlaying = false;
        this.mIsPause = true;
    }
    
    static void setNativeLibraryLoaded() {
        UnityEnviroment.mIsLoadNativeLibrary = true;
    }
    
    static void setNativeLibraryUnload() {
        UnityEnviroment.mIsLoadNativeLibrary = false;
    }
    
    static boolean getIsNativeLibraryLoaded() {
        return UnityEnviroment.mIsLoadNativeLibrary;
    }

    final void windowFocusChanged(final boolean isWindowFocus) {
        this.mIsWindowFocus = isWindowFocus;
    }
    
    final void setIsPause(final boolean isPause) {
        this.mIsPause = isPause;
    }
    
    final boolean getIsPause() {
        return this.mIsPause;
    }
    
    final void setIsPlaying(final boolean isPlaying) {
        this.mIsPlaying = isPlaying;
    }
    
    final boolean isActive() {
        return UnityEnviroment.mIsLoadNativeLibrary && this.mIsWindowFocus && !this.mIsPause && !this.mIsPlaying;
    }
    
    final boolean getIsPlaying() {
        return this.mIsPlaying;
    }
    
    @Override
    public final String toString() {
        return super.toString();
    }
    
    static {
        UnityEnviroment.mIsLoadNativeLibrary = false;
    }
}
