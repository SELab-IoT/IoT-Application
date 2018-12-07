package kr.ac.hanyang.selab.iot_application.controller;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.domain.PEP;
import kr.ac.hanyang.selab.iot_application.presentation.DeviceListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.DeviceRegistrationActivity;
import kr.ac.hanyang.selab.iot_application.presentation.PEPListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.PEPListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequestFactory;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequester;
import kr.ac.hanyang.selab.iot_application.utill.DialogUtil;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpUtil;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequest;

public class PEPListController {
    private final String TAG = "PEPListController";

    private PEPListActivity activity;
    private PEPListAdapter listAdapter;

    public PEPListController(PEPListActivity activity, PEPListAdapter adapter){
        this.activity = activity;
        this.listAdapter = adapter;
    }

    //For testing... one pep group.
    public void listUp(){

        DialogUtil.getInstance().startProgress(activity);

        Handler httpHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, msg.toString());
                Bundle data = msg.getData();
                String profile = data.getString("msg");
                if(profile != null) {
                    try {
                        JSONArray jsonPEPGrps = new JSONArray(profile);
                        int grp_cnt = jsonPEPGrps.length();
                        for(int i=0; i < grp_cnt; i++) {
                            JSONObject jsonPEPGrp = jsonPEPGrps.getJSONObject(i);
                            JSONArray jsonPEPs = jsonPEPGrp.getJSONArray("pepProfiles");
                            int pep_cnt = jsonPEPs.length();
                            for(int j=0; j<pep_cnt; j++){
                                JSONObject jsonPEP = jsonPEPs.getJSONObject(j);
                                PEP pep = new PEP(jsonPEP);
                                addPEPToList(pep);
                            }
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException on query profile",e);
                    }
                } else {
                    Log.e(TAG, "No Response");
                }
                DialogUtil.getInstance().stopProgress(activity);
            }
        };

        listAdapter.clearAll();

        // TODO: 여기 나중에 POST로 고치고 플랫폼매니저에서는 세션키든 패스워드든 사용해서 검증시킬 것.
        // GET이면 userId만 알면 아무나 다 PEP 목록 볼 수 있자너...
        String url = HttpUtil.PLATFORM_MANAGER + "pep-group/profile/" + Login.getId();
        String method = "GET";
        HttpRequest request = HttpRequestFactory.getInstance().create(httpHandler, url, method, null, true);
        HttpRequester http = new HttpRequester(request);
        http.execute();

    }

    private void addPEPToList(PEP pep) {
        listAdapter.addPEP(pep);
        listAdapter.notifyDataSetChanged();
    }

    public void onPEPSelected(final PEP pep) {

        // 선택한 PEP에 로그인
        Handler httpHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String response = msg.getData().getString("msg");
                try {
                    JSONObject j = new JSONObject(response);
                    boolean loggedIn = j.getBoolean("login");
                    if(loggedIn)
                        showChooseNextActivityDialog(pep);
                    else
                        Toast.makeText(activity, "Login Failed.",  Toast.LENGTH_SHORT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        String pepIp = pep.getIp();
        String url =  "http://" + pepIp + "/login";
        String method = "POST";
        HttpRequest request = HttpRequestFactory.getInstance().create(httpHandler, url, method, null, true);
        HttpRequester http = new HttpRequester(request);
        http.execute();

    }


    private void showChooseNextActivityDialog(PEP pep){
        // 선택된 PEP로 Device 등록을 하러갈건지, Device 사용을 하러 갈건지 물어보고 액티비티 넘김
        final CharSequence[] arr = {"Device 등록", "Device 사용"};
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setIcon(R.drawable.ic_launcher_background);
        alert.setTitle("이 PEP 에서...");
        alert.setSingleChoiceItems(arr, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Class<?> nextActivity = (i == 0) ? DeviceRegistrationActivity.class
                        : DeviceListActivity.class ;

                Intent intent = new Intent(activity, nextActivity);
                intent.putExtra("pep", pep);
                activity.startActivity(intent);

                dialog.cancel();
            }
        });
        alert.create().show();
    }
}
