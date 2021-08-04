package pub.doric.library;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;

import com.github.pengfeizhou.jscore.JSONBuilder;
import com.github.pengfeizhou.jscore.JSValue;
import com.github.pengfeizhou.jscore.JavaValue;

import java.util.ArrayList;
import java.util.List;

import pub.doric.DoricContext;
import pub.doric.extension.bridge.DoricMethod;
import pub.doric.extension.bridge.DoricPlugin;
import pub.doric.extension.bridge.DoricPromise;
import pub.doric.library.scanner.BarcodeScannerActivity;
import pub.doric.plugin.DoricJavaPlugin;
import pub.doric.utils.ThreadMode;

@DoricPlugin(name = "barcodeScanner")
public class DoricBarcodeScannerPlugin extends DoricJavaPlugin {
    public DoricBarcodeScannerPlugin(DoricContext doricContext) {
        super(doricContext);
    }

    private DoricPromise promise;

    @DoricMethod(thread = ThreadMode.UI)
    public void scan(JSValue configValue, DoricPromise promise) {
        Intent intent = new Intent(getDoricContext().getContext(), BarcodeScannerActivity.class);
        Object obj = jsValueToBundle(configValue);
        if (obj instanceof Bundle) {
            intent.putExtra("CONFIG", (Bundle) obj);
        }
        getDoricContext().startActivityForResult(intent, 0x123);
        this.promise = promise;
    }

    @DoricMethod(thread = ThreadMode.UI)
    public void numberOfCameras(DoricPromise promise) {
        promise.resolve(new JavaValue(Camera.getNumberOfCameras()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x123) {
            if (resultCode == Activity.RESULT_OK) {
                int format = data.getIntExtra("format", 0);
                String formatNote = data.getStringExtra("formatNote");
                String rawContent = data.getStringExtra("rawContent");
                promise.resolve(new JavaValue(new JSONBuilder()
                        .put("format", format)
                        .put("formatNote", formatNote)
                        .put("rawContent", rawContent)
                        .toJSONObject()));
            } else {
                String error = "Scan canceled";
                if (data != null) {
                    error = data.getStringExtra("error");
                }
                promise.reject(new JavaValue(error));
            }
        }
    }

    private static Object jsValueToBundle(JSValue jsValue) {
        if (jsValue.isArray()) {
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < jsValue.asArray().size(); i++) {
                list.add(jsValueToBundle(jsValue.asArray().get(i)));
            }
            return list;
        } else if (jsValue.isObject()) {
            Bundle bundle = new Bundle();
            for (String key : jsValue.asObject().propertySet()) {
                JSValue value = jsValue.asObject().getProperty(key);
                Object object = jsValueToBundle(value);
                if (object instanceof String) {
                    bundle.putString(key, (String) object);
                } else if (object instanceof Double) {
                    bundle.putDouble(key, (Double) object);
                } else if (object instanceof Boolean) {
                    bundle.putBoolean(key, (Boolean) object);
                } else if (object instanceof Bundle) {
                    bundle.putBundle(key, bundle);
                } else if (object instanceof List<?>) {
                    List<?> list = (List<?>) object;
                    if (list.size() > 0) {
                        Object aObject = list.get(0);
                        if (aObject instanceof String) {
                            String[] strings = new String[list.size()];
                            for (int i = 0; i < list.size(); i++) {
                                strings[i] = (String) list.get(i);
                            }
                            bundle.putStringArray(key, strings);
                        } else if (aObject instanceof Double) {
                            double[] doubles = new double[list.size()];
                            for (int i = 0; i < list.size(); i++) {
                                doubles[i] = (Double) list.get(i);
                            }
                            bundle.putDoubleArray(key, doubles);
                        } else if (aObject instanceof Boolean) {
                            boolean[] booleans = new boolean[list.size()];
                            for (int i = 0; i < list.size(); i++) {
                                booleans[i] = (Boolean) list.get(i);
                            }
                            bundle.putBooleanArray(key, booleans);
                        }
                    }
                }
            }
            return bundle;
        } else if (jsValue.isString()) {
            return jsValue.asString().value();
        } else if (jsValue.isNumber()) {
            return jsValue.asNumber().value();
        } else if (jsValue.isBoolean()) {
            return jsValue.asBoolean().value();
        } else {
            return null;
        }
    }
}
