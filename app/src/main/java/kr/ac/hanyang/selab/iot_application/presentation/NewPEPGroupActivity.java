package kr.ac.hanyang.selab.iot_application.presentation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.controller.NewPEPGroupController;
import kr.ac.hanyang.selab.iot_application.controller.PEPRegistrationController;
import kr.ac.hanyang.selab.iot_application.domain.PEP;

public class NewPEPGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pepgroup);

        Button btn = (Button)findViewById(R.id.btn_create_group);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String group_name = ((EditText)findViewById(R.id.input_group_name)).getText().toString();
                String group_pw = ((EditText)findViewById(R.id.input_group_pw)).getText().toString();
                JSONObject pepProfile = null;
                try {
                    pepProfile = new JSONObject(getIntent().getStringExtra("pepProfile"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new NewPEPGroupController(NewPEPGroupActivity.this, pepProfile).createNewPEPGroup(group_name, group_pw);
            }
        });

    }
}
