package kr.ac.hanyang.selab.iot_application.presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.controller.Login;
import kr.ac.hanyang.selab.iot_application.controller.MainMenuController;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        Login.autoLogin();
        setHandlers();
    }

    private void setHandlers(){
        Button registerPEP = findViewById(R.id.btn_pep_registration);
        Button pepList = findViewById(R.id.btn_registered_pep_list);
        Button editPolicy = findViewById(R.id.btn_policy_edit);
        registerPEP.setOnClickListener(new MenuButtonHandler());
        pepList.setOnClickListener(new MenuButtonHandler());
        editPolicy.setOnClickListener(new MenuButtonHandler());
    }

    class MenuButtonHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int id = view.getId();
            MainMenuController con = new MainMenuController();
            switch(id){
                case R.id.btn_pep_registration:
                    con.registerPEP(MainMenuActivity.this);
                    break;
                case R.id.btn_registered_pep_list:
                    con.pepList(MainMenuActivity.this);
                    break;
                case R.id.btn_policy_edit:
                    con.editPolicy(MainMenuActivity.this);
                    break;
            }
        }
    }


}
