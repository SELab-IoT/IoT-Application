package kr.ac.hanyang.selab.iot_application.controller;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import kr.ac.hanyang.selab.iot_application.domain.Device;
import kr.ac.hanyang.selab.iot_application.domain.PEP;
import kr.ac.hanyang.selab.iot_application.presentation.DeviceListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.DeviceListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.HttpRequester;

public class DeviceRegistrationController {
/*
    private final String TAG = "UnregisteredDevListCon";

    private String pepIP;

    private DeviceListActivity activity;
    private DeviceListAdapter listAdapter;
    private Handler httpHandler;

    public DeviceRegistrationController(DeviceListActivity activity, DeviceListAdapter adapter){
        this.activity = activity;
        this.listAdapter = adapter;
        this.pepIP = activity.getIntent().getStringExtra("pepIP");
    }

    public void listUp(){

        httpHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, msg.toString());
                Bundle data = msg.getData();
                String profile = data.getString("msg");
                if(profile != null) {
                    // Handle JSONArray (device id list)
                } else {
                    Log.e(TAG, "No Response");
                }
            }
        };

        String url = pep.getIp() + "profile/frebern";
        String method = "GET";
        HttpRequester http = new HttpRequester(httpHandler, url, method, null);
        http.execute();

    }

    private void addDeviceToList(Device device) {
        listAdapter.addDevice(device);
        listAdapter.notifyDataSetChanged();
    }

    public void requestDeviceRegistration(Device device) {
    }

    */
}
