package kr.ac.hanyang.selab.iot_application.utill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.widget.EditText;

public class DialogUtil {

    private ProgressDialog progress = null;
    private Activity tempActivity = null;

    // Make class Singleton.
    private DialogUtil(){}
    public static DialogUtil getInstance() {
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder {
        private static final DialogUtil INSTANCE = new DialogUtil();
    }

    public void startProgress(Activity activity){
        this.tempActivity = activity;
        tempActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress == null)
                    progress = ProgressDialog.show(activity, "Now Loading...", "Now Loading...", true, true);
            }
        });
    }

    public void stopProgress(){
        if(tempActivity == null) return;
        tempActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress != null) {
                    progress.dismiss();
                    progress = null;
                }
            }
        });
    }

    public void showPasswordPrompt(Activity activity, Handler onOk, Handler onCancel)
    {
        AlertDialog.Builder prompt = new AlertDialog.Builder(activity);
        prompt.setTitle("PEP Group PW");
        prompt.setMessage("Input PEP Group Password");

        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        prompt.setView(input);

        prompt.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("pepGroupPW", input.getText().toString());
                msg.setData(bundle);
                onOk.sendMessage(msg);
            }
        });

        prompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onCancel.sendMessage(new Message());
            }
        });

        // show it
        prompt.show();

    }

    public static void showMessage(Context activity, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message).setTitle(title);
        builder.create().show();
    }

}
