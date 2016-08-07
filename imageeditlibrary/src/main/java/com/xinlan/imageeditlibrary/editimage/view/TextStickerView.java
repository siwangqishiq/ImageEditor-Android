package com.xinlan.imageeditlibrary.editimage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.utils.RectUtil;

/**
 * 文本贴图处理控件
 * <p/>
 * Created by panyi on 2016/6/9.
 */
public class TextStickerView extends View {
    public static final float TEXT_SIZE_DEFAULT = 80;
    public static final int PADDING = 32;


    private String mText;
    private TextPaint mPaint = new TextPaint();
    private Paint debugPaint = new Paint();
    private Paint mHelpPaint = new Paint();

    private Rect mTextRect = new Rect();// warp text rect record
    private RectF mHelpBoxRect = new RectF();
    private Rect mDeleteRect = new Rect();//删除按钮位置
    private Rect mRotateRect = new Rect();//旋转按钮位置

    private RectF mDeleteDstRect = new RectF();
    private RectF mRotateDstRect = new RectF();

    private Bitmap mDeleteBitmap;
    private Bitmap mRotateBitmap;

    private int mCurrentMode = IDLE_MODE;
    //控件的几种模式
    private static final int IDLE_MODE = 2;//正常
    private static final int MOVE_MODE = 3;//移动模式
    private static final int ROTATE_MODE = 4;//旋转模式
    private static final int DELETE_MODE = 5;//删除模式

    private EditText mEditText;//输入控件

    private int layout_x = 0;
    private int layout_y = 0;

    private float last_x = 0;
    private float last_y = 0;

    private float mRotateAngle = 0;
    private float mScale = 1;

    private Matrix mMatrix = new Matrix();

    public TextStickerView(Context context) {
        super(context);
        initView(context);
    }

    public TextStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setEditText(EditText textView) {
        this.mEditText = textView;
    }

    private void initView(Context context) {
        debugPaint.setColor(Color.parseColor("#66ff0000"));

        mDeleteBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sticker_delete);
        mRotateBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.sticker_rotate);

        mDeleteRect.set(0, 0, mDeleteBitmap.getWidth(), mDeleteBitmap.getHeight());
        mRotateRect.set(0, 0, mRotateBitmap.getWidth(), mRotateBitmap.getHeight());

        mDeleteDstRect = new RectF(0, 0, Constants.STICKER_BTN_HALF_SIZE << 1, Constants.STICKER_BTN_HALF_SIZE << 1);
        mRotateDstRect = new RectF(0, 0, Constants.STICKER_BTN_HALF_SIZE << 1, Constants.STICKER_BTN_HALF_SIZE << 1);

        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(TEXT_SIZE_DEFAULT);

        mHelpPaint.setColor(Color.BLACK);
        mHelpPaint.setStyle(Paint.Style.STROKE);
        mHelpPaint.setAntiAlias(true);
        mHelpPaint.setStrokeWidth(4);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        layout_x = getMeasuredWidth() / 2;
        layout_y = getMeasuredHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(mText))
            return;

        drawContent(canvas);
    }

    private void drawContent(Canvas canvas) {
        drawText(canvas);

        canvas.drawRoundRect(mHelpBoxRect, 10, 10, mHelpPaint);

        //draw x and rotate button
        int offsetValue = ((int) mDeleteDstRect.width()) >> 1;
        mDeleteDstRect.offsetTo(mHelpBoxRect.left - offsetValue, mHelpBoxRect.top - offsetValue);
        mRotateDstRect.offsetTo(mHelpBoxRect.right - offsetValue, mHelpBoxRect.bottom - offsetValue);

        canvas.drawBitmap(mDeleteBitmap, mDeleteRect, mDeleteDstRect, null);
        canvas.drawBitmap(mRotateBitmap, mRotateRect, mRotateDstRect, null);


        canvas.drawRect(mRotateDstRect, debugPaint);
        canvas.drawRect(mDeleteDstRect, debugPaint);
    }

    private void drawText(Canvas canvas) {
        int x = layout_x;
        int y = layout_y;

        mPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        mTextRect.offset(x - (mTextRect.width() >> 1), y);

        mHelpBoxRect.set(mTextRect.left - PADDING, mTextRect.top - PADDING
                , mTextRect.right + PADDING, mTextRect.bottom + PADDING);
        RectUtil.scaleRect(mHelpBoxRect, mScale);

        canvas.save();
        //canvas.rotate(60, x, y);
        //canvas.scale(2f,2f,x,y);
        canvas.scale(mScale, mScale, mHelpBoxRect.centerX(), mHelpBoxRect.centerY());
        canvas.drawText(mText, x, y, mPaint);
        canvas.restore();

        //canvas.drawRect(mTextRect, debugPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mDeleteDstRect.contains(x, y)) {// 删除模式
                    mCurrentMode = DELETE_MODE;
                } else if (mRotateDstRect.contains(x, y)) {// 旋转按钮
                    mCurrentMode = ROTATE_MODE;
                    last_x = mRotateDstRect.centerX();
                    last_y = mRotateDstRect.centerY();
                    ret = true;
                } else if (mHelpBoxRect.contains(x, y)) {// 移动模式
                    mCurrentMode = MOVE_MODE;
                    last_x = x;
                    last_y = y;
                    ret = true;
                }// end if

                if (mCurrentMode == DELETE_MODE) {// 删除选定贴图
                    mCurrentMode = IDLE_MODE;// 返回空闲状态
                    clearTextContent();
                    invalidate();
                }// end if
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                if (mCurrentMode == MOVE_MODE) {// 移动贴图
                    mCurrentMode = MOVE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;

                    layout_x += dx;
                    layout_y += dy;

                    invalidate();

                    last_x = x;
                    last_y = y;
                } else if (mCurrentMode == ROTATE_MODE) {// 旋转 缩放文字操作
                    mCurrentMode = ROTATE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;

                    updateRotateAndScale(dx, dy);

                    invalidate();
                    last_x = x;
                    last_y = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ret = false;
                mCurrentMode = IDLE_MODE;
                break;
        }// end switch

        return ret;
    }

    public void clearTextContent() {
        if (mEditText != null) {
            mEditText.setText(null);
        }
        //setText(null);
    }


    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    public void updateRotateAndScale(final float dx, final float dy) {
        float c_x = mHelpBoxRect.centerX();
        float c_y = mHelpBoxRect.centerY();

        float x = mRotateDstRect.centerX();
        float y = mRotateDstRect.centerY();

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;// 计算缩放比

        mScale *= scale;

        //System.out.println("mScale = " + mScale);

        float newWidth = mHelpBoxRect.width() * mScale;
//        if (newWidth / initWidth < MIN_SCALE) {// 最小缩放值检测
//            return;
//        }


//        this.matrix.postScale(scale, scale, this.dstRect.centerX(),
//                this.dstRect.centerY());// 存入scale矩阵
//        RectUtil.scaleRect(this.dstRect, scale);// 缩放目标矩形
//
//        // 重新计算工具箱坐标
//        helpBox.set(dstRect);
//        updateHelpBoxRect();// 重新计算
//        rotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
//                - BUTTON_WIDTH);
//        deleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
//                - BUTTON_WIDTH);
//
//        detectRotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
//                - BUTTON_WIDTH);
//        detectDeleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
//                - BUTTON_WIDTH);
//
//        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
//        if (cos > 1 || cos < -1)
//            return;
//        float angle = (float) Math.toDegrees(Math.acos(cos));
//        // System.out.println("angle--->" + angle);
//
        // 定理
//        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向
//
//        int flag = calMatrix > 0 ? 1 : -1;
//        angle = flag * angle;
//
//        roatetAngle += angle;
//        this.matrix.postRotate(angle, this.dstRect.centerX(),
//                this.dstRect.centerY());
//
//        RectUtil.rotateRect(this.detectRotateRect, this.dstRect.centerX(),
//                this.dstRect.centerY(), roatetAngle);
//        RectUtil.rotateRect(this.detectDeleteRect, this.dstRect.centerX(),
//                this.dstRect.centerY(), roatetAngle);
    }

}//end class
