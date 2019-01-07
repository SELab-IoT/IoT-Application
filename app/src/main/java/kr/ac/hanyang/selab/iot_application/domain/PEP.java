package kr.ac.hanyang.selab.iot_application.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PEP implements Serializable {
    private String pepId;
    private String pepName;
    private String ip;
    private List<Device> devices;

    public PEP(JSONObject pep){

        if(devices == null)
            devices = new ArrayList<>();
        devices.clear();

        try {
            pepId = pep.getString("pepId");
            pepName = pep.getString("pepName");
            ip = pep.getString("ip");
            JSONArray jsonDevices = pep.getJSONArray("deviceProfiles");
            int len = jsonDevices.length();
            for(int i=0; i<len; i++) {
                JSONObject jsonDevice = jsonDevices.getJSONObject(i);
                Device device = new Device(jsonDevice);
                devices.add(device);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getId() {
        return pepId;
    }

    public String getPEPName() {return pepName; }

    public String getIp() {
        return ip;
    }

    public List<Device> getDevices() {
        return devices;
    }

}
