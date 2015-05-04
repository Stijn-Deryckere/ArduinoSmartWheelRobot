package be.howest.nmct.blauw;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final String DEVICE_NAME="STIJN";
    private UUID MY_UUID;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private android.os.Handler mHandler;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    private Button btnEnableDisableBluetooth;
    private Button btnConnect;
    private Button btnDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(); //Initialize bluetoothadapter

        btnEnableDisableBluetooth = (Button) findViewById(R.id.btnEnableDisableBluetooth);
        btnEnableDisableBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDisableBluetooth(); //Turn on/off bluetooth
            }
        });
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDevice();
                connectDevice();
            }
        });
        btnDrive = (Button) findViewById(R.id.btnDrive);
        btnDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bt = new byte[]{1};
                connectedThread.write(bt);
            }
        });
    }

    private void connectDevice() {
        try{
            connectThread = new ConnectThread(mBluetoothDevice);
            connectThread.run();
            connectedThread = new ConnectedThread(mBluetoothSocket);
            connectedThread.run();
            Toast.makeText(getApplicationContext(),"Connection established with Arduino",Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }

    }

    private void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"This device does not support bluetooth",Toast.LENGTH_SHORT).show();
        }
    }
    private void enableDisableBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(),"Bluetooth enabled",Toast.LENGTH_SHORT).show();
        }
        else{
            mBluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(),"bluetooth disabled",Toast.LENGTH_SHORT).show();
        }
    }
    private void searchDevice() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                if(device.getName().contains(DEVICE_NAME)){
                    mBluetoothDevice = device;
                    MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"No paired devices",Toast.LENGTH_SHORT).show();
        }
    }


    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mBluetoothSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mBluetoothSocket.connect();
                //
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mBluetoothSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) { }
        }
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("SOCKET",e.toString());
            }
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                mmOutStream.write('A');
                Log.e("INWRITE",""+bytes);
            } catch (IOException e) {
                Log.e("INWRITE","FOUTMELDING: "+e.toString());
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
