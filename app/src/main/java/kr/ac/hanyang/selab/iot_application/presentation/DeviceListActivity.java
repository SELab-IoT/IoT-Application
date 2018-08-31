package kr.ac.hanyang.selab.iot_application.presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.controller.DeviceListController;
import kr.ac.hanyang.selab.iot_application.domain.Device;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.DeviceListAdapter;

public class DeviceListActivity extends AppCompatActivity {

    private static final String TAG = "DeviceListActivity";

    private RecyclerView listView;
    private RecyclerView.LayoutManager listLayout;
    private DeviceListAdapter listAdapter;

    DeviceListController con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        setHandlers();
        if(con == null)
            con = new DeviceListController(this, listAdapter);
        con.listUp();
    }

    private void setHandlers(){

        listView = findViewById(R.id.available_device_list);
        listView.setHasFixedSize(true);

        listLayout = new LinearLayoutManager(this);
        listView.setLayoutManager(listLayout);

        listAdapter = new DeviceListAdapter(new ArrayList<Device>());
        listView.setAdapter(listAdapter);
        listAdapter.setItemClick(new DeviceListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                Device device = listAdapter.getDevice(position);
                con.onDeviceSelected(device);
            }
        });

    }

}
