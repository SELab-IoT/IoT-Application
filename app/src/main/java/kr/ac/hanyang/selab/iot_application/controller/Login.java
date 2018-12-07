package kr.ac.hanyang.selab.iot_application.controller;

// TODO: 시간나면 Singleton으로
public class Login {

    //Maybe Temporal
    private static String id="frebern";
    private static String pwd="selab10T";
    private static String sessionKey;

    //Temporal
    public static boolean autoLogin(){
        return login(Login.id, Login.pwd);
    }

    public static boolean login(String id, String pwd){
        //TODO : PlatformManager 로그인 구현 후 로그인 요청해서 세션키 받아오기.
        sessionKey="sessionKey";
        return true;
    }

    public static String getId() {
        return id;
    }

    public static String getSessionKey() {
        return sessionKey;
    }

}
