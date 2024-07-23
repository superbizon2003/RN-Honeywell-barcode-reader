package com.duytq94.HoneywellBarcodeReader;

import java.lang.reflect.Method;
import java.util.Set;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import static com.duytq94.HoneywellBarcodeReader.HoneywellBarcodeReaderPackage.TAG;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerUnavailableException;

@SuppressWarnings("unused")
public class HoneywellBarcodeReaderModule extends ReactContextBaseJavaModule implements BarcodeReader.BarcodeListener {

    // Debugging
    private static final boolean D = true;

    private static BarcodeReader barcodeReader;
    private AidcManager manager;
    private BarcodeReader reader;
    private ReactApplicationContext mReactContext;

    private static final String BARCODE_READ_SUCCESS = "barcodeReadSuccess";
    private static final String BARCODE_READ_FAIL = "barcodeReadFail";

    public HoneywellBarcodeReaderModule(ReactApplicationContext reactContext) {
        super(reactContext);

        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "HoneywellBarcodeReader";
    }

    /**
     * Send event to javascript
     * @param eventName Name of the event
     * @param params Additional params
     */
    private void sendEvent(String eventName, @Nullable WritableMap params) {
        if (mReactContext.hasActiveCatalystInstance()) {
            if (D) Log.d(TAG, "Sending event: " + eventName);
            mReactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
        }
    }

    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
        if (D) Log.d(TAG, "HoneywellBarcodeReader - Barcode scan read");
        WritableMap params = Arguments.createMap();
        params.putString("data", barcodeReadEvent.getBarcodeData());
        sendEvent(BARCODE_READ_SUCCESS, params);
    }

    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        if (D) Log.d(TAG, "HoneywellBarcodeReader - Barcode scan failed");
        sendEvent(BARCODE_READ_FAIL, null);
    }

    /*******************************/
    /** Methods Available from JS **/
    /*******************************/

    @ReactMethod
    public void startReader(final Promise promise) {
        AidcManager.create(mReactContext, new CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                reader = manager.createBarcodeReader();
                if(reader != null){
                    try {
                        reader.addBarcodeListener(HoneywellBarcodeReaderModule.this);
                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.PROPERTY_EAN_13_ENABLED, true);
                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.PROPERTY_EAN_8_CHECK_DIGIT_TRANSMIT_ENABLED, true);
                        
                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.PROPERTY_EAN_13_ENABLED, true);
                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED, true);
                        
                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.PROPERTY_UPC_A_ENABLE, true);
                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.PROPERTY_UPC_A_CHECK_DIGIT_TRANSMIT_ENABLED, true);
                        
                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.PROPERTY_UPC_E_ENABLED, true);
                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.PROPERTY_UPC_E_CHECK_DIGIT_TRANSMIT_ENABLED, true);

                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.PROPERTY_CODABAR_ENABLED, true);
                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.CODABAR_CHECK_DIGIT_MODE_CHECK, true);
                        
                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.PROPERTY_INTERLEAVED_25_ENABLED, true);
                        HoneywellBarcodeReaderModule.this.setReaderProprety(reader.INTERLEAVED_25_CHECK_DIGIT_MODE_CHECK, true);
                        
                        reader.claim();
                        promise.resolve(true);
                    } catch (ScannerUnavailableException e) {
                        promise.resolve(false);
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @ReactMethod
    public void stopReader(Promise promise) {
        if (reader != null) {
            try {
                reader.release();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (manager != null) {
            try {
                manager.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void setReaderProprety(String propName, int value)  {
        try{
            HoneywellBarcodeReaderModule.this.setReaderProprety(propName , value);
        }catch(Exception e){

        }
    }

    @ReactMethod
    public void setReaderProprety(String propName, boolean value)  {
        try{
            reader.setProperty(propName , value);
        }catch(Exception e){

        }
    }

    @ReactMethod
    public void setReaderProprety(String propName, String value)  {
        try{
            reader.setProperty(propName , value);
        }catch(Exception e){

        }
    }

    private boolean isCompatible() {
        // This... is not optimal. Need to find a better way to performantly check whether device has a Honeywell scanner 
        return Build.BRAND.toLowerCase().contains("honeywell");
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("BARCODE_READ_SUCCESS", BARCODE_READ_SUCCESS);
        constants.put("BARCODE_READ_FAIL", BARCODE_READ_FAIL);
        constants.put("isCompatible", isCompatible());
        return constants;
    }

}
