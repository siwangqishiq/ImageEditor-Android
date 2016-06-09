package com.xinlan.imageeditlibrary.editimage.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.xinlan.imageeditlibrary.editimage.ui.ColorPicker;
import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;


/**
 * 添加文本贴图
 *
 * @author 潘易
 */
public class AddTextFragment extends Fragment {
    public static final int INDEX = 5;
    public static final String TAG = AddTextFragment.class.getName();

    private View mainView;
    private EditImageActivity activity;
    private View backToMenu;// 返回主菜单

    private EditText mInputText;//输入框
    private ImageView mTextColorSelector;//颜色选择器
    private TextStickerView mTextStickerView;// 文字贴图显示控件

    private ColorPicker mColorPicker;

    private int mTextColor = Color.WHITE;

    public static AddTextFragment newInstance(EditImageActivity activity) {
        AddTextFragment fragment = new AddTextFragment();
        fragment.activity = activity;
        fragment.mTextStickerView = activity.mTextStickerView;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_add_text, null);
        backToMenu = mainView.findViewById(R.id.back_to_main);
        mInputText = (EditText) mainView.findViewById(R.id.text_input);
        mTextColorSelector = (ImageView) mainView.findViewById(R.id.text_color);
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backToMenu.setOnClickListener(new BackToMenuClick());// 返回主菜单

        mTextStickerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideInput();
                    return true;
                }
                return false;
            }
        });

        mColorPicker = new ColorPicker(getActivity(), 255, 255, 255);
        mTextColorSelector.setOnClickListener(new SelectColorBtnClick());
    }

    /**
     * 颜色选择 按钮点击
     */
    private final class SelectColorBtnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            mColorPicker.show();
            Button okColor = (Button) mColorPicker.findViewById(R.id.okColorButton);
            okColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeTextColor(mColorPicker.getColor());
                    mColorPicker.dismiss();
                }
            });
        }
    }//end inner class

    /**
     * 修改字体颜色
     *
     * @param newColor
     */
    private void changeTextColor(int newColor) {
        this.mTextColor = newColor;
        mTextColorSelector.setBackgroundColor(mTextColor);
    }

    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
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
    public void backToMain() {
        activity.mode = EditImageActivity.MODE_NONE;
        activity.bottomGallery.setCurrentItem(MainMenuFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        activity.bannerFlipper.showPrevious();
        mTextStickerView.setVisibility(View.GONE);
    }

    public void onShow() {
        activity.mode = EditImageActivity.MODE_TEXT;
        activity.bottomGallery.setCurrentItem(AddTextFragment.INDEX);
        activity.mainImage.setImageBitmap(activity.mainBitmap);
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.bannerFlipper.showNext();

        mTextStickerView.setVisibility(View.VISIBLE);
    }
}// end class
