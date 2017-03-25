package com.unity3d.player;

import android.media.*;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.net.*;
import java.io.*;
import android.content.res.*;
import android.view.*;

public final class VideoManager extends FrameLayout implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback, MediaController.MediaPlayerControl
{
    private static boolean bIsLog;
    private final UnityPlayer mUnityPlayer;
    private final Context mContext;
    private final SurfaceView mSurfaceView;
    private final SurfaceHolder mSurfaceHolder;
    private final String mFileName;
    private final int mControlMode;
    private final int mScalingMode;
    private final boolean mIsURL;
    private final long mVideoOffset;
    private final long mVideoLength;
    private final FrameLayout mFrameLayout;
    private final Display mDisplay;
    private int n;
    private int o;
    private int p;
    private int q;
    private MediaPlayer mMediaPlayer;
    private MediaController mMediaController;
    private boolean t;
    private boolean u;
    private int v;
    private boolean w;
    private int x;
    private boolean y;
    
    private static void VideoLog(final String s) {
        Log.v("Video", "VideoPlayer: " + s);
    }
    
    protected VideoManager(final UnityPlayer b, final Context c, final String fileName, final int backgroundColor, final int controlMode, final int scalingMode, final boolean isURL, final long videoOffset, final long videoLength) {
        super(c);
        this.t = false;
        this.u = false;
        this.v = 0;
        this.w = false;
        this.x = 0;
        this.mUnityPlayer = b;
        this.mContext = c;
        this.mFrameLayout = this;
        this.mSurfaceView = new SurfaceView(c);
        (this.mSurfaceHolder = this.mSurfaceView.getHolder()).addCallback((SurfaceHolder.Callback)this);
        this.mSurfaceHolder.setType(3);
        this.mFrameLayout.setBackgroundColor(backgroundColor);
        this.mFrameLayout.addView((View)this.mSurfaceView);
        this.mDisplay = ((WindowManager)this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.mFileName = fileName;
        this.mControlMode = controlMode;
        this.mScalingMode = scalingMode;
        this.mIsURL = isURL;
        this.mVideoOffset = videoOffset;
        this.mVideoLength = videoLength;
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("fileName: " + this.mFileName);
        }
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("backgroundColor: " + backgroundColor);
        }
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("controlMode: " + this.mControlMode);
        }
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("scalingMode: " + this.mScalingMode);
        }
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("isURL: " + this.mIsURL);
        }
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("videoOffset: " + this.mVideoOffset);
        }
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("videoLength: " + this.mVideoLength);
        }
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.y = true;
    }
    
    public final void onControllerHide() {
    }
    
    protected final void onPause() {
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("onPause called");
        }
        if (!this.w) {
            this.pause();
            this.w = false;
        }
        if (this.mMediaPlayer != null) {
            this.x = this.mMediaPlayer.getCurrentPosition();
        }
        this.y = false;
    }
    
    protected final void onResume() {
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("onResume called");
        }
        if (!this.y && !this.w) {
            this.start();
        }
        this.y = true;
    }
    
    protected final void onDestroy() {
        this.onPause();
        this.doCleanUp();
        UnityPlayer.runOnNewThread(new Runnable() {
            @Override
            public final void run() {
                com.unity3d.player.VideoManager.this.mUnityPlayer.hideVideoPlayer();
            }
        });
    }
    
    private void a() {
        this.doCleanUp();
        try {
            this.mMediaPlayer = new MediaPlayer();
            if (this.mIsURL) {
                this.mMediaPlayer.setDataSource(this.mContext, Uri.parse(this.mFileName));
            }
            else if (this.mVideoLength != 0L) {
                final FileInputStream fileInputStream = new FileInputStream(this.mFileName);
                this.mMediaPlayer.setDataSource(fileInputStream.getFD(), this.mVideoOffset, this.mVideoLength);
                fileInputStream.close();
            }
            else {
                final AssetManager assets = this.getResources().getAssets();
                try {
                    final AssetFileDescriptor openFd = assets.openFd(this.mFileName);
                    this.mMediaPlayer.setDataSource(openFd.getFileDescriptor(), openFd.getStartOffset(), openFd.getLength());
                    openFd.close();
                }
                catch (IOException ex2) {
                    final FileInputStream fileInputStream2 = new FileInputStream(this.mFileName);
                    this.mMediaPlayer.setDataSource(fileInputStream2.getFD());
                    fileInputStream2.close();
                }
            }
            this.mMediaPlayer.setDisplay(this.mSurfaceHolder);
            this.mMediaPlayer.setScreenOnWhilePlaying(true);
            this.mMediaPlayer.setOnBufferingUpdateListener((MediaPlayer.OnBufferingUpdateListener)this);
            this.mMediaPlayer.setOnCompletionListener((MediaPlayer.OnCompletionListener)this);
            this.mMediaPlayer.setOnPreparedListener((MediaPlayer.OnPreparedListener)this);
            this.mMediaPlayer.setOnVideoSizeChangedListener((MediaPlayer.OnVideoSizeChangedListener)this);
            this.mMediaPlayer.setAudioStreamType(3);
            this.mMediaPlayer.prepare();
            if (this.mControlMode == 0 || this.mControlMode == 1) {
                (this.mMediaController = new MediaController(this.mContext)).setMediaPlayer((MediaController.MediaPlayerControl)this);
                this.mMediaController.setAnchorView((View)this);
                this.mMediaController.setEnabled(true);
                this.mMediaController.show();
            }
        }
        catch (Exception ex) {
            if (com.unity3d.player.VideoManager.bIsLog) {
                VideoLog("error: " + ex.getMessage() + ex);
            }
            this.onDestroy();
        }
    }
    
    public final boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (n == 4 || (this.mControlMode == 2 && n != 0 && !keyEvent.isSystem())) {
            this.onDestroy();
            return true;
        }
        if (this.mMediaController != null) {
            return this.mMediaController.onKeyDown(n, keyEvent);
        }
        return super.onKeyDown(n, keyEvent);
    }
    
    public final boolean onTouchEvent(final MotionEvent motionEvent) {
        final int n = motionEvent.getAction() & 0xFF;
        if (this.mControlMode == 2 && n == 0) {
            this.onDestroy();
            return true;
        }
        if (this.mMediaController != null) {
            return this.mMediaController.onTouchEvent(motionEvent);
        }
        return super.onTouchEvent(motionEvent);
    }
    
    public final void onBufferingUpdate(final MediaPlayer mediaPlayer, final int v) {
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("onBufferingUpdate percent:" + v);
        }
        this.v = v;
    }
    
    public final void onCompletion(final MediaPlayer mediaPlayer) {
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("onCompletion called");
        }
        this.onDestroy();
    }
    
    public final void onVideoSizeChanged(final MediaPlayer mediaPlayer, final int p3, final int q) {
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("onVideoSizeChanged called " + p3 + "x" + q);
        }
        if (p3 == 0 || q == 0) {
            if (com.unity3d.player.VideoManager.bIsLog) {
                VideoLog("invalid video width(" + p3 + ") or height(" + q + ")");
            }
            return;
        }
        this.t = true;
        this.p = p3;
        this.q = q;
        if (this.u && this.t) {
            this.b();
        }
    }
    
    public final void onPrepared(final MediaPlayer mediaPlayer) {
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("onPrepared called");
        }
        this.u = true;
        if (this.u && this.t) {
            this.b();
        }
    }
    
    public final void surfaceChanged(final SurfaceHolder surfaceHolder, final int n, final int n2, final int o) {
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("surfaceChanged called " + n + " " + n2 + "x" + o);
        }
        if (this.n != n2 || this.o != o) {
            this.n = n2;
            this.o = o;
            this.updateVideoLayout();
        }
    }
    
    public final void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("surfaceDestroyed called");
        }
        this.doCleanUp();
    }
    
    public final void surfaceCreated(final SurfaceHolder surfaceHolder) {
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("surfaceCreated called");
        }
        this.a();
        this.seekTo(this.x);
    }
    
    protected final void doCleanUp() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
        this.p = 0;
        this.q = 0;
        this.u = false;
        this.t = false;
    }
    
    private void b() {
        if (this.isPlaying()) {
            return;
        }
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("startVideoPlayback");
        }
        this.updateVideoLayout();
        if (!this.w) {
            this.start();
        }
    }
    
    protected final void updateVideoLayout() {
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("updateVideoLayout");
        }
        if (this.n == 0 || this.o == 0) {
            final WindowManager windowManager = (WindowManager)this.mContext.getSystemService(Context.WINDOW_SERVICE);
            this.n = windowManager.getDefaultDisplay().getWidth();
            this.o = windowManager.getDefaultDisplay().getHeight();
        }
        int n = this.n;
        int n2 = this.o;
        final float n3 = this.p / this.q;
        final float n4 = this.n / this.o;
        if (this.mScalingMode == 1) {
            if (n4 <= n3) {
                n2 = (int)(this.n / n3);
            }
            else {
                n = (int)(this.o * n3);
            }
        }
        else if (this.mScalingMode == 2) {
            if (n4 >= n3) {
                n2 = (int)(this.n / n3);
            }
            else {
                n = (int)(this.o * n3);
            }
        }
        else if (this.mScalingMode == 0) {
            n = this.p;
            n2 = this.q;
        }
        if (com.unity3d.player.VideoManager.bIsLog) {
            VideoLog("frameWidth = " + n + "; frameHeight = " + n2);
        }
        this.mFrameLayout.updateViewLayout((View)this.mSurfaceView, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(n, n2, 17));
    }
    
    public final boolean canPause() {
        return true;
    }
    
    public final boolean canSeekBackward() {
        return true;
    }
    
    public final boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public final int getBufferPercentage() {
        if (this.mIsURL) {
            return this.v;
        }
        return 100;
    }
    
    public final int getCurrentPosition() {
        if (this.mMediaPlayer == null) {
            return 0;
        }
        return this.mMediaPlayer.getCurrentPosition();
    }
    
    public final int getDuration() {
        if (this.mMediaPlayer == null) {
            return 0;
        }
        return this.mMediaPlayer.getDuration();
    }
    
    public final boolean isPlaying() {
        final boolean b = this.u && this.t;
        if (this.mMediaPlayer == null) {
            return !b;
        }
        return this.mMediaPlayer.isPlaying() || !b;
    }
    
    public final void pause() {
        if (this.mMediaPlayer == null) {
            return;
        }
        this.mMediaPlayer.pause();
        this.w = true;
    }
    
    public final void seekTo(final int n) {
        if (this.mMediaPlayer == null) {
            return;
        }
        this.mMediaPlayer.seekTo(n);
    }
    
    public final void start() {
        if (this.mMediaPlayer == null) {
            return;
        }
        this.mMediaPlayer.start();
        this.w = false;
    }
    
    static {
        VideoManager.bIsLog = false;
    }
}
