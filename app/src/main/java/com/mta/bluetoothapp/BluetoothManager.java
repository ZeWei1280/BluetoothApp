package com.mta.bluetoothapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BluetoothManager {
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;

    public BluetoothManager(Context context, ActivityResultLauncher<Intent> enableBluetoothLauncher) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
        this.enableBluetoothLauncher = enableBluetoothLauncher;
    }

    public boolean isBluetoothSupported() {
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "This device doesn't support Bluetooth.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public void enableBluetooth() {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(bluetoothIntent);
        }
    }

    public void disableBluetooth() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                boolean disableSuccess = bluetoothAdapter.disable();
                if (disableSuccess) {
                    Toast.makeText(context, "Bluetooth is turned off", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to turn off Bluetooth", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Bluetooth permissions are required to turn off Bluetooth.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions(AppCompatActivity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MainActivity.REQUEST_BLUETOOTH_PERMISSIONS);
    }
}
