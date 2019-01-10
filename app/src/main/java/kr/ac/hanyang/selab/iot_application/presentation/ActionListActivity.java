package kr.ac.hanyang.selab.iot_application.presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.controller.ActionListController;
import kr.ac.hanyang.selab.iot_application.domain.DeviceAction;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.ActionListAdapter;

public class ActionListActivity extends AppCompatActivity {

    private static final String TAG = "ActionListActivity";

    private RecyclerView listView;
    private RecyclerView.LayoutManager listLayout;
    private ActionListAdapter listAdapter;

    ActionListController con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_list);
        setHandlers();
        if(con == null)
            con = new ActionListController(this, listAdapter);
        con.listUp();
    }

    private void setHandlers(){

        listView = findViewById(R.id.action_list);
        listView.setHasFixedSize(true);

        listLayout = new LinearLayoutManager(this);
        listView.setLayoutManager(listLayout);

        listAdapter = new ActionListAdapter(new ArrayList<DeviceAction>());
        listView.setAdapter(listAdapter);
        listAdapter.setItemClick(new ActionListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                DeviceAction action = listAdapter.getAction(position);
                con.onActionSelected(action);
            }
        });

    }

}
