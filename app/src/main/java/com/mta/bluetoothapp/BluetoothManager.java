package com.mta.bluetoothapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/**
 * This class manages Bluetooth operations for the application.
 *
 * Note: Instead of using Toast for displaying messages, this class uses statusMessage.
 * This approach is chosen because Toast messages are not easily captured by automation tools.
 * The statusMessage is used to present important Bluetooth status updates.
 */
public class BluetoothManager {
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private String statusMessage;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private MainActivity mainActivity;

    public BluetoothManager(MainActivity activity) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = activity;
        this.mainActivity = activity;
        this.statusMessage = "Bluetooth app is opened";

        // Initialize the ActivityResultLauncher for turning Bluetooth on
        this.enableBluetoothLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        setStatusMessage("Bluetooth is turned on");
                    } else {
                        setStatusMessage("Failed to turn on Bluetooth");
                    }
                    mainActivity.updateBluetoothUI();
                }
        );
    }

    public boolean isBluetoothSupported() {
        if (bluetoothAdapter == null) {
            setStatusMessage("This device doesn't support Bluetooth.");
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

    /**
     * Due to security and user experience reasons,
     * Android does not allow apps to disable Bluetooth programmatically since Android 13 (API level 33).
     */
    public void disableBluetooth() {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            // Handle case where Bluetooth has already been manually disabled
            setStatusMessage("Bluetooth is already turned off");
            mainActivity.updateBluetoothUI();
            return;
        }

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                boolean disableSuccess = bluetoothAdapter.disable(); // Deprecated method, no longer usable as of Android 13 (API level 33)
                if (disableSuccess) {
                    setStatusMessage("Bluetooth is turned off");
                } else {
                    setStatusMessage("Failed to turn off Bluetooth");
                }
            } else {
                setStatusMessage("Failed to turn off Bluetooth");
            }
        }
        mainActivity.updateBluetoothUI();
    }

    public boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions(AppCompatActivity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MainActivity.REQUEST_BLUETOOTH_PERMISSIONS);
    }

    private void setStatusMessage(String msg) {
        statusMessage = msg;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}