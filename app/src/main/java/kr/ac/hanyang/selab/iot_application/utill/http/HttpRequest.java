package kr.ac.hanyang.selab.iot_application.utill.http;
import android.os.Handler;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequest {
    private URL url;
    private String method;
    private Handler handler;
    private String params;

    public HttpRequest(Handler handler, URL url, String method, String params){
        this.url = url;
        this.method = method;
        this.handler = handler;
        this.params = params;
    }

    public HttpRequest(Handler handler, String url, String method, String params){
        try {
            this.url = new URL(url);
            this.method = method;
            this.handler = handler;
            this.params = params;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public URL getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Handler getHandler() {
        return handler;
    }

    public String getParams() {
        return params;
    }

}
