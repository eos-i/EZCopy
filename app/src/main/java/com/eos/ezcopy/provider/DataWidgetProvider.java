package com.eos.ezcopy.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.eos.ezcopy.R;
import com.eos.ezcopy.service.DataListWidgetService;
import com.eos.ezcopy.utils.CommonConstant;

public class DataWidgetProvider extends AppWidgetProvider {

    public static final String CLICK_ACTION = "data.list.item.action.click";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i(CommonConstant.ONEXXXX, "onReceive");
        if (intent.getAction().equals(CLICK_ACTION)) {
            Log.i(CommonConstant.ONEXXXX, "click action");
            String data = intent.getStringExtra("data");
            //将点击的内容进行复制
//            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData mClipData = ClipData.newPlainText("Label", data);
//            cm.setPrimaryClip(mClipData);
            Log.i(CommonConstant.ONEXXXX, "复制成功 data = " + data);
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
        //获取小部件view
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.data_list_widget);

        //serviceIntent是启动DataListWidgetService的intent
        Intent serviceIntent = new Intent(context, DataListWidgetService.class);
        //设置ListView的适配器
        //将listview和service关联起来更新view
        remoteViews.setRemoteAdapter(R.id.lv_text_list, serviceIntent);

        //设置响应 “GridView(gridview)” 的intent模板
        //说明：“集合控件(如GridView、ListView、StackView等)”中包含很多子元素，如GridView包含很多格子。
        //它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步：
        //  (1)通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
        //  (2)然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中 通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”
        Intent listIntent = new Intent(context, DataWidgetProvider.class);
        listIntent.setAction(CLICK_ACTION);
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
}

