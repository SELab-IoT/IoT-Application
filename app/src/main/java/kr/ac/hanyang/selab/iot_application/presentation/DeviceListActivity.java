package kr.ac.hanyang.selab.iot_application.presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Map;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.controller.DeviceListController;

public class DeviceListActivity extends AppCompatActivity {

    private static final String TAG = "PEPListActivity";

    private RecyclerView listView;
    private RecyclerView.LayoutManager listLayout;
    private DeviceListAdapter listAdapter;

    DeviceListController con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peplist);
        setHandlers();
        if(con == null)
            con = new DeviceListController(this, listAdapter);

    }

    private void setHandlers(){

        Button searchDevice = findViewById(R.id.btn_device_search);
        searchDevice.setOnClickListener(new DeviceListButtonHandler());

        listView = findViewById(R.id.pep_list);
        listView.setHasFixedSize(true);

        listLayout = new LinearLayoutManager(this);
        listView.setLayoutManager(listLayout);

        listAdapter = new DeviceListAdapter(new ArrayList<String>());
        listView.setAdapter(listAdapter);
        listAdapter.setItemClick(new DeviceListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                String device = listAdapter.getDevice(position);
                //TODO:디바이스 프로필 조회 후 액션 선택 액티비티로 넘기기.
            }
        });

    }

    class DeviceListButtonHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //TODO: 디바이스 목록 조회
            con.listUp();
        }
    }
}
