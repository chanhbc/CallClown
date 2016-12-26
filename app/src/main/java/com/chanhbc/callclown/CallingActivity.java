package com.chanhbc.callclown;

import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.io.IOException;

public class CallingActivity extends AppCompatActivity implements CameraManager.OnCameraReadyListener, TextureView.SurfaceTextureListener {
    private TextureView ttvCamera;
    private CameraManager cameraManager;
    private boolean isReady;
    private Button btnEndCall;
    private TextureView ttvView;
    private MediaPlayer mediaPlayer;
    private float widthVideo;
    private float heightVideo;
    private float widthView;
    private float heightView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        getIntent();
        initializeComponents();
    }

    private void initializeComponents() {
        isReady = false;
        cameraManager = new CameraManager();
        ttvCamera = (TextureView) findViewById(R.id.ttv_camera);
        cameraManager.setupWithTextureView(ttvCamera);
        cameraManager.setOnCameraReadyListener(this);
        btnEndCall = (Button) findViewById(R.id.btn_end_call);
        ttvView = (TextureView) findViewById(R.id.textureView);
        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        widthView = getResources().getDisplayMetrics().widthPixels;
        heightView = getResources().getDisplayMetrics().heightPixels;
        ttvView.setSurfaceTextureListener(this);
        cropCenterVideo();
    }

    private void cropCenterVideo() {
        AssetFileDescriptor assetFileDescriptor = null;
        try {
            assetFileDescriptor = getAssets().openFd("clown.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(
                assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(),
                assetFileDescriptor.getLength());
        heightVideo = Float.parseFloat(metaRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        widthVideo = Float.parseFloat(metaRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        float scaleX = 1.0f;
        float scaleY = 1.0f;
        if (widthVideo > widthView && heightVideo > heightView) {
            scaleX = widthVideo / widthView;
            scaleY = heightVideo / heightView;
        } else if (widthVideo < widthView && heightVideo < heightView) {
            scaleY = widthView / widthVideo;
            scaleX = heightView / heightVideo;
        } else if (widthView > widthVideo) {
            scaleY = (widthView / widthVideo) / (heightView / heightVideo);
        } else if (heightView > heightVideo) {
            scaleX = (heightView / heightVideo) / (widthView / widthVideo);
        }
        int pointX = (int) (widthView / 2);
        int pointY = (int) (heightView / 2);
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, pointX, pointY);
        ttvView.setTransform(matrix);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReady) {
            cameraManager.openCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraManager.closeCamera();
    }

    @Override
    public void onCameraReady() {
        if (isReady) {
            return;
        }
        isReady = true;
        cameraManager.openCamera();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        try {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd("clown.mp4");
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(
                    assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength());
            heightVideo = Float.parseFloat(metaRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            widthVideo = Float.parseFloat(metaRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepare();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Log.d("SCALLING", "...");
                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    finish();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
