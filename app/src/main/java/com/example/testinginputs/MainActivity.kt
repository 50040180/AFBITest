@file:Suppress("ControlFlowWithEmptyBody")

package com.example.testinginputs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.testinginputs.ui.theme.TestingInputsTheme
import java.util.UUID
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.Set;

public class MainActivity{

    InputStream inStream;
    OutputStream outputStream;
    private static final int REQUEST_ENABLE_BT = 1;

    public void pairDevice() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);}

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            Object[] devices = pairedDevices.toArray();
            BluetoothDevice device = (BluetoothDevice) devices[0];
            ParcelUuid[] uuid = device.getUuids();
            try {
                BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(uuid[0].getUuid());
                socket.connect();
                Toast.makeText(this, "Socket connected", Toast.LENGTH_LONG).show();
                outputStream = socket.getOutputStream();
                inStream = socket.getInputStream();
            } catch (IOException e) {
                Toast.makeText(this, "Exception found", Toast.LENGTH_LONG).show();
            }

        }
    }


    public void SendMessage(View v) {
        EditText outMessage = (EditText) findViewById(R.id.editText);
        try {
            if (outputStream != null)
                outputStream.write(outMessage.toString().getBytes());
            TextView displayMessage = (TextView) findViewById(R.id.textView);
            Scanner s = new Scanner(inStream).useDelimiter("\\A");
            displayMessage.setText(s.hasNext() ? s.next() : "");
        } catch (IOException e) {/*Do nothing*/}
        Toast.makeText(this,"No output stream", Toast.LENGTH_LONG).show();
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pairDevice();
    }
