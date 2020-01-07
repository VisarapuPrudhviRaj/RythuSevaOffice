package nk.bluefrog.library.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.R;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PermissionResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraActivity extends BluefrogActivity implements View.OnClickListener {
    /*
     * constants --------------------
     */
    public static final String RESULT_IMG_PATH = "image_path";
    public static final String CAM_ORIENTATION = "cam_orientation";
    public static final String SAVE_LOCATION = "save_location";
    public static final String ENABLE_SCREEN_TOUCH = "enable_screen_touch";
    public static final int CAM_PORTRATE = Configuration.ORIENTATION_PORTRAIT;
    public static final int CAM_LANDSCAPE = Configuration.ORIENTATION_LANDSCAPE;
    Bitmap bitmap;
    String path;
    private CameraView cameraPreview;
    private PictureCallback mPicture;
    private RelativeLayout rlCamera;
    private boolean isInFrontFacing = false;
    private int orientationMode = 2;
    private int threshold = 30;
    private OrientationEventListener mOrientationListener;
    private AppCompatImageView ivRoatateCamera;
    private LinearLayout llButtons;
    private ImageButton buttonRotate;
    private int deviceRatationAngle = 0;
    private ImageButton buttonCapture;
    private AppCompatImageView ivCapturedImage;
    private ImageButton ibCancel;
    private ImageButton ibSave;
    private RelativeLayout rlCaptureLayout;
    private File savedFile;
    private Button bvPluse;
    private Button bvMinus;
    private FrameLayout flIndicator;
    private int rotateMode = Configuration.ORIENTATION_UNDEFINED;
    private String saveLocation = "BFMT";
    private boolean enableTouch = false;
    private boolean isCameraPermissionsGranted = false;
    private String TAG = "CameraActivity";
    private RelativeLayout rlRotateCam;
    private int cam = CameraView.FACING_BACK;
    private LinearLayout llZoom;
    private FrameLayout flSaveButtons;
    private Handler mBackgroundHandler;
    private Handler handlEquipment = new Handler() {
        public void handleMessage(android.os.Message msg) {
            closeProgressDialog();
            flIndicator.setVisibility(View.GONE);
            ivCapturedImage.setImageBitmap(bitmap);
            savedFile = new File(path);
            flSaveButtons.setVisibility(View.VISIBLE);
            refreshGallery(savedFile);

        }
    };
    private CameraView.Callback mCallback = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);
            Runtime.getRuntime().gc();

            flIndicator.setVisibility(View.VISIBLE);

            if (rotateMode != Configuration.ORIENTATION_UNDEFINED && mOrientationListener.canDetectOrientation()) {
                ivRoatateCamera.setVisibility(View.GONE);
                mOrientationListener.disable();
            }
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }

            rlCaptureLayout.setVisibility(View.GONE);
            llZoom.setVisibility(View.GONE);
            AspectRatio ratio = null;
            try {
                ratio = cameraPreview.getAspectRatio();
            } catch (Exception e) {
            }

            saveImageWithThread(data, pictureFile.getAbsolutePath());
            //new SaveImageAsync(data, ratio).execute(pictureFile.getAbsolutePath());
        }

    };

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Intent cameraIntent = getIntent();

        if (cameraIntent != null) {
            orientationMode = rotateMode = cameraIntent.getIntExtra(CAM_ORIENTATION, 0);
            saveLocation = cameraIntent.getStringExtra(SAVE_LOCATION);
            enableTouch = cameraIntent.getBooleanExtra(ENABLE_SCREEN_TOUCH, enableTouch);
            if (saveLocation == null) {
                saveLocation = "BFMT";
            }
        }

        askCompactPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        isCameraPermissionsGranted = true;
                        addCameraView();
                    }

                    @Override
                    public void permissionDenied() {
                        Helper.showToast(CameraActivity.this, getString(R.string.accept_cam_permissions));
                        setCancelResult();
                    }

                    @Override
                    public void permissionForeverDenied() {
                        openSettingsApp(CameraActivity.this);
                    }
                });

    }

    private void addCameraView() {
        setContentView(R.layout.camera_view);
        initView();
    }

    private void initView() {
        Runtime.getRuntime().gc();
        flSaveButtons = (FrameLayout) findViewById(R.id.flSaveButtons);
        rlRotateCam = (RelativeLayout) findViewById(R.id.rlRotateCam);
        llZoom = (LinearLayout) findViewById(R.id.llZoom);
        flIndicator = (FrameLayout) findViewById(R.id.flIndicator);
        bvPluse = (Button) findViewById(R.id.bvPluse);
        bvMinus = (Button) findViewById(R.id.bvMinus);
        rlCaptureLayout = (RelativeLayout) findViewById(R.id.rlCaptureLayout);
        ibCancel = (ImageButton) findViewById(R.id.ibCancel);
        ibSave = (ImageButton) findViewById(R.id.ibSave);

        rlCamera = (RelativeLayout) findViewById(R.id.llCamera);
        cameraPreview = (CameraView) findViewById(R.id.cameraPreview);
        ivRoatateCamera = (AppCompatImageView) findViewById(R.id.ivRoatateCamera);

        buttonCapture = (ImageButton) findViewById(R.id.buttonCapture);
        buttonRotate = (ImageButton) findViewById(R.id.buttonRotate);

        ivCapturedImage = (AppCompatImageView) findViewById(R.id.ivCapturedImage);
        llButtons = (LinearLayout) findViewById(R.id.llButtons);

        /* Listeners ----------------------------------------*/
        buttonRotate.setOnClickListener(this);
        buttonCapture.setOnClickListener(this);

        if (enableTouch) {
            cameraPreview.setOnClickListener(this);
        }
        ibCancel.setOnClickListener(this);
        ibSave.setOnClickListener(this);
        bvMinus.setOnClickListener(this);
        bvPluse.setOnClickListener(this);

        if (cameraPreview != null) {
            cameraPreview.addCallback(mCallback);
        }

        if (rotateMode != Configuration.ORIENTATION_UNDEFINED) {
            if (rotateMode == CAM_PORTRATE) {
                bvPluse.setRotation(0);
                bvMinus.setRotation(0);
                buttonRotate.setRotation(0);
                ivRoatateCamera.setImageDrawable(getResources().getDrawable(R.drawable.rotate_device_to_portrate));
            } else if (rotateMode == CAM_LANDSCAPE) {
                bvPluse.setRotation(90);
                bvMinus.setRotation(90);
                buttonRotate.setRotation(90);
                ivRoatateCamera.setImageDrawable(getResources().getDrawable(R.drawable.rotate_device_to_landscape));
            }
        }

        int orientation = ((WindowManager) getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay().getOrientation();

        if (rotateMode == CAM_LANDSCAPE) {

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {

                updateLayout(false);

            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

                updateLayout(true);
            }
        } else if (rotateMode == CAM_PORTRATE) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {

                updateLayout(true);

            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

                updateLayout(false);

            }
        }

    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    private void initialiseOrientationListener() {

        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {

                if (rotateMode == CAM_LANDSCAPE) {
                    if (isLandscape(orientation)) {

                        if (orientationMode == Configuration.ORIENTATION_PORTRAIT) {
                            // Landscape
                            onScreenRotate(Configuration.ORIENTATION_LANDSCAPE);
                        }
                        orientationMode = Configuration.ORIENTATION_LANDSCAPE;

                    } else {

                        if (orientationMode == Configuration.ORIENTATION_LANDSCAPE) {
                            // Unknown
                            onScreenRotate(Configuration.ORIENTATION_PORTRAIT);
                        }
                        orientationMode = Configuration.ORIENTATION_PORTRAIT;

                    }
                } else if (rotateMode == CAM_PORTRATE) {
                    if (isPortrait(orientation)) {

                        if (orientationMode == Configuration.ORIENTATION_LANDSCAPE) {
                            // Landscape
                            onScreenRotate(Configuration.ORIENTATION_PORTRAIT);
                        }
                        orientationMode = Configuration.ORIENTATION_PORTRAIT;

                    } else {

                        if (orientationMode == Configuration.ORIENTATION_PORTRAIT) {
                            // Unknown
                            onScreenRotate(Configuration.ORIENTATION_LANDSCAPE);
                        }
                        orientationMode = Configuration.ORIENTATION_LANDSCAPE;

                    }
                } else {
                    deviceRatationAngle = orientation;
                    int angle = 360 - orientation;
                    buttonRotate.setRotation(angle);
                    bvPluse.setRotation(angle);
                    bvMinus.setRotation(angle);
                }
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            Log.e("Error ", "Sensor is missing");
        }
    }

    private void onScreenRotate(int orientation) {
        if (this.rotateMode == orientation) {
            updateLayout(true);
        } else {
            updateLayout(false);
        }

    }

    /*(orientation >= (90 - threshold) && orientation <= (90 + threshold))*/

    public void onResume() {
        super.onResume();
        if (isCameraPermissionsGranted) {
           /* List<AspectRatio> aspectRatios = new ArrayList<>(cameraPreview.getSupportedAspectRatios());
            if (aspectRatios.size() > 0) {
                cameraPreview.setAspectRatio(aspectRatios.get(aspectRatios.size() - 1));
            }*/
            if (!cameraPreview.start()) {
                initialiseOrientationListener();
            } else {
                Helper.showToast(this, "Unable to start camera");
                finish();
            }
        }
    }

    private void setCancelResult() {
        setResult(RESULT_CANCELED, null);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCameraPermissionsGranted) {
            cameraPreview.stop();
        }
    }

    private boolean hasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isLandscape(int orientation) {
        if (orientation >= (270 - threshold)
                && orientation <= (270 + threshold)) {
            return true;
        } else
            return false;
    }

    private boolean isPortrait(int orientation) {
        return (orientation >= (360 - threshold) && orientation <= 360)
                || (orientation >= 0 && orientation <= threshold);
    }

    public void updateLayout(boolean needToShow) {
        if (needToShow) {
//            ivRoatateCamera.setVisibility(View.GONE);
            rlRotateCam.setVisibility(View.GONE);
//            cameraPreview.start();
        } else {
            rlRotateCam.setVisibility(View.VISIBLE);
//            ivRoatateCamera.setVisibility(View.VISIBLE);
//            cameraPreview.stop();
        }
    }

    public void onCaptureImage() {
        if (!cameraPreview.takePicture()) {
            Helper.showToast(this, "Camera is not ready");
            finish();
        }
    }

    public void onChangeCamera() {
        int camerasNumber = Camera.getNumberOfCameras();
        if (camerasNumber > 1) {
            alterCamera();
        } else {
            Helper.showToast(this, getString(R.string.phone_does_not_contains_camera));
        }

    }

    public void alterCamera() {

        if (cameraPreview != null) {
            int facing = cameraPreview.getFacing();
            cam = (facing == CameraView.FACING_FRONT ? CameraView.FACING_BACK : CameraView.FACING_FRONT);
            cameraPreview.setFacing(cam);

        }
    }

    @Override
    public void onBackPressed() {
        setCancelResult();
    }

    /**
     * Create a File for saving an image
     */
    private File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), saveLocation);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(saveLocation, getString(R.string.failed_to_create_directory));
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private void setResult(String fileLocation) {

        flIndicator.setVisibility(View.VISIBLE);

        Intent intent = new Intent();
        intent.putExtra(RESULT_IMG_PATH, fileLocation);
        setResult(RESULT_OK, intent);

        flIndicator.setVisibility(View.GONE);
        finish();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonCapture) {
            onCaptureImage();
        } else if (id == R.id.buttonRotate) {
            cameraPreview.setVisibility(View.GONE);
            onChangeCamera();
            cameraPreview.setVisibility(View.VISIBLE);
        } else if (id == R.id.cameraPreview) {
            flIndicator.setVisibility(View.VISIBLE);
            onCaptureImage();
        } else if (id == R.id.ibCancel) {
            cameraPreview.setVisibility(View.VISIBLE);
            rlCaptureLayout.setVisibility(View.VISIBLE);
            flSaveButtons.setVisibility(View.GONE);
            llZoom.setVisibility(View.VISIBLE);
            try {
                savedFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (rotateMode != Configuration.ORIENTATION_UNDEFINED && mOrientationListener.canDetectOrientation()) {
                ivRoatateCamera.setVisibility(View.VISIBLE);
                mOrientationListener.enable();
            }
            savedFile = null;
        } else if (id == R.id.ibSave) {
            setResult(savedFile.getAbsolutePath());
        } else if (id == R.id.bvPluse) {
            cameraPreview.setZoom(true);
        } else if (id == R.id.bvMinus) {
            cameraPreview.setZoom(false);
        }
    }

    private void zoomCamera(boolean b) {
//        Camera.Parameters parameters = mCallback.getParameters();
//        int maxZoom = parameters.getMaxZoom();
//        if (parameters.isZoomSupported()) {
//            if (zoom >=0 && zoom < maxZoom) {
//                parameters.setZoom(zoom);
//            } else {
//                // zoom parameter is incorrect
//            }
//        }

    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    private void saveImageWithThread(final byte[] imgData, final String imagePath) {

        showProgressDialog("Please Wait...");
        flIndicator.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                try {
                    path = imagePath;
                    if (saveImage(imgData, imagePath)) {
                        bitmap = decodeToBitmap(imagePath, null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                handlEquipment.sendEmptyMessage(0);
            }

        }.start();
    }

    private boolean saveImage(byte[] imgData, String path) {
        File file = new File(path);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(imgData);
            os.close();
        } catch (IOException e) {
            Log.w(TAG, "Cannot write to " + file, e);
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
        return true;
    }

    private Bitmap decodeToBitmap(String path, AspectRatio ratio) throws IOException {
        Bitmap resultBitmap = compressImage(path, ratio);
        int reqWidth = resultBitmap.getWidth();
        int reqHeight = resultBitmap.getHeight();

        Bitmap scaledBitmap = saveImage(path, resultBitmap, reqWidth, reqHeight);

//        = Bitmap.createScaledBitmap(resultBitmap, reqWidth, reqHeight, true);

        /*if (orientationMode == Configuration.ORIENTATION_PORTRAIT && cam == CameraView.FACING_FRONT) {
            Matrix matrix180 = new Matrix();
            matrix180.postRotate(180);
            return Bitmap.createBitmap(scaledBitmap, 0, 0, reqWidth, reqHeight, matrix180, true);
        }*/

        return scaledBitmap;
    }

    public Bitmap decodeToBitmap(String path, byte[] img, int reqWidth, int reqHeight) throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeByteArray(img, 0, img.length, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap resultBitmap = BitmapFactory.decodeFile(path, options);
//        Bitmap resultBitmap = BitmapFactory.decodeByteArray(img, 0, img.length, options);

        ExifInterface exif = new ExifInterface(path);

        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        } else if (orientation == 3) {
            matrix.postRotate(180);
        } else if (orientation == 8) {
            matrix.postRotate(270);
        }
        resultBitmap = Bitmap.createBitmap(resultBitmap, 0, 0,
                resultBitmap.getWidth(), resultBitmap.getHeight(), matrix,
                true);

        saveImage(path, resultBitmap, reqWidth, reqHeight);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, reqWidth, reqHeight, true);

        if (orientationMode == Configuration.ORIENTATION_PORTRAIT && cam == CameraView.FACING_FRONT) {
            Matrix matrix180 = new Matrix();
            matrix180.postRotate(180);
            return Bitmap.createBitmap(scaledBitmap, 0, 0, reqWidth, reqHeight, matrix180, true);
        }

        return scaledBitmap;
    }

    private Bitmap saveImage(String fileLocation, Bitmap resultBitmap, int reqWidth, int reqHeight) throws IOException {

        Bitmap bitmap = resultBitmap.copy(resultBitmap.getConfig(), true);
        bitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true);
        if (orientationMode == Configuration.ORIENTATION_PORTRAIT) {
            Matrix matrix = new Matrix();
            if (cam == CameraView.FACING_FRONT) {
                matrix.postRotate(180);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, reqWidth, reqHeight, matrix, true);
            }
        } else if (orientationMode == Configuration.ORIENTATION_LANDSCAPE) {
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            Matrix matrix = new Matrix();
            int angle = 0;
            if (45 < deviceRatationAngle && deviceRatationAngle <= 135) {
                angle = 90;
            } else if (135 < deviceRatationAngle && deviceRatationAngle <= 225) {
                angle = 180;
            } else if (225 < deviceRatationAngle && deviceRatationAngle <= 315) {
                angle = 270;
            }

            if (cam == CameraView.FACING_FRONT) {
                angle += 180;
                if ((45 < deviceRatationAngle && deviceRatationAngle <= 135) || (225 < deviceRatationAngle && deviceRatationAngle <= 315) || angle == 450) {
                    angle -= 180;
                }
            }

            matrix.postRotate(angle);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] byteArray = stream.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileLocation);
            fos.write(byteArray);
            fos.close();
        } catch (IOException e) {
            Log.w("CameraActivity", "Cannot write to " + fileLocation, e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        return bitmap;
    }

    public Bitmap compressImage(String filePath, AspectRatio ratio) {

       /* if (ratio != null && ratio.getX() == 4 && ratio.getY() == 3) {
            ivCapturedImage.setAdjustViewBounds(true);
        } else {
            ivCapturedImage.setAdjustViewBounds(false);
        }*/

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float maxHeight = displayMetrics.heightPixels;
        float maxWidth = displayMetrics.widthPixels;

        float maxRatio = maxWidth / maxHeight;
        float imgRatio;
        imgRatio = actualWidth / actualHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scaledBitmap;

    }

    private class SaveImageAsync extends AsyncTask<String, Void, Bitmap> {

        private byte[] imgData;
        private String path;
        private AspectRatio ratio;

        public SaveImageAsync(byte[] data, AspectRatio ratio) {
            this.imgData = data;
            this.ratio = ratio;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            flIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            path = params[0];
            try {

                if (saveImage(imgData, path)) {
                   /* DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels;
                    width = (width / 100) * 75;
                    height = (height / 100) * 75;*/

                    bitmap = decodeToBitmap(path, ratio);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        private boolean saveImage(byte[] imgData, String path) {
            File file = new File(path);
            OutputStream os = null;
            try {
                os = new FileOutputStream(file);
                os.write(imgData);
                os.close();
            } catch (IOException e) {
                Log.w(TAG, "Cannot write to " + file, e);
                return false;
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            flIndicator.setVisibility(View.GONE);
            ivCapturedImage.setImageBitmap(bitmap);
            savedFile = new File(path);
            flSaveButtons.setVisibility(View.VISIBLE);
            refreshGallery(savedFile);
        }
    }

}
