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

    public void connect(BluetoothDevice device) throws IOException{
        blueSocket = device.createRfcommSocketToServiceRecord(uuid);
        blueSocket.connect();

        out = blueSocket.getOutputStream();
        in = blueSocket.getInputStream();

        // TODO: 요 부분 Timeout 걸어야 함. 안걸면 PEP 아닌 기기에 대해서 하염없이 무한루프 돌아감
        // TODO: 근데 어떻게 걸어야 할 지 모르겠음
        beginListenForData();
    }

    public void closeAll(){
        try{
            send("close");

            // TODO: send 후 바로 닫으면 예외 터지는데 어떻게 지연시켜야 할지 고민
            Thread.sleep(100);

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
                try{
                    while(!Thread.currentThread().isInterrupted()){
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
                                    Log.d(TAG, data);
                                }else{
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                } catch (IOException e){
                    Log.e(TAG, "Exception during beginListenForData()", e);
                }
            }
        });
        workerThread.start();
    }

}
