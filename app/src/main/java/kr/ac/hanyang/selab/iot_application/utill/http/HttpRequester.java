package kr.ac.hanyang.selab.iot_application.utill.http;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class HttpRequester extends AsyncTask<Void, Void, String> {

    private static final String TAG = "HttpRequester";

    private HttpRequest httpRequest;

    public HttpRequester(HttpRequest httpRequest){
        this.httpRequest = httpRequest;
    }

    private String request(){

        Log.d(TAG,"Request: "+ httpRequest.getUrl().toString()+"("+ httpRequest.getMethod()+")");
        HttpURLConnection con = HttpUtil.createHttpURLConnection(httpRequest);
        try{
            Log.d(TAG, "REQUEST BODY:"+ httpRequest.getParams());
            //GET은 이렇게 파라미터 첨부를 할 수 없음.
            if(!httpRequest.getMethod().equalsIgnoreCase("GET")) {
                OutputStream out = con.getOutputStream();
                out.write(httpRequest.getParams().getBytes("UTF-8"));
                out.flush();
                out.close();
            }

            if(con.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String page = "";
            String line = "";

            while((line = reader.readLine()) != null)
                builder.append(line);
            page = builder.toString();
            reader.close();

            Log.d(TAG,"Response: "+page);
            return page;

        } catch (IOException e) {
            Log.e(TAG, "Check the destination server is opened.", e);
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
        httpRequest.getHandler().sendMessage(msg);
    }
}
