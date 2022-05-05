package com.eos.ezcopy.activity;

import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RemoteViewsService;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eos.ezcopy.R;
import com.eos.ezcopy.databinding.ActivityMainBinding;
import com.eos.ezcopy.manager.PreferencesManager;
import com.eos.ezcopy.provider.DataWidgetProvider;
import com.eos.ezcopy.service.DataListWidgetService;
import com.eos.ezcopy.utils.CommonConstant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding = null;
    private MyRCTextAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initEvent();
        initData();
        initAdapter();
    }

    private void initEvent() {
        binding.ivBtnAdd.setOnClickListener(this);
        binding.ivBtnOne.setOnClickListener(this);
        binding.ivBtnTwo.setOnClickListener(this);
    }

    private void initData() {
        Log.i(CommonConstant.ONEXXXX, "main to init sp");
        Set<String> textSet = PreferencesManager.getInstance().getTextDataList();
        if (textSet == null || textSet.size() == 0) {
            textSet = new HashSet<>();
            textSet.add("1111111");
            textSet.add("222222");
            textSet.add("5555555555555555555555555555555555555555555555555555555555555555555555");
            textSet.add("333333");
            textSet.add("444444");
            textSet.add("666666666666666666666666666666666666666666666666666666666666666666666666666666");
            textSet.add("777");
            PreferencesManager.getInstance().setTextDataList(textSet);
        }
    }

    private void initAdapter() {
        List<String> dataList = new ArrayList<>(PreferencesManager.getInstance().getTextDataList());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rvTextList.setLayoutManager(linearLayoutManager);
        myAdapter = new MyRCTextAdapter(dataList);
        myAdapter.setOnItemClickListener(new MyRCTextAdapter.MyOnClickListener() {
            @Override
            public void onClick(String data) {
                ClipboardManager cm = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", data);
                cm.setPrimaryClip(mClipData);
                Toast.makeText(MainActivity.this, "已复制：" + data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(int position) {
                showModifyDialog(position);
            }
        });
        binding.rvTextList.setAdapter(myAdapter);
    }

    private void showModifyDialog(int position) {
        AlertDialog mDialog = new AlertDialog.Builder(this).create();
        View view = getLayoutInflater().inflate(R.layout.layout_add_dialog, null);
        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setView(view);
        mDialog.setCancelable(true);
        mDialog.show();
        TextView title = view.findViewById(R.id.tv_add_title);
        TextView confirm = view.findViewById(R.id.tv_add_btn);
        title.setText("修改条目");
        EditText content = view.findViewById(R.id.et_add_content);
        view.findViewById(R.id.tv_add_btn).setOnClickListener(view1 -> {
            String newContent = content.getText().toString();
            if(newContent.length() == 0) {
                Toast.makeText(MainActivity.this, "文本为空！", Toast.LENGTH_SHORT).show();
            } else {
                modifyData(position, newContent);
            }
            mDialog.dismiss();
        });
    }

    private void modifyData(int position, String newContent) {
        //更新sp中的数据
        Set<String> set = new HashSet<>();
        //重新创建一个set对象
        Set<String> prefSet = PreferencesManager.getInstance().getTextDataList();
        List<String> list = new ArrayList<>(prefSet);
        list.set(position, newContent);
        set.addAll(list);
        //将getStringSet返回的set添加进去而不是直接使用
        PreferencesManager.getInstance().setTextDataList(set);
        //更新list中的数据并更新UI
        myAdapter.modifyData(position, newContent);
        //更新widget中的数据
        final AppWidgetManager mgr = AppWidgetManager.getInstance(this);
        final ComponentName cn = new ComponentName(this, DataWidgetProvider.class);
        //刷新数据
        RemoteViewsService.RemoteViewsFactory factory = DataListWidgetService.getListRemoteViewsFactory();
        if(factory != null) {
            factory.onDataSetChanged();
        }
        //这句话会调用RemoteViewSerivce中RemoteViewsFactory的onDataSetChanged()方法。
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                R.id.lv_text_list);
    }

    /**
     * 更新list和sp的数据
     *
     * @param newContent 新的数据
     */
    private void insertData(String newContent) {
        //更新sp中的数据
        Set<String> set = new HashSet<>(); // 重新创建一个set对象
        Set<String> prefSet = PreferencesManager.getInstance().getTextDataList();
        set.addAll(prefSet); // 将getStringSet返回的set添加进去而不是直接使用
        set.add(newContent); // 新添加的数据
        PreferencesManager.getInstance().setTextDataList(set);
        //更新list中的数据并更新UI
        myAdapter.addData(newContent);
        //更新widget中的数据
        final AppWidgetManager mgr = AppWidgetManager.getInstance(this);
        final ComponentName cn = new ComponentName(this, DataWidgetProvider.class);
        // 调用数据添加
        RemoteViewsService.RemoteViewsFactory factory = DataListWidgetService.getListRemoteViewsFactory();
        if(factory != null) {
            factory.onDataSetChanged();
        }
        // 这句话会调用RemoteViewSerivce中RemoteViewsFactory的onDataSetChanged()方法。
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                R.id.lv_text_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_btn_add:
                Toast.makeText(this, "变换", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_btn_one:
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                showAddDialog();
                break;
            case R.id.iv_btn_two:
                Toast.makeText(this, "其他功能", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void showAddDialog() {
        AlertDialog mDialog = new AlertDialog.Builder(this).create();
        View view = getLayoutInflater().inflate(R.layout.layout_add_dialog, null);
        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setView(view);
        mDialog.setCancelable(true);
        mDialog.show();
        TextView title = view.findViewById(R.id.tv_add_title);
        TextView confirm = view.findViewById(R.id.tv_add_btn);
        EditText content = view.findViewById(R.id.et_add_content);
        view.findViewById(R.id.tv_add_btn).setOnClickListener(view1 -> {
            String newContent = content.getText().toString();
            if(newContent.length() == 0) {
                Toast.makeText(MainActivity.this, "文本为空！", Toast.LENGTH_SHORT).show();
            } else {
                insertData(newContent);
            }
            mDialog.dismiss();
        });
    }
}

class MyRCTextAdapter extends RecyclerView.Adapter<MyRCTextAdapter.MyRCTextViewHolder> {

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

    interface MyOnClickListener {
        void onClick(String data);
        void onLongClick(int position);
    }
}