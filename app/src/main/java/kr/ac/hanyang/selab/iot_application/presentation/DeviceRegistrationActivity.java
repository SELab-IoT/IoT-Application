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
import kr.ac.hanyang.selab.iot_application.controller.DeviceRegistrationController;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.BluetoothDeviceListAdapter;

public class DeviceRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "DeviceRegistrationActivity";

    private RecyclerView listView;
    private RecyclerView.LayoutManager listLayout;
    private BluetoothDeviceListAdapter listAdapter;

    DeviceRegistrationController con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pep_registration);
        setHandlers();
//        if(con == null)
//            con = new DeviceRegistrationController(this, listAdapter);

//        con.listUp();

    }

    private void setHandlers(){

        Button searchDevice = findViewById(R.id.btn_device_search);
        searchDevice.setOnClickListener(new DeviceSearchButtonHandler());

        listView = findViewById(R.id.pep_list);
        listView.setHasFixedSize(true);

        listLayout = new LinearLayoutManager(this);
        listView.setLayoutManager(listLayout);

        listAdapter = new BluetoothDeviceListAdapter(new ArrayList<Map<String, String>>());
        listView.setAdapter(listAdapter);
        listAdapter.setItemClick(new BluetoothDeviceListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                Map<String, String> device = listAdapter.getBluetoothDevice(position);
//                con.???(device);
            }
        });

    }

    class DeviceSearchButtonHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            // PEP에게 목록 조회하기
//            con.listUp();
        }
    }
}
