package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;


/**
 * 工具栏主菜单
 *
 * @author panyi
 */
public class MainMenuFragment extends Fragment implements View.OnClickListener {
    public static final int INDEX = 0;

    public static final String TAG = MainMenuFragment.class.getName();
    private View mainView;
    private EditImageActivity activity;

    private View stickerBtn;// 贴图按钮
    private View fliterBtn;// 滤镜按钮
    private View cropBtn;// 剪裁按钮
    private View rotateBtn;// 旋转按钮
    private View mTextBtn;//文字型贴图添加
    private View mPaintBtn;//编辑按钮

    public static MainMenuFragment newInstance(EditImageActivity activity) {
        MainMenuFragment fragment = new MainMenuFragment();
        fragment.activity = activity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_main_menu,
                null);
        stickerBtn = mainView.findViewById(R.id.btn_stickers);
        fliterBtn = mainView.findViewById(R.id.btn_fliter);
        cropBtn = mainView.findViewById(R.id.btn_crop);
        rotateBtn = mainView.findViewById(R.id.btn_rotate);
        mTextBtn = mainView.findViewById(R.id.btn_text);
        mPaintBtn = mainView.findViewById(R.id.btn_paint);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stickerBtn.setOnClickListener(this);
        fliterBtn.setOnClickListener(this);
        cropBtn.setOnClickListener(this);
        rotateBtn.setOnClickListener(this);
        mTextBtn.setOnClickListener(this);
        mPaintBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == stickerBtn) {
            onStickClick();
        } else if (v == fliterBtn) {
            onFilterClick();
        } else if (v == cropBtn) {
            onCropClick();
        } else if (v == rotateBtn) {
            onRotateClick();
        } else if (v == mTextBtn) {
            onAddTextClick();
        } else if (v == mPaintBtn) {
            onPaintClick();
        }
    }

    /**
     * 贴图模式
     *
     * @author panyi
     */
    private void onStickClick() {
        activity.mode = EditImageActivity.MODE_STICKERS;
        activity.mStirckerFragment.getmStickerView().setVisibility(
                View.VISIBLE);
        activity.bottomGallery.setCurrentItem(StirckerFragment.INDEX);
        activity.bannerFlipper.showNext();
    }

    /**
     * 滤镜模式
     *
     * @author panyi
     */
    private void onFilterClick() {
        activity.mode = EditImageActivity.MODE_FILTER;
        activity.mFliterListFragment.setCurrentBitmap(activity.mainBitmap);
        activity.mainImage.setImageBitmap(activity.mainBitmap);
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setScaleEnabled(false);
        activity.bottomGallery.setCurrentItem(FliterListFragment.INDEX);
        activity.bannerFlipper.showNext();
    }

    /**
     * 裁剪模式
     *
     * @author panyi
     */
    private void onCropClick() {
        activity.mode = EditImageActivity.MODE_CROP;
        activity.mCropPanel.setVisibility(View.VISIBLE);
        activity.mainImage.setImageBitmap(activity.mainBitmap);
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.bottomGallery.setCurrentItem(CropFragment.INDEX);
        activity.mainImage.setScaleEnabled(false);// 禁用缩放
        //
        RectF r = activity.mainImage.getBitmapRect();
        activity.mCropPanel.setCropRect(r);
        // System.out.println(r.left + "    " + r.top);
        activity.bannerFlipper.showNext();
    }

    /**
     * 图片旋转模式
     *
     * @author panyi
     */
    private void onRotateClick() {
        activity.mode = EditImageActivity.MODE_ROTATE;
        activity.bottomGallery.setCurrentItem(RotateFragment.INDEX);
        activity.mainImage.setImageBitmap(activity.mainBitmap);
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setVisibility(View.GONE);

        activity.mRotatePanel.addBit(activity.mainBitmap,
                activity.mainImage.getBitmapRect());
        activity.mRotateFragment.mSeekBar.setProgress(0);
        activity.mRotatePanel.reset();
        activity.mRotatePanel.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showNext();
    }

    /**
     * 插入文字模式
     *
     * @author panyi
     */
    private void onAddTextClick() {
        activity.mAddTextFragment.onShow();
    }

    /**
     * 自由绘制模式
     */
    private void onPaintClick() {
        activity.mPaintFragment.onShow();
    }

}// end class
