#!/usr/bin/env bash
##############################################################################
##
##  Publish JS,Android,iOS
##
##############################################################################

CURRENT_DIR=$(cd $(dirname $0); pwd)
CURRENT_VERSION=$(cat $CURRENT_DIR/version)

echo "Current version is "$CURRENT_VERSION

## JS
cd $CURRENT_DIR && npm version $CURRENT_VERSION --allow-same-version

## Android
echo "version="$CURRENT_VERSION > $CURRENT_DIR/doric-android/version.properties

## iOS
sed -i "" "s/\(version[ ]*= \)'[0-9 \.]*'/\1'$CURRENT_VERSION'/g" $CURRENT_DIR/DoricBarcodeScanner.podspec

# git save
cd $CURRENT_DIR/

echo "Commit changes"
git add .
git commit -m "Release v${CURRENT_VERSION}"

git tag ${CURRENT_VERSION}

git push 

git push --tags

echo "Publish JS"
cd $CURRENT_DIR/ && npm publish 
echo "Publish Android"
cd $CURRENT_DIR/example/android && ./gradlew clean publishAll 
echo "Publish iOS"
cd $CURRENT_DIR && pod trunk push DoricBarcodeScanner.podspec --allow-warnings