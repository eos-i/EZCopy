package com.eos.ezcopy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eos.ezcopy.R;
import com.eos.ezcopy.databinding.ActivityMainBinding;
import com.eos.ezcopy.manager.PreferencesManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        initAdapter();
    }

    private void initAdapter() {
        List<String> dataList = new ArrayList<>(PreferencesManager.getInstance().getTextDataList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rvTextList.setLayoutManager(linearLayoutManager);
        MyRCTextAdapter myAdapter = new MyRCTextAdapter(dataList);
        myAdapter.setOnItemClickListener(new  MyRCTextAdapter.MyOnClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(MainActivity.this, "點擊！！", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvTextList.setAdapter(myAdapter);
    }

    private void initData() {
        Log.i("1xxxx", "main to init sp");
        PreferencesManager.initializeInstance(getApplicationContext());
        Set<String> textSet = new HashSet<String>();
        textSet.add("1111111");
        textSet.add("222222");
        textSet.add("5555555555555555555555555555555555555555555555555555555555555555555555");
        textSet.add("333333");
        textSet.add("444444");
        textSet.add("666666666666666666666666666666666666666666666666666666666666666666666666666666");
        PreferencesManager.getInstance().setTextDataList(textSet);
    }
}

class MyRCTextAdapter extends RecyclerView.Adapter<MyRCTextAdapter.MyRCTextViewHolder> {

    private List<String> textList;

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
        MyRCTextAdapter.MyRCTextViewHolder viewHolder = new MyRCTextViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_widget_data_list, parent,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRCTextViewHolder holder, int position) {
        holder.itemText.setText(textList.get(position));
//        holder.itemText.requestFocus();
        holder.itemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick();
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

    interface MyOnClickListener {
        void onClick();
    }
}