package com.eos.ezcopy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eos.ezcopy.R;
import com.eos.ezcopy.listener.DoubleClickListener;
import com.eos.ezcopy.utils.CommonConstant;

import java.util.Collections;
import java.util.List;

public class MyRCTextAdapter extends RecyclerView.Adapter<MyRCTextAdapter.MyRCTextViewHolder> {

    private final List<String> textList;
    private Context context;
    private MyOnClickListener listener;

    public void setOnItemClickListener(MyOnClickListener listener) {
        this.listener = listener;
    }

    public MyRCTextAdapter(Context context, List<String> textList) {
        this.context = context;
        this.textList = textList;
    }

    @NonNull
    @Override
    public MyRCTextAdapter.MyRCTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyRCTextViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_widget_data_list, parent, false));
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MyRCTextViewHolder holder, int position) {
        holder.itemText.setText(textList.get(position));
        holder.itemText.setOnClickListener(new DoubleClickListener(new DoubleClickListener.DoubleClickCallBack() {
            @Override
            public void oneClick(View view) {
                Toast.makeText(context, "单击", Toast.LENGTH_SHORT).show();
                listener.onClick(textList.get(position));
            }

            @Override
            public void doubleClick(View view) {
                Toast.makeText(context, "双击", Toast.LENGTH_SHORT).show();
                listener.onDoubleClick(position);
            }
        }));
        holder.itemText.setOnFocusChangeListener((v, hasFocus) -> {
            Log.i(CommonConstant.ONEXXXX, "item v = " + v + ", hasFocus = " + hasFocus);
            //为true表示焦点给到当前view
            if (hasFocus) {
                //当view没有焦点时，此次主动回调onClick()方法，使得单击双击正常判断
                //否则，会受android:focusableInTouchMode="true"影响，具体表现为：
                //点击一次view获取到焦点，再次点击才会回调onClick()方法
                //总结：view设置focusableInTouchMode为true时，表示view可获得焦点，
                //     在没有获取到焦点前点击时是不会回调onClick()方法的。
                v.performClick();
            }
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
        void onDoubleClick(int position);
    }
}
