package kr.ac.hanyang.selab.iot_application.presentation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import kr.ac.hanyang.selab.iot_application.R;

public class PEPListAdapter extends RecyclerView.Adapter<PEPListAdapter.ViewHolder> {
    private List<Map<String, String>> dataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ViewHolder(TextView v){
            super(v);
            textView = v;
        }
    }

    public PEPListAdapter(List<Map<String, String>> dataSet){
        this.dataSet = dataSet;
    }

    public void addPEP(Map pep){
        dataSet.add(pep);
        this.notifyDataSetChanged();
    }

    public Map<String, String> getPEP(int index){
        return dataSet.get(index);
    }

    @Override
    public PEPListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.peplist_text_view, parent, false);
        return new ViewHolder(v);
    }

    private ItemClick itemClick;
    public interface ItemClick{
        public void onClick(View view, int position);
    }
    public void setItemClick(ItemClick itemClick){
        this.itemClick = itemClick;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        final int POSITION = position;
        Map pep = dataSet.get(POSITION);
        holder.textView.setText(pep.get("pepName") + ":" + pep.get("pepAddress"));

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
