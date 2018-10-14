package com.example.yusuf.beaconcheck;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.os.Handler;

import static android.content.Context.BLUETOOTH_SERVICE;

public class BleClient {

    final String TAG = "BleClient";
    final UUID SERVICE_UUID = UUID.fromString("7-6-5-4-3");
    Map<String, BluetoothDevice> scanResults;
    BtleScanCallback scanCallback;
    Boolean scanning;
    Context context;
    Activity activity;
    Handler handler;
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;
    BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt mGatt;

    public BleClient(Context context, Activity activity) {
        this.scanning = false;
        this.context = context;
        this.activity = activity;
        this.handler = new Handler();
        this.bluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        this.bluetoothAdapter = this.bluetoothManager.getAdapter();
        this.bluetoothLeScanner = this.bluetoothAdapter.getBluetoothLeScanner();
    }

    public void run() {

        // Check to make sure the device is BLE enabled
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG, "Device is not BLE Enabled.");
            return;
        }

        startScan();
    }

    private void startScan() {
        if (!hasPermissions() || scanning) {
            return;
        }

        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(SERVICE_UUID))
                .build();
        filters.add(scanFilter);
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();
        scanResults = new HashMap<>();
        scanCallback = new BtleScanCallback(scanResults);
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        // Stop scan after 10 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
                Log.d(TAG, "FOUND " + scanResults.size() + " Devices");
            }
        }, 10000);
        scanning = true;
        bluetoothLeScanner.startScan(filters, settings, scanCallback);
    }

    private void stopScan() {
        if (scanning && bluetoothAdapter != null && bluetoothAdapter.isEnabled() && bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
            scanComplete();
        }

        scanCallback = null;
        scanning = false;
        handler = null;
    }

    private void scanComplete() {
        if (scanResults.isEmpty()) {
            return;
        }
        for (String deviceAddress : scanResults.keySet()) {
            Log.d(TAG, "Found device: " + deviceAddress + " with UUID: " + scanResults.get(deviceAddress).getUuids());
        }



    }

    private boolean hasPermissions() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth is not enabled.");
            return false;
        } else if (!hasLocationPermissions()) {
            requestLocationPermission();
            return false;
        }
        return true;
    }

    private boolean hasLocationPermissions() {
        return activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }

    /**
     * Nested class to handle BLE call back during scan
     */
    private class BtleScanCallback extends ScanCallback {

        private Map<String, BluetoothDevice> mScanResults;

        public BtleScanCallback(Map<String, BluetoothDevice> sResults) {
            this.mScanResults = sResults;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            addScanResult(result);
            GattClientCallback gattClientCallback = new GattClientCallback();
            mGatt = result.getDevice().connectGatt(context, false, gattClientCallback);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                addScanResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "BLE Scan Failed with code " + errorCode);
        }

        private void addScanResult(ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();
            scanResults.put(deviceAddress, device);
        }

    }

    public void disconnectGattServer() {
        Log.d(TAG, "Closing Gatt connection");
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }

    private class GattClientCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "onConnectionStateChange newState: " + newState);

            if (status == BluetoothGatt.GATT_FAILURE) {
                Log.e(TAG, "Connection Gatt failure status " + status);
                disconnectGattServer();
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                // handle anything not SUCCESS as failure
                Log.e(TAG, "Connection not GATT sucess status " + status);
                disconnectGattServer();
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected to device " + gatt.getDevice().getAddress());
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected from device");
                disconnectGattServer();
            }
        }
    }
}
