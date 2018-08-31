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
import kr.ac.hanyang.selab.iot_application.controller.PEPRegistrationController;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.BluetoothPEPListAdapter;

public class PEPRegistrationActivity extends AppCompatActivity{

    private static final String TAG = "PEPRegistrationActivity";

    private RecyclerView listView;
    private RecyclerView.LayoutManager listLayout;
    private BluetoothPEPListAdapter listAdapter;

    PEPRegistrationController con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pep_registration);
        setHandlers();
        if(con == null)
            con = new PEPRegistrationController(this, listAdapter);

        con.listUp();

    }

    private void setHandlers(){

        Button searchPEP = findViewById(R.id.btn_pep_search);
        searchPEP.setOnClickListener(new PEPListButtonHandler());

        listView = findViewById(R.id.pep_list);
        listView.setHasFixedSize(true);

        listLayout = new LinearLayoutManager(this);
        listView.setLayoutManager(listLayout);

        listAdapter = new BluetoothPEPListAdapter(new ArrayList<Map<String, String>>());
        listView.setAdapter(listAdapter);
        listAdapter.setItemClick(new BluetoothPEPListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                Map<String, String> pep = listAdapter.getBluetoothDevice(position);
                con.connect(pep);
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
