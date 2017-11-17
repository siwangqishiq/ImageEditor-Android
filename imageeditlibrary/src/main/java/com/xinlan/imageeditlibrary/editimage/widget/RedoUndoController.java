package com.xinlan.imageeditlibrary.editimage.widget;

import android.graphics.Bitmap;
import android.view.View;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;

/**
 * Created by panyi on 2017/11/15.
 * <p>
 * 前一步 后一步操作类
 */
public class RedoUndoController implements View.OnClickListener {
    private View mRootView;
    private View mUndoBtn;//撤销按钮
    private View mRedoBtn;//重做按钮
    private EditImageActivity mActivity;
    private EditCache mEditCache = new EditCache();//保存前一次操作内容 用于撤销操作

    private EditCache.ListModify mObserver = new EditCache.ListModify() {
        @Override
        public void onCacheListChange(EditCache cache) {
            updateBtns();
        }
    };

    public RedoUndoController(EditImageActivity activity, View panelView) {
        this.mActivity = activity;
        this.mRootView = panelView;

        mUndoBtn = mRootView.findViewById(R.id.uodo_btn);
        mRedoBtn = mRootView.findViewById(R.id.redo_btn);

        mUndoBtn.setOnClickListener(this);
        mRedoBtn.setOnClickListener(this);

        updateBtns();
        mEditCache.addObserver(mObserver);
    }

    public void switchMainBit(Bitmap mainBitmap, Bitmap newBit) {
        if (mainBitmap == null || mainBitmap.isRecycled())
            return;

        mEditCache.push(mainBitmap);
        mEditCache.push(newBit);
    }


    @Override
    public void onClick(View v) {
        if (v == mUndoBtn) {
            undoClick();
        } else if (v == mRedoBtn) {
            redoClick();
        }//end if
    }


    /**
     * 撤销操作
     */
    protected void undoClick() {
        //System.out.println("Undo!!!");
        Bitmap lastBitmap = mEditCache.getNextCurrentBit();
        if (lastBitmap != null && !lastBitmap.isRecycled()) {
            mActivity.changeMainBitmap(lastBitmap, false);
        }
    }

    /**
     * 取消撤销
     */
    protected void redoClick() {
        //System.out.println("Redo!!!");
        Bitmap preBitmap = mEditCache.getPreCurrentBit();
        if (preBitmap != null && !preBitmap.isRecycled()) {
            mActivity.changeMainBitmap(preBitmap, false);
        }
    }

    /**
     * 根据状态更新按钮显示
     */
    public void updateBtns() {
        //System.out.println("缓存Size = " + mEditCache.getSize() + "  current = " + mEditCache.getCur());
        //System.out.println("content = " + mEditCache.debugLog());
        mUndoBtn.setVisibility(mEditCache.checkNextBitExist() ? View.VISIBLE : View.INVISIBLE);
        mRedoBtn.setVisibility(mEditCache.checkPreBitExist() ? View.VISIBLE : View.INVISIBLE);
    }

    public void onDestroy() {
        if (mEditCache != null) {
            mEditCache.removeObserver(mObserver);
            mEditCache.removeAll();
        }
    }

}//end class
