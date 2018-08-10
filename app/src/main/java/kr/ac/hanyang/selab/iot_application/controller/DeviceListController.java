package kr.ac.hanyang.selab.iot_application.controller;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.hanyang.selab.iot_application.domain.Device;
import kr.ac.hanyang.selab.iot_application.presentation.DeviceListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.DeviceListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.HttpRequester;

public class DeviceListController {

    private final String TAG = "DeviceListController";

    private List<Device> devices = new ArrayList<>();

    private DeviceListActivity activity;
    private DeviceListAdapter listAdapter;
    private Handler httpHandler;

    public DeviceListController(DeviceListActivity activity, DeviceListAdapter adapter){
        this.activity = activity;
        this.listAdapter = adapter;
    }

    //For testing... one pep group, one pep.
    public void listUp(){

        httpHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String profile = data.getString("msg");
                try {
                    JSONArray pepGrps = new JSONArray(profile);
                    JSONObject pepGrp = pepGrps.getJSONObject(0);
                    JSONArray peps = pepGrp.getJSONArray("pepProfiles");
                    JSONObject pep = peps.getJSONObject(0);
                    JSONArray jsonDevices = pep.getJSONArray("deviceProfiles");
                    int len = jsonDevices.length();
                    for(int i=0; i<len; i++) {
                        JSONObject jsonDevice = jsonDevices.getJSONObject(i);
                        Device device = new Device(jsonDevice);
                        devices.add(device);
                        addDeviceToList(device.getDeviceID());
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException on query profile",e);
                }
            }
        };

        String url = HttpRequester.PLATFORM_MANAGER + "profile/frebern";
        String method = "GET";
        HttpRequester http = new HttpRequester(httpHandler, url, method, null);
        http.request();

    }

    private void addDeviceToList(String deviceID) {
        listAdapter.addDevice(deviceID);
        listAdapter.notifyDataSetChanged();
    }

}
