package com.eos.ezcopy.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.eos.ezcopy.R;
import com.eos.ezcopy.manager.PreferencesManager;
import com.eos.ezcopy.service.DataListWidgetService;
import com.eos.ezcopy.utils.CommonConstant;

import java.util.ArrayList;
import java.util.List;

public class DataWidgetProvider extends AppWidgetProvider {

    public static final String TAG = "ImgAppWidgetProvider";
    public static final String CLICK_ACTION = "data.list.item.action.click";
    private static int index;
    private static List<String> textList;

    private ComponentName thisWidget;
    private RemoteViews remoteViews;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(CommonConstant.ONEXXXX, "onReceive");
        if (intent.getAction().equals(CLICK_ACTION)) {
            int viewIndex = intent.getIntExtra("data", 0);
            Log.i(CommonConstant.ONEXXXX, "click action");
            //将点击的内容进行复制
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", "AAAAA");
            cm.setPrimaryClip(mClipData);
            Toast.makeText(context, "复制成功 = " + viewIndex, Toast.LENGTH_SHORT).show();
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(CommonConstant.ONEXXXX, "onUpdate appWidgetIds.length = " + appWidgetIds.length);
//        textList = (List<String>) PreferencesManager.getInstance().getTextDataList();
//        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.data_lis_widget);
////        remoteViews.setOnClickFillInIntent();
////        remoteViews.setPendingIntentTemplate();
//
//        Intent clickIntent = new Intent();
//        clickIntent.setAction(CLICK_ACTION);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
////        remoteViews.setRemoteAdapter(R.id.lv_text_list, pendingIntent);
//
//        updateView(context, remoteViews, appWidgetManager);

        //获取SP中的数据
        textList = new ArrayList<>(PreferencesManager.getInstance().getTextDataList());

        //
        thisWidget = new ComponentName(context, DataWidgetProvider.class);
        //获取小部件view
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.data_list_widget);

        //serviceIntent是启动DataListWidgetService的intent
        Intent serviceIntent = new Intent(context, DataListWidgetService.class);
        //设置ListView的适配器
        //将listview和service关联起来更新view
        remoteViews.setRemoteAdapter(R.id.lv_text_list, serviceIntent);

        // 设置响应 “GridView(gridview)” 的intent模板
        // 说明：“集合控件(如GridView、ListView、StackView等)”中包含很多子元素，如GridView包含很多格子。
        //     它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步。
        //        (01) 通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
        //        (02) 然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中 通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”

        Intent listIntent = new Intent();
        listIntent.setAction(CLICK_ACTION);
        listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[0]);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, listIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //设置intent模板
        remoteViews.setPendingIntentTemplate(R.id.lv_text_list, pendingIntent);
        //调用集合管理器对集合进行更新
        appWidgetManager.updateAppWidget(appWidgetIds[0], remoteViews);
//
//        AppWidgetManager manager = AppWidgetManager.getInstance(context);
//        manager.notifyAppWidgetViewDataChanged(appWidgetIds[0], R.id.lv_text_list);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    public static List<String> getList() {
        return textList;
    }
}

