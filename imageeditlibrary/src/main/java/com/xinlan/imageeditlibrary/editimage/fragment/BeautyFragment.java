package com.xinlan.imageeditlibrary.editimage.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;
import java.lang.ref.WeakReference;


/**
 * 美颜功能
 *
 * @author 潘易
 */
public class BeautyFragment extends BaseEditFragment implements SeekBar.OnSeekBarChangeListener {
    public static final String TAG = BeautyFragment.class.getName();

    public static final int INDEX = ModuleConfig.INDEX_BEAUTY;

    private View mainView;
    private View backToMenu;// 返回主菜单

    private SeekBar mSmoothValueBar;
    private SeekBar mWhiteValueBar;

    private BeautyTask mHandleTask;

    private int mSmooth = 0;//磨皮值
    private int mWhiteSkin = 0;//美白值

    private WeakReference<Bitmap> mResultBitmapRef;

    public static BeautyFragment newInstance() {
        BeautyFragment fragment = new BeautyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_beauty, null);

        mSmoothValueBar = (SeekBar) mainView.findViewById(R.id.smooth_value_bar);
        mWhiteValueBar = (SeekBar) mainView.findViewById(R.id.white_skin_value_bar);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单

        mSmoothValueBar.setOnSeekBarChangeListener(this);
        mWhiteValueBar.setOnSeekBarChangeListener(this);
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        doBeautyTask();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    protected void doBeautyTask() {
        if (mHandleTask != null && !mHandleTask.isCancelled()) {
            mHandleTask.cancel(true);
        }
        mSmooth = mSmoothValueBar.getProgress();
        mWhiteSkin = mWhiteValueBar.getProgress();

        if (mSmooth == 0 && mWhiteSkin == 0) {
            activity.mainImage.setImageBitmap(activity.getMainBit());
            return;
        }

        mHandleTask = new BeautyTask(mSmooth, mWhiteSkin);
        mHandleTask.execute(0);
    }

    /**
     * 返回按钮逻辑
     *
     * @author panyi
     */
    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }// end class

    /**
     * 返回主菜单
     */
    @Override
    public void backToMain() {
        this.mSmooth = 0;
        this.mWhiteSkin = 0;
        mSmoothValueBar.setProgress(0);
        mWhiteValueBar.setProgress(0);

        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setImageBitmap(activity.getMainBit());// 返回原图

        activity.mainImage.setVisibility(View.VISIBLE);
        activity.mainImage.setScaleEnabled(true);
        activity.bannerFlipper.showPrevious();
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_BEAUTY;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setScaleEnabled(false);
        activity.bannerFlipper.showNext();
    }

    public void applyBeauty() {
        if (mResultBitmapRef.get() != null && (mSmooth != 0 || mWhiteSkin != 0)) {
            activity.changeMainBitmap(mResultBitmapRef.get(),true);
        }

        backToMain();
    }

    /**
     * 美颜操作任务
     */
    private final class BeautyTask extends AsyncTask<Integer, Void, Bitmap> {
        private float smoothVal;
        private float whiteVal;

        private Dialog dialog;
        private Bitmap srcBitmap;

        public BeautyTask(float smooth, float white) {
            this.smoothVal = smooth;
            this.whiteVal = white;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = BaseActivity.getLoadingDialog(getActivity(), R.string.handing,
                    false);
            dialog.show();
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            srcBitmap = Bitmap.createBitmap(activity.getMainBit().copy(
                    Bitmap.Config.ARGB_8888, true));
            //System.out.println("smoothVal = "+smoothVal+"     whiteVal = "+whiteVal);
            PhotoProcessing.handleSmoothAndWhiteSkin(srcBitmap, smoothVal, whiteVal);
            return srcBitmap;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
            dialog.dismiss();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result == null)
                return;

            mResultBitmapRef = new WeakReference<Bitmap>(result);
            activity.mainImage.setImageBitmap(mResultBitmapRef.get());
        }
    }//end inner class

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandleTask != null && !mHandleTask.isCancelled()) {
            mHandleTask.cancel(true);
        }
    }
}// end class
