package com.example.yusuf.beaconcheck;

import java.io.IOException;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import static java.lang.Thread.sleep;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;



public class BLEGattServer {

    Context context;
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;
    Activity activity;
    Handler mHandler;

    public BLEGattServer(Context context, Activity act){
        this.context = context;
        this.bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = this.bluetoothManager.getAdapter();
        this.bluetoothLeScanner = this.bluetoothAdapter.getBluetoothLeScanner();
        this.activity = act;
        this.mHandler = new Handler();
    }

//    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            Log.d("BLESERVER", "EXECUTING CALLBACK");
//        }
//    };

    public ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanFailed(int errorCode) {
            Log.d("BLE_SERVER", "SCAN FAILED WITH ERROR: " + errorCode);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAdress = device.getAddress();
            Log.d("BLE_SERVER", "SCAN FOUND DEVICE WITH ADDRESS: " + deviceAdress + " WITH NAME: " + device.getName());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d("BLE_SERVER", "Back Scan Results came back.");
        }
    };

    public void run(){
        Log.d("BLE_SERVER", "STARTED THREAD");

        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        Log.d("BLE_SERVER", "STARTING SCAN");
        bluetoothLeScanner.startScan(filters, settings, scanCallback);
        Log.d("BLE_SERVER", "LE SCAN STARTED");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("BLE_SERVER", "SCAN STOPPED");
                bluetoothLeScanner.stopScan(scanCallback);
            }
        }, 1000000);
    }

//    public void leScanCallback(){
//        Log.d("BLESCAN", "Scan finished.");
//    }

//    public void scanLeDevice(Context context, Boolean enable) {
//        Handler mHandler = new Handler();
//
//        // Call stopLeScan after 10 Seconds
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                bluetoothAdapter.stopLeScan(BLEGattServer.mLeScanCallback);
//            }
//        }, 10000);
//
//        BluetoothAdapter.startLeScan(BLEGattServer.mLeScanCallback);
//        Log.d("BLE Found", "");
//    }
}
