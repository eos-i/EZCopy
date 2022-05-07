package com.eos.ezcopy.helper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.eos.ezcopy.bean.ItemDeleteEvent;
import com.eos.ezcopy.bean.ItemMoveEvent;

import org.greenrobot.eventbus.EventBus;

public class MyItemHelperCallback extends ItemTouchHelper.Callback {

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //支持上下拖动，左右滑动
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        //开启长按拖动
        return true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //拖动排序
        EventBus.getDefault().post(new ItemMoveEvent(viewHolder.getAdapterPosition(), target.getAdapterPosition()));
        return true;
    }

    @Override
    public float getMoveThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        //拖动距离：自身高度的0.9倍
        return 0.9f;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        //开启滑动
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //滑动删除
        EventBus.getDefault().post(new ItemDeleteEvent(viewHolder.getAdapterPosition()));
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        //滑动距离：自身宽度的0.2倍
        return 0.2f;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        //滑动速度：5像素/s
        return 5f;
    }

    @Override
    public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        //手指离开item后动画持续时间：1s
        return 1000L;
    }
}
