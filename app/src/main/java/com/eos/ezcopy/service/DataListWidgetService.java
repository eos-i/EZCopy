package com.eos.ezcopy.service;

import static com.eos.ezcopy.provider.DataWidgetProvider.CLICK_ACTION;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.eos.ezcopy.R;
import com.eos.ezcopy.provider.DataWidgetProvider;

import java.util.List;

public class DataListWidgetService extends RemoteViewsService {

    @Override
    public void onStart(Intent intent, int startId){
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    static class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

        private final Context mContext;
        private final List<String> mList;

        public ListRemoteViewsFactory(Context context, Intent intent){
            mContext = context;
            mList = DataWidgetProvider.getList();

            if(Looper.myLooper() == null){
                Looper.prepare();
            }
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
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
            if(position<0 || position>=mList.size())
                return null;
            String content = mList.get(position);
            final RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_data_list);
            rv.setTextViewText(R.id.tv_copy_text, content);

            // 设置 第position位的“视图”对应的响应事件
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra("data", position);
            rv.setOnClickFillInIntent(R.id.tv_copy_text, fillInIntent);

//            Intent intent = new Intent();
//            Intent clickIntent = new Intent();
//            clickIntent.setAction(CLICK_ACTION);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, clickIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//            rv.setPendingIntentTemplate(R.id.tv_copy_text, pendingIntent);
//            rv.setOnClickFillInIntent(R.id.tv_copy_text, intent);
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
