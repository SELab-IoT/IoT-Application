package kr.ac.hanyang.selab.iot_application.presentation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import kr.ac.hanyang.selab.iot_application.R;
import kr.ac.hanyang.selab.iot_application.domain.Device;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<Device> dataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView view;
        public LinearLayout layout;
        public ViewHolder(LinearLayout v){
            super(v);
            view = (TextView) v.getChildAt(0);
        }
    }

    public DeviceListAdapter(List<Device> dataSet){
        this.dataSet = dataSet;
    }

    public void clearAll(){
        dataSet.clear();
    }
    public void addDevice(Device device){
        dataSet.add(device);
    }

    public Device getDevice(int index){
        return dataSet.get(index);
    }

    @Override
    public DeviceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LinearLayout v = (LinearLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
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
        Device device = dataSet.get(POSITION);
        holder.view.setText(device.getDeviceName());

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
