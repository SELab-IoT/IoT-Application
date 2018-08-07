package kr.ac.hanyang.selab.iot_application.controller;

import android.content.Context;
import android.content.Intent;

import kr.ac.hanyang.selab.iot_application.presentation.PEPListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.PolicyEditorActivity;

public class MainMenuController {
    public void searchPEP(Context ctx){
        Intent intent = new Intent(ctx, PEPListActivity.class);
        ctx.startActivity(intent);
    }
    public void editPolicy(Context ctx){
        Intent intent = new Intent(ctx, PolicyEditorActivity.class);
        ctx.startActivity(intent);
    }
}
