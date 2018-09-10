package kr.ac.hanyang.selab.iot_application.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.domain.DeviceAction;

public class ActionListAdapter extends RecyclerView.Adapter<ActionListAdapter.ViewHolder> {

    private List<DeviceAction> dataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView view;
        public LinearLayout layout;
        public ViewHolder(LinearLayout v){
            super(v);
            view = (TextView) v.getChildAt(0);
        }
    }

    public ActionListAdapter(List<DeviceAction> dataSet){
        this.dataSet = dataSet;
    }

    public void clearAll(){
        dataSet.clear();
    }
    public void addAction(DeviceAction action){
        dataSet.add(action);
    }

    public DeviceAction getAction(int index){
        return dataSet.get(index);
    }

    @Override
    public ActionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    private ItemClick itemClick;
    public interface ItemClick{
        void onClick(View view, int position);
    }
    public void setItemClick(ItemClick itemClick){
        this.itemClick = itemClick;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        final int POSITION = position;
        DeviceAction action = dataSet.get(POSITION);

        String params = "";
        int size = action.getParams().size();
        for (int i = 0; i < size; i++) {
            Map<String, String> param = action.getParams().get(i);
            String p = param.get("name") + ": " + param.get("type");
            params += p;
            if((i+1) < size) params += ", ";
        }

        holder.view.setText(action.getActionName() + "("+ params +")");

        holder.view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(itemClick != null)
                    itemClick.onClick(v, POSITION);
            }
        });

    }

    @Override
    public int getItemCount(){
        return dataSet.size();
    }

}