package kr.ac.hanyang.selab.iot_application.utill;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class HttpRequester extends AsyncTask<Void, Void, String> {

    private static final String TAG = "HttpRequester";

    // TODO: 테스팅 시 selab.hanyang.ac.kr로 플랫폼 매니저 이식 후 아래 주소 바꾸고 테스팅.
    public static final String PLATFORM_MANAGER = "http://166.104.185.84:8080/";

    private Handler handler;
    private URL url;
    private String method;
    private ContentValues params;

    public HttpRequester(Handler handler, String url, String method, ContentValues params){
        try {
            this.handler = handler;
            this.url = new URL(url);
            this.method = method;
            this.params = params;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private String request(){

        Log.d(TAG,"Request: "+url+"("+method+")");

        HttpURLConnection con = null;
        StringBuffer params = new StringBuffer();
        if(this.params == null)
            params.append("");
        else{
            boolean isAnd = false;
            String key, value;
            for(Map.Entry<String, Object> param : this.params.valueSet()){
                key = param.getKey();
                value = param.getValue().toString();
                if(isAnd)
                    params.append("&");
                params.append(key).append("=").append(value);
                isAnd = !isAnd & this.params.size() >= 2;
            }
        }

        try{
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod(this.method);
            con.setRequestProperty("Accept-Charset","UTF-8");
            con.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");

            String strParams = params.toString();
            OutputStream out = con.getOutputStream();
            out.write(strParams.getBytes("UTF-8"));
            out.flush();
            out.close();

            if(con.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            String line;
            String page = "";

            while((line = reader.readLine()) != null)
                page += line;

            Log.d(TAG,"Response: "+page);
            return page;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
