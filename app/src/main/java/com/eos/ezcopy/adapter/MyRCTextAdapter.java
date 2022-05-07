package com.eos.ezcopy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eos.ezcopy.R;

import java.util.Collections;
import java.util.List;

public class MyRCTextAdapter extends RecyclerView.Adapter<MyRCTextAdapter.MyRCTextViewHolder> {

    private final List<String> textList;

    private MyOnClickListener listener;

    public void setOnItemClickListener(MyOnClickListener listener) {
        this.listener = listener;
    }

    public MyRCTextAdapter(List<String> textList) {
        this.textList = textList;
    }

    @NonNull
    @Override
    public MyRCTextAdapter.MyRCTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyRCTextViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_widget_data_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyRCTextViewHolder holder, int position) {
        holder.itemText.setText(textList.get(position));
        holder.itemText.setOnClickListener(view -> listener.onClick(textList.get(position)));
        holder.itemText.setOnLongClickListener(v -> {
            listener.onLongClick(position);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return textList.size();
    }

    static class MyRCTextViewHolder extends RecyclerView.ViewHolder {

        TextView itemText;

        public MyRCTextViewHolder(@NonNull View itemView) {
            super(itemView);
            itemText = itemView.findViewById(R.id.tv_copy_text);
        }
    }

    public void addData(String data) {
        textList.add(data);
        notifyItemInserted(textList.size() - 1);
    }

    public void modifyData(int position, String newContent) {
        textList.set(position, newContent);
        notifyItemChanged(position);
    }

    public void moveData(int srcPosition, int dstPosition) {
        Collections.swap(textList, srcPosition, dstPosition);
        notifyItemMoved(srcPosition, dstPosition);
    }

    public void deleteData(int position) {
        textList.remove(position);
        notifyItemRemoved(position);
    }

    public interface MyOnClickListener {
        void onClick(String data);
        void onLongClick(int position);
    }
}
