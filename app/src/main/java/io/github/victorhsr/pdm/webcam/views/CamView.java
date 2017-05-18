package io.github.victorhsr.pdm.webcam.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;

/**
 * Created by victor on 22/03/17.
 */

public class CamView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceHolder holder;
    private Camera camera;
    private int width;
    private int height;
    private ByteArrayOutputStream framebuffer;
    private Context context;
    private boolean isFlashEnabled = false;

    public CamView(Context context) {
        super(context);
        this.context = context;
        this.camera = getCameraInstance();
        updateCameraPreferences();
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private Camera getCameraInstance() {

        releaseCamera();

        return Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        try {
            YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, this.width, this.height, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0, 0, this.width, this.height), 100, baos);
            framebuffer = baos;
        } catch (Exception e) {
            Log.e("[CamView]", e.getMessage() == null ? "":e.getMessage());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
        } catch (Exception e) {
            Log.e("[CamView]", e.getMessage() == null ? "":e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            camera.stopPreview();
        } catch (Exception e) {
            Log.e("[CamView]", e.getMessage() == null ? "":e.getMessage());
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(320, 240);
            this.width = parameters.getPreviewSize().width;
            this.height = parameters.getPreviewSize().height;
            parameters.setPreviewFormat(ImageFormat.NV21);
            camera.setParameters(parameters);
            camera.setPreviewCallback(this);
            camera.startPreview();
        } catch (Exception e) {
            Log.e("[CamView]", e.getMessage() == null ? "":e.getMessage());
        }
    }

    public ByteArrayOutputStream getFramebuffer() {
        return framebuffer;
    }

    private void updateCameraPreferences() {

        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);

        int rotation = 0;

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            rotation = activity.getWindowManager().getDefaultDisplay()
                    .getRotation();
        }

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    public void useFlash() {

        Camera.Parameters cameraParameters = camera.getParameters();

        if (!isFlashEnabled) {
            cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(cameraParameters);

            isFlashEnabled = true;
        } else {
            cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(cameraParameters);

            isFlashEnabled = false;
        }
    }

}
