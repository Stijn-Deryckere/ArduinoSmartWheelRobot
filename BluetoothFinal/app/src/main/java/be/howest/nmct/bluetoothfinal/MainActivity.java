package be.howest.nmct.bluetoothfinal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String DEVICE_NAME = "STIJN";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;

    private Button btnConnect;
    private Button btnSend;
    private EditText txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnSend = (Button) findViewById(R.id.btnSend);
        txtMessage = (EditText) findViewById(R.id.txtMessage);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectDevice();
                try{
                    openBT();
                }
                catch(IOException ioe){
                    Log.e("FOUTMELDING",ioe.toString());
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    sendData();
                }
                catch(IOException ioe){
                    Log.e("FOUTMELDING",ioe.toString());
                }
            }
        });



    }

    private void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }
    }

    private void connectDevice() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if(device.getName().contains(DEVICE_NAME)){
                    mmDevice = device;
                }
            }
        }
    }

    private void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        mmOutputStream = mmSocket.getOutputStream();
        Toast.makeText(getApplicationContext(),"Connection established",Toast.LENGTH_SHORT).show();
    }

    void sendData() throws IOException {
        String msg = txtMessage.getText().toString();
        mmOutputStream.write(msg.getBytes());
        Toast.makeText(getApplicationContext(),"Message sent",Toast.LENGTH_SHORT).show();
    }
}
