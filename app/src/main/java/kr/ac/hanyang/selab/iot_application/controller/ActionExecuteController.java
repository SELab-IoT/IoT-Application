package kr.ac.hanyang.selab.iot_application.controller;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
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
import kr.ac.hanyang.selab.iot_application.domain.PEP;
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
        t.append(action.getActionName());

        List<Map<String, String>> params = action.getParams();
        int size = params.size();
        for(int i=0; i<size; i++)
            addParamInput(params.get(i));
    }

    private void addParamInput(Map<String, String> param){

        String name = param.get("name");
        String type = param.get("type");

        TextInputLayout field = new TextInputLayout(activity);
        field.setHint(name + "(" + type + ")");

        TextInputEditText input = new TextInputEditText(activity);
        field.addView(input);

        LinearLayout paramsLayout = activity.findViewById(R.id.layout_params);
        paramsLayout.addView(field);

        params.add(field);

    }

    public void execute(){

        // 실행 결과 통보
        httpHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                Toast.makeText(activity, data.toString(), Toast.LENGTH_LONG);
            }
        };

        // Set Payload
        Intent intent = activity.getIntent();
        DeviceAction action = (DeviceAction) intent.getSerializableExtra("action");
        Device device = (Device) intent.getSerializableExtra("device");

        ContentValues requestParam = new ContentValues();

        JSONArray paramValues = new JSONArray();
        int size = params.size();
        for(int i=0; i<size; i++) {
            String param = params.get(i).getEditText().getText().toString();
            paramValues.put(param);
        }

        requestParam.put("userId", Login.getId());
        requestParam.put("action", action.getActionName());
        requestParam.put("params", paramValues.toString());

        PEP pep = (PEP) intent.getSerializableExtra("pep");

        String deviceId = device.getDeviceId();
        String pepIp = pep.getIp();
        String url =  "http://" + pepIp + "/devices/" + deviceId;
        String method = "POST";
        HttpRequester http = new HttpRequester(httpHandler, url, method, requestParam);
        http.execute();

    }

}
