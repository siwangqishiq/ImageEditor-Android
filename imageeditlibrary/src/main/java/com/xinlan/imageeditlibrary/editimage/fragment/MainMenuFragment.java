package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;


/**
 * 工具栏主菜单
 * 
 * @author panyi
 * 
 */
public class MainMenuFragment extends Fragment {
	public static final String TAG = MainMenuFragment.class.getName();
	private View mainView;
	private EditImageActivity activity;

	private View stickerBtn;// 贴图按钮
	private View fliterBtn;// 滤镜按钮
	private View cropBtn;// 剪裁按钮
	private View rotateBtn;// 旋转按钮

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
		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		stickerBtn.setOnClickListener(new StickerClick());
		fliterBtn.setOnClickListener(new FliterClick());
		cropBtn.setOnClickListener(new CropClick());
		rotateBtn.setOnClickListener(new RotateClick());
	}

	/**
	 * 贴图模式
	 * 
	 * @author panyi
	 * 
	 */
	private final class StickerClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			activity.mode = EditImageActivity.MODE_STICKERS;
			activity.mStirckerFragment.getmStickerView().setVisibility(
					View.VISIBLE);
			activity.bottomGallery.setCurrentItem(1);
			activity.bannerFlipper.showNext();
		}
	}// end inner class

	/**
	 * 滤镜模式
	 * 
	 * @author panyi
	 * 
	 */
	private final class FliterClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			activity.mode = EditImageActivity.MODE_FILTER;
			activity.mFliterListFragment.setCurrentBitmap(activity.mainBitmap);
			activity.mainImage.setImageBitmap(activity.mainBitmap);
			activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
			activity.mainImage.setScaleEnabled(false);
			activity.bottomGallery.setCurrentItem(2);
			activity.bannerFlipper.showNext();
		}
	}// end inner class

	/**
	 * 裁剪模式
	 * 
	 * @author panyi
	 * 
	 */
	private final class CropClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			activity.mode = EditImageActivity.MODE_CROP;
			activity.mCropPanel.setVisibility(View.VISIBLE);
			activity.mainImage.setImageBitmap(activity.mainBitmap);
			activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
			activity.bottomGallery.setCurrentItem(3);
			activity.mainImage.setScaleEnabled(false);// 禁用缩放
			//
			RectF r = activity.mainImage.getBitmapRect();
			activity.mCropPanel.setCropRect(r);
			// System.out.println(r.left + "    " + r.top);
			activity.bannerFlipper.showNext();
		}
	}// end inner class

	/**
	 * 图片旋转模式
	 * 
	 * @author panyi
	 * 
	 */
	private final class RotateClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			activity.mode = EditImageActivity.MODE_ROTATE;
			activity.bottomGallery.setCurrentItem(4);
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
	}// end inner class

}// end class
