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
        Button searchPEP = findViewById(R.id.btn_pep_search);
        Button editPolicy = findViewById(R.id.btn_policy_edit);
        searchPEP.setOnClickListener(new MenuButtonHandler());
        editPolicy.setOnClickListener(new MenuButtonHandler());
    }

    class MenuButtonHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int id = view.getId();
            MainMenuController con = new MainMenuController();
            switch(id){
                case R.id.btn_pep_search:
                    con.searchPEP(MainMenuActivity.this);
                    break;
                case R.id.btn_policy_edit:
                    con.editPolicy(MainMenuActivity.this);
                    break;
            }
        }
    }


}
