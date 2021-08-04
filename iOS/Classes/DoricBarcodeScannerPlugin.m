#import "DoricBarcodeScannerPlugin.h"
#import <AVFoundation/AVFoundation.h>
#import "DoricBarcodeScannerVC.h"

@implementation DoricBarcodeScannerPlugin
- (void)numberOfCameras:(NSDictionary *)dic withPromise:(DoricPromise *)promise {
    NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    [promise resolve:@(devices.count)];
}

- (void)scan:(NSDictionary *)dic withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_main_queue(), ^{
        DoricBarcodeScannerVC *vc = [DoricBarcodeScannerVC new];
        __weak typeof(self) _self = self;
        vc.scanner.resultBlock = ^(NSArray<AVMetadataMachineReadableCodeObject *> *codes) {
            __strong typeof(_self) self = _self;



            [self.doricContext.vc.navigationController popViewControllerAnimated:YES];
        };
        [self.doricContext.vc.navigationController pushViewController:vc animated:YES];
    });
}
@end