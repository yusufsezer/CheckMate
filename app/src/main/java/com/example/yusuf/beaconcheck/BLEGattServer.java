package com.example.yusuf.beaconcheck;

import java.io.IOException;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogRecord;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import static java.lang.Thread.sleep;
import android.content.Context;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;



public class BLEGattServer {

    Context context;
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;
    Activity activity;
    Handler mHandler;
    HashMap<String, BluetoothDevice> devices = new HashMap<String, BluetoothDevice>();
    BluetoothDevice rudy;
    BluetoothGatt rudyGatt;
    Boolean rudyFound = false;

    public BLEGattServer(Context context, Activity act){
        this.context = context;
        this.bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = this.bluetoothManager.getAdapter();
        this.bluetoothLeScanner = this.bluetoothAdapter.getBluetoothLeScanner();
        this.activity = act;
        this.mHandler = new Handler();
    }

    public ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanFailed(int errorCode) {
            Log.d("BLE_SERVER", "SCAN FAILED WITH ERROR: " + errorCode);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();
            String uuid = device.getUuids() != null ? device.getUuids().toString() : "null";
            if (!devices.containsKey(deviceAddress)) {
                devices.put(deviceAddress, device);
                Log.d("BLE_SERVER", "SCAN FOUND DEVICE WITH ADDRESS: " + deviceAddress + " WITH NAME: " + device.getName() + " WITH UUID: " + uuid);
                if (device.getName() != null && device.getName().equals("Rudy Junior 2")){
                    rudy = device;
                    Log.d("BLE_Server", "Connecting to Rudy...");
                    GattClientCallback gattClientCallback = new GattClientCallback();
                    rudyGatt = device.connectGatt(context, false, gattClientCallback);
                }
            }
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
                .setScanMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .build();

        Log.d("BLE_SERVER", "STARTING SCAN");
        bluetoothLeScanner.startScan(filters, settings, scanCallback);
        Log.d("BLE_SERVER", "LE SCAN STARTED");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("BLE_SERVER", "SCAN STOPPED");
                bluetoothLeScanner.stopScan(scanCallback);
                Log.d("BLE_SERVER", "FOUND " + devices.size() + " Devices");
            }
        }, 10000);
    }

    private class GattClientCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.d("BLE_SERVER", "GATT CONNECTION FAILED");
                disconnectGattServer();
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE_SERVER", "NOT FAILURE, BUT NOT SUCCESS?");
                disconnectGattServer();
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BLE_SERVER", "CONNECTED TO RUDY VIA GATT");
                rudyFound = true;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BLE_SERVER", "DISCONNECTED FROM RUDY");
                disconnectGattServer();
            }
        }

        public void disconnectGattServer() {
            rudyFound = false;
            if (rudyGatt != null) {
                rudyGatt.disconnect();
                rudyGatt.close();
            }
        }
    }
}
