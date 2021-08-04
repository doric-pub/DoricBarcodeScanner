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
package pub.doric.library;

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
            if (data == null) {
                data = new Intent();
            }
            int format = data.getIntExtra("format", 0);
            int result = data.getIntExtra("result", 1);
            String formatNote = data.getStringExtra("formatNote");
            String rawContent = data.getStringExtra("rawContent");
            promise.resolve(new JavaValue(new JSONBuilder()
                    .put("format", format)
                    .put("formatNote", formatNote)
                    .put("rawContent", rawContent)
                    .put("result", result)
                    .toJSONObject()));
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
