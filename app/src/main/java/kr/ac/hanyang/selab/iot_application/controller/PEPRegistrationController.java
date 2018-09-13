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
import kr.ac.hanyang.selab.iot_application.utill.DialogUtil;

public class PEPRegistrationController {

    private final String TAG = "PEPRegistrationCon";
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

                    DialogUtil.getInstance().stopProgress(activity);
                    DialogUtil.showMessage(activity, "PEP Info:", json.toString() +"\n이 이후 단계(PEP 등록 단계)는 차후 개발");

                    // TODO: 가져온 PEP 프로필로 등록절차 진행.
                    JSONObject profile = json.getJSONObject("profile");
                    String pepId = profile.getString("pepId");
                    String pepIp = profile.getString("pepIp");


                } catch (JSONException e) {
                    DialogUtil.getInstance().stopProgress(activity);
                    DialogUtil.showMessage(activity, "ERROR:","Error Occurred during get PEP profile...");
                    e.printStackTrace();
                }
            }
        };
        if(bluetooth == null)
            bluetooth = new BluetoothService(blueHandler);
    }

    public void listUp(){
        Log.d(TAG, "listUp");

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
        DialogUtil.getInstance().startProgress(activity);

        // 맥 주소로 연결 후, 쓰레드를 통해 메시지 읽기 대기.
        String mac = pep.get("mac");
        BluetoothDevice bluePEP = findPepByMac(mac);

        // 페어링 이후 연결
        bluetooth.connect(bluePEP);

        // Profile 요청 - 읽기 대기중인 핸들러에서 Profile 읽을 것임.
        bluetooth.send("profile");
    }

}
