package kr.ac.hanyang.selab.iot_application.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceAction implements Serializable{

    private String actionId;
    private String actionName;
    private List<Map<String, String>> params;

    public DeviceAction(JSONObject action){

        params = new ArrayList<>();
        try {
            actionId = action.getString("actionId");
            actionName = action.getString("actionName");
            JSONArray params = new JSONArray(action.getString("params"));

            int len = params.length();
            for(int i=0;i<len;i++){
                JSONObject jsonParam = params.getJSONObject(i);
                String name = jsonParam.getString("name");
                String type = jsonParam.getString("type");
                Map<String, String> param = new HashMap<>();
                param.put("name", name);
                param.put("type", type);
                this.params.add(param);
            }
        } catch (JSONException e) {
            params.clear();
            e.printStackTrace();
        }
    }

    public String getActionId() {
        return actionId;
    }

    public String getActionName() {
        return actionName;
    }

    public List<Map<String, String>> getParams() {
        return params;
    }
}