package com.eos.ezcopy.activity;

import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViewsService;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eos.ezcopy.R;
import com.eos.ezcopy.adapter.MyRCTextAdapter;
import com.eos.ezcopy.bean.ItemDeleteEvent;
import com.eos.ezcopy.bean.ItemMoveEvent;
import com.eos.ezcopy.databinding.ActivityMainBinding;
import com.eos.ezcopy.helper.MyItemHelperCallback;
import com.eos.ezcopy.manager.PreferencesManager;
import com.eos.ezcopy.provider.DataWidgetProvider;
import com.eos.ezcopy.service.DataListWidgetService;
import com.eos.ezcopy.utils.CommonConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //单击选中，双击复制，三击修改
    //单击复制，双击修改
    //需要区分单击后focus变化
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
        //注册EventBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
        //设置ItemTouchHelper.Callback
        MyItemHelperCallback callback = new MyItemHelperCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.rvTextList);
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
            if (newContent.length() == 0) {
                Toast.makeText(MainActivity.this, "文本为空！", Toast.LENGTH_SHORT).show();
            } else {
                modifyData(position, newContent);
            }
            mDialog.dismiss();
        });
    }

    /**
     * 修改item
     */
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
        //更新应用内数据列表
        myAdapter.modifyData(position, newContent);
        //更新widget列表数据
        updateWidgetData();
    }

    /**
     * 新增item
     */
    private void insertData(String newContent) {
        //更新sp中的数据
        //重新创建一个set对象
        Set<String> set = new HashSet<>();
        //获取原有数据set
        Set<String> prefSet = PreferencesManager.getInstance().getTextDataList();
        //将getStringSet返回的set添加进去而不是直接使用
        set.addAll(prefSet);
        //新添加的数据
        set.add(newContent);
        PreferencesManager.getInstance().setTextDataList(set);
        //更新应用内数据列表
        myAdapter.addData(newContent);
        //更新widget列表数据
        updateWidgetData();
    }

    /**
     * 移动item
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void moveData(ItemMoveEvent event) {
        int srcPosition = event.getSrcPosition();
        int dstPosition = event.getDstPosition();
        //更新sp数据
        Set<String> set = new HashSet<>();
        Set<String> preSet = PreferencesManager.getInstance().getTextDataList();
        List<String> list = new ArrayList<>(preSet);
        Collections.swap(list, srcPosition, dstPosition);
        set.addAll(list);
        PreferencesManager.getInstance().setTextDataList(set);
        //更新应用内数据列表
        myAdapter.moveData(srcPosition, dstPosition);
        //更新widget列表数据
        updateWidgetData();
    }

    /**
     * 删除item
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteData(ItemDeleteEvent event) {
        //更新sp数据
        int position = event.getPosition();
        Set<String> set = new HashSet<>();
        Set<String> preSet = PreferencesManager.getInstance().getTextDataList();
        List<String> list = new ArrayList<>(preSet);
        list.remove(position);
        set.addAll(list);
        PreferencesManager.getInstance().setTextDataList(set);
        //更新应用内列表数据
        myAdapter.deleteData(position);
        //更新widget列表数据
        updateWidgetData();
    }

    /**
     * 更新widget列表数据，全局更新
     */
    private void updateWidgetData() {
        //更新widget中的数据
        final AppWidgetManager mgr = AppWidgetManager.getInstance(this);
        final ComponentName cn = new ComponentName(this, DataWidgetProvider.class);
        //调用数据添加
        RemoteViewsService.RemoteViewsFactory factory = DataListWidgetService.getListRemoteViewsFactory();
        if (factory != null) {
            factory.onDataSetChanged();
        }
        //下面这句话会调用RemoteViewService中RemoteViewsFactory的onDataSetChanged()方法
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.lv_text_list);
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
                Toast.makeText(this, "说明", Toast.LENGTH_SHORT).show();
                showInstructions();
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
        confirm.setOnClickListener(view1 -> {
            String newContent = content.getText().toString();
            if (newContent.length() == 0) {
                Toast.makeText(MainActivity.this, "文本为空！", Toast.LENGTH_SHORT).show();
            } else {
                insertData(newContent);
            }
            mDialog.dismiss();
        });
    }

    private void showInstructions() {
        AlertDialog mDialog = new AlertDialog.Builder(this).create();
        View view = getLayoutInflater().inflate(R.layout.dialog_instructions, null);
        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setView(view);
        mDialog.setCancelable(true);
        mDialog.show();
        TextView title = view.findViewById(R.id.tv_add_title);
        TextView confirm = view.findViewById(R.id.tv_confirm_btn);
        confirm.setOnClickListener(view1 -> {
            mDialog.dismiss();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}