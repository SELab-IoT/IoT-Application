package kr.ac.hanyang.selab.iot_application.controller;

import android.content.ContentValues;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.hanyang.selab.iot_application.utill.Hash;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequest;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequestFactory;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpRequester;
import kr.ac.hanyang.selab.iot_application.utill.http.HttpUtil;

public class Login {

    private static final String TAG = "Login";

    //Temporal
    private static String id="frebern";
    private static String pwd="passwd";

    private static String sessionKey;

    //Temporal
    public static boolean autoLogin(){
        return login(id, pwd);
    }

    public static boolean login(String id, String pwd){

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    JSONObject json = new JSONObject(msg.getData().getString("msg"));
                    sessionKey = json.getString("sessionKey");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        String hashed = Hash.SHA3_256(pwd);

        String url = HttpUtil.PLATFORM_MANAGER + "login";
        String method = "POST";
        ContentValues params = new ContentValues();
        params.put("userId", id);
        params.put("userPW", hashed);
        HttpRequest request = HttpRequestFactory.getInstance().create(handler, url, method, params, false);
        HttpRequester http = new HttpRequester(request);
        http.execute();

        return true;
    }

    public static String getId() {
        return id;
    }

    public static String getSessionKey() {
        return sessionKey;
    }

}
