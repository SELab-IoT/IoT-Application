package kr.ac.hanyang.selab.iot_application.controller;

import android.content.Intent;

import java.util.List;

import kr.ac.hanyang.selab.iot_application.domain.Device;
import kr.ac.hanyang.selab.iot_application.domain.PEP;
import kr.ac.hanyang.selab.iot_application.presentation.ActionListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.DeviceListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.DeviceListAdapter;

public class DeviceListController {

    private final String TAG = "AvailableDevListCon";

    private DeviceListActivity activity;
    private DeviceListAdapter listAdapter;

    private PEP pep;

    public DeviceListController(DeviceListActivity activity, DeviceListAdapter adapter){
        this.activity = activity;
        this.listAdapter = adapter;
        this.pep = (PEP) activity.getIntent().getSerializableExtra("pep");
    }

    public void listUp(){

        listAdapter.clearAll();

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
