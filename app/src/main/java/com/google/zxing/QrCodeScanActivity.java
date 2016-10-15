
package com.google.zxing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.zxing.camera.CameraManager;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.decoding.CaptureActivityHandler;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import cn.darkal.networkdiagnosis.R;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class QrCodeScanActivity extends Activity implements SurfaceHolder.Callback {

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 1.00f;
    private boolean vibrate;
    private static final int REQUEST_CODE_GALLERY = 0x0708;

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scan);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    private void vibrate(long duration) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {
                0, duration
        };
        vibrator.vibrate(pattern, -1);
    }

    private int getCurrentOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            default:
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        handler = null;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        viewfinderView.setVisibility(View.VISIBLE);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            surfaceHolder.addCallback(this);
        }

        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(Result result, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String url = result.getText();

        // 只识别http或者https开头的url
        if ((url.startsWith("http://") == false) && (url.startsWith("https://") == false)) {
            Toast.makeText(this, "未发现二维码", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("jdhttpmonitor://webview?param=" + Uri.encode("{\"url\":\"" + url + "\"" + ", \"from\":\"app\"" + "}")));
            startActivity(intent);
        }

        // 不管成功还是失败，都关闭二维码扫描窗口
        finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }

        if (cameraManager.isOpen()) {
            Log.w(QrCodeScanActivity.class.getSimpleName(),
                    "initCamera() while already open -- late SurfaceView callback?");
            return;
        }

        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                Map<DecodeHintType, Object> decodeHints = new EnumMap<DecodeHintType, Object>(
                        DecodeHintType.class);
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints,
                        characterSet, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(QrCodeScanActivity.class.getSimpleName(), ioe);
            return;
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(QrCodeScanActivity.class.getSimpleName(), "Unexpected error initializing camera",
                    e);
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(QrCodeScanActivity.class.getSimpleName(),
                    "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.qrcode_completed);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public void onGalleryClick(View view) {
        // 设定action和miniType
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 以需要返回值的模式开启一个Activity
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                if (resultCode == -1) {
                    try {
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                this.getContentResolver(), uri);

                        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
                        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
                                bitmap.getHeight());

                        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(
                                bitmap.getWidth(), bitmap.getHeight(), pixels);
                        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                                rgbLuminanceSource));
                        QRCodeReader qrCodeReader = new QRCodeReader();
                        HashMap<DecodeHintType, String> decodeHintTypeStringHashMap = new HashMap<DecodeHintType, String>();
                        decodeHintTypeStringHashMap.put(DecodeHintType.CHARACTER_SET, "utf-8");
                        Result result = qrCodeReader.decode(binaryBitmap,
                                decodeHintTypeStringHashMap);
                        String url = result.getText();
                        playBeepSoundAndVibrate();

                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setData(Uri.parse("jdhttpmonitor://webview?param=" + Uri.encode("{\"url\":\"" + url + "\"" + ", \"from\":\"app\"" + "}")));
                        startActivity(intent);

                        // 启动二级页之后，关闭二维码扫描窗口
                        finish();

                    } catch (com.google.zxing.NotFoundException e) {
                        Toast.makeText(this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    } catch (com.google.zxing.ChecksumException e) {
                        Toast.makeText(this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    } catch (com.google.zxing.FormatException e) {
                        Toast.makeText(this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
