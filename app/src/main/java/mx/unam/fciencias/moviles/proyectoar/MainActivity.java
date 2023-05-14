package mx.unam.fciencias.moviles.proyectoar;

import ai.deepar.ar.ARErrorType;
import ai.deepar.ar.AREventListener;
import ai.deepar.ar.ARTouchInfo;
import ai.deepar.ar.ARTouchType;
import ai.deepar.ar.CameraResolutionPreset;
import ai.deepar.ar.DeepAR;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.lifecycle.LifecycleOwner;


public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, AREventListener {

    private final int defaultLensFacing = CameraSelector.LENS_FACING_FRONT;
    private ARSurfaceProvider surfaceProvider = null;
    private int lensFacing = defaultLensFacing;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private DeepAR deepAR;

    private boolean recording = false;
    private boolean currentSwitchRecording = false;

    private int width = 0;
    private int height = 0;

    private File videoFileName;

    private LinearLayout buttonCollection;
    private SeekBar brushSizeBar;
    private float[] color;
    private float scale;
    private static final float minValue = 0.005f; //50/10000
    private static final float maxValue = 0.25f;  //2500/10000



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO },
                    1);
            Log.i("onStart", "Requesting Permissions");
        } else {
            initializeDeepAR();
        }
        super.onStart();
    }




    private void initializeDeepAR() {
        deepAR = new DeepAR(this);
        deepAR.setLicenseKey(BuildConfig.deepar_api_key);
        deepAR.initialize(this, this);
        Log.i("DeepAR", "Deep AR initialize");
        setupCamera();
        Log.i("Camera","Camera Sutup");
    }



    private void setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    // bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }
    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        CameraResolutionPreset cameraResolutionPreset = CameraResolutionPreset.P1920x1080;
        int width;
        int height;
        int orientation = getScreenOrientation();

        RelativeLayout.LayoutParams openActivityParams = new RelativeLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics())
        );

        RelativeLayout.LayoutParams buttonCollectionParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        RelativeLayout.LayoutParams brushSizeBarParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics())
        );

        if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            width = cameraResolutionPreset.getWidth();
            height =  cameraResolutionPreset.getHeight();

            openActivityParams.setMargins(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics()),
                    0, 0);

            buttonCollection.setOrientation(LinearLayout.HORIZONTAL);
            buttonCollectionParams.setMargins(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()),
                    0, 0);

            brushSizeBarParams.setMargins(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                    0, 0);
        } else {
            width = cameraResolutionPreset.getHeight();
            height = cameraResolutionPreset.getWidth();

            openActivityParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            openActivityParams.setMargins(
                    0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()),
                    0
            );

            buttonCollection.setOrientation(LinearLayout.VERTICAL);
            buttonCollectionParams.setMargins(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 85, getResources().getDisplayMetrics()),
                    0, 0);

            brushSizeBarParams.setMargins(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, getResources().getDisplayMetrics()),
                    0, 0);
        }
        buttonCollection.setLayoutParams(buttonCollectionParams);
        brushSizeBar.setLayoutParams(brushSizeBarParams);
        brushSizeBar.setProgress(1225);
        brushSizeBar.getThumb().setTint(Color.BLACK);

        Size cameraResolution = new Size(width, height);
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        Preview preview = new Preview.Builder()
                .setTargetResolution(cameraResolution)
                .build();

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
        if(surfaceProvider == null) {
            surfaceProvider = new ARSurfaceProvider(this, deepAR);
        }
        preview.setSurfaceProvider(surfaceProvider);
        surfaceProvider.setMirror(lensFacing == CameraSelector.LENS_FACING_FRONT);

    }

    @Override
    public void screenshotTaken(Bitmap bitmap) {

    }

    @Override
    public void videoRecordingStarted() {

    }

    @Override
    public void videoRecordingFinished() {

    }

    @Override
    public void videoRecordingFailed() {

    }

    @Override
    public void videoRecordingPrepared() {

    }

    @Override
    public void shutdownFinished() {

    }

    @Override
    public void initialized() {

    }

    @Override
    public void faceVisibilityChanged(boolean b) {

    }

    @Override
    public void imageVisibilityChanged(String s, boolean b) {

    }

    @Override
    public void frameAvailable(Image image) {

    }

    @Override
    public void error(ARErrorType arErrorType, String s) {

    }

    @Override
    public void effectSwitched(String s) {

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}