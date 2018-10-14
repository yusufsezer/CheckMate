package com.example.yusuf.beaconcheck;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.os.Handler;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import static com.example.yusuf.beaconcheck.Constants.CHARACTERISTIC_ECHO_UUID;
import static com.example.yusuf.beaconcheck.Constants.SERVICE_UUID;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;


public class BleServer {

    private static final String TAG = "BleServer";
    private Handler mHandler;
    private List<BluetoothDevice> mDevices;
    private Context context;
    private BluetoothGattServer mGattServer;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private String[] courseIdList;
    private String[] friendsList;
    private String netId;

    public BleServer(Context context, String[] courseIdList, String netId, String[] friendsList){
        this.friendsList = friendsList;
        this.context = context;
        this.netId = netId;
        mHandler = new Handler();
        mDevices = new ArrayList<>();
        mBluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        this.courseIdList = courseIdList;

    }

    public void run(){
        // Check if bluetooth is enabled
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth not enabled!");
            return;
        }

        // Check advertising
        if (!mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            return;
        }
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        GattServerCallback gattServerCallback = new GattServerCallback();
        mGattServer = mBluetoothManager.openGattServer(context, gattServerCallback);
        setupServer();
        startAdvertising();
    }

    private void setupServer() {
        BluetoothGattService service = new BluetoothGattService(SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic writeCharacteristic = new BluetoothGattCharacteristic(
                CHARACTERISTIC_ECHO_UUID,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(writeCharacteristic);
        mGattServer.addService(service);
    }

    private void stopServer() {
        if (mGattServer != null) {
            mGattServer.close();
        }
    }

    private void restartServer() {
        stopAdvertising();
        stopServer();
        setupServer();
        startAdvertising();
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d(TAG, "Peripheral advertising started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.d(TAG, "Peripheral advertising failed: " + errorCode);
        }
    };

    private void startAdvertising() {
        if (mBluetoothLeAdvertiser == null) {
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder().setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .build();

        ParcelUuid parcelUuid = new ParcelUuid(SERVICE_UUID);
        AdvertiseData data = new AdvertiseData.Builder().setIncludeDeviceName(false)
                .addServiceUuid(parcelUuid)
                .build();

        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
    }

    private void stopAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
        }
    }

    public void addDevice( final BluetoothDevice device) {
        Log.d(TAG, "Deviced added: " + device.getAddress());
        mHandler.post(new Runnable(){
            @Override
            public void run(){
                Log.d(TAG, "Added device " + device.getAddress());
                mDevices.add(device);
            }
        });
    }

    public void removeDevice(final BluetoothDevice device) {
        Log.d(TAG, "Deviced removed: " + device.getAddress());
        mHandler.post(new Runnable(){
            @Override
            public void run(){
                mDevices.remove(device);
            }
        });
    }

    private class GattServerCallback extends BluetoothGattServerCallback {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.d(TAG, "onConnectionStateChange " + device.getAddress() + "\nstatus " + status + "\nnewState " + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected to device: " + device.getAddress());
                addDevice(device);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e(TAG, "State disconnected.");
                removeDevice(device);
            }
        }

        public void onCharacteristicWriteRequest(BluetoothDevice device,
                                                 int requestId,
                                                 BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite,
                                                 boolean responseNeeded,
                                                 int offset,
                                                 byte[] value) {
            super.onCharacteristicWriteRequest(device,
                    requestId,
                    characteristic,
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value);
            if (characteristic.getUuid().equals(CHARACTERISTIC_ECHO_UUID)) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
                byte[] messageBytes = new byte[0];
                try {
                    messageBytes = netId.getBytes("UTF-8");
                    characteristic.setValue(messageBytes);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Failed to convert message string to byte array");
                }
                String courseId = "";
                try {
                    courseId = new String(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.d(TAG, "Can't decode message. Closing connection with: " + device.getAddress());
                    mGattServer.cancelConnection(device);
                }
                // If the user is a member of this class, respond with netId and parse attendance for names
                if (Arrays.asList(courseIdList).contains(courseId.substring(0, 5))) {
                    stopAdvertising();
                    Log.d(TAG, "Sending back: " + Arrays.toString(value));
                    pushFriendNotification(courseId);
                    mGattServer.notifyCharacteristicChanged(device, characteristic, false);
                    mGattServer.cancelConnection(device);
                } else {
                    Log.d(TAG, "User does not belong to this class. Closing connection with: " + device.getAddress());
                    mGattServer.cancelConnection(device);
                }
            }
        }
    }

    public void pushFriendNotification(String message) {
        Log.d(TAG, message);
        String[] names = message.substring(6).split("-");

        String content = "";
        if (names.length > 0) {
            Log.d(TAG, "Friends found");
            content += "Say hi to your friends: ";
            for(String name: names) {
                content += name + ", ";
            }
            content = content.substring(0, content.length()-2);
            Log.d(TAG, content);
        }
        NotificationChannel channel = new NotificationChannel("id", "asdf", 5);
        Notification mBuilder = new NotificationCompat.Builder(context, channel.getId())
                .setSmallIcon(R.drawable.bell)
                .setContentTitle("You've Checked into Class!")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify("BeaconCheck", 1, mBuilder);
    }
}
