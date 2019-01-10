package kr.ac.hanyang.selab.iot_application.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.presentation.NewPEPGroupActivity;
import kr.ac.hanyang.selab.iot_application.presentation.PEPGroupListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.BluetoothPEPListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.BluetoothService;
import kr.ac.hanyang.selab.iot_application.utill.DialogUtil;
import kr.ac.hanyang.selab.iot_application.utill.Hash;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequest;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequestFactory;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequester;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpUtil;

public class PEPRegistrationController {

    private final String TAG = "PEPRegistrationCon";
    private BluetoothService bluetooth = null;

    private Handler blueHandler;

    private Set<BluetoothDevice> pepList = new HashSet<>();

    private Activity activity;
    private BluetoothPEPListAdapter listAdapter;

    public PEPRegistrationController(Activity activity, BluetoothPEPListAdapter adapter){
        this.activity = activity;
        this.listAdapter = adapter;

        this.blueHandler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Log.d(TAG, "Selected PEP Profile : "+msg);
                disconnect();
                try {
                    JSONObject json = new JSONObject(msg.getData().getString("msg"));
//                    DialogUtil.showMessage(activity, "선택한 PEP Profile:", json.toString());
                    JSONObject pepProfile = json.getJSONObject("profile");
                    registerPEP(pepProfile);
                } catch (JSONException e) {
                    DialogUtil.showMessage(activity, "Error Occurred!","잘못된 형식의 PEP Profile 입니다.");
                }
                DialogUtil.getInstance().stopProgress();
            }
        };
        if(bluetooth == null)
            bluetooth = new BluetoothService(blueHandler);
    }

    private void registerPEP(JSONObject pepProfile){
        // Step 5. PEP Group 조회 ~ Step 6. PEPGroup 반환
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //Step 6. 반환받은 메시지따라 7a, 7b 선택해서 처리.
                try {
                    JSONObject json = new JSONObject(msg.getData().getString("msg"));

                    boolean hasGroup = json.getBoolean("hasGroup");
                    if(hasGroup) // PEP가 속한 PEPGroup이 있는 경우 (7.b)
                        addUserToPEPGroup(json.getLong("pepGroupId"));
                    else // PEP가 속한 PEPGroup이 없는 경우 (7.a)
                        addPEPtoPEPGroup(pepProfile.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        String userId = Login.getId();
        String pepId = null;
        try {
            pepId = pepProfile.getString("pepId");
        } catch (JSONException e) {
            DialogUtil.showMessage(activity, "Error!", "잘못된 PEP 프로필 입니다!(No PEP ID in pepProfile)");
            e.printStackTrace();
            return;
        }
        String url = HttpUtil.PLATFORM_MANAGER+"groups/"+userId+"/"+pepId;
        HttpRequest request = HttpRequestFactory.getInstance().createGETRequest(handler, url, null);
        new HttpRequester(request).execute();
    }

    //Step 7.a. PEP가 포함된 PEP Group이 없는 경우 ... 즉, PEP 자체도 아직 PM에 없음.
    private void addPEPtoPEPGroup(String pepProfile){
        // 다이얼로그 보여줘서 선택지 2개 보여준 후 선택지 따라 액티비티 전환하기.
        // 7.a.1. 새 그룹 생성하기
        // 7.a.2. 기존 그룹에 추가하기.
        final CharSequence[] arr = {"새 그룹 생성하기", "기존 그룹에 추가하기"};
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setIcon(R.drawable.ic_launcher_background);
        alert.setTitle("이 PEP 를...");
        alert.setSingleChoiceItems(arr, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Class<?> nextActivity = (i == 0) ? NewPEPGroupActivity.class
                        : PEPGroupListActivity.class ;

                Intent intent = new Intent(activity, nextActivity);
                intent.putExtra("pepProfile", pepProfile);
                activity.startActivity(intent);
                dialog.cancel();
            }
        });
        alert.create().show();

    }

    //Step 7.b. PEP가 포함된 PEP Group이 있는 경우
    private void addUserToPEPGroup(long pepGroupId){
        // 유저를 해당 PEP Group에 등록
        // 적합한 유저는 PEP Group의 패스워드를 이미 알고있다고 가정한다.
        Handler onOk = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // 유저 등록 요청
                String userId = Login.getId();
                String pepGroupPW = msg.getData().getString("pepGroupPW");
                requestAddUserToPEPGroup(userId, pepGroupId, pepGroupPW);
            }
        };
        Handler onCancel = new Handler();
        DialogUtil.getInstance().showPasswordPrompt(activity, onOk, onCancel);
    }

    private void requestAddUserToPEPGroup(String userId, long pepGroupId, String pepGroupPW){

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    JSONObject json = new JSONObject(msg.getData().getString("msg"));
                    boolean success = json.getBoolean("success");
                    if(success){
                        DialogUtil.showMessage(activity, "유저 등록 성공!", "PEP Group 가입에 성공했습니다!");
                    } else {
                        DialogUtil.showMessage(activity, "유저 등록 실패!", "Reason : "+json.getString("reason"));
                        addUserToPEPGroup(pepGroupId); //그룹 패스워드 다시 입력 받기
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        String url = HttpUtil.PLATFORM_MANAGER + "groups";
        String hashed = Hash.SHA3_256(pepGroupPW);
        ContentValues params = new ContentValues();
        params.put("userId", userId);
        params.put("pepGroupId", pepGroupId);
        params.put("pepGroupPW", hashed);
        HttpRequest request = HttpRequestFactory.getInstance().createPOSTRequest(handler, url, params);
        new HttpRequester(request).execute();
    }


    // 휴대폰 주변 블루투스 페어링 된 기기 전부 검색
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

    public void connect(Map<String, String> pep){
        try {
            // 맥 주소로 연결 후, 쓰레드를 통해 메시지 읽기 대기.
            Log.d(TAG, "Get MAC Address");
            String mac = pep.get("mac");
            BluetoothDevice bluePEP = findPepByMac(mac);

            // 페어링 이후 연결
            Log.d(TAG, "Connect");
            bluetooth.connect(bluePEP);

            // Profile 요청 - 읽기 대기중인 핸들러에서 Profile 읽을 것임(생성자 참조).
            Log.d(TAG, "Get PEP Profile");
            bluetooth.send("profile");
        } catch (IOException e){
            Log.e(TAG, "connect()");
            e.printStackTrace();
            DialogUtil.getInstance().stopProgress();
            DialogUtil.showMessage(activity, "Error Occurred!", "전원 혹은 블루투스가 꺼져있거나, PEP가 아닙니다.");
        }
    }

}
