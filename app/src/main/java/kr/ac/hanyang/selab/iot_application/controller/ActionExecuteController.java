package kr.ac.hanyang.selab.iot_application.controller;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequestFactory;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequester;
import kr.ac.hanyang.selab.iot_application.utill.DialogUtil;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequest;

public class ActionExecuteController {

    private final String TAG = "ActionExecCon";

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

        DialogUtil.getInstance().startProgress(activity);

        // 실행 결과
        httpHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                DialogUtil.getInstance().stopProgress();

                Bundle data = msg.getData();
                Log.d(TAG, data.toString());
                try {
                    JSONObject json = new JSONObject(data.getString("msg"));
                    //{"advices":[],"decision":true/false,"success":true/false}
                    boolean decision = json.getBoolean("decision");
                    boolean success = json.getBoolean("success");
                    String resultMsg;
                    resultMsg = "정책 평가 결과 : "+ (decision ? "Permit" : "Deny(혹은 NA/Ind)") + "\n";
                    resultMsg += "디바이스 동작 여부 : " + success;
                    //resultMsg += advices

                    DialogUtil.showMessage(activity, "PDP Evaluation Result:", resultMsg);

                } catch (JSONException e) {
                    DialogUtil.showMessage(activity, "Error:", "Error Occurred during get result");
                    e.printStackTrace();
                }
            }
        };

        // Set Payload
        Intent intent = activity.getIntent();
        DeviceAction action = (DeviceAction) intent.getSerializableExtra("action");
        Device device = (Device) intent.getSerializableExtra("device");

        ContentValues requestParam = new ContentValues();

        JSONArray jsonParams = new JSONArray();
        int size = params.size();
        for(int i=0; i<size; i++) {
            String paramValue = params.get(i).getEditText().getText().toString();
            String paramName = action.getParams().get(i).get("name");
            String paramType = action.getParams().get(i).get("type");

            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("name", paramName);
                jsonParam.put("type", paramType);
                jsonParam.put("value", paramValue);
                jsonParams.put(jsonParam);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        requestParam.put("deviceName", device.getDeviceName());
        requestParam.put("macAddress", device.getMacAddress());
        requestParam.put("actionName", action.getActionName());
        requestParam.put("actionId", action.getActionId());
        requestParam.put("params", jsonParams.toString());

        PEP pep = (PEP) intent.getSerializableExtra("pep");

        String deviceId = device.getDeviceId();
        String pepIp = pep.getIp();
        String url =  "http://" + pepIp + "/devices/" + deviceId;
        String method = "POST";
        HttpRequest request = HttpRequestFactory.getInstance().create(httpHandler, url, method, requestParam, true);
        HttpRequester http = new HttpRequester(request);
        http.execute();

    }

}
