package kr.ac.hanyang.selab.iot_application.presentation;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Map;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.controller.PEPListController;
import kr.ac.hanyang.selab.iot_application.utill.BluetoothService;

public class PEPListActivity extends AppCompatActivity{

    private static final String TAG = "PEPListActivity";

    private static String EXTRA_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private RecyclerView listView;
    private RecyclerView.LayoutManager listLayout;
    private PEPListAdapter listAdapter;

    PEPListController con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peplist);
        setHandlers();
        if(con == null)
            con = new PEPListController(this, listAdapter);

    }

    private void setHandlers(){

        Button searchPEP = findViewById(R.id.btn_bluetooth_search);
        searchPEP.setOnClickListener(new PEPListButtonHandler());

        listView = findViewById(R.id.pep_list);
        listView.setHasFixedSize(true);

        listLayout = new LinearLayoutManager(this);
        listView.setLayoutManager(listLayout);

        listAdapter = new PEPListAdapter(new ArrayList<Map<String, String>>());
        listView.setAdapter(listAdapter);
        listAdapter.setItemClick(new PEPListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                Map<String, String> pep = listAdapter.getPEP(position);
                con.connect(pep.get("mac"));
            }
        });

    }

    protected void onDestroy() {
        con.disconnect();
        super.onDestroy();
    }

    class PEPListButtonHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //페어링된기기 검색
            con.listUp();
        }
    }



}
