'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

exports.BarcodeFormat = void 0;
(function (BarcodeFormat) {
    BarcodeFormat[BarcodeFormat["UNKNOWN"] = 0] = "UNKNOWN";
    /** Aztec 2D barcode format. */
    BarcodeFormat[BarcodeFormat["AZTEC"] = 1] = "AZTEC";
    /** Code 39 1D format. */
    BarcodeFormat[BarcodeFormat["CODE_39"] = 2] = "CODE_39";
    /** Code 93 1D format. */
    BarcodeFormat[BarcodeFormat["CODE_93"] = 3] = "CODE_93";
    /** EAN-8 1D format. */
    BarcodeFormat[BarcodeFormat["EAN_8"] = 4] = "EAN_8";
    /** EAN-13 1D format. */
    BarcodeFormat[BarcodeFormat["EAN_13"] = 5] = "EAN_13";
    /** Code 128 1D format. */
    BarcodeFormat[BarcodeFormat["CODE_128"] = 6] = "CODE_128";
    /** Data Matrix 2D barcode format. */
    BarcodeFormat[BarcodeFormat["DATA_MATRIX"] = 7] = "DATA_MATRIX";
    /** QR Code 2D barcode format. */
    BarcodeFormat[BarcodeFormat["QR"] = 8] = "QR";
    /** ITF (Interleaved Two of Five) 1D format. */
    BarcodeFormat[BarcodeFormat["ITF"] = 9] = "ITF";
    /** UPC-E 1D format. */
    BarcodeFormat[BarcodeFormat["UPC_E"] = 10] = "UPC_E";
    /** PDF417 format. */
    BarcodeFormat[BarcodeFormat["PDF_417"] = 11] = "PDF_417";
})(exports.BarcodeFormat || (exports.BarcodeFormat = {}));
exports.ScanResult = void 0;
(function (ScanResult) {
    ScanResult[ScanResult["SUCCESS"] = 0] = "SUCCESS";
    ScanResult[ScanResult["CANCELED"] = 1] = "CANCELED";
    ScanResult[ScanResult["PERMISSION_NOT_GRANTED"] = 2] = "PERMISSION_NOT_GRANTED";
    ScanResult[ScanResult["NO_DATA"] = 3] = "NO_DATA";
})(exports.ScanResult || (exports.ScanResult = {}));
function barcodeScanner(context) {
    return {
        scan: (config) => {
            return context.callNative("barcodeScanner", "scan", config);
        },
        numberOfCameras: () => {
            return context.callNative("barcodeScanner", "numberOfCameras");
        },
    };
}

exports.barcodeScanner = barcodeScanner;
//# sourceMappingURL=bundle_barcodescanner.js.map
