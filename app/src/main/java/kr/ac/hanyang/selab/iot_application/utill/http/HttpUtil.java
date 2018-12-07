package kr.ac.hanyang.selab.iot_application.utill.http;

import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {

    public static final String PLATFORM_MANAGER = "http://selab.hanyang.ac.kr:8080/";

    private HttpUtil(){}

    public static String toGETParameterForm(ContentValues params) {
        if(params == null) return "";
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (Map.Entry<String, Object> param : params.valueSet()) {
                result.append(first ? "" : "&");
                first = false;
                result.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(param.getValue().toString(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static String toPOSTParameterForm(ContentValues params) {

        JSONObject ps = new JSONObject();
        if(params != null) {
            for(Map.Entry<String, Object> param : params.valueSet()){
                String key = param.getKey();
                Object value = param.getValue();
                try {
                    ps.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return ps.toString();
        }
        else return "";
    }

    public static HttpURLConnection createHttpURLConnection(HttpRequest httpRequest) {

        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) httpRequest.getUrl().openConnection();
            con.setRequestProperty("Accept-Charset","UTF-8");
            //application/x-www-form-urlencoded은 파라메터 포맷은 똑같고 url에 넣는가 OutputStream으로 쏘는가 차이이다.
            con.setRequestProperty("Content-type", "application/json; charset=UTF-8");
            con.setRequestMethod(httpRequest.getMethod());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return con;

    }

}
