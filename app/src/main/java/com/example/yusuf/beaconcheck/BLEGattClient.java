package com.example.yusuf.beaconcheck;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.provider.SyncStateContract;
import android.util.Log;

import java.util.UUID;

public class BLEGattClient {

    Context context;
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeAdvertiser bluetoothLeAdvertiser;
    Handler mHandler;

    public BLEGattClient(Context context){
        this.context = context;
        this.bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = this.bluetoothManager.getAdapter();
        this.bluetoothLeAdvertiser = this.bluetoothAdapter.getBluetoothLeAdvertiser();
        this.mHandler = new Handler();
    }

    public void run(String courseId){
        Log.d("BLE_Advertising", "Thread started running.");
        AdvertiseSettings settings = buildAdvertiseSettings();
        AdvertiseData data = buildAdvertiseData(courseId);
        AdvertiseCallback advertiseCallback = new SampleAdvertiseCallback();
        bluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback);
    }

    public AdvertiseSettings buildAdvertiseSettings() {
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        settingsBuilder.setTimeout(0);
        return settingsBuilder.build();
    }

    private AdvertiseData buildAdvertiseData(String courseId) {

        /**
         * Note: There is a strict limit of 31 Bytes on packets sent over BLE Advertisements.
         *  This includes everything put into AdvertiseData including UUIDs, device info, &
         *  arbitrary service or manufacturer data.
         *  Attempting to send packets over this limit will result in a failure with error code
         *  AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE. Catch this error in the
         *  onStartFailure() method of an AdvertiseCallback implementation.
         */

        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.addServiceUuid(new ParcelUuid(UUID.randomUUID()));
        dataBuilder.setIncludeDeviceName(true);

        /* For example - this will cause advertising to fail (exceeds size limit) */
        //String failureData = "asdghkajsghalkxcjhfa;sghtalksjcfhalskfjhasldkjfhdskf";

        return dataBuilder.build();
    }

    private class SampleAdvertiseCallback extends AdvertiseCallback {

        @Override
        public void onStartFailure(int errorCode) {
            Log.d("BLE_Advertising", "Advertising failed to start");

            super.onStartFailure(errorCode);
            sendFailureIntent(errorCode);

        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d("BLE_Advertising", "Advertising successfully started");
            super.onStartSuccess(settingsInEffect);
        }
    }

    /**
     * Builds and sends a broadcast intent indicating Advertising has failed. Includes the error
     * code as an extra. This is intended to be picked up by the {@code AdvertiserFragment}.
     */
    private void sendFailureIntent(int errorCode) {
        Intent failureIntent = new Intent();
//        failureIntent.setAction(ADVERTISING_FAILED);
//        failureIntent.putExtra(ADVERTISING_FAILED_EXTRA_CODE, errorCode);
        Log.d("BLE_Advertising", "Advertising failed");
        //sendBroadcast(failureIntent);
    }

}
