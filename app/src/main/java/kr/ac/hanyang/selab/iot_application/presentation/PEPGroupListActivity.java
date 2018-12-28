package kr.ac.hanyang.selab.iot_application.presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.controller.PEPGroupListController;
import kr.ac.hanyang.selab.iot_application.domain.PEPGroup;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.PEPGroupListAdapter;

public class PEPGroupListActivity extends AppCompatActivity {

    private static final String TAG = "PEPGrpListActivity";

    private RecyclerView listView;
    private RecyclerView.LayoutManager listLayout;
    private PEPGroupListAdapter listAdapter;

    PEPGroupListController con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pepgrouplist);

        setHandlers();
        if(con == null)
            con = new PEPGroupListController(this, listAdapter);

        con.listUp();

    }

    private void setHandlers(){

        listView = findViewById(R.id.pep_list);
        listView.setHasFixedSize(true);

        listLayout = new LinearLayoutManager(this);
        listView.setLayoutManager(listLayout);

        listAdapter = new PEPGroupListAdapter(new ArrayList<PEPGroup>());
        listView.setAdapter(listAdapter);
        listAdapter.setItemClick(new PEPGroupListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                PEPGroup pepGroup = listAdapter.getPEPGroup(position);
                con.onPEPGroupSelected(pepGroup);
            }
        });

    }

}
