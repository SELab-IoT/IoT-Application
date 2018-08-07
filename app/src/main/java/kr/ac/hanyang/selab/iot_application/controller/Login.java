package kr.ac.hanyang.selab.iot_application.controller;

public class Login {

    //Maybe Temporal
    private static String id="frebern";
    private static String pwd="selab10T";

    //Temporal
    public static boolean autoLogin(){
        return login(Login.id, Login.pwd);
    }

    public static boolean login(String id, String pwd){

        return true;
    }
}
