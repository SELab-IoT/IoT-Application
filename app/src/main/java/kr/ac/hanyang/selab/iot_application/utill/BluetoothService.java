package kr.ac.hanyang.selab.iot_application.utill;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import kr.ac.hanyang.selab.iot_application.presentation.PEPListActivity;

public class BluetoothService {

    // RFCOMM Protocol
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    private static final String TAG = "BluetoothService";
    private BluetoothAdapter adapter;
    private Activity activity;
    private Handler handler;

    public BluetoothService(Activity ac, Handler h){
        activity = ac;
        handler = h;
        adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter == null) Log.e(TAG,"BLUETOOTH IS NOT AVAILABLE");
    }

    public boolean getDeviceState(){
        Log.d(TAG, "Check Bluetooth Support");
        if(adapter == null)
            Log.d(TAG, "Bluetooth is not available");
        else
            Log.d(TAG, "Bluetooth is available");
        return adapter == null;
    }

    public void enableBluetooth(){
        Log.i(TAG, "Check the enabled Bluetooth");
        if(adapter.isEnabled()){
            Log.d(TAG, "Bluetooth Enable Now");

            //Do Something...
            scanDevice();
        } else {
            Log.d(TAG, "Bluetooth Enable Request");
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(i, REQUEST_ENABLE_BT);
        }
    }

    public boolean scanDevice(){
        Log.d(TAG, "Scan Device");
        return adapter.startDiscovery();
    }

    public void connect(String address){
        // Bluetooth Device Object
        BluetoothDevice device = adapter.getRemoteDevice(address);
        Log.d(TAG, "Get Device Info \n"+"address : "+address);

        //TODO:페어링 시작
        connect(device);

    }

    private int state;
    private static final int STATE_NONE = 0;
    private static final int STATE_LISTEN = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;

    public synchronized void setState(int state){
        Log.d(TAG, "setState() "+this.state+" -> "+state);
        this.state = state;
    }

    public synchronized int getState(){
        return state;
    }

    public synchronized void start(){
        Log.e(TAG, "start");
        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }

        if(connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: "+device);
        if(state == STATE_CONNECTING)
            cleanConnectThread();

        cleanConnectedThread();

        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);

    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
        Log.d(TAG, "connected");
        cleanConnectThread();
        cleanConnectedThread();

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();

        setState(STATE_CONNECTED);
    }

    public synchronized void stop(){
        Log.d(TAG, "stop");
        cleanConnectThread();
        cleanConnectedThread();
        setState(STATE_NONE);
    }

    public void write(byte[] out){
        ConnectedThread r;
        synchronized(this){
            if(state != STATE_CONNECTED) return;
            r = connectedThread;
        }
        r.write(out);
    }

    private void cleanConnectThread(){
        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
    }

    private void cleanConnectedThread(){
        if(connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }
    }

    private void connectionFailed(){
        setState(STATE_LISTEN);
    }

    private void connectionLost(){
        setState(STATE_LISTEN);
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket blueSocket;
        private final BluetoothDevice blueDevice;

        public ConnectThread(BluetoothDevice device){
            blueDevice = device;
            BluetoothSocket tmp = null;
            try{
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            }catch (IOException e){
                Log.e(TAG,"create() failed", e);
            }
            blueSocket = tmp;
        }

        public void run(){
            Log.i(TAG, "Begin ConnectThread");
            setName("ConnectThread");

            adapter.cancelDiscovery();
            try {
                blueSocket.connect();
                Log.d(TAG, "Connect Success");
            }catch(IOException e){
                connectionFailed();
                try{
                    blueSocket.close();
                }catch (IOException e2){
                    Log.e(TAG, "unable to close() during connection failure", e2);
                }
                BluetoothService.this.start();
                return;
            }

            synchronized (BluetoothService.this) {
                connectThread = null;
            }

            connected(blueSocket, blueDevice);

        }

        public void cancel(){
            try{
                blueSocket.close();
            } catch(IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket blueSocket;
        private final InputStream in;
        private final OutputStream out;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "create ConnectedThread");
            blueSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }catch (IOException e){
                Log.e(TAG, "Streams not created", e);
            }
            in = tmpIn; out = tmpOut;
        }

        public void run(){
            Log.i(TAG, "Begin ConnectedThread");
            byte[] buf = new byte[1024];
            int bytes;

            while(true){
                try{
                    bytes = in.read(buf);
                } catch (IOException e){
                    Log.e(TAG, "Exception during read", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buf){
            try{
                out.write(buf);
            } catch (IOException e){
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel(){
            try{
                blueSocket.close();
            } catch (IOException e){
                Log.e(TAG, "close() connected socket failed", e);
            }
        }

    }

}
