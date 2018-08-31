package kr.ac.hanyang.selab.iot_application.presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.controller.PEPListController;
import kr.ac.hanyang.selab.iot_application.domain.PEP;
import kr.ac.hanyang.selab.iot_application.presentation.adapter.PEPListAdapter;

public class PEPListActivity extends AppCompatActivity {

    private static final String TAG = "PEPListActivity";

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

        con.listUp();

    }

    private void setHandlers(){

        Button searchPEP = findViewById(R.id.btn_query);
        searchPEP.setOnClickListener(new PEPListButtonHandler());

        listView = findViewById(R.id.pep_list);
        listView.setHasFixedSize(true);

        listLayout = new LinearLayoutManager(this);
        listView.setLayoutManager(listLayout);

        listAdapter = new PEPListAdapter(new ArrayList<PEP>());
        listView.setAdapter(listAdapter);
        listAdapter.setItemClick(new PEPListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                PEP pep = listAdapter.getPEP(position);
                con.onPEPSelected(pep);
            }
        });

    }

    class PEPListButtonHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick btn_query");
            // 사용가능한 PEP 목록 조회
            con.listUp();
        }
    }
}
