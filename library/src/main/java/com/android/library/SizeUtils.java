package com.android.library;

import android.content.res.Resources;

/**
 * -----------------------------------------------
 * 作    者：高延荣
 * 电    话：18963580395
 * 创建日期：2020/11/22 20:26
 * 描    述：
 * 修订历史：
 * -----------------------------------------------
 */
public class SizeUtils {

    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
