package kr.ac.hanyang.selab.iot_application.utill;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {

    public static int MESSAGE_READ = 1;

    private final String TAG = "BluetoothService";

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private BluetoothAdapter blueAdapter;

    private BluetoothSocket blueSocket; //Client Socket
    private OutputStream out;
    private InputStream in;

    private Thread workerThread;
    private final char EOM = '\0';
    private byte[] readBuffer;
    private int readBufferPosition = 0;


    private Handler blueHandler;

    public BluetoothService(Handler handler){
        this.blueHandler = handler;
        blueAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public Set<BluetoothDevice> getPairedDevices(){
        return blueAdapter.getBondedDevices();
    }

    public void connectTo(BluetoothDevice device){
        try{
            blueSocket = device.createRfcommSocketToServiceRecord(uuid);
            blueSocket.connect();

            out = blueSocket.getOutputStream();
            in = blueSocket.getInputStream();

            beginListenForData();

        } catch (IOException e){
            Log.e(TAG, "connectTo()", e);
        }
    }

    public void closeAll(){
        try{
            workerThread.interrupt();
            in.close();
            out.close();
            blueSocket.close();
        } catch(Exception e){
            Log.e("TAG","Exception during closeAll()", e);
        }
    }

    public boolean send(String msg){
        msg += EOM;//End of Msg
        try{
            out.write(msg.getBytes());
            return true;
        }catch (IOException e){
            Log.e(TAG,"Exception during send("+msg+")", e);
            return false;
        }
    }

    public void beginListenForData(){
        readBuffer = new byte[1024];
        readBufferPosition = 0;
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()){
                    try{
                        int bytesAvailable = in.available();
                        if(bytesAvailable > 0){
                            byte[] packet = new byte[bytesAvailable];
                            in.read(packet);
                            for(int i=0; i<bytesAvailable; i++){
                                byte b = packet[i];
                                if(b == EOM) {
                                    byte[] encoded = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encoded, 0, encoded.length);
                                    final String data = new String(encoded, "US-ASCII");
                                    readBufferPosition = 0;
                                    Bundle bundle = new Bundle();
                                    Message msg = new Message();
                                    bundle.putString("msg", data);
                                    msg.setData(bundle);
                                    blueHandler.sendMessage(msg);
                                }else{
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException e){
                        Log.e(TAG, "Exception during beginListenForData()", e);
                    }
                }
            }
        });
        workerThread.start();
    }

    /*

    private synchronized void setState(int state){
        Log.d(TAG, "setState() "+this.state+" -> "+state);
        this.state = state;
    }

    public synchronized int getState(){
        return state;
    }

    public synchronized void start(){
        Log.d(TAG, "start()");
        stop();
    }

    public synchronized BluetoothSocket connect(BluetoothDevice device){
        Log.d(TAG, "connect() to "+device.getName());
        stop();
        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
        Log.d(TAG, "connected() to "+device.getName());
        stop();
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        setState(STATE_CONNECTED);
    }

    public synchronized void stop(){
        Log.d(TAG, "stop()");
        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        if(connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }
        setState(STATE_NONE);
    }

    public void write(byte[] out){
        ConnectedThread r;
        synchronized(this){
            if(state != STATE_CONNECTED) return;
            r = connectedThread;
        }
        r.write(out); //Unsynchronized write
    }

    public void connectionFailed(){
        setState(STATE_LISTEN);
    }

    public void connectionLost(){
        setState(STATE_LISTEN);
    }


    //이건 Client Socket. ServerSocket은 Pi에서 구현
    private class ConnectThread extends Thread {
        private final BluetoothSocket blueSocket;
        private final BluetoothDevice blueDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            blueDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) { }
            blueSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            blueAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                blueSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    blueSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
//            manageConnectedSocket(blueSocket);
        }

        // Will cancel an in-progress connection, and close the socket /
        public void cancel() {
            try {
                blueSocket.close();
            } catch (IOException e) { }
        }
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    blueHandler
                            .obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        // Call this from the main activity to shutdown the connection
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


    */

}
