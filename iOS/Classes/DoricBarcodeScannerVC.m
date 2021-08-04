//
//  DoricBarcodeScannerVC.m
//  DoricBarcodeScanner
//
//  Created by pengfei.zhou on 2021/8/4.
//

#import "DoricBarcodeScannerVC.h"

@interface DoricScannerOverlay : UIView
@property(nonatomic, strong) UIView *line;
@property(nonatomic, assign) CGRect scanLineRect;

- (void)startAnimating;

- (void)stopAnimating;
@end

@implementation DoricScannerOverlay
- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        _line = [UIView new];
        _line.backgroundColor = [UIColor redColor];
        _line.translatesAutoresizingMaskIntoConstraints = NO;
        [self addSubview:_line];
    }
    return self;
}

- (CGRect)scanLineRect {
    if (CGRectIsEmpty(_scanLineRect)) {
        CGRect scanRect = [self calculateScanRect];
        CGFloat positionY = scanRect.origin.y + (scanRect.size.height / 2);
        return CGRectMake(scanRect.origin.x, positionY, scanRect.size.width, 1);
    }
    return _scanLineRect;
}

- (CGRect)calculateScanRect {
    CGRect rect = self.frame;
    CGFloat frameWidth = rect.size.width;
    CGFloat frameHeight = rect.size.height;
    BOOL isLandscape = frameWidth > frameHeight;
    CGFloat widthOnPortrait = isLandscape ? frameHeight : frameWidth;
    CGFloat scanRectWidth = widthOnPortrait * 0.8;
    CGFloat aspectRatio = 3.0 / 4.0;
    CGFloat scanRectHeight = scanRectWidth * aspectRatio;
    if (isLandscape) {
        frameHeight += [[UIApplication sharedApplication] statusBarFrame].size.height;
    }
    CGFloat scanRectOriginX = (frameWidth - scanRectWidth) / 2;
    CGFloat scanRectOriginY = (frameHeight - scanRectHeight) / 2;
    return CGRectMake(scanRectOriginX, scanRectOriginY, scanRectWidth, scanRectHeight);
}

- (void)drawRect:(CGRect)rect {
    CGContextRef context = UIGraphicsGetCurrentContext();
    UIColor *overlayColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.55];
    CGContextSetFillColorWithColor(context, overlayColor.CGColor);
    CGContextFillRect(context, self.bounds);
    CGRect holeRect = [self calculateScanRect];
    CGRect holeRectIntersection = CGRectIntersection(holeRect, rect);
    [UIColor.clearColor setFill];
    UIRectFill(holeRectIntersection);

    CGRect lineRect = self.scanLineRect;
    self.line.frame = lineRect;
    CGFloat cornerSize = 30;
    UIBezierPath *path = [UIBezierPath new];

    [path moveToPoint:CGPointMake(holeRect.origin.x, holeRect.origin.y + cornerSize)];
    [path addLineToPoint:CGPointMake(holeRect.origin.x, holeRect.origin.y)];
    [path addLineToPoint:CGPointMake(holeRect.origin.x + cornerSize, holeRect.origin.y)];

    CGFloat rightHoleX = holeRect.origin.x + holeRect.size.width;
    [path moveToPoint:CGPointMake(rightHoleX - cornerSize, holeRect.origin.y)];
    [path addLineToPoint:CGPointMake(rightHoleX, holeRect.origin.y)];
    [path addLineToPoint:CGPointMake(rightHoleX, holeRect.origin.y + cornerSize)];

    CGFloat bottomHoleY = holeRect.origin.y + holeRect.size.height;
    [path moveToPoint:CGPointMake(rightHoleX, bottomHoleY - cornerSize)];
    [path addLineToPoint:CGPointMake(rightHoleX, bottomHoleY)];
    [path addLineToPoint:CGPointMake(rightHoleX - cornerSize, bottomHoleY)];

    [path moveToPoint:CGPointMake(holeRect.origin.x + cornerSize, bottomHoleY)];
    [path addLineToPoint:CGPointMake(holeRect.origin.x, bottomHoleY)];
    [path addLineToPoint:CGPointMake(holeRect.origin.x, bottomHoleY - cornerSize)];

    path.lineWidth = 2;
    [UIColor.greenColor setStroke];
    [path stroke];
}


- (void)startAnimating {
    [self.layer removeAnimationForKey:@"flashAnimation"];
    CABasicAnimation *flash = [CABasicAnimation new];
    flash.keyPath = @"opacity";
    flash.fromValue = @(0);
    flash.toValue = @(1);
    flash.duration = 0.25;
    flash.autoreverses = true;
    flash.repeatCount = HUGE;
    [self.line.layer addAnimation:flash forKey:@"flashAnimation"];
}

- (void)stopAnimating {
    [self.layer removeAnimationForKey:@"flashAnimation"];
}

@end

@interface DoricBarcodeScannerVC ()
@property(nonatomic, strong) DoricScannerOverlay *scanRect;
@end

@implementation DoricBarcodeScannerVC

- (void)viewDidLoad {
    [super viewDidLoad];
    UIView *previewView = [[UIView alloc] initWithFrame:self.view.frame];
    previewView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    [self.view addSubview:previewView];
    [self setupScanRect:self.view.bounds];
    if (self.restrictedBarcodeTypes.count > 0) {
        self.scanner = [[MTBBarcodeScanner alloc] initWithMetadataObjectTypes:self.restrictedBarcodeTypes
                                                                  previewView:previewView];
    } else {
        self.scanner = [[MTBBarcodeScanner alloc] initWithPreviewView:previewView];
    }

}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    if (self.scanner.isScanning) {
        [self.scanner stopScanning];
    }
    [self.scanRect startAnimating];
    [MTBBarcodeScanner requestCameraPermissionWithSuccess:^(BOOL success) {
        if (success) {
            [self startScan];
        } else {
            if (self.errorCallback) {
                self.errorCallback(2);
            }
        }
    }];
}

- (void)viewWillDisappear:(BOOL)animated {
    [self.scanRect stopAnimating];
    [self.scanner stopScanning];
    if (self.isFlashOn) {
        [self setFlashState:NO];
    }
    [super viewWillDisappear:animated];
}

- (void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id)coordinator {
    [super viewWillTransitionToSize:size withTransitionCoordinator:coordinator];
    [self setupScanRect:CGRectMake(0, 0, size.width, size.height)];
}


- (void)startScan {
    NSError *error;
    [self.scanner startScanningWithCamera:self.cameraInUse resultBlock:self.resultBlock error:&error];
    if (error) {
        if (self.errorCallback) {
            self.errorCallback(4);
        }
        return;
    }
    if (self.autoEnableFlash) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t) (0.15 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self setFlashState:YES];
        });
    }
}

- (void)cancel {
    if (self.errorCallback) {
        self.errorCallback(1);
    }
}

- (AVCaptureDevice *)device {
    return [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
}

- (BOOL)isFlashOn {
    return self.device.flashMode == AVCaptureFlashModeOn || self.device.torchMode == AVCaptureTorchModeOn;
}

- (BOOL)hasTorch {
    return self.device.hasTorch;
}

- (void)setFlashState:(BOOL)on {
    if (!self.hasTorch) {
        return;
    }
    NSError *error;
    [self.device lockForConfiguration:&error];
    if (error) {
        return;
    }
    self.device.flashMode = on ? AVCaptureFlashModeOn : AVCaptureFlashModeOff;
    self.device.torchMode = on ? AVCaptureTorchModeOn : AVCaptureTorchModeOff;
    [self.device unlockForConfiguration];
    [self updateToggleFlashButton];
}

- (void)onToggleFlash {
    [self setFlashState:!self.isFlashOn];
}

- (MTBCamera)cameraInUse {
    return self.useCamera == 1 ? MTBCameraFront : MTBCameraBack;
}

- (void)updateToggleFlashButton {
    if (!self.hasTorch) {
        return;
    }
    NSString *buttonText = self.isFlashOn ? (self.flashOnLabel ?: @"Flash on") : (self.flashOffLabel ?: @"Flash off");
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:buttonText
                                                                              style:UIBarButtonItemStylePlain
                                                                             target:self
                                                                             action:@selector(onToggleFlash)];
}

- (void)setupScanRect:(CGRect)bounds {
    [self.scanRect stopAnimating];
    [self.scanRect removeFromSuperview];
    self.scanRect = [[DoricScannerOverlay alloc] initWithFrame:bounds];
    self.scanRect.translatesAutoresizingMaskIntoConstraints = NO;
    self.scanRect.backgroundColor = UIColor.clearColor;
    [self.view addSubview:self.scanRect];
    [self.scanRect startAnimating];
}

- (void)dealloc {
    if (self.errorCallback) {
        self.errorCallback(1);
    }
}
@end
