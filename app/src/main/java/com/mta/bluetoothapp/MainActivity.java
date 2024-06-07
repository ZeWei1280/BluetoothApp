package com.mta.bluetoothapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private Switch switchBluetooth;
    private BluetoothManager bluetoothManager;

    private final ActivityResultLauncher<Intent> enableBluetoothLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    updateSwitch();
                    Toast.makeText(MainActivity.this, "Bluetooth is turned on", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to turn on Bluetooth", Toast.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        switchBluetooth = findViewById(R.id.switch_bluetooth);
        bluetoothManager = new BluetoothManager(this, enableBluetoothLauncher);

        if (!bluetoothManager.isBluetoothSupported()) {
            switchBluetooth.setEnabled(false); // 禁用開關
            return;
        }

        updateSwitch();

        switchBluetooth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!bluetoothManager.checkPermissions()) {
                // 請求權限
                bluetoothManager.requestPermissions(this);
            } else {
                toggleBluetooth(isChecked);
            }
        });
    }

    // 應用程序目標API級別高於23並且需要動態請求權限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                toggleBluetooth(switchBluetooth.isChecked());
            } else {
//                Snackbar.make(findViewById(R.id.main), "Bluetooth is turned on", Snackbar.LENGTH_SHORT).show();

//                Toast.makeText(this, "Bluetooth permissions are required to use this feature.", Toast.LENGTH_SHORT).show();
                switchBluetooth.setChecked(!switchBluetooth.isChecked()); // 還原開關狀態
            }
        }
    }

    private void toggleBluetooth(boolean enable) {
        if (enable) {
            bluetoothManager.enableBluetooth();
        } else {
            bluetoothManager.disableBluetooth();
        }
        updateSwitch();
    }

    private void updateSwitch() {
        switchBluetooth.setChecked(bluetoothManager.isBluetoothEnabled());
    }
}
