package kr.ac.hanyang.selab.iot_application.controller;

import android.content.Intent;
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
import kr.ac.hanyang.selab.iot_application.presentation.ActionListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.DeviceListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.DeviceListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.HttpRequester;

public class DeviceListController {

    private final String TAG = "AvailableDevListCon";

    private DeviceListActivity activity;
    private DeviceListAdapter listAdapter;
    
    private PEP pep;

    public DeviceListController(DeviceListActivity activity, DeviceListAdapter adapter){
        this.activity = activity;
        this.listAdapter = adapter;
        pep = (PEP) activity.getIntent().getSerializableExtra("pep");
    }

    public void listUp(){
        List<Device> devices = pep.getDevices();
        int size = devices.size();
        for(int i=0; i<size; i++)
            addDeviceToList(devices.get(i));
    }

    private void addDeviceToList(Device device) {
        listAdapter.addDevice(device);
        listAdapter.notifyDataSetChanged();
    }

    public void onDeviceSelected(Device device) {
        Intent intent = new Intent(activity, ActionListActivity.class);
        intent.putExtra("device", device);
        intent.putExtra("pep", pep);
        activity.startActivity(intent);
    }
}
