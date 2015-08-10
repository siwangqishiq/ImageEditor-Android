package com.xinlan.imageeditlibrary.editimage.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;


/**
 * 滤镜列表fragment
 * 
 * @author panyi
 * 
 */
public class FliterListFragment extends Fragment {
	public static final String TAG = FliterListFragment.class.getName();
	private View mainView;
	private View backBtn;// 返回主菜单按钮

	private Bitmap fliterBit;// 滤镜处理后的bitmap
	private EditImageActivity activity;

	private LinearLayout mFilterGroup;// 滤镜列表
	private String[] fliters = PhotoProcessing.FILTERS;
	private Bitmap currentBitmap;// 标记变量

	public static FliterListFragment newInstance(EditImageActivity activity) {
		FliterListFragment fragment = new FliterListFragment();
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
		mainView = inflater.inflate(R.layout.fragment_edit_image_fliter, null);
		backBtn = mainView.findViewById(R.id.back_to_main);
		mFilterGroup = (LinearLayout) mainView.findViewById(R.id.fliter_group);
		return mainView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backToMain();
			}
		});
		setUpFliters();
	}

	/**
	 * 返回主菜单
	 */
	public void backToMain() {
		currentBitmap = activity.mainBitmap;
		fliterBit = null;
		activity.mainImage.setImageBitmap(activity.mainBitmap);// 返回原图
		activity.mode = EditImageActivity.MODE_NONE;
		activity.bottomGallery.setCurrentItem(0);
		activity.mainImage.setScaleEnabled(true);
		activity.bannerFlipper.showPrevious();
	}

	/**
	 * 保存滤镜处理后的图片
	 */
	public void saveFilterImage() {
		// System.out.println("保存滤镜处理后的图片");
		if (currentBitmap == activity.mainBitmap) {// 原始图片
			// System.out.println("原始图片");
			backToMain();
			return;
		} else {// 经滤镜处理后的图片
			// System.out.println("滤镜图片");
			SaveImageTask saveTask = new SaveImageTask();
			saveTask.execute(fliterBit);
		}// end if
	}

	/**
	 * 保存滤镜处理图片任务
	 * 
	 * @author panyi
	 * 
	 */
	private final class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {
		private Dialog dialog;

		@Override
		protected Boolean doInBackground(Bitmap... params) {
			return saveBitmap(params[0], activity.saveFilePath);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dialog.dismiss();
		}

		@Override
		protected void onCancelled(Boolean result) {
			super.onCancelled(result);
			dialog.dismiss();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result) {// 保存图片成功
				if (activity.mainBitmap != null
						&& !activity.mainBitmap.isRecycled()) {
					activity.mainBitmap.recycle();
				}
				activity.mainBitmap = fliterBit;
				backToMain();
			}// end if
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = EditImageActivity.getLoadingDialog(getActivity(),
					"图片保存中...", false);
			dialog.show();
		}
	}// end inner class

	/**
	 * 保存Bitmap图片到指定文件
	 * 
	 * @param bm
	 */
	public static boolean saveBitmap(Bitmap bm, String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		// System.out.println("保存文件--->" + f.getAbsolutePath());
	}

	/**
	 * 装载滤镜
	 */
	private void setUpFliters() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		params.leftMargin = 20;
		params.rightMargin = 20;
		mFilterGroup.removeAllViews();
		for (int i = 0, len = fliters.length; i < len; i++) {
			TextView text = new TextView(activity);
			text.setTextColor(Color.WHITE);
			text.setTextSize(20);
			text.setText(fliters[i]);
			mFilterGroup.addView(text, params);
			text.setTag(i);
			text.setOnClickListener(new FliterClick());
		}// end for i
	}

	@Override
	public void onDestroy() {
		if (fliterBit != null && (!fliterBit.isRecycled())) {
			fliterBit.recycle();
		}
		super.onDestroy();
	}

	/**
	 * 选择滤镜效果
	 */
	private final class FliterClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			int position = ((Integer) v.getTag()).intValue();
			if (position == 0) {// 原始图片效果
				activity.mainImage.setImageBitmap(activity.mainBitmap);
				currentBitmap = activity.mainBitmap;
				return;
			}
			// 滤镜处理
			ProcessingImage task = new ProcessingImage();
			task.execute(position);
		}
	}// end inner class

	/**
	 * 图片滤镜处理任务
	 * 
	 * @author panyi
	 * 
	 */
	private final class ProcessingImage extends
			AsyncTask<Integer, Void, Bitmap> {
		private Dialog dialog;
		private Bitmap srcBitmap;

		@Override
		protected Bitmap doInBackground(Integer... params) {
			int type = params[0];
			if (srcBitmap != null && !srcBitmap.isRecycled()) {
				srcBitmap.recycle();
			}

			srcBitmap = Bitmap.createBitmap(activity.mainBitmap.copy(
					Bitmap.Config.RGB_565, true));
			return PhotoProcessing.filterPhoto(srcBitmap, type);
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
			if (fliterBit != null && (!fliterBit.isRecycled())) {
				fliterBit.recycle();
			}
			fliterBit = result;
			activity.mainImage.setImageBitmap(fliterBit);
			currentBitmap = fliterBit;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = BaseActivity.getLoadingDialog(getActivity(), "图片处理中...",
					false);
			dialog.show();
		}

	}// end inner class

	public Bitmap getCurrentBitmap() {
		return currentBitmap;
	}

	public void setCurrentBitmap(Bitmap currentBitmap) {
		this.currentBitmap = currentBitmap;
	}
}// end class
