package com.eos.ezcopy.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
        super.onReceive(context, intent);
        Log.i(CommonConstant.ONEXXXX, "onReceive");
        if (intent.getAction().equals(CLICK_ACTION)) {
            String data = intent.getStringExtra("data");
            int id = intent.getIntExtra("id", -1);
            int id1 = intent.getIntExtra("id1", -2);
            int EXTRA_APPWIDGET_ID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -3);
            Log.i(CommonConstant.ONEXXXX, "click action");
            Bundle bundle = intent.getBundleExtra("data1");
            int bundleInt = -5;
            if(bundle != null) {
                bundleInt = bundle.getInt("data");
            }
            //将点击的内容进行复制
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", data);
            cm.setPrimaryClip(mClipData);
            Log.i(CommonConstant.ONEXXXX, "复制成功 = " + data + ", id = " + id + ", id1 = " + id1 + ", EXTRA_APPWIDGET_ID = " + EXTRA_APPWIDGET_ID + ", bundleInt = " + bundleInt);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i(CommonConstant.ONEXXXX, "onUpdate appWidgetIds.length = " + appWidgetIds.length);

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        //获取SP中的数据
        textList = new ArrayList<>(PreferencesManager.getInstance().getTextDataList());

        //
        thisWidget = new ComponentName(context, DataWidgetProvider.class);
        //获取小部件view
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.data_list_widget);

        //serviceIntent是启动DataListWidgetService的intent
        Intent serviceIntent = new Intent(context, DataListWidgetService.class);
//        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //设置ListView的适配器
        //将listview和service关联起来更新view
        remoteViews.setRemoteAdapter(R.id.lv_text_list, serviceIntent);

        // 设置响应 “GridView(gridview)” 的intent模板
        // 说明：“集合控件(如GridView、ListView、StackView等)”中包含很多子元素，如GridView包含很多格子。
        //     它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步。
        //        (01) 通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
        //        (02) 然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中 通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”

        Intent listIntent = new Intent(context, DataWidgetProvider.class);
        listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        listIntent.setData(Uri.parse(listIntent.toUri(Intent.URI_INTENT_SCHEME)));
        listIntent.setAction(CLICK_ACTION);
        listIntent.putExtra("id1", 0);
        listIntent.putExtra("data", "null1");
        listIntent.putExtra("id", -4);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, listIntent, PendingIntent.FLAG_IMMUTABLE);
        //设置intent模板
        remoteViews.setPendingIntentTemplate(R.id.lv_text_list, pendingIntent);

        //调用集合管理器对集合进行更新
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    public static List<String> getList() {
        return textList;
    }
}

