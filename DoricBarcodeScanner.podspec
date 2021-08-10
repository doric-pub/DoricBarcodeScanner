Pod::Spec.new do |s|
    s.name             = 'DoricBarcodeScanner'
    s.version          = '0.1.3'
    s.summary          = 'Doric library for scanning barcode'
  
    s.description      = <<-DESC
    This is a doric extension library for scanning barcode
                             DESC

    s.homepage         = 'https://github.com/doric-pub/DoricBarcodeScanner'
    s.license          = { :type => 'Apache-2.0', :file => 'LICENSE' }
    s.author           = { 'pengfei.zhou' => 'pengfeizhou@foxmail.com' }
    s.source           = { :git => 'https://github.com/doric-pub/DoricBarcodeScanner.git', :tag => s.version.to_s }
  
    s.ios.deployment_target = '9.0'
  
    s.source_files = 'iOS/Classes/**/*'
    s.resource     =  "dist/**/*"
    s.public_header_files = 'iOS/Classes/**/*.h'
    s.dependency 'DoricCore'
    s.dependency 'MTBBarcodeScanner'
end
