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

import java.io.IOException;
import java.io.InputStream;

import pub.doric.Doric;
import pub.doric.DoricComponent;
import pub.doric.DoricLibrary;
import pub.doric.DoricRegistry;

@DoricComponent
public class DoricBarcodeScannerLibrary extends DoricLibrary {
    @Override
    public void load(DoricRegistry registry) {
        try {
            InputStream is = Doric.application().getAssets().open("bundle_barcodescanner.js");
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            String content = new String(bytes);
            registry.registerJSBundle("barcodescanner", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        registry.registerNativePlugin(DoricBarcodeScannerPlugin.class);
    }
}
