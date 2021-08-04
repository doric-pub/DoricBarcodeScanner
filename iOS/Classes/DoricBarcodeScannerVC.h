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
@end


NS_ASSUME_NONNULL_END
