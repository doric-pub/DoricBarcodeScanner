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
        vc.autoEnableFlash = [dic[@""] autoEnableFlash];
        vc.flashOnLabel = dic[@"flashOnLabel"];
        vc.flashOffLabel = dic[@"flashOffLabel"];
        vc.useCamera = [dic[@"useCamera"] unsignedIntValue];
        NSMutableArray *types = [NSMutableArray new];
        NSArray<NSNumber *> *nums = dic[@"restrictFormat"];
        if (nums.count > 0) {
            [nums enumerateObjectsUsingBlock:^(NSNumber *obj, NSUInteger idx, BOOL *stop) {
                [types addObject:[self intToFormat:[obj unsignedShortValue]]];
            }];
        }
        vc.restrictedBarcodeTypes = types;
        __block BOOL called = NO;
        __weak typeof(self) _self = self;
        vc.resultBlock = ^(NSArray<AVMetadataMachineReadableCodeObject *> *codes) {
            __strong typeof(_self) self = _self;
            AVMetadataMachineReadableCodeObject *code = codes.firstObject;
            called = YES;
            [promise resolve:@{
                    @"result": @(0),
                    @"format": @([self formatToInt:code.type]),
                    @"formatNote": code.type,
                    @"rawContent": code.stringValue,
            }];
            [self.doricContext.vc.navigationController popViewControllerAnimated:YES];
        };
        vc.errorCallback = ^(NSUInteger errorType) {
            if (called) {
                [promise resolve:@{
                        @"result": @(errorType),
                        @"format": @(0),
                        @"formatNote": @"Unknown",
                        @"rawContent": @"",
                }];
                return;
            }
            __strong typeof(_self) self = _self;
            called = YES;
            [promise resolve:@{
                    @"result": @(errorType),
                    @"format": @(0),
                    @"formatNote": @"Unknown",
                    @"rawContent": @"",
            }];
            [self.doricContext.vc.navigationController popViewControllerAnimated:YES];
        };
        [self.doricContext.vc.navigationController pushViewController:vc animated:YES];
    });
}

- (NSUInteger)formatToInt:(AVMetadataObjectType)type {
    if ([type isEqualToString:AVMetadataObjectTypeAztecCode]) {
        return 1;
    } else if ([type isEqualToString:AVMetadataObjectTypeCode39Code]) {
        return 2;
    } else if ([type isEqualToString:AVMetadataObjectTypeCode93Code]) {
        return 3;
    } else if ([type isEqualToString:AVMetadataObjectTypeEAN8Code]) {
        return 4;
    } else if ([type isEqualToString:AVMetadataObjectTypeEAN13Code]) {
        return 5;
    } else if ([type isEqualToString:AVMetadataObjectTypeCode128Code]) {
        return 6;
    } else if ([type isEqualToString:AVMetadataObjectTypeDataMatrixCode]) {
        return 7;
    } else if ([type isEqualToString:AVMetadataObjectTypeQRCode]) {
        return 8;
    } else if ([type isEqualToString:AVMetadataObjectTypeITF14Code]) {
        return 9;
    } else if ([type isEqualToString:AVMetadataObjectTypeUPCECode]) {
        return 10;
    } else if ([type isEqualToString:AVMetadataObjectTypePDF417Code]) {
        return 11;
    } else {
        return 0;
    }
}

- (AVMetadataObjectType)intToFormat:(NSUInteger)val {
    switch (val) {
        case 1:
            return AVMetadataObjectTypeAztecCode;
        case 2:
            return AVMetadataObjectTypeCode39Code;
        case 3:
            return AVMetadataObjectTypeCode93Code;
        case 4:
            return AVMetadataObjectTypeEAN8Code;
        case 5:
            return AVMetadataObjectTypeEAN13Code;
        case 6:
            return AVMetadataObjectTypeCode128Code;
        case 7:
            return AVMetadataObjectTypeDataMatrixCode;
        case 8:
            return AVMetadataObjectTypeQRCode;
        case 9:
            return AVMetadataObjectTypeITF14Code;
        case 10:
            return AVMetadataObjectTypeUPCECode;
        case 11:
            return AVMetadataObjectTypePDF417Code;
        default:
            return AVMetadataObjectTypeQRCode;
    }
}
@end