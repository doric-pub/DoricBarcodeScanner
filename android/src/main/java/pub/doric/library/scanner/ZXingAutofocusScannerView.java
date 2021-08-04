package pub.doric.library.scanner;

import android.content.Context;
import android.hardware.Camera;

import me.dm7.barcodescanner.core.CameraWrapper;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ZXingAutofocusScannerView extends ZXingScannerView {
    private boolean autofocusPresence = false;
    private boolean callbackFocus = false;

    public ZXingAutofocusScannerView(Context context) {
        super(context);
    }

    @Override
    public void setupCameraPreview(CameraWrapper cameraWrapper) {
        if (cameraWrapper != null && cameraWrapper.mCamera != null && cameraWrapper.mCamera.getParameters() != null) {
            try {
                Camera.Parameters parameters = cameraWrapper.mCamera.getParameters();
                autofocusPresence = parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                cameraWrapper.mCamera.setParameters(parameters);
            } catch (Exception e) {
                callbackFocus = true;
            }
        }
        super.setupCameraPreview(cameraWrapper);
    }

    @Override
    public void setAutoFocus(boolean state) {
        if (autofocusPresence) {
            super.setAutoFocus(callbackFocus);
        }
    }
}
