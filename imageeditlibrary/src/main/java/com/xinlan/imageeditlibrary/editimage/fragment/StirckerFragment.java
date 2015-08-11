package com.xinlan.imageeditlibrary.editimage.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.adapter.StickerAdapter;
import com.xinlan.imageeditlibrary.editimage.adapter.StickerTypeAdapter;
import com.xinlan.imageeditlibrary.editimage.model.StickerBean;
import com.xinlan.imageeditlibrary.editimage.utils.Matrix3;
import com.xinlan.imageeditlibrary.editimage.view.StickerItem;
import com.xinlan.imageeditlibrary.editimage.view.StickerView;

/**
 * 贴图分类fragment
 *
 * @author panyi
 */
public class StirckerFragment extends Fragment {
    public static final String TAG = StirckerFragment.class.getName();
    public static final String STICKER_FOLDER = "stickers";

    private View mainView;
    private EditImageActivity activity;
    private ViewFlipper flipper;
    private View backToMenu;// 返回主菜单
    private RecyclerView typeList;// 贴图分类列表
    private RecyclerView stickerList;// 贴图素材列表
    private View backToType;// 返回类型选择
    private StickerView mStickerView;// 贴图显示控件
    private StickerAdapter mStickerAdapter;// 贴图列表适配器

    private LoadStickersTask mLoadStickersTask;
    private List<StickerBean> stickerBeanList = new ArrayList<StickerBean>();

    public static StirckerFragment newInstance(EditImageActivity activity) {
        StirckerFragment fragment = new StirckerFragment();
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
        mainView = inflater.inflate(R.layout.fragment_edit_image_sticker_type,
                null);
        this.mStickerView = activity.mStickerView;
        flipper = (ViewFlipper) mainView.findViewById(R.id.flipper);
        flipper.setInAnimation(activity, R.anim.in_bottom_to_top);
        flipper.setOutAnimation(activity, R.anim.out_bottom_to_top);

        //
        backToMenu = mainView.findViewById(R.id.back_to_main);
        typeList = (RecyclerView) mainView
                .findViewById(R.id.stickers_type_list);
        typeList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typeList.setLayoutManager(mLayoutManager);
        typeList.setAdapter(new StickerTypeAdapter(this));
        backToType = mainView.findViewById(R.id.back_to_type);// back按钮

        stickerList = (RecyclerView) mainView.findViewById(R.id.stickers_list);
        // stickerList.setHasFixedSize(true);
        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(
                activity);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        stickerList.setLayoutManager(stickerListLayoutManager);
        mStickerAdapter = new StickerAdapter(this);
        stickerList.setAdapter(mStickerAdapter);

        //loadStickersData();

        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单
        backToType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {// 返回上一级列表
                flipper.showPrevious();
            }
        });
    }

    //导入贴图数据
    private void loadStickersData() {
        if (mLoadStickersTask != null) {
            mLoadStickersTask.cancel(true);
        }
        mLoadStickersTask = new LoadStickersTask();
        mLoadStickersTask.execute(1);
    }


    /**
     * 导入贴图数据
     */
    private final class LoadStickersTask extends AsyncTask<Integer, Void, Void> {
        private Dialog loadDialog;

        public LoadStickersTask() {
            super();
            loadDialog = BaseActivity.getLoadingDialog(getActivity(), "正在载入贴图数据...", false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            stickerBeanList.clear();
            AssetManager assetManager = getActivity().getAssets();
            try {
                String[] lists =   assetManager.list(STICKER_FOLDER);
                for(String parentPath:lists){

                }//end for each
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadDialog.dismiss();

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            loadDialog.dismiss();
        }
    }//end inner class

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadStickersTask != null) {
            mLoadStickersTask.cancel(true);
        }
    }

    /**
     * 跳转至贴图详情列表
     *
     * @param path
     */
    public void swipToStickerDetails(String path) {
        mStickerAdapter.addStickerImages(path);
        flipper.showNext();
    }

    /**
     * 从Assert文件夹中读取位图数据
     *
     * @param fileName
     * @return
     */
    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 选择贴图加入到页面中
     *
     * @param path
     */
    public void selectedStickerItem(String path) {
        mStickerView.addBitImage(getImageFromAssetsFile(path));
    }

    public StickerView getmStickerView() {
        return mStickerView;
    }

    public void setmStickerView(StickerView mStickerView) {
        this.mStickerView = mStickerView;
    }

    /**
     * 返回主菜单页面
     *
     * @author panyi
     */
    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }// end inner class

    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(0);
        mStickerView.setVisibility(View.GONE);
        activity.bannerFlipper.showPrevious();
    }

    /**
     * 保存贴图任务
     *
     * @author panyi
     */
    private final class SaveStickersTask extends
            AsyncTask<Bitmap, Void, Bitmap> {
        private Dialog dialog;

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            // System.out.println("保存贴图!");
            Matrix touchMatrix = activity.mainImage.getImageViewMatrix();

            Bitmap resultBit = Bitmap.createBitmap(params[0]).copy(
                    Bitmap.Config.RGB_565, true);
            Canvas canvas = new Canvas(resultBit);

            float[] data = new float[9];
            touchMatrix.getValues(data);// 底部图片变化记录矩阵原始数据
            Matrix3 cal = new Matrix3(data);// 辅助矩阵计算类
            Matrix3 inverseMatrix = cal.inverseMatrix();// 计算逆矩阵
            Matrix m = new Matrix();
            m.setValues(inverseMatrix.getValues());

            LinkedHashMap<Integer, StickerItem> addItems = mStickerView
                    .getBank();
            for (Integer id : addItems.keySet()) {
                StickerItem item = addItems.get(id);
                item.matrix.postConcat(m);// 乘以底部图片变化矩阵
                canvas.drawBitmap(item.bitmap, item.matrix, null);
            }// end for
            saveBitmap(resultBit, activity.saveFilePath);
            return resultBit;
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
            mStickerView.clear();
            activity.changeMainBitmap(result);
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = BaseActivity.getLoadingDialog(getActivity(), "图片合成保存中...",
                    false);
            dialog.show();
        }
    }// end inner class

    /**
     * 保存贴图层 合成一张图片
     */
    public void saveStickers() {
        // System.out.println("保存 合成图片");
        SaveStickersTask task = new SaveStickersTask();
        task.execute(activity.mainBitmap);
    }

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
