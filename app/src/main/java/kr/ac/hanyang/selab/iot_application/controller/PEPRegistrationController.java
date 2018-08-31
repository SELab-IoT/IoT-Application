package kr.ac.hanyang.selab.iot_application.controller;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kr.ac.hanyang.selab.iot_application.presentation.PEPRegistrationActivity;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.BluetoothPEPListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.BluetoothService;

public class PEPRegistrationController {

    private final String TAG = "PEPRegistrationController";
    private BluetoothService bluetooth = null;

    private Handler blueHandler;

    private Set<BluetoothDevice> pepList = new HashSet<>();

    //여기 커플링 정말 싫다... 나중에 정말 시간 남으면 리팩토링 시도
    private PEPRegistrationActivity activity;
    private BluetoothPEPListAdapter listAdapter;

    public PEPRegistrationController(final PEPRegistrationActivity activity, BluetoothPEPListAdapter adapter){
        this.activity = activity;
        this.listAdapter = adapter;
        this.blueHandler = new Handler(){
            public void handleMessage(Message msg){

                super.handleMessage(msg);
                Log.d(TAG, "handle Message : "+msg);
                bluetooth.closeAll();

                try {
                    JSONObject json = new JSONObject(msg.getData().getString("msg"));
                    JSONObject profile = json.getJSONObject("profile");
                    String pepID = profile.getString("pepID");
                    String pepIP = profile.getString("pepIP");

                    // TODO: 가져온 PEP 프로필로 등록절차 진행.


                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
        listAdapter.addBluetoothDevice(profile);
    }

    public void connect(Map<String, String> pep) {
        // 맥 주소로 연결 후, 쓰레드를 통해 메시지 읽기 대기.
        String mac = pep.get("mac");
        BluetoothDevice bluePEP = findPepByMac(mac);

        // 페어링 이후 연결
        bluetooth.connect(bluePEP);

        // Profile 요청 - 읽기 대기중인 핸들러에서 Profile 읽을 것임.
        bluetooth.send("profile");
    }

}
