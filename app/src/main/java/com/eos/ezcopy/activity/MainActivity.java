package com.eos.ezcopy.activity;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.felipecsl.gifimageview.library.GifImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding = null;
    private MyRCTextAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initEvent();
//        initData();
        initAdapter();
    }

    private void initEvent() {
        //??????EventBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        binding.ivBtnEnum.setOnClickListener(this);
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
        myAdapter = new MyRCTextAdapter(this, dataList);
        myAdapter.setOnItemClickListener(new MyRCTextAdapter.MyOnClickListener() {
            @Override
            public void onClick(String data) {
                ClipboardManager cm = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", data);
                cm.setPrimaryClip(mClipData);
                Toast.makeText(MainActivity.this, "????????????" + data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDoubleClick(int position) {
                showModifyDialog(position);
            }
        });
        binding.rvTextList.setAdapter(myAdapter);
        //??????ItemTouchHelper.Callback
        MyItemHelperCallback callback = new MyItemHelperCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.rvTextList);
    }

    /**
     * ??????item
     */
    private void modifyData(int position, String newContent) {
        //??????sp????????????
        Set<String> set = new HashSet<>();
        //??????????????????set??????
        Set<String> prefSet = PreferencesManager.getInstance().getTextDataList();
        List<String> list = new ArrayList<>(prefSet);
        list.set(position, newContent);
        set.addAll(list);
        //???getStringSet?????????set?????????????????????????????????
        PreferencesManager.getInstance().setTextDataList(set);
        //???????????????????????????
        myAdapter.modifyData(position, newContent);
        //??????widget????????????
        updateWidgetData();
    }

    /**
     * ??????item
     */
    private void insertData(String newContent) {
        //??????sp????????????
        //??????????????????set??????
        Set<String> set = new HashSet<>();
        //??????????????????set
        Set<String> prefSet = PreferencesManager.getInstance().getTextDataList();
        //???getStringSet?????????set?????????????????????????????????
        set.addAll(prefSet);
        //??????????????????
        set.add(newContent);
        PreferencesManager.getInstance().setTextDataList(set);
        //???????????????????????????
        myAdapter.addData(newContent);
        //??????widget????????????
        updateWidgetData();
    }

    /**
     * ??????item
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void moveData(ItemMoveEvent event) {
        int srcPosition = event.getSrcPosition();
        int dstPosition = event.getDstPosition();
        //??????sp??????
        Set<String> set = new HashSet<>();
        Set<String> preSet = PreferencesManager.getInstance().getTextDataList();
        List<String> list = new ArrayList<>(preSet);
        Collections.swap(list, srcPosition, dstPosition);
        set.addAll(list);
        PreferencesManager.getInstance().setTextDataList(set);
        //???????????????????????????
        myAdapter.moveData(srcPosition, dstPosition);
        //??????widget????????????
        updateWidgetData();
    }

    /**
     * ??????item
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteData(ItemDeleteEvent event) {
        //??????sp??????
        int position = event.getPosition();
        Set<String> set = new HashSet<>();
        Set<String> preSet = PreferencesManager.getInstance().getTextDataList();
        List<String> list = new ArrayList<>(preSet);
        list.remove(position);
        set.addAll(list);
        PreferencesManager.getInstance().setTextDataList(set);
        //???????????????????????????
        myAdapter.deleteData(position);
        //??????widget????????????
        updateWidgetData();
    }

    /**
     * ??????widget???????????????????????????
     */
    private void updateWidgetData() {
        //??????widget????????????
        final AppWidgetManager mgr = AppWidgetManager.getInstance(this);
        final ComponentName cn = new ComponentName(this, DataWidgetProvider.class);
        //??????????????????
        RemoteViewsService.RemoteViewsFactory factory = DataListWidgetService.getListRemoteViewsFactory();
        if (factory != null) {
            factory.onDataSetChanged();
        }
        //????????????????????????RemoteViewService???RemoteViewsFactory???onDataSetChanged()??????
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.lv_text_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_btn_enum:
                Toast.makeText(this, "??????", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_btn_one:
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                showAddDialog();
                break;
            case R.id.iv_btn_two:
                Toast.makeText(this, "??????", Toast.LENGTH_SHORT).show();
                showInstructionsDialog();
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
                Toast.makeText(MainActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
            } else {
                insertData(newContent);
            }
            mDialog.dismiss();
        });
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
        title.setText("????????????");
        EditText content = view.findViewById(R.id.et_add_content);
        confirm.setOnClickListener(view1 -> {
            String newContent = content.getText().toString();
            if (newContent.length() == 0) {
                Toast.makeText(MainActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
            } else {
                modifyData(position, newContent);
            }
            mDialog.dismiss();
        });
    }

    /**
     * ???????????????
     * ????????????
     * ????????????
     * ????????????
     * ????????????
     * ?????????????????????????????????bug
     */
    private void showInstructionsDialog() {
        AlertDialog mDialog = new AlertDialog.Builder(this).create();
        View view = getLayoutInflater().inflate(R.layout.dialog_instructions, null);
        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setView(view);
        mDialog.setCancelable(true);
        mDialog.show();
        TextView title = view.findViewById(R.id.tv_add_title);
        TextView confirm = view.findViewById(R.id.tv_confirm_btn);
        GifImageView addGif = view.findViewById(R.id.giv_add);
        showAddGif(addGif);
        confirm.setOnClickListener(view1 -> {
            mDialog.dismiss();
        });
    }

    @SuppressLint("ResourceType")
    private void showAddGif(GifImageView gifImageView) {
        gifImageView.setScaleType(ImageView.ScaleType.CENTER);
        try {
            gifImageView.setBytes(steamToByte(getResources().openRawResource(R.drawable.delete)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        gifImageView.startAnimation();

//        gifImageView.setBytes(byte[] bytes); //??????gif??????????????????byte[]????????????
//
//        gifImageView.startAnimation(); //????????????gif???
//
//        gifImageView.stopAnimation(); //????????????gif???
//
//        gifImageView.isAnimating(); //??????gif????????????????????????
    }

    public static byte[] steamToByte(InputStream input) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        byte[] b = new byte[1024];
        while ((len = input.read(b, 0, b.length)) != -1) {
            baos.write(b, 0, len);
        }
        return baos.toByteArray();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}