//
//  DoricBarcodeScannerVC.h
//  DoricBarcodeScanner
//
//  Created by pengfei.zhou on 2021/8/4.
//

#import <Foundation/Foundation.h>
#import <MTBBarcodeScanner/MTBBarcodeScanner.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoricBarcodeScannerVC : UIViewController
@property(nonatomic, strong) MTBBarcodeScanner *scanner;
@property(nonatomic, copy) NSArray <NSString *> *restrictedBarcodeTypes;
@property(nonatomic, copy) NSString *flashOnLabel;
@property(nonatomic, copy) NSString *flashOffLabel;
@property(nonatomic, assign) NSUInteger useCamera;
@property(nonatomic, copy) void (^errorCallback)(NSUInteger errorType);
@property(nonatomic, copy) void (^resultBlock)(NSArray<AVMetadataMachineReadableCodeObject *> *codes);
@property(nonatomic, assign) BOOL autoEnableFlash;
@end

NS_ASSUME_NONNULL_END
