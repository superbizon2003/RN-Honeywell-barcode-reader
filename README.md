# Это форк проекта https://github.com/BishoSBA/RN-Honeywell-barcode-reader

Проверил на Expo 53, получилось заставить работать на устройстве Honeywell EDA50K

# React Native Honeywell Barcode Reader

This package works with Honeywell devices that have an integrated barcode scanner, like the Honeywell EDA50K and EDA51 (tested).

This version fixed the event will fire twice.

This version is a fork from https://github.com/duytq94/react-native-honeywell-barcode-reader

## Installation

```
npm install superbizon2003/RN-Honeywell-barcode-reader#main
```

## Link automatically:

```
react-native link bishosba_honeywell-barcode-reader
```

## Link manually (recommend):

1. In `app\build.gradle` add

```
implementation project(':bishosba_honeywell-barcode-reader')
```

2. In `settings.gradle` add

```
include ':bishosba_honeywell-barcode-reader'
project(':bishosba_honeywell-barcode-reader').projectDir = new File(rootProject.projectDir, '../node_modules/bishosba_honeywell-barcode-reader/android')
```

3. In `MainApplication.java`

Add this line to import package

```
import com.duytq94.HoneywellBarcodeReader.HoneywellBarcodeReaderPackage;
```

and add this line to getPackages()

```
new HoneywellBarcodeReaderPackage()
```

## Usage

First you'll want to check whether the device is a Honeywell scanner:

```js
import HoneywellBarcodeReader from "bishosba_honeywell-barcode-reader";

HoneywellBarcodeReader.isCompatible; // true or false
```

The barcode reader needs to be "claimed" by your application; meanwhile no other application can use it. You can do that like this:

```js
HoneywellBarcodeReader.startReader().then((claimed) => {
  console.log(claimed ? "Barcode reader is claimed" : "Barcode reader is busy");
});
```

To get events from the barcode scanner:

```js
HoneywellBarcodeReader.onBarcodeReadSuccess((event) => {
  console.log("Received data", event);
});

HoneywellBarcodeReader.onBarcodeReadFail(() => {
  console.log("Barcode read failed");
});
```

To free the claim and stop the reader, also freeing up resources:

```js
HoneywellBarcodeReader.stopReader().then(() => {
  console.log("Freedom!");
});
```

To stop receiving events:

```js
HoneywellBarcodeReader.offBarcodeReadSuccess();
HoneywellBarcodeReader.offBarcodeReadFail();
```
