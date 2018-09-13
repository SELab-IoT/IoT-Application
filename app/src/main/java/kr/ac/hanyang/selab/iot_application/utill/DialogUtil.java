package kr.ac.hanyang.selab.iot_application.utill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

public class DialogUtil {

    private ProgressDialog progress = null;

    // Make class Singleton.
    private DialogUtil(){}
    public static DialogUtil getInstance() {
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final DialogUtil INSTANCE = new DialogUtil();
    }

    public void startProgress(Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress == null)
                    progress = ProgressDialog.show(activity, "Now Loading...", "Now Loading...", true, true);
            }
        });
    }

    public void stopProgress(Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress != null) {
                    progress.dismiss();
                    progress = null;
                }
            }
        });
    }

    public static void showMessage(Context activity, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message).setTitle(title);
        builder.create().show();
    }

}
