package kr.ac.hanyang.selab.iot_application.presentation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kr.ac.hanyang.selab.iot_application.R;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<String> dataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ViewHolder(TextView v){
            super(v);
            textView = v;
        }
    }

    public DeviceListAdapter(List<String> dataSet){
        this.dataSet = dataSet;
    }

    public void clearAll(){
        dataSet.clear();
    }
    public void addDevice(String device){
        dataSet.add(device);
    }

    public String getDevice(int index){
        return dataSet.get(index);
    }

    @Override
    public DeviceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        TextView v = (TextView)LayoutInflater.from(parent.getContext()).inflate(R.layout.peplist_text_view, parent, false);
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
        String device = dataSet.get(POSITION);
        holder.textView.setText(device);

        holder.textView.setOnClickListener(new View.OnClickListener(){
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
