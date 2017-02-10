package com.xinlan.imageeditlibrary.editimage.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;


/**
 * 用户自由绘制模式 操作面板
 * 可设置画笔粗细 画笔颜色
 * custom draw mode panel
 *
 * @author panyi
 */
public class PaintFragment extends Fragment implements View.OnClickListener {
    public static final int INDEX = 6;
    public static final String TAG = PaintFragment.class.getName();

    private View mainView;
    private EditImageActivity activity;
    private View backToMenu;// 返回主菜单
    private RecyclerView mColorListView;//颜色列表View

    public boolean isEraser = false;//是否是擦除模式

    public int[] mPaintColors = {Color.RED, Color.CYAN, Color.YELLOW, Color.GREEN, Color.WHITE, Color.BLUE, Color.GRAY};

    public static PaintFragment newInstance(EditImageActivity activity) {
        PaintFragment fragment = new PaintFragment();
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
        mainView = inflater.inflate(R.layout.fragment_edit_paint, null);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        mColorListView = (RecyclerView) mainView.findViewById(R.id.paint_color_list);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backToMenu.setOnClickListener(this);// 返回主菜单

        initColorListView();
    }

    private void initColorListView(){
        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(activity);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mColorListView.setLayoutManager(stickerListLayoutManager);
    }

    @Override
    public void onClick(View v) {
        if (v == backToMenu) {//back button click
            backToMain();
        }//end if
    }

    /**
     * 返回主菜单
     */
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showPrevious();
    }

    public void onShow() {
        activity.mode = EditImageActivity.MODE_PAINT;
        activity.bottomGallery.setCurrentItem(PaintFragment.INDEX);
        activity.mainImage.setImageBitmap(activity.mainBitmap);
        activity.bannerFlipper.showNext();
    }

}// end class
