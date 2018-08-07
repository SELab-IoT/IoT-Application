package kr.ac.hanyang.selab.iot_application.presentation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        if(con == null)
            con = new PEPListController(this, listAdapter);
        setHandlers();
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
                Map pep = listAdapter.getPEP(position);
                con.connect(pep);
            }
        });

    }

    public void onDiscoveryFinished() {
        //프로그래스 바 제거.
    }

    class PEPListButtonHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //프로그래스 바 활성화

            //검색 시작
            con.searchPEP();
        }
    }

    //For Bluetooth Enable
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case BluetoothService.REQUEST_ENABLE_BT:
                if(resultCode == Activity.RESULT_OK)
                    con.doDiscovery();
                else
                    Log.d(TAG, "Bluetooth is not enabled");
                break;
        }
    }


}
