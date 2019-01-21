package kr.ac.hanyang.selab.iot_application.controller;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.hanyang.selab.iot_application.domain.PEPGroup;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.PEPGroupListAdapter;
import kr.ac.hanyang.selab.iot_application.utill.DialogUtil;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequest;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequestFactory;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequester;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpUtil;

public class PEPGroupListController {

    private static final String TAG = "PEPGrpListCon";

    private Activity activity;
    private PEPGroupListAdapter listAdapter;

    public PEPGroupListController(Activity activity, PEPGroupListAdapter listAdapter) {
        this.activity = activity;
        this.listAdapter = listAdapter;
    }

    public void listUp() {
        DialogUtil.getInstance().startProgress(activity);
        Handler httpHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, msg.toString());
                Bundle data = msg.getData();
                String profile = data.getString("msg");
                if(profile != null) {
                    try {
                        JSONArray jsonPEPGrps = new JSONArray(profile);
                        int grp_cnt = jsonPEPGrps.length();
                        for(int i=0; i < grp_cnt; i++) {
                            JSONObject jsonPEPGrp = jsonPEPGrps.getJSONObject(i);
                            long pepGroupId = jsonPEPGrp.getLong("pepGroupId");
                            String pepGroupName = jsonPEPGrp.getString("pepGroupName");
                            addPEPGroupToList(new PEPGroup(pepGroupId, pepGroupName));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException on query profile",e);
                    }
                } else {
                    Log.e(TAG, "No Response");
                }
                DialogUtil.getInstance().stopProgress();
            }
        };

        listAdapter.clearAll();

        String url = HttpUtil.PLATFORM_MANAGER + "pep-group/profile/";
        String method = "POST";
        HttpRequest request = HttpRequestFactory.getInstance().create(httpHandler, url, method, null, true);
        HttpRequester http = new HttpRequester(request);
        http.execute();
    }

    private void addPEPGroupToList(PEPGroup pepGroup) {
        listAdapter.addPEPGroup(pepGroup);
        listAdapter.notifyDataSetChanged();
    }

    public void onPEPGroupSelected(final PEPGroup pepGroup) {

        //Step 7.a.2. 기존 그룹에 추가 ~ Step 8
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, msg.toString());
                try {
                    JSONObject json = new JSONObject(msg.getData().getString("msg"));

                    boolean success = json.getBoolean("success");
                    if(success)
                        DialogUtil.showMessage(activity, "성공!", "선택한 PEPGroup에 PEP가 정상적으로 등록되었습니다!");
                    else
                        DialogUtil.showMessage(activity, "등록 실패..!", "오류가 발생했습니다! : "+json.getString("reason"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        String url = HttpUtil.PLATFORM_MANAGER + "groups";

        JSONObject pepProfile = null;
        try {
            pepProfile = new JSONObject(activity.getIntent().getStringExtra("pepProfile"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContentValues params = new ContentValues();
        params.put("userId", Login.getId());
        params.put("pepGroupId", pepGroup.getId());
        params.put("pepProfile", pepProfile.toString());
        HttpRequest request = HttpRequestFactory.getInstance().createPOSTRequest(handler, url, params);
        HttpRequester http = new HttpRequester(request);
        http.execute();
    }

}
