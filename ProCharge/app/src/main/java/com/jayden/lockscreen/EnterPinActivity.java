package com.jayden.lockscreen;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.fingerprint.FingerprintManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;

import com.jayden.batteryalarm.R;
import com.jayden.batteryalarm.activity.MainActivity;
import com.jayden.batteryalarm.model.AlarmData;
import com.jayden.batteryalarm.model.IntruderData;
import com.jayden.batteryalarm.service.AlarmService;
import com.jayden.batteryalarm.templates.Constant;
import com.jayden.batteryalarm.templates.DBHelper;
import com.jayden.batteryalarm.util.SharedPreferencesApplication;
import com.jayden.lockscreen.fingerprint.Animate;
import com.jayden.lockscreen.fingerprint.FingerPrintListener;
import com.jayden.lockscreen.fingerprint.FingerprintHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class EnterPinActivity extends AppCompatActivity {

    public static final String TAG = "EnterPinActivity";
    SharedPreferencesApplication sh = new SharedPreferencesApplication();
    public static int lastImageNo = 0;
    public int counPicture = 0 ;

    public static final int RESULT_BACK_PRESSED = RESULT_FIRST_USER;
    public static final String EXTRA_SET_PIN = "set_pin";
    public static final String EXTRA_FONT_TEXT = "textFont";
    public static final String EXTRA_FONT_NUM = "numFont";

    private static final int PIN_LENGTH = 4;
    private static final String FINGER_PRINT_KEY = "FingerPrintKey";

    private PinLockView mPinLockView;
    private TextView mTextTitle;
    private TextView mTextAttempts;
    private TextView mTextFingerText;
    private AppCompatImageView mImageViewFingerView;

    private Cipher mCipher;
    private KeyStore mKeyStore;
    private boolean mSetPin = false;
    private String mFirstPin = "";
    private String when_toCall ="" ;
    private int mTryCount = 0;

    private AnimatedVectorDrawable showFingerprint;
    private AnimatedVectorDrawable fingerprintToTick;
    private AnimatedVectorDrawable fingerprintToCross;

    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_enterpin);

        mTextAttempts =  findViewById(R.id.attempts);
        mTextTitle =  findViewById(R.id.title);
        mImageViewFingerView =  findViewById(R.id.fingerView);
        mTextFingerText =  findViewById(R.id.fingerText);

        textureView = findViewById(R.id.texture);
        textureView.setSurfaceTextureListener(textureListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showFingerprint = (AnimatedVectorDrawable) getDrawable(R.drawable.show_fingerprint);
            fingerprintToTick = (AnimatedVectorDrawable) getDrawable(R.drawable.fingerprint_to_tick);
            fingerprintToCross = (AnimatedVectorDrawable) getDrawable(R.drawable.fingerprint_to_cross);
        }

        mSetPin = getIntent().getBooleanExtra(EXTRA_SET_PIN, false);
        when_toCall = getIntent().getStringExtra("WHEN_CALL");
        Log.e("WHEN_TOCALL", when_toCall +  " ");

        if (when_toCall != null){
            if (when_toCall.equalsIgnoreCase("THEFTALARMSET")) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EnterPinActivity.this, R.style.alert_dialog);
                builder.setTitle("Alert ");
                builder.setMessage("Password Required for Theft Alarm dismiss ");
                builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                //Creating dialog box
                android.app.AlertDialog dialog = builder.create();
                dialog.show();

                Button btn1 = dialog.findViewById(android.R.id.button1);
                btn1.setTextColor(EnterPinActivity.this.getResources().getColor(R.color.colorPrimary));
            }
        }

        if (mSetPin) {
            changeLayoutForSetPin();
        } else {
            String pin = sh.getUserPin(EnterPinActivity.this);
            if (pin.equals("")) {
                changeLayoutForSetPin();
                mSetPin = true;
            } else {
                checkForFingerPrint();
            }
        }

        final PinLockListener pinLockListener = new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                if (mSetPin) {
                    setPin(pin);
                } else {
                    checkPin(pin);
                }
            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "Pin empty");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }

        };

        mPinLockView = findViewById(R.id.pinlockView);
        IndicatorDots mIndicatorDots =  findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(pinLockListener);

        mPinLockView.setPinLength(PIN_LENGTH);
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);

        checkForFont();
    }
    private void checkForFont() {
        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_FONT_TEXT)) {

            String font = intent.getStringExtra(EXTRA_FONT_TEXT);
            setTextFont(font);
        }
        if (intent.hasExtra(EXTRA_FONT_NUM)) {
            String font = intent.getStringExtra(EXTRA_FONT_NUM);
            setNumFont(font);
        }
    }

    private void setTextFont(String font) {
        try {
            Typeface typeface = Typeface.createFromAsset(getAssets(), font);

            mTextTitle.setTypeface(typeface);
            mTextAttempts.setTypeface(typeface);
            mTextFingerText.setTypeface(typeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNumFont(String font) {
        try {
            Typeface typeface = Typeface.createFromAsset(getAssets(), font);

            mPinLockView.setTypeFace(typeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//
    private void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            //Generate the key//
            KeyGenerator mKeyGenerator = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                //Initialize an empty KeyStore//
                mKeyStore.load(null);

                //Initialize the KeyGenerator//
                mKeyGenerator.init(new
                        //Specify the operation(s) this key can be used for//
                        KeyGenParameterSpec.Builder(FINGER_PRINT_KEY,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                        //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            }
            //Generate the key//
            if (mKeyGenerator != null) {
                mKeyGenerator.generateKey();
            }
        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            throw new FingerprintException(exc);
        }
    }

    //Create a new method that we’ll use to initialize our mCipher//
    public boolean initCipher() {
        try {
            //Obtain a mCipher instance and configure it with the properties required for fingerprint authentication//
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCipher = Cipher.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES + "/"
                                + KeyProperties.BLOCK_MODE_CBC + "/"
                                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            }
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            Log.e(TAG, "Failed to get Cipher");
            return false;
        }
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(FINGER_PRINT_KEY,
                    null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the mCipher has been initialized successfully//
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to init Cipher");
            return false;
        }
    }

    private void setPin(String pin) {
        if (mFirstPin.equals("")) {
            mFirstPin = pin;
            mTextTitle.setText(getString(R.string.pinlock_secondPin));
            mPinLockView.resetPinLockView();
        } else {
            if (pin.equals(mFirstPin)) {
                sh.setUserPin(EnterPinActivity.this , Utils.sha256(pin));
                setResult(RESULT_OK);
                finish();
            } else {
                shake();
                mTextTitle.setText(getString(R.string.pinlock_tryagain));
                mPinLockView.resetPinLockView();
                mFirstPin = "";
            }
        }
    }

    private void checkPin(String pin) {
        if (Utils.sha256(pin).equalsIgnoreCase(sh.getUserPin(EnterPinActivity.this))) {
            setResult(RESULT_OK);
            Log.e("AVT" , "Check Pin" );
            if (when_toCall != null) {
                Log.e("AVT" , "when_toCall Not null" );
                if (when_toCall.equalsIgnoreCase("TherftAlarm")){
                    Log.e("AVT" , "when_toCall : " + when_toCall );
                    stopService(new Intent(EnterPinActivity.this , AlarmService.class));
                    Intent myIntent = new Intent(this, MainActivity.class);
                    myIntent.putExtra("DONE" ,"DONEPWD");
                    startActivity(myIntent);
                    finish();
                }
            }
            finish();
        } else {
            shake();
            mTryCount++;
            Log.e("Count " , mTryCount + " ");

            mTextAttempts.setText(getString(R.string.pinlock_wrongpin));
            mPinLockView.resetPinLockView();

//            if (mTryCount == 1) {
////                mTextAttempts.setText(getString(R.string.pinlock_firsttry));
////                mPinLockView.resetPinLockView();
////            } else if (mTryCount == 2) {
////                mTextAttempts.setText(getString(R.string.pinlock_secondtry));
////                mPinLockView.resetPinLockView();
////            } else if (mTryCount > 2) {
////                setResult(RESULT_TOO_MANY_TRIES);
////                finish();
////            }
        }
    }

    private void shake() {
        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(mPinLockView, "translationX",
                0, 25, -25, 25, -25, 15, -15, 6, -6, 0).setDuration(1000);
        objectAnimator.start();
    }

    private void changeLayoutForSetPin() {
        mImageViewFingerView.setVisibility(View.GONE);
        mTextFingerText.setVisibility(View.GONE);
        mTextAttempts.setVisibility(View.GONE);
        mTextTitle.setText(getString(R.string.pinlock_settitle));
    }

    private void checkForFingerPrint() {
        final FingerPrintListener fingerPrintListener = new FingerPrintListener() {
            @Override
            public void onSuccess() {
                setResult(RESULT_OK);
                Animate.animate(mImageViewFingerView, fingerprintToTick);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("AVT" , "Check Pin" );
                        if (when_toCall != null) {
                            Log.e("AVT" , "when_toCall Not null" );
                            if (when_toCall.equalsIgnoreCase("TherftAlarm")){
                                Log.e("AVT" , "when_toCall : " + when_toCall );
                                stopService(new Intent(EnterPinActivity.this , AlarmService.class));
                                Intent myIntent = new Intent(EnterPinActivity.this, MainActivity.class);
                                myIntent.putExtra("DONE" ,"DONEPWD");
                                startActivity(myIntent);
                            }
                        }
                        finish();
                    }
                }, 750);
            }

            @Override
            public void onFailed() {
                Animate.animate(mImageViewFingerView, fingerprintToCross);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animate.animate(mImageViewFingerView, showFingerprint);
                    }
                }, 750);
            }

            @Override
            public void onError(CharSequence errorString) {
                Toast.makeText(EnterPinActivity.this, errorString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHelp(CharSequence helpString) {
                Toast.makeText(EnterPinActivity.this, helpString, Toast.LENGTH_SHORT).show();
            }

        };

        if (sh.getUserFingurePrint(EnterPinActivity.this)) {
            // If you’ve set your app’s minSdkVersion to anything lower than 23, then you’ll need to verify that the device is running Marshmallow
            // or higher before executing any fingerprint-related code
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                if (fingerprintManager != null && fingerprintManager.isHardwareDetected()) {
                    //Get an instance of KeyguardManager and FingerprintManager//
                    KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                    FingerprintManager mFingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

                    //Check whether the user has granted your app the USE_FINGERPRINT permission//
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
                            != PackageManager.PERMISSION_GRANTED) {
                        // If your app doesn't have this permission, then display the following text//
                        Toast.makeText(EnterPinActivity.this, "Please enable the fingerprint permission", Toast.LENGTH_LONG).show();
                        mImageViewFingerView.setVisibility(View.GONE);
                        mTextFingerText.setVisibility(View.GONE);
                        return;
                    }

                    //Check that the user has registered at least one fingerprint//
                    if (!mFingerprintManager.hasEnrolledFingerprints()) {
                        // If the user hasn’t configured any fingerprints, then display the following message//
                        Toast.makeText(EnterPinActivity.this,
                                "No fingerprint configured. Please register at least one fingerprint in your device's Settings",
                                Toast.LENGTH_LONG).show();
                        mImageViewFingerView.setVisibility(View.GONE);
                        mTextFingerText.setVisibility(View.GONE);
                        return;
                    }

                    //Check that the lockscreen is secured//
                    if (!mKeyguardManager.isKeyguardSecure()) {
                        // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                        Toast.makeText(EnterPinActivity.this, "Please enable lockscreen security in your device's Settings", Toast.LENGTH_LONG).show();
                        mImageViewFingerView.setVisibility(View.GONE);
                        mTextFingerText.setVisibility(View.GONE);
                        return;
                    } else {
                        try {
                            generateKey();
                            if (initCipher()) {
                                //If the mCipher is initialized successfully, then create a CryptoObject instance//
                                FingerprintManager.CryptoObject mCryptoObject = new FingerprintManager.CryptoObject(mCipher);

                                // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                                // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                                FingerprintHandler helper = new FingerprintHandler(this);
                                helper.startAuth(mFingerprintManager, mCryptoObject);
                                helper.setFingerPrintListener(fingerPrintListener);
                            }
                        } catch (FingerprintException e) {
                            Log.e(TAG, "Failed to generate key for fingerprint.", e);
                        }
                    }
                } else {
                    mImageViewFingerView.setVisibility(View.GONE);
                    mTextFingerText.setVisibility(View.GONE);
                }
            } else {
                mImageViewFingerView.setVisibility(View.GONE);
                mTextFingerText.setVisibility(View.GONE);
            }
        }
        else {
            mImageViewFingerView.setVisibility(View.GONE);
            mTextFingerText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_BACK_PRESSED);
        super.onBackPressed();
    }

    private static class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        Log.e("WHEN_TOCALL", when_toCall +  " ");
        if (when_toCall.equalsIgnoreCase("TherftAlarm")) {
            startBackgroundThread();
            if (textureView.isAvailable()) {
                openCamera();
            } else {
                textureView.setSurfaceTextureListener(textureListener);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lastImageNo = sh.getIntruderimagecount(EnterPinActivity.this);
                    takePicture();
                }
            }, 10000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause" + mTryCount );
        if (when_toCall.equalsIgnoreCase("TherftAlarm")) {
            closeCamera();
            stopBackgroundThread();
            if (mTryCount < 3){
                Log.e("DELETE " , mTryCount + " lastImageNo : " +lastImageNo + " IntruderCoun " + sh.getIntruderimagecount(EnterPinActivity.this));
                if(lastImageNo != 0 ) {
                    for (int i = lastImageNo; i <= sh.getIntruderimagecount(EnterPinActivity.this); i++) {
                        File myDir = new File(Environment.getExternalStorageDirectory() + "/.BATTERYALARM" + "/.intruder_images");
                        String fname = "SAL-inruder" + i + ".jpg";
                        File file = new File(myDir, fname);
                        Log.e("DELETE ", "filePath : " + file.getAbsolutePath());
                        new DBHelper(EnterPinActivity.this).deleteIntruder(file.getAbsolutePath());
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeCamera();
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.e(TAG , " open your camera here ");
            //open your camera here
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
            Log.e(TAG , " Transform you image captured size according to the surface width and height ");
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void takePicture() {

        sh.setIntruderimagecount(EnterPinActivity.this , sh.getIntruderimagecount(EnterPinActivity.this) + 1 );
        if(null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            jpegSizes = Objects.requireNonNull(characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(ImageFormat.JPEG);
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION , 270 );
            File myDir = new File(Environment.getExternalStorageDirectory() + "/.BATTERYALARM" + "/.intruder_images");
            if (!myDir.exists()){
                myDir.mkdirs();
            }
            String fname = "SAL-inruder" + sh.getIntruderimagecount(EnterPinActivity.this) + ".jpg";
            final File file = new File(myDir, fname);
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    try (Image image = reader.acquireLatestImage()) {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    try (OutputStream output = new FileOutputStream(file)) {
                        output.write(bytes);
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(EnterPinActivity.this, "Saved: " + file, Toast.LENGTH_SHORT).show();

                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd , yyyy" , Locale.getDefault());
                    SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss a" , Locale.getDefault());

                    IntruderData data = new IntruderData();
                    data.setIntruder_date(sdf.format(Calendar.getInstance().getTime()));
                    data.setIntruder_time(sdf1.format(Calendar.getInstance().getTime()));
                    data.setIntruder_path(file.getAbsolutePath());
                    new DBHelper(EnterPinActivity.this).addIntruderData(data);
                    new DBHelper(EnterPinActivity.this).closeDatabase();

                    createCameraPreview();

                    if (counPicture < 3) {
                        new CountDownTimer(1000, 1000) {
                            @Override
                            public void onFinish() {
                            }

                            @Override
                            public void onTick(long millisUntilFinished) {
                                Log.e("Counter in call", " jhgfjsdhsf" +  counPicture );
                                takePicture();
                                counPicture++;
                            }
                        }.start();
                    }
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(EnterPinActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            String cameraId = manager.getCameraIdList()[1];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EnterPinActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    protected void updatePreview() {
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }
}