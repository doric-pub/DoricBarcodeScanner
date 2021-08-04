#import "DoricBarcodeScannerLibrary.h"
#import "DoricBarcodeScannerPlugin.h"

@implementation DoricBarcodeScannerLibrary
- (void)load:(DoricRegistry *)registry {
    NSString *path = [[NSBundle mainBundle] bundlePath];
    NSString *fullPath = [path stringByAppendingPathComponent:@"bundle_barcodescanner.js"];
    NSString *jsContent = [NSString stringWithContentsOfFile:fullPath encoding:NSUTF8StringEncoding error:nil];
    [registry registerJSBundle:jsContent withName:@"doric-barcodescanner"];
    [registry registerNativePlugin:DoricBarcodeScannerPlugin.class withName:@"barcodeScanner"];
}
@end