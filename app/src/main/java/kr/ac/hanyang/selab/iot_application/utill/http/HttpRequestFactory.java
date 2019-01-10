package kr.ac.hanyang.selab.iot_application.utill.http;

import android.content.ContentValues;
import android.os.Handler;

import java.net.MalformedURLException;
import java.net.URL;

import kr.ac.hanyang.selab.iot_application.controller.Login;

// 아마 userInfo 때문에 만들었던것 같은데 더 좋은 방법으로 리팩토링 할 수 있을 것 같다.
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
        if(method.equalsIgnoreCase("GET"))
            return createGETRequest(handler, url, params);
        else if(method.equalsIgnoreCase("POST"))
            return createPOSTRequest(handler, url, params);
        return null;
    }

    public HttpRequest createGETRequest(Handler handler, String strUrl, ContentValues params){
        String strParams = HttpUtil.toGETParameterForm(params);
        URL url = null;
        try {
            url = new URL(strUrl + "?" + strParams);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new HttpRequest(handler, url, "GET", strParams);
    }

    public HttpRequest createPOSTRequest(Handler handler, String strUrl, ContentValues params){
        String strParams = HttpUtil.toPOSTParameterForm(params);
        URL url = null;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new HttpRequest(handler, url, "POST", strParams);
    }
}
