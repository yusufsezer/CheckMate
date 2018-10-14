package com.example.yusuf.beaconcheck;

import java.io.IOException;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
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
    Handler mHandler;

    public BLEGattServer(Context context){
        this.context = context;
        this.bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = this.bluetoothManager.getAdapter();
        this.bluetoothLeScanner = this.bluetoothAdapter.getBluetoothLeScanner();
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
            Log.d("BLESERVER", "SCAN FAILED WITH ERROR: " + errorCode);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d("BLESERVER", "SCAN SUCCEEDED");
        }
    };

    public void run(){
        Log.d("BLESERVER", "STARTED THREAD");

        // Call stopLeScan after 10 Seconds
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("BLESERVER", "ABOUT TO CALL BACK");
                bluetoothLeScanner.stopScan(scanCallback);
            }
        }, 10000);

        Log.d("BLESERVER", "STARTING SCAN");
        bluetoothLeScanner.startScan(scanCallback);
        Log.d("BLESERVER", "LE SCAN STARTED");
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
