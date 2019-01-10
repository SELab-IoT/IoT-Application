package kr.ac.hanyang.selab.iot_application.controller;

import android.content.Context;
import android.content.Intent;

import kr.ac.hanyang.selab.iot_application.presentation.PEPListActivity;
import kr.ac.hanyang.selab.iot_application.presentation.PEPRegistrationActivity;
import kr.ac.hanyang.selab.iot_application.presentation.PolicyEditorActivity;

public class MainMenuController {

    // 새 PEP 등록
    // 먼저 PEP와 블루투스 페어링을 한 후여야 한다.
    public void registerPEP(Context ctx){
        Intent intent = new Intent(ctx, PEPRegistrationActivity.class);
        ctx.startActivity(intent);
    }

    // 등록된 PEP 목록
    // PEP에 Device를 등록하거나, 등록된 Device를 사용할 수 있음.
    public void pepList(Context ctx){
        Intent intent = new Intent(ctx, PEPListActivity.class);
        ctx.startActivity(intent);
    }

    // 정책 편집
    // 현재 미구현
    public void editPolicy(Context ctx){
        Intent intent = new Intent(ctx, PolicyEditorActivity.class);
        ctx.startActivity(intent);
    }

}
