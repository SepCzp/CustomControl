package example.czp.customcontrol.canvas;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Arrays;

import example.czp.customcontrol.R;

/**
 * Created by chenzipeng on 2018/7/27.
 * function:
 */
public class PieView extends View {

    private static final String TAG = PieView.class.getSimpleName();


    private String[] mStrings = new String[]{"武则天", "凯", "尴尬差点中奖",
            "安琪拉", "谢谢惠顾", "老夫子", "黄忠",
            "尴尬差点中奖", "元歌", "谢谢惠顾"};

    private int mCount = mStrings.length;

    // private int[] mImages = new int[]{R.drawable.iphone, R.drawable.danfan, R.drawable.f040, R.drawable.ipad, R.drawable.f015};
    private int[] mImages = new int[]{R.drawable.f040, R.drawable.f040,
            R.drawable.f015, R.drawable.f040, R.drawable.f015};

    private int[] sectorColor = new int[]{Color.parseColor("#EE82EE"), Color.parseColor("#FFDEAD")};

    /**
     * 图片
     */
    private Bitmap[] mBitmaps = new Bitmap[mStrings.length];

    /**
     * 画背景
     */
    private Paint mBgPaint;

    /**
     * 绘制扇形
     */
    private Paint mArcPaint;

    /**
     * 绘制文字
     */
    private Paint mTextPaint;

    /**
     * 半径
     */
    private int mRadius;

    /**
     * 圆心坐标
     */
    private int mCenter;

    /**
     * 弧形的起始角度
     */
    private int startAngle;

    private int[] angles = new int[mCount];

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SHIFT, 48, getResources().getDisplayMetrics());
    private RectF sectorRectF;

    /**
     * 弧形划过的角度
     */
    private int sweepAngle;
    /**
     * 下标
     */
    private int position;
    private ObjectAnimator animator;
    private RotateListener listener;
    private int rotateToPosition;

    public PieView(Context context) {
        this(context, null);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        int width = Math.min(w, h);

        mCenter = width / 2;
        //半径
        mRadius = (width - getPaddingLeft() * 2) / 2;

        //设置框高都一样
        setMeasuredDimension(width, width);
    }

    /**
     * 初始化
     */
    private void init() {

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(Color.parseColor("#FF4500"));

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);

        for (int i = 0; i < mCount; i++) {
            mBitmaps[i] = BitmapFactory.decodeResource(getResources(), mImages[i % 5]);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = (event.getX() - mCenter);
            float y = (event.getY() - mCenter);
            float touchAngle = 0;
            int touchRadius = 0;
            //判断点击的范围是否在转盘内
            if (x < 0 && y > 0) {//第二象限
                touchAngle += 180;
            } else if (x < 0 && y < 0) {//第三象限
                touchAngle += 180;
            } else if (x > 0 && y < 0) {//第四象限
                touchAngle += 360;
            }
            //Math.atan(y/x) 返回正数值表示相对于 x 轴的逆时针转角，返回负数值则表示顺时针转角。
            // 返回值乘以 180/π，将弧度转换为角度。
            touchAngle += (float) Math.toDegrees(Math.atan(y / x));

            touchRadius = (int) Math.sqrt(x * x + y * y);
            if (touchRadius < mRadius) {
                position = -Arrays.binarySearch(angles, (int) touchAngle) - 1;
                Log.d(TAG, "onTouchEvent: " + mStrings[position - 1]);
            }

            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //1.绘制背景
        canvas.drawCircle(mCenter, mCenter, mCenter - getPaddingLeft() / 2, mBgPaint);
        //2.绘制扇形
        //2.1设置每一个扇形的角度
        sweepAngle = 360 / mCount;
        startAngle = 0;
        //2.2设置扇形绘制的范围
        sectorRectF = new RectF(getPaddingLeft(), getPaddingLeft(),
                mCenter * 2 - getPaddingLeft(), mCenter * 2 - getPaddingLeft());
        for (int i = 0; i < mCount; i++) {
            mArcPaint.setColor(sectorColor[i % 2]);
            //sectorRectF 扇形绘制范围  startAngle 弧开始绘制角度 sweepAngle 每次绘制弧的角度
            // useCenter 是否连接圆心
            canvas.drawArc(sectorRectF, startAngle, sweepAngle, true, mArcPaint);
            //3.绘制文字
            drawTexts(canvas, mStrings[i]);
            //4.绘制图片
            drawIcons(canvas, mBitmaps[i]);
            angles[i] = startAngle;
            Log.d(TAG, "onDraw: " + angles[i] + "     " + i);
            startAngle += sweepAngle;
        }
        super.onDraw(canvas);
    }

    /**
     * 以二分之一的半径的长度，扇形的一半作为图片的中心点
     * 图片的宽度为imageWidth
     *
     * @param canvas
     * @param mBitmap
     */
    private void drawIcons(Canvas canvas, Bitmap mBitmap) {
        int imageWidth = mRadius / 10;
        //计算半边扇形的角度 度=Math.PI/180 弧度=180/Math.PI
        float angle = (float) ((startAngle + sweepAngle / 2) * Math.PI / 180);
        //计算中心点的坐标
        int r = mRadius / 2;
        float x = (float) (mCenter + r * Math.cos(angle));
        float y = (float) (mCenter + r * Math.sin(angle));
        //设置绘制图片的范围
        RectF rectF = new RectF(x - imageWidth, y - imageWidth, x + imageWidth, y + imageWidth);
        canvas.drawBitmap(mBitmap, null, rectF, null);
    }

    /**
     * 使用path添加一个路径
     * 绘制文字的路径
     *
     * @param canvas
     * @param mString
     */
    private void drawTexts(Canvas canvas, String mString) {
        Path path = new Path();
        //添加一个圆弧的路径
        path.addArc(sectorRectF, startAngle, sweepAngle);
        String startText = null;
        String endText = null;
        //测量文字的宽度
        float textWidth = mTextPaint.measureText(mString);
        //水平偏移
        int hOffset = (int) (mRadius * 2 * Math.PI / mCount / 2 - textWidth / 2);
        //计算弧长 处理文字过长换行
        int l = (int) ((360 / mCount) * Math.PI * mRadius / 180);
        if (textWidth > l * 4 / 5) {
            int index = mString.length() / 2;
            startText = mString.substring(0, index);
            endText = mString.substring(index, mString.length());

            float startTextWidth = mTextPaint.measureText(startText);
            float endTextWidth = mTextPaint.measureText(endText);
            //水平偏移
            hOffset = (int) (mRadius * 2 * Math.PI / mCount / 2 - startTextWidth / 2);
            int endHOffset = (int) (mRadius * 2 * Math.PI / mCount / 2 - endTextWidth / 2);
            //文字高度
            int h = (int) ((mTextPaint.ascent() + mTextPaint.descent()) * 1.5);

            //根据路径绘制文字
            //hOffset 水平的偏移量 vOffset 垂直的偏移量
            canvas.drawTextOnPath(startText, path, hOffset, mRadius / 6, mTextPaint);
            canvas.drawTextOnPath(endText, path, endHOffset, mRadius / 6 - h, mTextPaint);
        } else {
            //根据路径绘制文字
            canvas.drawTextOnPath(mString, path, hOffset, mRadius / 6, mTextPaint);
        }

    }


    public void rotate(final int i) {

        rotateToPosition = 360 / mCount * (mCount - i);
        float toDegree = 360f * 5 + rotateToPosition;

        animator = ObjectAnimator.ofFloat(PieView.this, "rotation", 0, toDegree);
        animator.setDuration(5000);
        animator.setRepeatCount(0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setAutoCancel(true);
        animator.start();

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //指针指向的方向为270度
                if (listener != null) {
                    rotateToPosition = 270 - rotateToPosition;
                    if (rotateToPosition < 0) {
                        rotateToPosition += 360;
                    } else if (rotateToPosition == 0) {
                        rotateToPosition = 270;
                    }
                    position = -Arrays.binarySearch(angles, rotateToPosition) - 1;
                    listener.value(mStrings[position - 1]);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {


            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void setListener(RotateListener listener) {
        this.listener = listener;
    }

    public void setValue(String v1, String v2) {
//        for (int i = 0; i < 10; i++) {
//            if(i==0||i==2||i==4||i==7){
//                mStrings[i]=v2;
//            }else {
//                mStrings[i]=v1;
//            }
//        }
    }

    public interface RotateListener {

        void value(String s);

    }
}
