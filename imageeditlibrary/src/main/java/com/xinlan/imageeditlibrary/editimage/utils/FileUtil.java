package com.xinlan.imageeditlibrary.editimage.utils;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by panyi on 16/10/23.
 */
public class FileUtil {
    public static boolean checkFileExist(final String path){
        if(TextUtils.isEmpty(path))
            return false;

        File file = new File(path);
        return file.exists();
    }
}//end class
