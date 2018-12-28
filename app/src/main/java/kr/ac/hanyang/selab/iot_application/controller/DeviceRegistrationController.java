package kr.ac.hanyang.selab.iot_application.controller;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kr.ac.hanyang.selab.iot_application.domain.Device;
import kr.ac.hanyang.selab.iot_application.domain.PEP;
import kr.ac.hanyang.selab.iot_application.presentation.DeviceRegistrationActivity;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.UnregisteredDeviceListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequestFactory;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequester;
import kr.ac.hanyang.selab.iot_application.utill.DialogUtil;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequest;

public class DeviceRegistrationController {

    private final String TAG = "DevRegistrationCon";

    private DeviceRegistrationActivity activity;
    private UnregisteredDeviceListAdapter listAdapter;

    public DeviceRegistrationController(DeviceRegistrationActivity activity, UnregisteredDeviceListAdapter adapter){
        this.activity = activity;
        this.listAdapter = adapter;
    }

    public void listUp(){

        DialogUtil.getInstance().startProgress(activity);

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                DialogUtil.getInstance().stopProgress(activity);
                String response = msg.getData().getString("msg");
                if(response != null) {
                    try {
                        JSONArray names = new JSONArray(response);
                        int len = names.length();

                        if(len == 0) DialogUtil.showMessage(activity, "Sorry", "No Devices nearby PEP");

                        for(int i=0; i<len; i++)
                            addDeviceNameToList(names.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "No Response");
                }
            }
        };

        listAdapter.clearAll();

        PEP pep = (PEP) activity.getIntent().getSerializableExtra("pep");

        String url = "http://" + pep.getIp() + "/devices/scan";
        String method = "GET";
        HttpRequest request = HttpRequestFactory.getInstance().create(handler, url, method, null, false);
        HttpRequester http = new HttpRequester(request);
        http.execute();

    }

    private void addDeviceNameToList(String deviceName) {
        listAdapter.addDeviceName(deviceName);
        listAdapter.notifyDataSetChanged();
    }

    public void requestDevicesRegistration(List<String> connectList) {

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                DialogUtil.getInstance().stopProgress(activity);

                String response = msg.getData().getString("msg");
                DialogUtil.showMessage(activity, "Device Registration Result:", response);
                Log.d(TAG, response);

            }
        };

        DialogUtil.getInstance().startProgress(activity);
        PEP pep = (PEP) activity.getIntent().getSerializableExtra("pep");
        String url = "http://" + pep.getIp() + "/devices";
        String method = "POST";
        ContentValues params = new ContentValues();
        JSONArray arr = new JSONArray(connectList);
        Log.d(TAG, arr.toString());
        params.put("deviceList", arr.toString());
        HttpRequest request = HttpRequestFactory.getInstance().create(handler, url, method, params, true);
        HttpRequester http = new HttpRequester(request);
        http.execute();
    }
}
