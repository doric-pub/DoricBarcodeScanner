#import "DoricBarcodeScannerPlugin.h"
#import <AVFoundation/AVFoundation.h>

@implementation DoricBarcodeScannerPlugin
- (void)numberOfCameras:(NSDictionary *)dic withPromise:(DoricPromise *)promise {
    NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    [promise resolve:@(devices.count)];
}

- (void)scan:(NSDictionary *)dic withPromise:(DoricPromise *)promise {

}
@end