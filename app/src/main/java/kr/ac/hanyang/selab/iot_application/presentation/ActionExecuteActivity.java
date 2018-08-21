package kr.ac.hanyang.selab.iot_application.presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.controller.ActionExecuteController;

public class ActionExecuteActivity extends AppCompatActivity {

    private ActionExecuteController con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_execute);

        if(con == null)
            con = new ActionExecuteController(this);

        con.setParamInputs();

        Button execute = findViewById(R.id.btn_action_execute);
        execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                con.execute();
            }
        });

    }
}
