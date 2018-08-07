package kr.ac.hanyang.selab.iot_application.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import kr.ac.hanyang.selab.iot_application.presentation.PEPListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.PEPListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.BluetoothService;

public class PEPListController {
    private BluetoothService bluetooth = null;

    //여기 커플링 정말 싫다... 나중에 정말 시간 남으면 리팩토링 시도
    private PEPListActivity activity;
    private PEPListAdapter adapter;

    public PEPListController(PEPListActivity activity, PEPListAdapter adapter){
        this.activity = activity;
        this.adapter = adapter;
    }

    public void searchPEP(){

        final Handler handle = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
            }
        };

        if(bluetooth == null)
            bluetooth = new BluetoothService(activity, handle);

        if(bluetooth.getDeviceState())
            bluetooth.enableBluetooth();
        else
            Toast.makeText(activity, "Please Check your Bluetooth Support.", Toast.LENGTH_LONG);

    }

    public void doDiscovery(){
        IntentFilter filter;

        // 한 개씩 찾아낼 때마다...
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(receiver, filter);

        // 검색이 끝났을 때
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(receiver, filter);

        bluetooth.scanDevice();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() != BluetoothDevice.BOND_BONDED)
                    //리스트에 추가하기.
                    addPEPToList(device);

            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                //검색이 끝났음을 알리기.
                activity.onDiscoveryFinished();
            }
        }
    };

    private void addPEPToList(BluetoothDevice device){
        String pepName = device.getName();
        String pepAddress = device.getAddress();
        Map pep = new HashMap();
        pep.put("pepName", pepName);
        pep.put("pepAddress", pepAddress);
        adapter.addPEP(pep);
    }

    public void connect(Map<String, String> pep){
        String name = pep.get("pepName");
        String address = pep.get("pepAddress");
        bluetooth.connect(address);
    }

}
