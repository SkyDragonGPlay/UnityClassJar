package com.unity3d.player;

import android.hardware.Camera;
import android.view.*;
import android.graphics.*;
import java.util.*;

final class UnityCamera
{
    private final Object[] loackObject;
    private final int mCameraID;
    private final int mWidth;
    private final int mHeight;
    private final int mDefaultFPS;
    Camera mCamera;
    Camera.Parameters mCameraParameters;
    Camera.Size mPreviewSize;
    int d;
    int[] e;
    UnityCameraSurface mCameraSurface;
    
    public UnityCamera(final int cameraID, final int width, final int height, final int fps) {
        this.loackObject = new Object[0];
        this.mCameraID = cameraID;
        this.mWidth = getNonzero(width, 640);
        this.mHeight = getNonzero(height, 480);
        this.mDefaultFPS = getNonzero(fps, 24);
    }
    
    private void init(final ICameraFrame cameraFrameCallback) {
        synchronized (this.loackObject) {
            this.mCamera = Camera.open(this.mCameraID);
            this.mCameraParameters = this.mCamera.getParameters();
            this.mPreviewSize = this.getSupportSize();
            this.e = this.e();
            this.d = this.d();
            a(this.mCameraParameters);
            this.mCameraParameters.setPreviewSize(this.mPreviewSize.width, this.mPreviewSize.height);
            this.mCameraParameters.setPreviewFpsRange(this.e[0], this.e[1]);
            this.mCamera.setParameters(this.mCameraParameters);
            final Camera.PreviewCallback previewCallbackWithBuffer = (Camera.PreviewCallback)new Camera.PreviewCallback() {
                long a = 0L;
                
                public final void onPreviewFrame(final byte[] array, final Camera camera) {
                    if (UnityCamera.this.mCamera != camera) {
                        return;
                    }
                    cameraFrameCallback.onCameraFrame(UnityCamera.this, array);
                }
            };
            final int n = this.mPreviewSize.width * this.mPreviewSize.height * this.d / 8 + 4096;
            this.mCamera.addCallbackBuffer(new byte[n]);
            this.mCamera.addCallbackBuffer(new byte[n]);
            this.mCamera.setPreviewCallbackWithBuffer((Camera.PreviewCallback)previewCallbackWithBuffer);
        }
    }
    
    private static void a(final Camera.Parameters parameters) {
        if (parameters.getSupportedColorEffects() != null) {
            parameters.setColorEffect("none");
        }
        if (parameters.getSupportedFocusModes().contains("continuous-video")) {
            parameters.setFocusMode("continuous-video");
        }
    }
    
    public final int getCameraID() {
        return this.mCameraID;
    }
    
    public final Camera.Size getPreviewSize() {
        return this.mPreviewSize;
    }
    
    public final void openCamera(final ICameraFrame cameraFrameCallback) {
        synchronized (this.loackObject) {
            if (this.mCamera == null) {
                this.init(cameraFrameCallback);
            }
            if (UnityConstants.IS_SDK_VERSION_GE_11 && UnityConstants.h.a(this.mCamera)) {
                this.mCamera.startPreview();
                return;
            }
            if (this.mCameraSurface == null) {
                (this.mCameraSurface = new UnityCameraSurface(3) {
                    Camera camera = UnityCamera.this.mCamera;
                    
                    public final void surfaceCreated(final SurfaceHolder previewDisplay) {
                        synchronized (UnityCamera.this.loackObject) {
                            if (UnityCamera.this.mCamera != camera) {
                                return;
                            }
                            try {
                                UnityCamera.this.mCamera.setPreviewDisplay(previewDisplay);
                                UnityCamera.this.mCamera.startPreview();
                            }
                            catch (Exception ex) {
                                UnityLog.Log(6, "Unable to initialize webcam data stream: " + ex.getMessage());
                            }
                        }
                    }
                    
                    @Override
                    public final void surfaceDestroyed(final SurfaceHolder surfaceHolder) {
                        synchronized (UnityCamera.this.loackObject) {
                            if (UnityCamera.this.mCamera != camera) {
                                return;
                            }
                            UnityCamera.this.mCamera.stopPreview();
                        }
                    }
                }).a();
            }
        }
    }
    
    public final void setCallbackBuffer(final byte[] array) {
        synchronized (this.loackObject) {
            if (this.mCamera != null) {
                this.mCamera.addCallbackBuffer(array);
            }
        }
    }
    
    public final void close() {
        synchronized (this.loackObject) {
            if (this.mCamera != null) {
                this.mCamera.setPreviewCallbackWithBuffer((Camera.PreviewCallback)null);
                this.mCamera.stopPreview();
                this.mCamera.release();
                this.mCamera = null;
            }
            if (this.mCameraSurface != null) {
                this.mCameraSurface.close();
                this.mCameraSurface = null;
            }
        }
    }
    
    private final int d() {
        this.mCameraParameters.setPreviewFormat(17);
        return ImageFormat.getBitsPerPixel(17);
    }
    
    private final int[] e() {
        final double n = this.mDefaultFPS * 1000;
        List<int[]> supportedPreviewFpsRange;
        if ((supportedPreviewFpsRange = (List<int[]>)this.mCameraParameters.getSupportedPreviewFpsRange()) == null) {
            supportedPreviewFpsRange = new ArrayList<int[]>();
        }
        int[] array = { this.mDefaultFPS * 1000, this.mDefaultFPS * 1000 };
        double n2 = Double.MAX_VALUE;
        for (final int[] array2 : supportedPreviewFpsRange) {
            final double n3;
            if ((n3 = Math.abs(Math.log(n / array2[0])) + Math.abs(Math.log(n / array2[1]))) < n2) {
                n2 = n3;
                array = array2;
            }
        }
        return array;
    }
    
    private final Camera.Size getSupportSize() {
        final double n = this.mWidth;
        final double n2 = this.mHeight;
        final List supportedPreviewSizes = this.mCameraParameters.getSupportedPreviewSizes();
        Camera.Size size = null;
        double n3 = Double.MAX_VALUE;

        Iterator<Camera.Size> iterator = supportedPreviewSizes.iterator();
        while(iterator.hasNext()) {
            Camera.Size size2 = iterator.next();
            final double n4;
            if ((n4 = Math.abs(Math.log(n / size2.width)) + Math.abs(Math.log(n2 / size2.height))) < n3) {
                n3 = n4;
                size = size2;
            }
        }
        return size;
    }
    
    private static final int getNonzero(final int n, final int n2) {
        if (n != 0) {
            return n;
        }
        return n2;
    }
    
    interface ICameraFrame
    {
        void onCameraFrame(final UnityCamera p0, final byte[] p1);
    }
}
