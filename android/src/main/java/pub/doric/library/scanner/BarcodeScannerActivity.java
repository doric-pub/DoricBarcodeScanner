/*
 * Copyright [2021] [Doric.Pub]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pub.doric.library.scanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.google.zxing.BarcodeFormat.*;

public class BarcodeScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView zXingScannerView;
    private static final int TOGGLE_FLASH = 200;
    private static final int REQUEST_TAKE_PHOTO_CAMERA_PERMISSION = 100;
    private String flashOnLabel = "Flash on";
    private String flashOffLabel = "Flash off";
    private final List<BarcodeFormat> restrictFormat = new ArrayList<>();
    private int useCamera = -1;
    private boolean autoEnableFlash = false;
    private float aspectTolerance = -1;
    private boolean useAutoFocus = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle configBundle = getIntent().getBundleExtra("CONFIG");
        if (configBundle != null) {
            flashOnLabel = configBundle.getString("flashOnLabel", "Flash on");
            flashOffLabel = configBundle.getString("flashOffLabel", "Flash off");
            double[] values = configBundle.getDoubleArray("restrictFormat");
            if (values != null && values.length > 0) {
                for (double val : values) {
                    BarcodeFormat barcodeFormat = intToBarcodeFormat((int) val);
                    if (barcodeFormat != null) {
                        restrictFormat.add(barcodeFormat);
                    }
                }
            }
            useCamera = (int) configBundle.getDouble("useCamera", -1);
            autoEnableFlash = configBundle.getBoolean("autoEnableFlash", false);
            Bundle androidConfig = configBundle.getBundle("androidConfig");
            if (androidConfig != null) {
                aspectTolerance = (float) androidConfig.getDouble("aspectTolerance", -1);
                useAutoFocus = androidConfig.getBoolean("useAutoFocus", false);
            }
        }
        zXingScannerView = new ZXingScannerView(this);
        zXingScannerView.setAutoFocus(useAutoFocus);

        if (restrictFormat.size() > 0) {
            zXingScannerView.setFormats(restrictFormat);
        }

        if (aspectTolerance >= 0) {
            zXingScannerView.setAspectTolerance(aspectTolerance);
        }

        if (autoEnableFlash) {
            zXingScannerView.setFlash(true);
            invalidateOptionsMenu();
        }

        setContentView(zXingScannerView);
    }

    @Override
    public void finish() {
        super.finish();
    }

    private BarcodeFormat intToBarcodeFormat(int v) {
        switch (v) {
            case 1:
                return AZTEC;
            case 2:
                return CODE_39;
            case 3:
                return CODE_93;
            case 4:
                return EAN_8;
            case 5:
                return EAN_13;
            case 6:
                return CODE_128;
            case 7:
                return DATA_MATRIX;
            case 8:
                return QR_CODE;
            case 9:
                return ITF;
            case 10:
                return UPC_E;
            case 11:
                return PDF_417;
            default:
                return null;
        }
    }

    private int barcodeFormatToInt(BarcodeFormat v) {
        switch (v) {
            case AZTEC:
                return 1;
            case CODE_39:
                return 2;
            case CODE_93:
                return 3;
            case EAN_8:
                return 4;
            case EAN_13:
                return 5;
            case CODE_128:
                return 6;
            case DATA_MATRIX:
                return 7;
            case QR_CODE:
                return 8;
            case ITF:
                return 9;
            case UPC_E:
                return 10;
            case PDF_417:
                return 11;
            default:
                return 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (zXingScannerView.getFlash()) {
            MenuItem item = menu.add(0,
                    TOGGLE_FLASH, 0, flashOffLabel);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            MenuItem item = menu.add(0,
                    TOGGLE_FLASH, 0, flashOnLabel);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == TOGGLE_FLASH) {
            zXingScannerView.setFlash(!zXingScannerView.getFlash());
            this.invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this);
        if (!requestCameraAccessIfNecessary()) {
            if (useCamera >= 0) {
                zXingScannerView.startCamera(useCamera);
            } else {
                zXingScannerView.startCamera();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        if (rawResult == null) {
            finishWithError(3);
        } else {
            Intent intent = new Intent();
            intent.putExtra("format", barcodeFormatToInt(rawResult.getBarcodeFormat()));
            intent.putExtra("formatNote", rawResult.getBarcodeFormat().toString());
            intent.putExtra("rawContent", rawResult.getText());
            intent.putExtra("result", 0);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void finishWithError(int errorCode) {
        Intent intent = new Intent();
        intent.putExtra("result", errorCode);
        intent.putExtra("format", 0);
        intent.putExtra("formatNote", "Unknown");
        intent.putExtra("rawContent", "");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private boolean requestCameraAccessIfNecessary() {
        String[] array = new String[]{Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, array, REQUEST_TAKE_PHOTO_CAMERA_PERMISSION);
            return true;
        }
        return false;
    }

    private boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_TAKE_PHOTO_CAMERA_PERMISSION) {
            if (verifyPermissions(grantResults)) {
                zXingScannerView.startCamera();
            } else {
                finishWithError(2);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
