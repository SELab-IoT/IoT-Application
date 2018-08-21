package kr.ac.hanyang.selab.iot_application.controller;

import android.content.Intent;
import android.widget.TextView;

import java.util.List;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.domain.Device;
import kr.ac.hanyang.selab.iot_application.domain.DeviceAction;
import kr.ac.hanyang.selab.iot_application.presentation.ActionListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.ActionListAdapter;

public class ActionListController {
    private final String TAG = "ActionListController";

    private ActionListActivity activity;
    private ActionListAdapter listAdapter;

    private Intent intent;
    private Device device;

    public ActionListController(ActionListActivity activity, ActionListAdapter adapter){
        this.activity = activity;
        this.listAdapter = adapter;
        this.intent = activity.getIntent();
        device = (Device) intent.getSerializableExtra("device");

    }

    public void listUp(){
        List<DeviceAction> actions = device.getActions();

        TextView t = activity.findViewById(R.id.text_device_Id);
        t.setText(t.getText() + device.getDeviceID());
        int size = actions.size();
        for(int i=0;i<size;i++)
            addActionToList(actions.get(i));

    }

    private void addActionToList(DeviceAction action) {
        listAdapter.addAction(action);
        listAdapter.notifyDataSetChanged();
    }

    public Device getDevice() {
        return device;
    }
}
