package com.example.list_maker;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.list_tracker.R;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder>{
    private ArrayList<Item> items;

    public ListAdapter(ArrayList<Item> items){
        super();
        this.items = items;
    }

    public ArrayList<Item> getItems(){return items;}

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false)
        );
    }

    public Item addItem(Item item){
        items.add(item);
        notifyItemInserted(items.size() - 1);
        return item;
    }

    public void addItems(ArrayList<Item> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void setItemsSilent(ArrayList<Item> items){
        this.items = items;
    }

    public ArrayList<Item> deleteItems(){
        ArrayList<Item> ret = new ArrayList<Item>();
        for(int i = items.size() - 1; i >= 0; --i){
            if(items.get(i).getChecked()){
                ret.add(0, items.remove(i));
            }
        }
        Log.v("deleteItems", "" + ret.size());
        notifyDataSetChanged();
        return ret;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Item curItem = items.get(position);

        holder.getTextView().setText(curItem.getTitle());

        CheckBox cb = holder.getCheckBox();

        cb.setChecked(false);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                cb.setChecked(isChecked);
                curItem.setChecked(isChecked);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    protected class ListViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final CheckBox checkBox;

        public ListViewHolder(View ListView){
            super(ListView);

            textView = (TextView) ListView.findViewById(R.id.tvItemTitle);
            checkBox = (CheckBox) ListView.findViewById(R.id.cbItemSelect);
        }

        public TextView getTextView(){return textView;}
        public CheckBox getCheckBox(){return checkBox;}
    }
}
