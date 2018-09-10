package kr.ac.hanyang.selab.iot_application.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.domain.Device;
import kr.ac.hanyang.selab.iot_application.domain.PEP;
import kr.ac.hanyang.selab.iot_application.presentation.DeviceListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.DeviceRegistrationActivity;
import kr.ac.hanyang.selab.iot_application.presentation.PEPListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.PEPListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.HttpRequester;

public class PEPListController {
    private final String TAG = "PEPListController";

    private PEPListActivity activity;
    private PEPListAdapter listAdapter;
    private Handler httpHandler;

    public PEPListController(PEPListActivity activity, PEPListAdapter adapter){
        this.activity = activity;
        this.listAdapter = adapter;
    }

    //For testing... one pep group.
    public void listUp(){

        httpHandler = new Handler(){
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

            }
        };

        listAdapter.clearAll();

        String url = HttpRequester.PLATFORM_MANAGER + "pep-group/profile/" + Login.getId();
        String method = "GET";
        HttpRequester http = new HttpRequester(httpHandler, url, method, null);
        http.execute();

    }

    private void addPEPToList(PEP pep) {
        listAdapter.addPEP(pep);
        listAdapter.notifyDataSetChanged();
    }

    public void onPEPSelected(final PEP pep) {

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
