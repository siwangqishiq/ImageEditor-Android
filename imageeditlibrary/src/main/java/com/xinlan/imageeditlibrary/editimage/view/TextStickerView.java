package com.xinlan.imageeditlibrary.editimage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * 文本贴图处理控件
 * Created by panyi on 2016/6/9.
 */
public class TextStickerView extends StickerView {
    private String mText;
    private Paint mPaint = new Paint();

    public TextStickerView(Context context) {
        super(context);
        initView();
    }

    public TextStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TextStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(45);
    }

    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    public void setTextColor(int newColor) {
        mPaint.setColor(newColor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(mText))
            return;

        int x = getWidth() >> 1;
        int y = getHeight() >> 1;
        canvas.drawText(mText, x, y, mPaint);
    }
}//end class
