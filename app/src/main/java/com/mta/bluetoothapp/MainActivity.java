package com.mta.bluetoothapp;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private Switch switchBluetooth;

    private TextView textMessageContent;
    private BluetoothManager bluetoothManager;

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

        // init widget
        textMessageContent = findViewById(R.id.text_message_content);
        switchBluetooth = findViewById(R.id.switch_bluetooth);
        bluetoothManager = new BluetoothManager(this);

        if (!bluetoothManager.isBluetoothSupported()) {
            switchBluetooth.setEnabled(false);
            return;
        }

        updateBluetoothUI();
        switchBluetooth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!bluetoothManager.checkPermissions()) {
                bluetoothManager.requestPermissions(this);
            } else {
                toggleBluetooth(isChecked);
            }
        });
    }

    private void toggleBluetooth(boolean enable) {
        if (enable) {
            bluetoothManager.enableBluetooth();
        } else {
            bluetoothManager.disableBluetooth();
        }

    }

    private void setSwitch() {
        switchBluetooth.setChecked(bluetoothManager.isBluetoothEnabled());
    }

    private void setStatusMessage(){
        textMessageContent.setText(bluetoothManager.getStatusMessage());
    }

    public void updateBluetoothUI(){
        setSwitch();
        setStatusMessage();
    }
}