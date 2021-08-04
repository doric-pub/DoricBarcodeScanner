import { BridgeContext } from "doric";

export enum BarcodeFormat {
  UNKNOWN = 0,
  /** Aztec 2D barcode format. */
  AZTEC = 1,
  /** Code 39 1D format. */
  CODE_39 = 2,
  /** Code 93 1D format. */
  CODE_93 = 3,
  /** EAN-8 1D format. */
  EAN_8 = 4,
  /** EAN-13 1D format. */
  EAN_13 = 5,
  /** Code 128 1D format. */
  CODE_128 = 6,
  /** Data Matrix 2D barcode format. */
  DATA_MATRIX = 7,
  /** QR Code 2D barcode format. */
  QR = 8,
  /** ITF (Interleaved Two of Five) 1D format. */
  ITF = 9,
  /** UPC-E 1D format. */
  UPC_E = 10,
  /** PDF417 format. */
  PDF_417 = 11,
}
export enum ScanResult {
  SUCCESS = 0,
  CANCELED = 1,
  PERMISSION_NOT_GRANTED = 2,
  NO_DATA = 3,
}

export function barcodeScanner(context: BridgeContext) {
  return {
    scan: (config?: {
      flashOnLabel?: string;
      flashOffLabel?: string;
      // Restricts the barcode format which should be read
      restrictFormat?: BarcodeFormat[];
      // Index of the camera which should used. -1 uses the default camera
      useCamera?: number;
      // Set to true to automatically enable flash on camera start
      autoEnableFlash?: boolean;
      // Android specific configuration
      androidConfig?: {
        // You can optionally set aspect ratio tolerance level
        // that is used in calculating the optimal Camera preview size.
        // On several Huawei devices you need to set this to 0.5.
        // This parameter is only supported on Android devices.
        aspectTolerance?: number;
        // Set to true to enable auto focus
        // This parameter is only supported on Android devices.
        useAutoFocus?: boolean;
      };
    }) => {
      return context.callNative("barcodeScanner", "scan", config) as Promise<{
        format: BarcodeFormat;
        formatNote: string;
        rawContent: string;
        result: ScanResult;
      }>;
    },
    numberOfCameras: () => {
      return context.callNative(
        "barcodeScanner",
        "numberOfCameras"
      ) as Promise<number>;
    },
  };
}
