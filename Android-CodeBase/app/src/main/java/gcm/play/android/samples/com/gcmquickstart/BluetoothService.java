package gcm.play.android.samples.com.gcmquickstart;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by joji on 5/7/16.
 */
public class BluetoothService extends IntentService {

    static final String TAG = "BLUETOOTHSERVICE";

    // Variables
    private String mStudentId;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceName;
    BluetoothDevice mBluetoothDevice;
    private DeviceConnector mDeviceConnector;

    // Constructor
    public BluetoothService() {
        super("BluetoothService");
    }

    // Actual task handling
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onHandleIntent(Intent intent) {

        // Read in the ID
        try {
            InputStream inputStream = openFileInput("student_id.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                mStudentId = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }

        // Turn on the bluetooth
        setBluetooth(true);
        try {
            Thread.sleep(1500); // Give a slight pause to turn on
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Find the Device to connect to
        mBluetoothDeviceName = "Naruto-Team";
//        mBluetoothDeviceName = "SONY:SRS-X5"; // for testing

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        Log.i(TAG, "Number of deviced: " + pairedDevices.size());
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Log.i(TAG, "mBluetoothDeviceName: " + mBluetoothDeviceName);

                if (mBluetoothDeviceName.equals(device.getName())) {
                    mBluetoothDevice = device;
                    Log.i(TAG, "Found: " + mBluetoothDevice.getName());

                    break;
                }
            }
        }

        long startTime = System.currentTimeMillis();
        // Connect to the Device
        if (mBluetoothDevice != null) {
            DeviceData data = new DeviceData(mBluetoothDevice);
            mDeviceConnector = new DeviceConnector(data);
            mDeviceConnector.connect();
        }


        while (mDeviceConnector.getState() != DeviceConnector.STATE_CONNECTED) {
            long currTime = System.currentTimeMillis();
            if (currTime - startTime > 5000) {
                mDeviceConnector.connect();
                startTime = currTime;
            }
        }
//        Thread.yield();

        Log.d("cmpe273", "Before Data Send");

        // Send the student id to the raspberry pi
        if (mDeviceConnector != null) {
            mDeviceConnector.write(mStudentId.getBytes());
        }

        Log.d("cmpe273", "After Data Send");


        // Some delay for the thread to finish
        try {
            Thread.sleep(10000); // Give a slight pause to turn on
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Turn off the bluetooth
        setBluetooth(false);

        // Close down
//        mDeviceConnector.stop();



    }

    // Helper to turn on/off bluetooth
    private boolean setBluetooth(boolean enable) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = mBluetoothAdapter.isEnabled();

        if (enable && !isEnabled) {
            return mBluetoothAdapter.enable();
        }
        else if(!enable && isEnabled) {
            return mBluetoothAdapter.disable();
        }

        // No need to change bluetooth state
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d("cmpe273", "onDestroy");

        // Turn off the bluetooth
//        setBluetooth(false);

        super.onDestroy();
    }
}
