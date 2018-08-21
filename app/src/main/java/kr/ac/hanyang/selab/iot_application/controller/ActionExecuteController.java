package kr.ac.hanyang.selab.iot_application.controller;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.domain.Device;
import kr.ac.hanyang.selab.iot_application.domain.DeviceAction;
import kr.ac.hanyang.selab.iot_application.presentation.ActionExecuteActivity;
import kr.ac.hanyang.selab.iot_application.utill.HttpRequester;

public class ActionExecuteController {

    private final String TAG = "ActionExecuteController";

    private ActionExecuteActivity activity;
    private List<TextInputLayout> params;

    private Handler httpHandler;

    public ActionExecuteController(ActionExecuteActivity activity){
        this.activity = activity;
        params = new ArrayList<>();
    }

    public void setParamInputs(){
        Intent intent = activity.getIntent();
        DeviceAction action = (DeviceAction)intent.getSerializableExtra("action");
        TextView t = activity.findViewById(R.id.text_action);
        t.append(action.getActionID());

        List<Map<String, String>> params = action.getParams();
        int size = params.size();
        for(int i=0; i<size; i++)
            addParamInput(params.get(i));
    }

    private void addParamInput(Map<String, String> param){

        String name = param.get("name");
        String type = param.get("type");

        TextInputLayout paramInput = new TextInputLayout(activity);
        paramInput.setHint(name + "(" + type + ")");
        params.add(paramInput);

        LinearLayout paramsLayout = activity.findViewById(R.id.layout_params);
        paramsLayout.addView(paramInput);

    }

    public void execute(){

        httpHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                Toast.makeText(activity, data.toString(), Toast.LENGTH_LONG);
            }
        };

        // Set Payload
        String action = ((TextView)activity.findViewById(R.id.text_action)).getText().toString();

        ContentValues requestParam = new ContentValues();
        JSONObject payload = new JSONObject();

        try {
            payload.put("action", action);
            JSONArray paramValues = new JSONArray();
            int size = params.size();
            for(int i=0; i<size; i++)
                paramValues.put(params.get(i));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestParam.put("param", payload.toString());

        Intent intent = activity.getIntent();
        String deviceID = ((Device) intent.getSerializableExtra("device")).getDeviceID();
        String pepIP = intent.getStringExtra("pepIP");
        String url =  pepIP + "/devices/" + deviceID;
        String method = "POST";
        HttpRequester http = new HttpRequester(httpHandler, url, method, requestParam);
        http.execute();

    }

}
