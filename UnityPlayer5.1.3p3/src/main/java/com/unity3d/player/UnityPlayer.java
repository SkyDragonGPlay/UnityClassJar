package com.unity3d.player;

import org.fmod.*;

import android.graphics.Rect;
import android.hardware.Camera;
import android.net.*;
import android.os.Process;
import android.widget.*;
import android.util.*;
import android.content.*;
import org.xmlpull.v1.*;
import android.app.*;
import android.content.res.*;

import java.util.*;
import android.content.pm.*;
import java.io.*;
import java.security.*;
import android.os.*;
import android.view.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

public class UnityPlayer extends FrameLayout implements UnityCamera.ICameraFrame
{
    public static Activity currentActivity;
    private boolean c;
    private boolean d;
    private final IUnityMotionEventHandler mUnityMotionEventHandler;
    private final UnitySurfaceViewManager mUnitySurfaceViewManager;
    private boolean g;
    private UnityEnviroment mUnityEnviroment;
    private final ConcurrentLinkedQueue<Runnable> mConcurrentLinkedQueue;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean k;
    GLThread mGLThread;
    private ContextWrapper mContextWrapper;
    private SurfaceView mSurfaceView;
    private WindowManager mWindowManager;
    private FMODAudioDevice mFMODAudioDevice;
    private static boolean p;
    private static boolean mIsLoadMainLib;
    private boolean mIsFinishing;
    private boolean s;
    private int t;
    private int u;
    private final UnityLocationManager v;
    private String w;
    private NetworkInfo mNetworkInfo;
    private Bundle mBundle;
    private List<UnityCamera> mCameraList;
    private VideoManager mVideoManager;
    UnityDialog b;
    private ProgressBar mProgressBar;
    private Runnable C;
    private Runnable D;
    private static Lock mLock;
    
    public UnityPlayer(final ContextWrapper contextWrapper) {
        super((Context)contextWrapper);
        this.c = false;
        this.d = false;
        this.g = false;
        this.mUnityEnviroment = new UnityEnviroment();
        this.mConcurrentLinkedQueue = new ConcurrentLinkedQueue();
        this.mBroadcastReceiver = null;
        this.k = false;
        this.mGLThread = new GLThread();
        this.s = true;
        this.t = 0;
        this.u = 0;
        this.w = null;
        this.mNetworkInfo = null;
        this.mBundle = new Bundle();
        this.mCameraList = new ArrayList();
        this.b = null;
        this.mProgressBar = null;
        this.C = new Runnable() {
            @Override
            public final void run() {
                final int k;
                if ((k = UnityPlayer.this.nativeActivityIndicatorStyle()) >= 0) {
                    if (UnityPlayer.this.mProgressBar == null) {
                        UnityPlayer.this.mProgressBar = new ProgressBar((Context)UnityPlayer.this.mContextWrapper, (AttributeSet)null, (new int[] { 16842874, 16843401, 16842873, 16843400 })[k]);
                        UnityPlayer.this.mProgressBar.setIndeterminate(true);
                        UnityPlayer.this.mProgressBar.setLayoutParams((ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-2, -2, 51));
                        UnityPlayer.this.addView((View)UnityPlayer.this.mProgressBar);
                    }
                    UnityPlayer.this.mProgressBar.setVisibility(VISIBLE);
                    UnityPlayer.this.bringChildToFront((View)UnityPlayer.this.mProgressBar);
                }
            }
        };
        this.D = new Runnable() {
            @Override
            public final void run() {
                if (UnityPlayer.this.mProgressBar != null) {
                    UnityPlayer.this.mProgressBar.setVisibility(GONE);
                    UnityPlayer.this.removeView((View)UnityPlayer.this.mProgressBar);
                    UnityPlayer.this.mProgressBar = null;
                }
            }
        };
        if (contextWrapper instanceof Activity) {
            UnityPlayer.currentActivity = (Activity)contextWrapper;
        }
        this.mUnitySurfaceViewManager = new UnitySurfaceViewManager((ViewGroup)this);
        this.mContextWrapper = contextWrapper;
        this.mUnityMotionEventHandler = ((contextWrapper instanceof Activity) ? new UnityMotionEventHandler(contextWrapper) : null);
        this.v = new UnityLocationManager((Context)contextWrapper, this);
        this.a();
        if (UnityConstants.IS_SDK_VERSION_GE_11) {
            UnityConstants.h.a((View)this);
        }
        if (UnityConstants.IS_SDK_VERSION_GE_11 && this.getStatusBarHidden()) {
            UnityConstants.h.a();
        }
        this.setFullscreen(true);
        a(this.mContextWrapper.getApplicationInfo());
        if (!UnityEnviroment.getIsNativeLibraryLoaded()) {
            final AlertDialog create;
            (create = new AlertDialog.Builder((Context)this.mContextWrapper).setTitle((CharSequence)"Failure to initialize!").setPositiveButton((CharSequence)"OK", (DialogInterface.OnClickListener)new DialogInterface.OnClickListener() {
                public final void onClick(final DialogInterface dialogInterface, final int n) {
                    UnityPlayer.this.finish();
                }
            }).setMessage((CharSequence)"Your hardware does not support this application, sorry!").create()).setCancelable(false);
            create.show();
            return;
        }
        this.nativeFile(this.mContextWrapper.getPackageCodePath());
        this.loadObbFiles();
        this.mSurfaceView = new SurfaceView((Context)contextWrapper);
        this.mSurfaceView.getHolder().setFormat(2);
        this.mSurfaceView.getHolder().addCallback((SurfaceHolder.Callback)new SurfaceHolder.Callback() {
            public final void surfaceCreated(final SurfaceHolder surfaceHolder) {
                UnityPlayer.a(UnityPlayer.this, surfaceHolder.getSurface());
            }
            
            public final void surfaceChanged(final SurfaceHolder surfaceHolder, final int n, final int n2, final int n3) {
                UnityPlayer.a(UnityPlayer.this, surfaceHolder.getSurface());
                UnityPlayer.this.excuteTask(new c() {
                    @Override
                    public final void a() {
                        UnityPlayer.this.h();
                    }
                });
            }
            
            public final void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
                UnityPlayer.a(UnityPlayer.this, (Surface)null);
            }
        });
        this.mSurfaceView.setFocusable(true);
        this.mSurfaceView.setFocusableInTouchMode(true);
        this.mUnitySurfaceViewManager.c((View)this.mSurfaceView);
        this.mIsFinishing = false;
        this.c();
        this.initJni((Context)contextWrapper);
        this.nativeInitWWW(WWW.class);
        if (UnityConstants.IS_SDK_VERSION_GE_17) {
            UnityConstants.UNITY_PRESENTATION.registerDisplayListener(this, (Context)this.mContextWrapper);
        }
        if (UnityConstants.IS_SDK_VERSION_GE_16) {
            UnityConstants.j.a(this);
        }
        this.mWindowManager = (WindowManager)this.mContextWrapper.getSystemService(Context.WINDOW_SERVICE);
        this.mContextWrapper.setTheme(this.getTheme());
        this.mGLThread.start();
    }
    
    private void a(final int n, final Surface surface) {
        if (this.c) {
            return;
        }
        this.b(0, surface);
    }
    
    private boolean b(final int n, final Surface surface) {
        if (!UnityEnviroment.getIsNativeLibraryLoaded()) {
            return false;
        }
        this.nativeRecreateGfxState(n, surface);
        return true;
    }
    
    public boolean displayChanged(final int n, final Surface surface) {
        if (n == 0) {
            this.c = (surface != null);
            this.runOnUiThread(new Runnable() {
                @Override
                public final void run() {
                    if (UnityPlayer.this.c) {
                        UnityPlayer.this.mUnitySurfaceViewManager.d((View)UnityPlayer.this.mSurfaceView);
                        return;
                    }
                    UnityPlayer.this.mUnitySurfaceViewManager.c((View)UnityPlayer.this.mSurfaceView);
                }
            });
        }
        return this.b(n, surface);
    }
    
    protected boolean installPresentationDisplay(final int n) {
        return UnityConstants.IS_SDK_VERSION_GE_17 && UnityConstants.UNITY_PRESENTATION.installPresentationDisplay(this, (Context)this.mContextWrapper, n);
    }
    
    private void a() {
        try {
            final File file;
            InputStream open;
            if ((file = new File(this.mContextWrapper.getPackageCodePath(), "assets/bin/Data/settings.xml")).exists()) {
                open = new FileInputStream(file);
            }
            else {
                open = this.mContextWrapper.getAssets().open("bin/Data/settings.xml");
            }
            final XmlPullParserFactory instance;
            (instance = XmlPullParserFactory.newInstance()).setNamespaceAware(true);
            final XmlPullParser pullParser;
            (pullParser = instance.newPullParser()).setInput(open, (String)null);
            String name = null;
            String attributeValue = null;
            for (int i = pullParser.getEventType(); i != 1; i = pullParser.next()) {
                if (i == 2) {
                    name = pullParser.getName();
                    for (int j = 0; j < pullParser.getAttributeCount(); ++j) {
                        if (pullParser.getAttributeName(j).equalsIgnoreCase("name")) {
                            attributeValue = pullParser.getAttributeValue(j);
                        }
                    }
                }
                else if (i == 3) {
                    name = null;
                }
                else if (i == 4 && attributeValue != null) {
                    if (name.equalsIgnoreCase("integer")) {
                        this.mBundle.putInt(attributeValue, Integer.parseInt(pullParser.getText()));
                    }
                    else if (name.equalsIgnoreCase("string")) {
                        this.mBundle.putString(attributeValue, pullParser.getText());
                    }
                    else if (name.equalsIgnoreCase("bool")) {
                        this.mBundle.putBoolean(attributeValue, Boolean.parseBoolean(pullParser.getText()));
                    }
                    else if (name.equalsIgnoreCase("float")) {
                        this.mBundle.putFloat(attributeValue, Float.parseFloat(pullParser.getText()));
                    }
                    attributeValue = null;
                }
            }
        }
        catch (Exception ex) {
            UnityLog.Log(6, "Unable to locate player settings. " + ex.getLocalizedMessage());
            this.finish();
        }
    }
    
    public Bundle getSettings() {
        return this.mBundle;
    }
    
    protected void restartFMODAudioDevice() {
        this.mFMODAudioDevice.stop();
        this.mFMODAudioDevice.start();
    }

    private void finish() {
        if (this.mContextWrapper instanceof Activity && !((Activity)this.mContextWrapper).isFinishing()) {
            ((Activity)this.mContextWrapper).finish();
        }
    }
    
    static void runOnNewThread(final Runnable runnable) {
        new Thread(runnable).start();
    }
    
    final void runOnUiThread(final Runnable runnable) {
        if (this.mContextWrapper instanceof Activity) {
            ((Activity)this.mContextWrapper).runOnUiThread(runnable);
            return;
        }
        UnityLog.Log(5, "Not running Unity from an Activity; ignored...");
    }
    
    public void init(final int n, final boolean b) {
    }
    
    public View getView() {
        return (View)this;
    }
    
    private void c() {
        final UnityBundleManager m = new UnityBundleManager((Activity)this.mContextWrapper);
        if (this.mContextWrapper instanceof NativeActivity) {
            this.nativeForwardEventsToDalvik(this.k = m.isForwardNativeEventsToDalvik());
        }
    }
    
    protected void kill() {
        Process.killProcess(Process.myPid());
    }
    
    public void quit() {
        this.mIsFinishing = true;
        if (!this.mUnityEnviroment.getIsPause()) {
            this.pause();
        }
        this.mGLThread.notifyQuit();
        try {
            this.mGLThread.join(4000L);
        }
        catch (InterruptedException ex) {
            this.mGLThread.interrupt();
        }
        if (this.mBroadcastReceiver != null) {
            this.mContextWrapper.unregisterReceiver(this.mBroadcastReceiver);
        }
        this.mBroadcastReceiver = null;
        if (UnityEnviroment.getIsNativeLibraryLoaded()) {
            this.removeAllViews();
        }
        if (UnityConstants.IS_SDK_VERSION_GE_17) {
            UnityConstants.UNITY_PRESENTATION.unregisterDisplayListener((Context)this.mContextWrapper);
        }
        if (UnityConstants.IS_SDK_VERSION_GE_16) {
            UnityConstants.j.a();
        }
        this.kill();
        unloadNativeLibrary();
    }
    
    private void closeCameras() {
        final Iterator<UnityCamera> iterator = this.mCameraList.iterator();
        while (iterator.hasNext()) {
            iterator.next().close();
        }
    }
    
    private void resumeCameras() {
        for (final UnityCamera unityCamera : this.mCameraList) {
            try {
                unityCamera.openCamera(this);
            }
            catch (Exception ex) {
                UnityLog.Log(6, "Unable to initialize camera: " + ex.getMessage());
                unityCamera.close();
            }
        }
    }
    
    public void pause() {
        if (this.mVideoManager != null) {
            this.mVideoManager.onPause();
            return;
        }
        this.reportSoftInputStr(null, 1, true);
        if (!this.mUnityEnviroment.getIsPlaying()) {
            return;
        }
        if (UnityEnviroment.getIsNativeLibraryLoaded()) {
            final Semaphore semaphore = new Semaphore(0);
            if (this.isFinishing()) {
                this.c(new Runnable() {
                    @Override
                    public final void run() {
                        UnityPlayer.this.f();
                        semaphore.release();
                    }
                });
            }
            else {
                this.c(new Runnable() {
                    @Override
                    public final void run() {
                        if (UnityPlayer.this.nativePause()) {
                            UnityPlayer.this.mIsFinishing = true;
                            UnityPlayer.this.f();
                            semaphore.release(2);
                            return;
                        }
                        semaphore.release();
                    }
                });
            }
            try {
                if (!semaphore.tryAcquire(4L, TimeUnit.SECONDS)) {
                    UnityLog.Log(5, "Timeout while trying to pause the Unity Engine.");
                }
            }
            catch (InterruptedException ex) {
                UnityLog.Log(5, "UI thread got interrupted while trying to pause the Unity Engine.");
            }
            if (semaphore.drainPermits() > 0) {
                this.quit();
            }
        }
        this.mUnityEnviroment.setIsPlaying(false);
        this.mUnityEnviroment.setIsPause(true);
        this.closeCameras();
        if (this.mFMODAudioDevice != null) {
            this.mFMODAudioDevice.stop();
        }
        this.mGLThread.notifyPause();
        this.v.d();
    }
    
    public void resume() {
        if (UnityConstants.IS_SDK_VERSION_GE_11) {
            UnityConstants.h.b((View)this);
        }
        this.mUnityEnviroment.setIsPause(false);
        this.onResume();
    }
    
    private void f() {
        if (this.mFMODAudioDevice != null) {
            this.mFMODAudioDevice.close();
        }
        this.nativeDone();
    }
    
    private void onResume() {
        if (!this.mUnityEnviroment.isActive()) {
            return;
        }
        if (this.mVideoManager != null) {
            this.mVideoManager.onResume();
            return;
        }
        this.mUnityEnviroment.setIsPlaying(true);
        this.resumeCameras();
        this.mGLThread.notifyResume();
        this.v.e();
        this.w = null;
        this.mNetworkInfo = null;
        if (UnityEnviroment.getIsNativeLibraryLoaded()) {
            this.loadObbFiles();
        }
        this.c(new Runnable() {
            @Override
            public final void run() {
                UnityPlayer.this.nativeResume();
            }
        });
        if (UnityPlayer.p && this.mFMODAudioDevice == null) {
            this.mFMODAudioDevice = new FMODAudioDevice();
        }
        if (this.mFMODAudioDevice != null && !this.mFMODAudioDevice.isRunning()) {
            this.mFMODAudioDevice.start();
        }
    }
    
    public void configurationChanged(final Configuration configuration) {
        if (this.mSurfaceView instanceof SurfaceView) {
            this.mSurfaceView.getHolder().setSizeFromLayout();
        }
        if (this.mVideoManager != null) {
            this.mVideoManager.updateVideoLayout();
        }
    }
    
    public void windowFocusChanged(final boolean isFocus) {
        this.mUnityEnviroment.windowFocusChanged(isFocus);
        if (isFocus && this.b != null) {
            this.reportSoftInputStr(null, 1, false);
        }
        if (UnityConstants.IS_SDK_VERSION_GE_11 && isFocus) {
            UnityConstants.h.b((View)this);
        }
        this.c(new Runnable() {
            @Override
            public final void run() {
                UnityPlayer.this.nativeFocusChanged(isFocus);
            }
        });
        this.mGLThread.notifyFocusChanged(isFocus);
        this.onResume();
    }
    
    private void h() {
        if (this.mContextWrapper instanceof NativeActivity) {
            float n = 0.0f;
            if (!this.getStatusBarHidden()) {
                final Activity activity = (Activity)this.mContextWrapper;
                final Rect rect = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                n = rect.top;
            }
            this.nativeSetTouchDeltaY(n);
        }
    }
    
    protected static boolean loadLibraryStatic(final String libraryPath) {
        try {
            System.loadLibrary(libraryPath);
        }
        catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            UnityLog.Log(6, "Unable to find " + libraryPath);
            return false;
        }
        catch (Exception ex) {
            UnityLog.Log(6, "Unknown error " + ex);
            return false;
        }
        return true;
    }
    
    protected boolean loadLibrary(final String s) {
        return loadLibraryStatic(s);
    }
    
    protected void startActivityIndicator() {
        this.runOnUiThread(this.C);
    }
    
    protected void stopActivityIndicator() {
        this.runOnUiThread(this.D);
    }




    
    protected static void lockNativeAccess() {
        UnityPlayer.mLock.lock();
    }
    
    protected static void unlockNativeAccess() {
        UnityPlayer.mLock.unlock();
    }
    
    private static void a(final ApplicationInfo applicationInfo) {
        if (UnityPlayer.mIsLoadMainLib && NativeLoader.load(applicationInfo.nativeLibraryDir)) {
            UnityEnviroment.setNativeLibraryLoaded();
        }
    }
    
    private static void unloadNativeLibrary() {
        if (!UnityEnviroment.getIsNativeLibraryLoaded()) {
            return;
        }
        lockNativeAccess();
        if (!NativeLoader.unload()) {
            unlockNativeAccess();
            throw new UnsatisfiedLinkError("Unable to unload libraries from libmain.so");
        }
        UnityEnviroment.setNativeLibraryUnload();
        unlockNativeAccess();
    }

    protected void forwardMotionEventToDalvik(final long downTime, final long eventTime, final int action, final int pointerCount, final int[] pointerIds, final float[] array2, final int metaState, final float xPrecision, final float yPrecision, final int deviceId, final int edgeFlags, final int source, final int flags, final int n12, final long[] array3, final float[] array4) {
        this.mUnityMotionEventHandler.forwardMotionEventToDalvik(downTime, eventTime, action, pointerCount, pointerIds,array2, metaState, xPrecision,
                                            yPrecision, deviceId, edgeFlags, source, flags, n12, array3, array4);
    }
    
    protected void setFullscreen(final boolean b) {
        if (UnityConstants.IS_SDK_VERSION_GE_11) {
            this.runOnUiThread(new Runnable() {
                @Override
                public final void run() {
                    UnityConstants.h.setSystemUiVisibility((View)UnityPlayer.this, b);
                }
            });
        }
    }
    
    protected void showSoftInput(final String s, final int n, final boolean b, final boolean b2, final boolean b3, final boolean b4, final String s2) {
        this.runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                (UnityPlayer.this.b = new UnityDialog((Context)UnityPlayer.this.mContextWrapper, UnityPlayer.this, s, n, b, b2, b3, s2)).show();
            }
        });
    }
    
    protected void hideSoftInput() {
        final Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                if (UnityPlayer.this.b != null) {
                    UnityPlayer.this.b.dismiss();
                    UnityPlayer.this.b = null;
                }
            }
        };
        if (UnityConstants.IS_SDK_VERSION_GE_21) {
            this.excuteTask(new c() {
                @Override
                public final void a() {
                    UnityPlayer.this.runOnUiThread(runnable);
                }
            });
            return;
        }
        this.runOnUiThread(runnable);
    }
    
    protected void setSoftInputStr(final String s) {
        this.runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                if (UnityPlayer.this.b != null && s != null) {
                    UnityPlayer.this.b.a(s);
                }
            }
        });
    }
    
    protected void reportSoftInputStr(final String s, final int n, final boolean b) {
        if (n == 1) {
            this.hideSoftInput();
        }
        this.excuteTask(new c() {
            @Override
            public final void a() {
                if (b) {
                    UnityPlayer.n(UnityPlayer.this);
                }
                else if (s != null) {
                    UnityPlayer.this.nativeSetInputString(s);
                }
                if (n == 1) {
                    UnityPlayer.this.nativeSoftInputClosed();
                }
            }
        });
    }
    
    protected int[] initCamera(int cameraID, final int width, final int height, final int fps) {
        UnityCamera unityCamera = new UnityCamera(cameraID, width, height, fps);
        try {
            unityCamera.openCamera(this);
            this.mCameraList.add(unityCamera);
            final Camera.Size size = unityCamera.getPreviewSize();
            return new int[] { size.width, size.height };
        }
        catch (Exception ex) {
            UnityLog.Log(6, "Unable to initialize camera: " + ex.getMessage());
            unityCamera.close();
            return null;
        }
    }
    
    protected void closeCamera(final int n) {
        final Iterator<UnityCamera> iterator = this.mCameraList.iterator();
        while (iterator.hasNext()) {
            final UnityCamera unityCamera;
            if ((unityCamera = iterator.next()).getCameraID() == n) {
                unityCamera.close();
                this.mCameraList.remove(unityCamera);
            }
        }
    }
    
    protected int getNumCameras() {
        if (!this.j()) {
            return 0;
        }
        return Camera.getNumberOfCameras();
    }
    
    public void onCameraFrame(final UnityCamera camera, final byte[] array) {
        this.excuteTask(new c() {
            final /* synthetic */ int cameraID = camera.getCameraID();
            final /* synthetic */ Camera.Size size = camera.getPreviewSize();
            
            @Override
            public final void a() {
                UnityPlayer.this.nativeVideoFrameCallback(cameraID, array, this.size.width, this.size.height);
                camera.setCallbackBuffer(array);
            }
        });
    }
    
    protected boolean isCameraFrontFacing(final int n) {
        final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(n, cameraInfo);
        return cameraInfo.facing == 1;
    }
    
    protected int getCameraOrientation(final int n) {
        final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(n, cameraInfo);
        return cameraInfo.orientation;
    }
    
    protected void showVideoPlayer(final String s, final int n, final int n2, final int n3, final boolean b, final int n4, final int n5) {
        this.runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                if (UnityPlayer.this.mVideoManager != null) {
                    return;
                }
                UnityPlayer.this.pause();
                UnityPlayer.this.mVideoManager = new VideoManager(UnityPlayer.this, (Context)UnityPlayer.this.mContextWrapper, s, n, n2, n3, b, n4, n5);
                UnityPlayer.this.addView((View)UnityPlayer.this.mVideoManager);
                UnityPlayer.this.mVideoManager.requestFocus();
                UnityPlayer.this.mUnitySurfaceViewManager.d((View)UnityPlayer.this.mSurfaceView);
            }
        });
    }
    
    protected void hideVideoPlayer() {
        this.runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                if (UnityPlayer.this.mVideoManager == null) {
                    return;
                }
                UnityPlayer.this.mUnitySurfaceViewManager.c((View)UnityPlayer.this.mSurfaceView);
                UnityPlayer.this.removeView((View)UnityPlayer.this.mVideoManager);
                UnityPlayer.this.mVideoManager = null;
                UnityPlayer.this.resume();
            }
        });
    }
    
    protected void Location_SetDesiredAccuracy(final float n) {
        this.v.b(n);
    }
    
    protected void Location_SetDistanceFilter(final float n) {
        this.v.a(n);
    }
    
    protected native void nativeSetLocationStatus(final int p0);
    
    protected native void nativeSetLocation(final float p0, final float p1, final float p2, final float p3, final double p4, final float p5);
    
    protected void Location_StartUpdatingLocation() {
        this.v.b();
    }
    
    protected void Location_StopUpdatingLocation() {
        this.v.stopUpdating();
    }
    
    protected boolean Location_IsServiceEnabledByUser() {
        return this.v.a();
    }
    
    private boolean j() {
        return this.mContextWrapper.getPackageManager().hasSystemFeature("android.hardware.camera") || this.mContextWrapper.getPackageManager().hasSystemFeature("android.hardware.camera.front");
    }
    
    protected boolean getStatusBarHidden() {
        return this.mBundle.getBoolean("hide_status_bar", true);
    }
    
    protected int getSplashMode() {
        return this.mBundle.getInt("splash_mode");
    }
    
    protected void executeGLThreadJobs() {
        Runnable runnable;
        while ((runnable = this.mConcurrentLinkedQueue.poll()) != null) {
            runnable.run();
        }
    }
    
    protected void disableLogger() {
        UnityLog.bShowLog = true;
    }
    
    private void c(final Runnable runnable) {
        if (!UnityEnviroment.getIsNativeLibraryLoaded()) {
            return;
        }
        if (Thread.currentThread() == this.mGLThread) {
            runnable.run();
            return;
        }
        this.mConcurrentLinkedQueue.add(runnable);
    }
    
    private void excuteTask(final c c) {
        if (this.isFinishing()) {
            return;
        }
        this.c(c);
    }
    
    protected boolean isFinishing() {
        if (!this.mIsFinishing) {
            final boolean isFinishing = this.mContextWrapper instanceof Activity && ((Activity)this.mContextWrapper).isFinishing();
            this.mIsFinishing = isFinishing;
            if (!isFinishing) {
                return false;
            }
        }
        return true;
    }
    
    private void loadObbFiles() {
        if (!this.mBundle.getBoolean("useObb")) {
            return;
        }
        String[] obbFilePaths;
        for (int length = (obbFilePaths = getObbFilePaths((Context)this.mContextWrapper)).length, i = 0; i < length; ++i) {
            final String obbFilePath = obbFilePaths[i];
            final String fileMD5 = getFileMD5(obbFilePath);
            if (this.mBundle.getBoolean(fileMD5)) {
                this.nativeFile(obbFilePath);
            }
            this.mBundle.remove(fileMD5);
        }
    }
    
    private static String[] getObbFilePaths(final Context context) {
        final String packageName = context.getPackageName();
        final Vector<String> vector = new Vector<String>();
        int versionCode;
        try {
            versionCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        }
        catch (PackageManager.NameNotFoundException ex) {
            return new String[0];
        }
        final File file;
        if (Environment.getExternalStorageState().equals("mounted") && (file = new File(Environment.getExternalStorageDirectory().toString() + "/Android/obb/" + packageName)).exists()) {
            if (versionCode > 0) {
                final String string = file + File.separator + "main." + versionCode + "." + packageName + ".obb";
                if (new File(string).isFile()) {
                    vector.add(string);
                }
            }
            if (versionCode > 0) {
                final String string2 = file + File.separator + "patch." + versionCode + "." + packageName + ".obb";
                if (new File(string2).isFile()) {
                    vector.add(string2);
                }
            }
        }
        final String[] array = new String[vector.size()];
        vector.toArray(array);
        return array;
    }
    
    private static String getFileMD5(final String s) {
        byte[] digest = null;
        try {
            final MessageDigest instance = MessageDigest.getInstance("MD5");
            final FileInputStream fileInputStream = new FileInputStream(s);
            final long length = new File(s).length();
            fileInputStream.skip(length - Math.min(length, 65558L));
            final byte[] array = new byte[1024];
            for (int i = 0; i != -1; i = fileInputStream.read(array)) {
                instance.update(array, 0, i);
            }
            digest = instance.digest();
        }
        catch (FileNotFoundException ex) {}
        catch (IOException ex2) {}
        catch (NoSuchAlgorithmException ex3) {}
        if (digest == null) {
            return null;
        }
        final StringBuffer sb = new StringBuffer();
        for (int j = 0; j < digest.length; ++j) {
            sb.append(Integer.toString((digest[j] & 0xFF) + 256, 16).substring(1));
        }
        return sb.toString();
    }
    
    private int getTheme() {
        int n = 16973831;
        try {
            n = this.mContextWrapper.getApplicationInfo().theme;
        }
        catch (Exception ex) {
            UnityLog.Log(5, "Failed to obtain current theme, applying best theme available on device");
        }
        if (n == 16973831) {
            n = a(this.getSettings().getBoolean("hide_status_bar", true));
        }
        return n;
    }
    
    private static int a(final boolean isHideStatusBar) {
        if (isHideStatusBar) {
            if (Build.VERSION.SDK_INT >= 21) {
                return 16974383;
            }
            if (Build.VERSION.SDK_INT >= 14) {
                return 16973933;
            }
            return 16973831;
        }
        else {
            if (Build.VERSION.SDK_INT >= 21) {
                return 16974382;
            }
            if (Build.VERSION.SDK_INT >= 14) {
                return 16973932;
            }
            return 16973830;
        }
    }
    
    public boolean injectEvent(final InputEvent inputEvent) {
        return this.nativeInjectEvent(inputEvent);
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        return this.injectEvent((InputEvent)keyEvent);
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        return this.injectEvent((InputEvent)keyEvent);
    }
    
    public boolean onKeyMultiple(final int n, final int n2, final KeyEvent keyEvent) {
        return this.injectEvent((InputEvent)keyEvent);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return this.injectEvent((InputEvent)motionEvent);
    }
    
    public boolean onGenericMotionEvent(final MotionEvent motionEvent) {
        return this.injectEvent((InputEvent)motionEvent);
    }
    
    static /* synthetic */ void a(final UnityPlayer unityPlayer, final Surface surface) {
        unityPlayer.a(0, surface);
    }
    
    static /* synthetic */ void n(final UnityPlayer unityPlayer) {
        unityPlayer.nativeSetInputCanceled(true);
    }
    
    static {
        UnityPlayer.currentActivity = null;
        new UnityUncaughtExceptionHandler().init();
        UnityPlayer.p = true;
        UnityPlayer.mIsLoadMainLib = false;
        UnityPlayer.mIsLoadMainLib = loadLibraryStatic("main");
        UnityPlayer.mLock = new ReentrantLock();
    }
    
    private abstract class c implements Runnable
    {
        @Override
        public final void run() {
            if (!UnityPlayer.this.isFinishing()) {
                this.a();
            }
        }
        
        public abstract void a();
    }
    
    private final class GLThread extends Thread
    {
        ArrayBlockingQueue arryBlockingQueue;
        boolean isAlive;
        
        GLThread() {
            this.isAlive = false;
            this.arryBlockingQueue = new ArrayBlockingQueue(32);
        }
        
        @Override
        public final void run() {
            this.setName("UnityMain");
            try {
                EnumCommand enumCommand;
                while ((enumCommand = (EnumCommand)this.arryBlockingQueue.take()) != EnumCommand.QUIT) {
                    if (enumCommand == EnumCommand.RESUME) {
                        this.isAlive = true;
                    }
                    else if (enumCommand == EnumCommand.PAUSE) {
                        this.isAlive = false;
                        UnityPlayer.this.executeGLThreadJobs();
                    }
                    else if (enumCommand == EnumCommand.FOCUS_LOST && !this.isAlive) {
                        UnityPlayer.this.executeGLThreadJobs();
                    }
                    if (this.isAlive) {
                        do {
                            UnityPlayer.this.executeGLThreadJobs();
                            if (!UnityPlayer.this.isFinishing() && !UnityPlayer.this.nativeRender()) {
                                UnityPlayer.this.finish();
                            }
                        } while (this.arryBlockingQueue.peek() == null && !Thread.interrupted());
                    }
                }
            }
            catch (InterruptedException ex) {}
        }
        
        public final void notifyQuit() {
            this.addCommand(EnumCommand.QUIT);
        }
        
        public final void notifyResume() {
            this.addCommand(EnumCommand.RESUME);
        }
        
        public final void notifyPause() {
            this.addCommand(EnumCommand.PAUSE);
        }
        
        public final void notifyFocusChanged(final boolean isFocus) {
            this.addCommand(isFocus ? EnumCommand.FOCUS_GAINED : EnumCommand.FOCUS_LOST);
        }
        
        private void addCommand(final EnumCommand a) {
            try {
                this.arryBlockingQueue.put(a);
            }
            catch (InterruptedException ex) {
                this.interrupt();
            }
        }
    }
    
    enum EnumCommand
    {
        PAUSE("PAUSE", 0),
        RESUME("RESUME", 1),
        QUIT("QUIT", 2),
        FOCUS_GAINED("FOCUS_GAINED", 3),
        FOCUS_LOST("FOCUS_LOST", 4);
        
        private EnumCommand(final String s, final int n) {
        }

//        static {
//            mUnitySurfaceViewManager = new EnumCommand[] { PAUSE.PAUSE, PAUSE.RESUME, PAUSE.QUIT, PAUSE.FOCUS_GAINED, PAUSE.FOCUS_LOST};
//        }
    }



    public static native void UnitySendMessage(final String p0, final String p1, final String p2);

    private final native void nativeFile(final String p0);

    private final native void initJni(final Context p0);

    private final native void nativeSetExtras(final Bundle p0);

    private final native void nativeSetTouchDeltaY(final float p0);

    private final native boolean nativeRender();

    private final native void nativeSetInputString(final String p0);

    private final native void nativeSetInputCanceled(final boolean p0);

    private final native boolean nativePause();

    private final native void nativeResume();

    private final native void nativeFocusChanged(final boolean p0);

    private final native void nativeRecreateGfxState(final int p0, final Surface p1);

    private final native void nativeDone();

    private final native void nativeSoftInputClosed();

    private final native void nativeInitWWW(final Class p0);

    private final native void nativeVideoFrameCallback(final int p0, final byte[] p1, final int p2, final int p3);

    private final native int nativeActivityIndicatorStyle();

    private final native boolean nativeInjectEvent(final InputEvent p0);

    protected final native void nativeAddVSyncTime(final long p0);

    final native void nativeForwardEventsToDalvik(final boolean p0);
}
