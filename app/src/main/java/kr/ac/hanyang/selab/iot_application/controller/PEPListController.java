package kr.ac.hanyang.selab.iot_application.controller;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.ac.hanyang.selab.iot_application.presentation.PEPListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.PEPListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.BluetoothService;

public class PEPListController {

    private final String TAG = "PEPListController";
    private BluetoothService bluetooth = null;

    private Handler blueHandler;

    private Set<BluetoothDevice> pepList = new HashSet<>();

    //여기 커플링 정말 싫다... 나중에 정말 시간 남으면 리팩토링 시도
    private PEPListActivity activity;
    private PEPListAdapter listAdapter;

    public PEPListController(PEPListActivity activity, PEPListAdapter adapter){
        this.activity = activity;
        this.listAdapter = adapter;
        this.blueHandler = new Handler(){
            public void handleMessage(Message msg){
                Log.d(TAG, "handle Message : "+msg);
                //Read한 메시지 처리 - UI 조작이든 뭐든.
            }
        };
        if(bluetooth == null)
            bluetooth = new BluetoothService(blueHandler);
    }

    public void listUp(){
        Log.d(TAG, "Connect");
        pepList = bluetooth.getPairedDevices();

        listAdapter.clearAll();
        for (BluetoothDevice pep : pepList)
            addPEPToList(pep);

        listAdapter.notifyDataSetChanged();
    }

    public void connect(String mac){
        BluetoothDevice pep = findPepByMac(mac);
        //TODO:pep 객체 가지고 실제 연결하기.
        bluetooth.connectTo(pep);
    }

    public void disconnect(){
        bluetooth.closeAll();
    }

    private BluetoothDevice findPepByMac(String mac){
        BluetoothDevice pep = null;
        Iterator<BluetoothDevice> i = pepList.iterator();
        while(i.hasNext()){
            pep = i.next();
            if(pep.getAddress().equals(mac))
                break;
        }
        return pep;
    }

    private void addPEPToList(BluetoothDevice pep){
        String pepName = pep.getName();
        String pepAddress = pep.getAddress();
        Log.d("AddPEPToList",pepName+" : "+pepAddress);
        Map<String, String> profile = new HashMap<>();
        profile.put("name", pepName);
        profile.put("mac", pepAddress);
        listAdapter.addPEP(profile);
    }

}
