package com.eos.ezcopy.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.eos.ezcopy.R;
import com.eos.ezcopy.manager.PreferencesManager;
import com.eos.ezcopy.utils.CommonConstant;

import java.util.ArrayList;
import java.util.List;

public class DataListWidgetService extends RemoteViewsService {

    private static ListRemoteViewsFactory listRemoteViewsFactory;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        listRemoteViewsFactory = new ListRemoteViewsFactory(this.getApplicationContext());
        return listRemoteViewsFactory;
    }

    public static ListRemoteViewsFactory getListRemoteViewsFactory() {
        return listRemoteViewsFactory;
    }

    static class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private final Context mContext;
        private List<String> mList;

        public ListRemoteViewsFactory(Context context) {
            mContext = context;
            mList = new ArrayList<>(PreferencesManager.getInstance().getTextDataList());
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            mList = new ArrayList<>(PreferencesManager.getInstance().getTextDataList());
        }

        @Override
        public void onDestroy() {
            mList.clear();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position < 0 || position >= mList.size())
                return null;
            String content = mList.get(position);
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_data_list);
            rv.setTextViewText(R.id.tv_copy_text, content);
            Log.i(CommonConstant.ONEXXXX, "getViewAt = " + position);
            //TODO:暂时不加widget中文字跑马灯效果
            //rv.setInt(R.id.tv_copy_text,"requestFocus",View.FOCUS_DOWN);
            //设置各个item对应的Intent
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra("data", content);
            rv.setOnClickFillInIntent(R.id.tv_copy_text, fillInIntent);
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
