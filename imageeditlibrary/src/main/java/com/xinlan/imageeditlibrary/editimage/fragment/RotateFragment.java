package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ModuleConfig;
import com.xinlan.imageeditlibrary.editimage.view.RotateImageView;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;


/**
 * 图片旋转Fragment
 *
 * @author 潘易
 */
public class RotateFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_ROTATE;
    public static final String TAG = RotateFragment.class.getName();
    private View mainView;
    private View backToMenu;// 返回主菜单
    public SeekBar mSeekBar;// 角度设定
    private RotateImageView mRotatePanel;// 旋转效果展示控件

    public static RotateFragment newInstance() {
        RotateFragment fragment = new RotateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_rotate, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backToMenu = mainView.findViewById(R.id.back_to_main);
        mSeekBar = (SeekBar) mainView.findViewById(R.id.rotate_bar);
        mSeekBar.setProgress(0);

        this.mRotatePanel = ensureEditActivity().mRotatePanel;
        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
        mSeekBar.setOnSeekBarChangeListener(new RotateAngleChange());
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_ROTATE;
        activity.mainImage.setImageBitmap(activity.getMainBit());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setVisibility(View.GONE);

        activity.mRotatePanel.addBit(activity.getMainBit(),
                activity.mainImage.getBitmapRect());
        activity.mRotateFragment.mSeekBar.setProgress(0);
        activity.mRotatePanel.reset();
        activity.mRotatePanel.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showNext();
    }

    /**
     * 角度改变监听
     *
     * @author panyi
     */
    private final class RotateAngleChange implements OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int angle,
                                      boolean fromUser) {
            // System.out.println("progress--->" + progress);
            mRotatePanel.rotateImage(angle);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }// end inner class

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
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(0);
        activity.mainImage.setVisibility(View.VISIBLE);
        this.mRotatePanel.setVisibility(View.GONE);
        activity.bannerFlipper.showPrevious();
    }

    /**
     * 保存旋转图片
     */
    public void applyRotateImage() {
        // System.out.println("保存旋转图片");
        if (mSeekBar.getProgress() == 0 || mSeekBar.getProgress() == 360) {// 没有做旋转
            backToMain();
            return;
        } else {// 保存图片
            SaveRotateImageTask task = new SaveRotateImageTask();
            task.execute(activity.getMainBit());
        }// end if
    }

    /**
     * 保存图片线程
     *
     * @author panyi
     */
    private final class SaveRotateImageTask extends
            AsyncTask<Bitmap, Void, Bitmap> {
        //private Dialog dialog;

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            RectF imageRect = mRotatePanel.getImageNewRect();
            Bitmap originBit = params[0];
            Bitmap result = Bitmap.createBitmap((int) imageRect.width(),
                    (int) imageRect.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            int w = originBit.getWidth() >> 1;
            int h = originBit.getHeight() >> 1;
            float centerX = imageRect.width() / 2;
            float centerY = imageRect.height() / 2;

            float left = centerX - w;
            float top = centerY - h;

            RectF dst = new RectF(left, top, left + originBit.getWidth(), top
                    + originBit.getHeight());
            canvas.save();
            //bug fixed  应用时不需要考虑图片缩放问题 重新加载图片时 缩放控件会自动填充屏幕
//            canvas.scale(mRotatePanel.getScale(), mRotatePanel.getScale(),
//                    imageRect.width() / 2, imageRect.height() / 2);
            canvas.rotate(mRotatePanel.getRotateAngle(), imageRect.width() / 2,
                    imageRect.height() / 2);

            canvas.drawBitmap(originBit, new Rect(0, 0, originBit.getWidth(),
                    originBit.getHeight()), dst, null);
            canvas.restore();
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            //dialog.dismiss();
            if (result == null)
                return;

            // 切换新底图
            activity.changeMainBitmap(result,true);
            backToMain();
        }
    }// end inner class
}// end class
