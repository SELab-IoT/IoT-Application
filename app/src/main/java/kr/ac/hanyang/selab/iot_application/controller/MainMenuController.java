package kr.ac.hanyang.selab.iot_application.controller;

import android.content.Context;
import android.content.Intent;

import kr.ac.hanyang.selab.iot_application.presentation.PEPListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.PEPRegistrationActivity;
import kr.ac.hanyang.selab.iot_application.presentation.PolicyEditorActivity;

public class MainMenuController {

    // 새 PEP 등록
    public void registerPEP(Context ctx){
        Intent intent = new Intent(ctx, PEPRegistrationActivity.class);
        ctx.startActivity(intent);
    }

    // 등록된 PEP 목록
    public void pepList(Context ctx){
        Intent intent = new Intent(ctx, PEPListActivity.class);
        ctx.startActivity(intent);
    }

    public void editPolicy(Context ctx){
        Intent intent = new Intent(ctx, PolicyEditorActivity.class);
        ctx.startActivity(intent);
    }

}
