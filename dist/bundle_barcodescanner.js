'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

function barcodeScanner(context) {
    return {
        scan: () => {
            return context.callNative("barcodeScanner", "scan");
        },
    };
}

exports.barcodeScanner = barcodeScanner;
//# sourceMappingURL=bundle_barcodescanner.js.map
