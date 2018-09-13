package kr.ac.hanyang.selab.iot_application.utill;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HttpRequester extends AsyncTask<Void, Void, String> {

    private static final String TAG = "HttpRequester";
    public static final String PLATFORM_MANAGER = "http://selab.hanyang.ac.kr:8080/";

    private Handler handler;
    private URL url;
    private String method;
    private String strParams;

    public HttpRequester(Handler handler, String url, String method, ContentValues params){
        try {
            this.handler = handler;
            this.url = new URL(url);
            this.method = method;

            if(method.equals("GET")) {
                this.strParams = toGETParameterForm(params);
                this.url = new URL(url.toString()+"?"+strParams);
            } else if(method.equals("POST"))
                this.strParams = toPOSTParameterForm(params);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private String toGETParameterForm(ContentValues params) {
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

    private String toPOSTParameterForm(ContentValues params) {
        //TODO: 차후 POST로 보낼 때 Json을 사용하게 되는 경우 이부분을 수정하도록 한다.
        JSONObject ps = new JSONObject();
        if(params != null) {
            for(Map.Entry<String, Object> param : params.valueSet()){
                String key = param.getKey();
                String value = param.getValue().toString();

                try {
                    //is value JSON?
                    JSONObject test = new JSONObject("{\"" + key + "\":" + value + "}");
                    ps.put(key, test.get(key));
                } catch (JSONException e) {
                    try {
                        ps.put(key, value);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }

            }
            return ps.toString();
        }
        else return "";
    }

    private String request(){

        Log.d(TAG,"Request: "+url+"("+method+")");

        HttpURLConnection con = null;

        try{

            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod(this.method);
            con.setRequestProperty("Accept-Charset","UTF-8");

            //application/x-www-form-urlencoded은 파라메터 포맷은 똑같고 url에 넣는가 OutputStream으로 쏘는가 차이이다.
            con.setRequestProperty("Content-type", "application/json; charset=UTF-8");

            Log.d(TAG, "REQUEST BODY:"+strParams);

            //GET은 이렇게 파라미터 첨부를 할 수 없음.
            if(!this.method.equals("GET")) {
                OutputStream out = con.getOutputStream();
                out.write(strParams.getBytes("UTF-8"));
                out.flush();
                out.close();
            }

            if(con.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String page = "";
            String line = "";

            while((line = reader.readLine()) != null)
                page += line;
            reader.close();

            Log.d(TAG,"Response: "+page);
            return page;

        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed URL", e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException - Check the destination server is opened.", e);
            e.printStackTrace();
        } finally {
            if(con != null) con.disconnect();
        }
        return null;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return request();
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("msg", s);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
