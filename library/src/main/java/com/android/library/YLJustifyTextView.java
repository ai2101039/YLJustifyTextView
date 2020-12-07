package com.android.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.icu.text.UFormat;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.InspectableProperty;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;


/**
 * -----------------------------------------------
 * 作    者：高延荣
 * 电    话：18963580395
 * 创建日期：2020/11/22 - 19:57 PM
 * 描    述：两端对齐textView
 * 修订历史：
 * -----------------------------------------------
 */
public class YLJustifyTextView extends androidx.appcompat.widget.AppCompatTextView {


    /*********************** 文字相关属性 ***********************/

    /*
     * 左侧文字、颜色、字号、行间距 -- 倍数、行间距 -- 数值、宽度、权重比
     */
    private String leftText;
    private int leftColor;
    private float leftSize, leftSpacingMulti, leftSpacingAdd, leftWidth, leftWidthWeight;
    /*
     * 右侧文字、颜色、字号、行间距 -- 倍数、行间距 -- 数值、宽度、权重比
     */
    private String rightText;
    private int rightColor;
    private float rightSize, rightSpacingMulti, rightSpacingAdd, rightWidth, rightWidthWeight;

    /*********************** 画笔相关属性 ***********************/

    /*
     * 左侧文字画笔、右侧文字画笔、顶部ViewLine画笔、底部ViewLine画笔
     */
    private TextPaint leftPaint, rightPaint;
    private Paint topViewLinePaint, bottomViewLinePaint;

    /*********************** ViewLine相关属性 ***********************/

    /*
     * 顶部ViewLine高度、底部ViewLine高度 、顶部ViewLine颜色、底部ViewLine颜色
     */
    private float topViewLineHeight, bottomViewLineHeight;

    private int topViewLineColor, bottomViewLineColor;


    /*********************** 其他 ***********************/

    private Context mContext;
    private Resources mResources;

    /**
     * 这个属性，只有在 控件 wrap_content 且按照 权重分配时才有效
     */
    private int widthWeightFlag_YL;

    /*
     * 左上右下 四个图片的宽度 （包括图片宽高 和 图片外边距）
     */
    private int drawLeft_W;
    private int drawLeft_H;
    private int drawTop_W;
    private int drawTop_H;
    private int drawRight_W;
    private int drawRight_H;
    private int drawBottom_W;
    private int drawBottom_H;


    /*
     *  左上右下 padding
     */
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;

    /*
     * 左边文字 和 右边文字的间隔
     */
    private float space;

    /*
     * 最小高度，最大高度
     */
    private float minHeight;
    private float maxHeight;


    /*********************** 构造器 ***********************/

    public YLJustifyTextView(Context context) {
        this(context, null);
    }

    public YLJustifyTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YLJustifyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mResources = context.getResources();

        /*
         * 1、设置自定义属性
         * 2、设置画笔
         */

        initCustomAttrs(attrs);
        initPaint();
    }

    /**
     * 获取自定义属性
     */
    private void initCustomAttrs(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.YLJustifyTextView);
        //  左侧文字
        leftText = ta.getString(R.styleable.YLJustifyTextView_leftText_YL);
        leftColor = ta.getColor(R.styleable.YLJustifyTextView_leftTextColor_YL, mResources.getColor(R.color.lib_000));
        leftSize = ta.getDimension(R.styleable.YLJustifyTextView_leftTextSize_YL, SizeUtils.dp2px(16F));
        leftSpacingMulti = ta.getFloat(R.styleable.YLJustifyTextView_leftTextSpacingMulti_YL, 1F);
        leftSpacingAdd = ta.getFloat(R.styleable.YLJustifyTextView_leftTextSpacingAdd_YL, 0F);
        leftWidth = ta.getDimension(R.styleable.YLJustifyTextView_leftTextWidth_YL, -2F);
        leftWidthWeight = ta.getFloat(R.styleable.YLJustifyTextView_leftTextWidthWeight_YL, 0F);

        //  右侧文字
        rightText = ta.getString(R.styleable.YLJustifyTextView_rightText_YL);
        rightColor = ta.getColor(R.styleable.YLJustifyTextView_rightTextColor_YL, mResources.getColor(R.color.lib_000));
        rightSize = ta.getDimension(R.styleable.YLJustifyTextView_rightTextSize_YL, SizeUtils.dp2px(16F));
        rightSpacingMulti = ta.getFloat(R.styleable.YLJustifyTextView_rightTextSpacingMulti_YL, 1F);
        rightSpacingAdd = ta.getFloat(R.styleable.YLJustifyTextView_rightTextSpacingAdd_YL, 0F);
        rightWidth = ta.getDimension(R.styleable.YLJustifyTextView_rightTextWidth_YL, -1F);
        rightWidthWeight = ta.getFloat(R.styleable.YLJustifyTextView_rightTextWidthWeight_YL, 0F);

        //  ViewLine
        topViewLineHeight = ta.getDimension(R.styleable.YLJustifyTextView_topViewLineHeight_YL, 0F);
        topViewLineColor = ta.getColor(R.styleable.YLJustifyTextView_topViewLineColor_YL, mResources.getColor(R.color.lib_000));
        bottomViewLineHeight = ta.getDimension(R.styleable.YLJustifyTextView_bottomViewLineHeight_YL, 0F);
        bottomViewLineColor = ta.getColor(R.styleable.YLJustifyTextView_bottomViewLineColor_YL, mResources.getColor(R.color.lib_000));

        //  权重标记
        widthWeightFlag_YL = ta.getInt(R.styleable.YLJustifyTextView_widthWeightFlag_YL, -1);

        //  左边文字和右边文字之间的间隔
        space = ta.getDimension(R.styleable.YLJustifyTextView_space, SizeUtils.dp2px(10));

        //  最小高度、最大高度
        minHeight = ta.getDimension(R.styleable.YLJustifyTextView_minHeight_YL, 0F);
        maxHeight = ta.getDimension(R.styleable.YLJustifyTextView_maxHeight_YL, Float.MAX_VALUE);

        ta.recycle();
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        leftPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        rightPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        bottomViewLinePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        topViewLinePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        leftPaint.setTextSize(leftSize);
        leftPaint.setColor(leftColor);

        rightPaint.setTextSize(rightSize);
        rightPaint.setColor(rightColor);

        bottomViewLinePaint.setStrokeWidth(bottomViewLineHeight);
        bottomViewLinePaint.setColor(bottomViewLineColor);

        topViewLinePaint.setStrokeWidth(topViewLineHeight);
        topViewLinePaint.setColor(topViewLineColor);
    }


    /**
     * 测量中最难的是 宽度的确定
     * <p>
     * 这里提供了三种宽度
     * 举例说明
     * 1、默认形式
     * leftTextWidth_YL = "wrap_content_YL"  或者  rightTextWidth_YL = "match_parent_YL"
     * 2、固定值
     * leftTextWidth_YL = "200dp"  或者  rightTextWidth_YL = "300dp"
     * 3、权重比
     * leftTextWidthWeight_YL = "1"  或者  rightTextWidthWeight_YL = "2"
     * <p>
     * 这三种优先级
     * <p>
     * 权重比 >　固定值　> 默认
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initDefaultAttrs();

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);

        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);


        //  留给左右文字的宽度
        int useWidth = Math.max((int) (width - paddingLeft - paddingRight - drawLeft_W - drawRight_W - space), 0);

        //  1、看是否有权重，按权重分
        if (leftWidthWeight > 0 && rightWidthWeight > 0) {
            //  根据权重进行计算
            computeWidthWithWeight(useWidth);
        }

        //  2、固定值
        else if (leftWidth > 0 || rightWidth > 0) {
            computeWidthWithExactly(useWidth);
        }

        //  3、都是默认值
        else if (leftWidth < 0 && rightWidth < 0) {
            computeWidthWithDefault(useWidth);
        }


        //  当前控件宽度为 wrap_content
        if (widthMode == View.MeasureSpec.AT_MOST || widthMode == View.MeasureSpec.UNSPECIFIED) {
            width = (int) (leftWidth + rightWidth + paddingLeft + paddingRight + drawLeft_W + drawRight_W + space);
        }


        /*  1、如果高度是 wrapContent
         *  2、左侧图片，左侧文字，右侧图片，右侧文字，算出这四个哪个高
         *  3、与 minHeight 取最大，再与 maxHeight取最小
         */
        if (heightMode == View.MeasureSpec.AT_MOST || heightMode == View.MeasureSpec.UNSPECIFIED) {
            int contentHeight = Math.max(Math.max(getLeftMeasureHeight(), getRightMeasureHeight()), Math.max(drawLeft_H, drawRight_H));
            height = (int) Math.min(Math.max(contentHeight, minHeight), maxHeight - paddingTop - paddingBottom) + paddingTop + paddingBottom + drawTop_H + drawBottom_H;
        }

        setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //  绘制一些图
        super.onDraw(canvas);

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int textUseHeight = height - paddingTop - paddingBottom - drawTop_H - drawBottom_H;

        //  绘制一些线
        if (topViewLineHeight != 0) {
            canvas.drawLine(0, 0, width, 0, topViewLinePaint);
        }

        if (bottomViewLineHeight != 0) {
            float lineY = height - bottomViewLineHeight;
            canvas.drawLine(0, lineY, width, lineY, bottomViewLinePaint);
        }


        //  绘制左侧文字
        StaticLayout leftStaticLayout = getLeftStaticLayout();
        if (leftStaticLayout != null) {
            canvas.save();
            int dx = paddingLeft + drawLeft_W;
            int dy = Math.max((textUseHeight - leftStaticLayout.getHeight()) / 2, 0) + paddingTop + drawTop_H;
            canvas.translate(dx, dy);
            leftStaticLayout.draw(canvas);
            canvas.restore();
        }

        //  绘制右侧文字
        StaticLayout rightStaticLayout = getRightStaticLayout();
        if (rightStaticLayout != null) {
            canvas.save();
            //  正常来说，右侧文字绘制的起始点是  宽度-右侧文字宽度-右侧内边距-右侧图宽
            //  但是有可能用户属性设置错误导致 绘制点会叠加在左侧区域
            float dx = Math.max(width - rightWidth - paddingRight - drawRight_W, paddingLeft + drawLeft_W + leftWidth + space);
            int dy = Math.max((textUseHeight - rightStaticLayout.getHeight()) / 2, 0) + paddingTop + drawTop_H;
            canvas.translate(dx, dy);
            rightStaticLayout.draw(canvas);
            canvas.restore();
        }

    }


    /**
     * 根据权重进行计算
     *
     * @param useWidth 可用宽度
     */
    private void computeWidthWithWeight(int useWidth) {

        //  有控件最大宽度，有左右的文字，有左右文字的权重，有左右文字权重标记位

        //  先测左右文字宽度
        switch (widthWeightFlag_YL) {
            case -1:
                leftWidth = getWidthByText(leftPaint, leftText);
                rightWidth = leftWidth * rightWidthWeight / leftWidthWeight;
                break;

            case -2:
                rightWidth = getWidthByText(rightPaint, rightText);
                leftWidth = rightWidth * leftWidthWeight / rightWidthWeight;
                break;
        }

        if (leftWidth + rightWidth > useWidth) {
            leftWidth = useWidth * leftWidthWeight / (leftWidthWeight + rightWidthWeight);
            rightWidth = useWidth * rightWidthWeight / (leftWidthWeight + rightWidthWeight);
        }

    }


    /**
     * 左或者右 有一个宽度确定时
     *
     * @param useWidth 可用宽度
     */
    private void computeWidthWithExactly(int useWidth) {
        if (leftWidth > 0) {
            //  左侧固定，右侧 match_parent
            if (rightWidth == -1F) {
                rightWidth = useWidth - leftWidth;
            }
            //  左侧固定，右侧 wrap_content
            else if (rightWidth == -2F) {
                //  测量右侧文字宽度
                rightWidth = getWidthByText(rightPaint, rightText);
                rightWidth = Math.min(rightWidth, useWidth - leftWidth);
            }
        } else if (rightWidth > 0) {
            //  右侧固定，左侧 match_parent
            if (leftWidth == -1F) {
                leftWidth = useWidth - rightWidth;
            }
            //  右侧固定，左侧 wrap_content
            else if (leftWidth == -2F) {
                //  测量左侧文字宽度
                leftWidth = getWidthByText(leftPaint, leftText);
                leftWidth = Math.min(leftWidth, useWidth - rightWidth);
            }
        }
    }

    /**
     * 左右都是默认值时
     *
     * @param useWidth 可用宽度
     */
    private void computeWidthWithDefault(int useWidth) {
        //  左侧 match_parent
        if (leftWidth == -1F) {
            leftWidth = useWidth;
            rightWidth = 0;
        }

        //  左侧 wrap_content
        else if (leftWidth == -2F) {
            leftWidth = getWidthByText(leftPaint, leftText);
            leftWidth = Math.min(leftWidth, useWidth);

            //  右侧 match_parent
            if (rightWidth == -1F) {
                rightWidth = useWidth - leftWidth;
            } else if (rightWidth == -2F) {
                rightWidth = getWidthByText(rightPaint, rightText);
                rightWidth = Math.min(rightWidth, useWidth - leftWidth);
            }
        }
    }

    /**
     * 通过文字，获取宽度
     *
     * @param paint 画笔
     * @param text  测量文字
     * @return 测量得到的文字宽度
     */
    private float getWidthByText(Paint paint, String text) {
        if (!TextUtils.isEmpty(text)) {
            return paint.measureText(text);
        }
        return 0;
    }


    /**
     * 控件的默认数据
     * 内间距、图片
     */
    private void initDefaultAttrs() {
        //  Drawable padding
        int compoundDrawablePadding = getCompoundDrawablePadding();

        //  左上右下 四个图片的宽度 （包括图片宽高 和 图片外边距 compoundDrawablePadding）
        Drawable[] compoundDrawables = getCompoundDrawables();
        drawLeft_W = compoundDrawables[0] != null ? compoundDrawables[0].getMinimumWidth() + compoundDrawablePadding : 0;
        drawLeft_H = compoundDrawables[0] != null ? compoundDrawables[0].getMinimumHeight() : 0;

        drawTop_W = compoundDrawables[1] != null ? compoundDrawables[1].getMinimumWidth() : 0;
        drawTop_H = compoundDrawables[1] != null ? compoundDrawables[1].getMinimumHeight() + compoundDrawablePadding : 0;


        drawRight_W = compoundDrawables[2] != null ? compoundDrawables[2].getMinimumWidth() + compoundDrawablePadding : 0;
        drawRight_H = compoundDrawables[2] != null ? compoundDrawables[2].getMinimumHeight() : 0;

        drawBottom_W = compoundDrawables[3] != null ? compoundDrawables[3].getMinimumWidth() : 0;
        drawBottom_H = compoundDrawables[3] != null ? compoundDrawables[3].getMinimumHeight() + compoundDrawablePadding : 0;

        //  左右内边距
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
    }

    /**
     * 初始化 staticLayout
     * <p>
     * CharSequence source,                 需要分行的字符串
     * int bufStart,                        需要分行的字符串从第几的位置开始
     * int bufEnd,                          需要分行的字符串到哪里结束
     * TextPaint paint,                     画笔对象
     * int outerWidth,                      layout的宽度，字符串超出宽度时自动换行。
     * Layout.Alignment align,              layout的对其方式，有ALIGN_CENTER， ALIGN_NORMAL， ALIGN_OPPOSITE 三种。
     * float spacingMulti,                   相对行间距，相对字体大小，1.5f表示行间距为1.5倍的字体高度。
     * float spacingAdd,                    在基础行距上添加多少  实际行间距等于这两者的和。
     * boolean includepad,                  是否启用更高的文字书写高度  是否留白
     * TextUtils.TruncateAt ellipsize,      从什么位置开始省略
     * int ellipsizedWidth                  超过多少开始省略
     */
    private StaticLayout getStaticLayout(@NonNull CharSequence source, @IntRange(from = 0) int start,
                                         @IntRange(from = 0) int end, @NonNull TextPaint paint,
                                         @IntRange(from = 0) int width,
                                         Layout.Alignment align,
                                         @FloatRange(from = 1.0) float spacingMulti, @FloatRange(from = 0.0) float spacingAdd) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder builder = StaticLayout.Builder.obtain(source, start, end, paint, width);
            builder.setAlignment(align);
            builder.setLineSpacing(spacingAdd, spacingMulti);
            return builder.build();
        } else {
            return new StaticLayout(source, start, end, paint, width, align, spacingMulti, spacingAdd, false);
        }
    }


    /**
     * @return
     */
    private int getLeftMeasureHeight() {
        StaticLayout leftStaticLayout = getLeftStaticLayout();
        return leftStaticLayout == null ? 0 : leftStaticLayout.getHeight();
    }

    private int getRightMeasureHeight() {
        StaticLayout rightStaticLayout = getRightStaticLayout();
        return rightStaticLayout == null ? 0 : rightStaticLayout.getHeight();
    }

    private StaticLayout getLeftStaticLayout() {
        if (!TextUtils.isEmpty(leftText)) {
            return getStaticLayout(leftText, 0, leftText.length(), leftPaint, (int) leftWidth, Layout.Alignment.ALIGN_NORMAL, leftSpacingMulti, leftSpacingAdd);
        } else {
            return null;
        }
    }

    private StaticLayout getRightStaticLayout() {
        if (!TextUtils.isEmpty(rightText)) {
            return getStaticLayout(rightText, 0, rightText.length(), rightPaint, (int) rightWidth, Layout.Alignment.ALIGN_OPPOSITE, rightSpacingMulti, rightSpacingAdd);
        } else {
            return null;
        }
    }


}
