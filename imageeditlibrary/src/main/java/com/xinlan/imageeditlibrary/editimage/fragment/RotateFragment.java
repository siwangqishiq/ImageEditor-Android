package com.xinlan.imageeditlibrary.editimage.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.view.RotateImageView;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;


/**
 * 图片旋转Fragment
 * 
 * @author 潘易
 * 
 */
public class RotateFragment extends Fragment {
	public static final String TAG = RotateFragment.class.getName();
	private View mainView;
	private EditImageActivity activity;
	private View backToMenu;// 返回主菜单
	public SeekBar mSeekBar;// 角度设定
	private RotateImageView mRotatePanel;// 旋转效果展示控件

	public static RotateFragment newInstance(EditImageActivity activity) {
		RotateFragment fragment = new RotateFragment();
		fragment.activity = activity;
		fragment.mRotatePanel = activity.mRotatePanel;
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
		backToMenu = mainView.findViewById(R.id.back_to_main);
		mSeekBar = (SeekBar) mainView.findViewById(R.id.rotate_bar);
		mSeekBar.setProgress(0);
		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单

		mSeekBar.setOnSeekBarChangeListener(new RotateAngleChange());
	}

	/**
	 * 角度改变监听
	 * 
	 * @author panyi
	 * 
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
	 * 
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
	public void saveRotateImage() {
		// System.out.println("保存旋转图片");
		if (mSeekBar.getProgress() == 0 || mSeekBar.getProgress() == 360) {// 没有做旋转
			backToMain();
			return;
		} else {// 保存图片
			SaveRotateImageTask task = new SaveRotateImageTask();
			task.execute(activity.mainBitmap);
		}// end if
	}

	/**
	 * 保存图片线程
	 * 
	 * @author panyi
	 * 
	 */
	private final class SaveRotateImageTask extends
			AsyncTask<Bitmap, Void, Bitmap> {
		private Dialog dialog;

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dialog.dismiss();
		}

		@Override
		protected void onCancelled(Bitmap result) {
			super.onCancelled(result);
			dialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = BaseActivity.getLoadingDialog(getActivity(), "图片保存中...",
					false);
			dialog.show();
		}

		@Override
		protected Bitmap doInBackground(Bitmap... params) {
			RectF imageRect = mRotatePanel.getImageNewRect();
			Bitmap originBit = params[0];
			Bitmap result = Bitmap.createBitmap((int) imageRect.width(),
					(int) imageRect.height(), Bitmap.Config.ARGB_4444);
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
			canvas.scale(mRotatePanel.getScale(), mRotatePanel.getScale(),
					imageRect.width() / 2, imageRect.height() / 2);
			canvas.rotate(mRotatePanel.getRotateAngle(), imageRect.width() / 2,
					imageRect.height() / 2);

			canvas.drawBitmap(originBit, new Rect(0, 0, originBit.getWidth(),
					originBit.getHeight()), dst, null);
			canvas.restore();

			saveBitmap(result, activity.saveFilePath);// 保存图片
			return result;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result == null)
				return;

			// 切换新底图
			if (activity.mainBitmap != null
					&& !activity.mainBitmap.isRecycled()) {
				activity.mainBitmap.recycle();
			}
			activity.mainBitmap = result;
			activity.mainImage.setImageBitmap(activity.mainBitmap);
			activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
			backToMain();
		}
	}// end inner class

	/**
	 * 保存Bitmap图片到指定文件
	 * 
	 * @param bm
	 * @param name
	 */
	public static void saveBitmap(Bitmap bm, String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("保存文件--->" + f.getAbsolutePath());
	}
}// end class
