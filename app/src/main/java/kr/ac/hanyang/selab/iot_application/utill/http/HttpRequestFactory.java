package kr.ac.hanyang.selab.iot_application.utill.http;

import android.content.ContentValues;
import android.os.Handler;

import java.net.MalformedURLException;
import java.net.URL;

import kr.ac.hanyang.selab.iot_application.controller.Login;

public class HttpRequestFactory {

    private static final HttpRequestFactory instance = new HttpRequestFactory();
    private HttpRequestFactory(){}

    public static HttpRequestFactory getInstance() {
        return instance;
    }

    public HttpRequest create(Handler handler, String url, String method, ContentValues params, boolean userInfo){

        if(userInfo) {
            String userId = Login.getId();
            String sessionKey = Login.getSessionKey();
            if(params == null)
                params = new ContentValues();
            params.put("userId", userId);
            params.put("sessionKey", sessionKey);
        }

        try {

            if(method.equalsIgnoreCase("GET"))
                return createGETRequest(handler, url, method, params);
            else if(method.equalsIgnoreCase("POST"))
                return createPOSTRequest(handler, url, method, params);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HttpRequest createGETRequest(Handler handler, String strUrl, String method, ContentValues params) throws MalformedURLException {
        String strParams = HttpUtil.toGETParameterForm(params);
        URL url = new URL(strUrl + "?" + strParams);
        return new HttpRequest(handler, url, method, strParams);
    }

    public HttpRequest createPOSTRequest(Handler handler, String strUrl, String method, ContentValues params) throws MalformedURLException {
        String strParams = HttpUtil.toPOSTParameterForm(params);
        URL url = new URL(strUrl);
        return new HttpRequest(handler, url, method, strParams);
    }
}
