package kr.ac.hanyang.selab.iot_application.presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.controller.DeviceRegistrationController;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.UnregisteredDeviceListAdapter;

public class DeviceRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "DevRegistrationActivity";

    private RecyclerView listView;
    private RecyclerView.LayoutManager listLayout;
    private UnregisteredDeviceListAdapter listAdapter;

    DeviceRegistrationController con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_registration);
        setHandlers();
        if(con == null)
            con = new DeviceRegistrationController(this, listAdapter);

        con.listUp();

    }

    private void setHandlers(){

        Button searchDevice = findViewById(R.id.btn_device_search);
        searchDevice.setOnClickListener(new DeviceSearchButtonHandler());

        listView = findViewById(R.id.bluetooth_device_list);
        listView.setHasFixedSize(true);

        listLayout = new LinearLayoutManager(this);
        listView.setLayoutManager(listLayout);

        listAdapter = new UnregisteredDeviceListAdapter(new ArrayList<String>());
        listView.setAdapter(listAdapter);
        listAdapter.setItemClick(new UnregisteredDeviceListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                String device = listAdapter.getDeviceName(position);
//                con.???(device);
            }
        });

    }

    class DeviceSearchButtonHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            con.listUp();
        }
    }
}
