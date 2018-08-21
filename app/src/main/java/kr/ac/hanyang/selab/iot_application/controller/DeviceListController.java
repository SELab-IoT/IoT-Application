package kr.ac.hanyang.selab.iot_application.controller;

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
import kr.ac.hanyang.selab.iot_application.presentation.DeviceListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.DeviceListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.HttpRequester;

public class DeviceListController {

    private final String TAG = "DeviceListController";

    private PEP pep;

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
                Log.d(TAG, msg.toString());
                Bundle data = msg.getData();
                String profile = data.getString("msg");
                if(profile != null) {
                    try {
                        //디버그를 위해 PEPGroup하고 PEP는 한개씩 있다고 가정.
                        JSONArray jsonPEPGrps = new JSONArray(profile);
                        JSONObject jsonPEPGrp = jsonPEPGrps.getJSONObject(0);
                        JSONArray jsonPEPs = jsonPEPGrp.getJSONArray("pepProfiles");
                        JSONObject jsonPEP = jsonPEPs.getJSONObject(0);

                        pep = new PEP(jsonPEP);
                        List<Device> devices = pep.getDevices();
                        int size = devices.size();
                        for (int i = 0; i < size; i++)
                            addDeviceToList(devices.get(i));

                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException on query profile",e);
                    }
                } else {
                    Log.e(TAG, "No Response");
                }

            }
        };

        String url = HttpRequester.PLATFORM_MANAGER + "profile/frebern";
        String method = "GET";
        HttpRequester http = new HttpRequester(httpHandler, url, method, null);
        http.execute();

    }

    private void addDeviceToList(Device device) {
        listAdapter.addDevice(device);
        listAdapter.notifyDataSetChanged();
    }

    public PEP getPEP() {
        return pep;
    }
}
