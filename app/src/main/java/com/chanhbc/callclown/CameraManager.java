package com.chanhbc.callclown;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.TextureView;
import android.view.ViewTreeObserver;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
public class CameraManager implements TextureView.SurfaceTextureListener {
    private TextureView ttvCamera;
    private int width;
    private int height;
    private Camera camera;
    private Camera.CameraInfo cameraInfo;
    private boolean isSetup;
    private OnCameraReadyListener onCameraReadyListener;

    public CameraManager() {
        cameraInfo = new Camera.CameraInfo();
    }

    public void setupWithTextureView(TextureView ttvCamera) {
        isSetup = false;
        this.ttvCamera = ttvCamera;
        this.ttvCamera.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isSetup) {
                    return;
                }
                isSetup = true;
                width = CameraManager.this.ttvCamera.getWidth();
                height = CameraManager.this.ttvCamera.getHeight();
                CameraManager.this.ttvCamera.setSurfaceTextureListener(CameraManager.this);
                onCameraReadyListener.onCameraReady();
            }
        });
    }

    public void openCamera() {
        if (camera != null) {
            camera.release();
        }
        // camera
        camera = Camera.open(1);
        Camera.Parameters parameters = camera.getParameters();
        //
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = getCameraSizeOptimal(previewSizes);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        Camera.Size pictureSize = getCameraSizeOptimal(pictureSizes);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);

        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setJpegQuality(100);

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        camera.setParameters(parameters);

        // camera orientation
        camera.setDisplayOrientation(90);
    }

    public void closeCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private Camera.Size getCameraSizeOptimal(List<Camera.Size> previewSizes) {
        Camera.Size sizeOptimal = null;
        float ratio = width > height ? (float) width / height : (float) height / width;
        int multiple = width * height;
        int d = Integer.MAX_VALUE;
        for (Camera.Size size : previewSizes) {
            int h = size.height;
            int w = size.width;
            float r = w > h ? (float) w / h : (float) h / w;
            int m = w * h;
            if (Math.abs(ratio - r) <= 0.1F) {
                if (Math.abs(multiple - m) < d)
                    d = Math.abs(multiple - m);
                sizeOptimal = size;
            }
        }
        if (sizeOptimal == null) {
            for (Camera.Size size : previewSizes) {
                int h = size.height;
                int w = size.width;
                int m = w * h;
                if (Math.abs(multiple - m) < d)
                    d = Math.abs(multiple - m);
                sizeOptimal = size;
            }
        }
        return sizeOptimal;
    }

    public void setOnCameraReadyListener(OnCameraReadyListener listener) {
        this.onCameraReadyListener = listener;
    }

    public interface OnCameraReadyListener {
        void onCameraReady();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        try {
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
            camera.autoFocus(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
