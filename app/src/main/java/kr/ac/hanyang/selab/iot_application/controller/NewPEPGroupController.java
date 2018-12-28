package kr.ac.hanyang.selab.iot_application.controller;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.hanyang.selab.iot_application.domain.PEP;
import kr.ac.hanyang.selab.iot_application.utill.DialogUtil;
import kr.ac.hanyang.selab.iot_application.utill.Hash;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequest;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequestFactory;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequester;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpUtil;

public class NewPEPGroupController {

    private static final String TAG = "NewPEPGroup";
    private Activity activity;
    private JSONObject pepProfile;

    public NewPEPGroupController(Activity activity, JSONObject pepProfile){
        this.activity = activity;
        this.pepProfile = pepProfile;
    }

    // Step 7.a.1. 새 그룹 생성 ~ Step 8
    public void createNewPEPGroup(String group_name, String group_pw) {
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String response = msg.getData().getString("msg");
                try {
                    JSONObject json = new JSONObject(response);
                    boolean success = json.getBoolean("success");
                    if(success)
                        DialogUtil.showMessage(activity, "성공!", "PEP Group이 정상적으로 생성되었습니다!\nPEP가 정상적으로 등록되었습니다!");
                    else
                        DialogUtil.showMessage(activity, "등록 실패..!", "오류가 발생했습니다! : "+json.getString("reason"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        String url = HttpUtil.PLATFORM_MANAGER + "groups";

        ContentValues params = new ContentValues();
        params.put("userId", Login.getId());
        params.put("pepGroupName", group_name);
        params.put("pepGroupPW", Hash.SHA3_256(group_pw));
        params.put("pepProfile", pepProfile.toString());
        HttpRequest request = HttpRequestFactory.getInstance().createPOSTRequest(handler, url, params);
        HttpRequester http = new HttpRequester(request);
        http.execute();
    }
}
