package kr.ac.hanyang.selab.iot_application.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Device implements Serializable {

    private String deviceId;
    private String deviceName;
    private String macAddress;
    private List<DeviceAction> actions;

    public Device(JSONObject device) {
        this.actions = new ArrayList<>();

        try {
            deviceId = device.getString("deviceId");
            deviceName = device.getString("deviceName");
            macAddress = device.getString("macAddress");
            JSONArray actions = device.getJSONArray("actions");
            int len = actions.length();
            for(int i=0;i<len;i++){
                JSONObject action = actions.getJSONObject(i);
                this.actions.add(new DeviceAction(action));
            }
        } catch (JSONException e) {
            actions.clear();
            e.printStackTrace();
        }
    }

    public Device(String id){
        this.deviceId = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getMacAddress() { return macAddress; }

    public List<DeviceAction> getActions() {
        return actions;
    }
}
