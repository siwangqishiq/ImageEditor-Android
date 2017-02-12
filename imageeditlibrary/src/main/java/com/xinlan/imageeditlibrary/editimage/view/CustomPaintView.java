package com.xinlan.imageeditlibrary.editimage.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by panyi on 17/2/11.
 */

public class CustomPaintView extends View {
    public CustomPaintView(Context context) {
        super(context);
    }

    public CustomPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomPaintView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}//end class
