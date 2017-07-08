package com.dandian.admission.widget;

import com.dandian.admission.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

public class HorizontalProgressBarWithNumber extends ProgressBar
{

	private static final int DEFAULT_TEXT_SIZE = 10;
	private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
	private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
	private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
	private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
	private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;

	/**
	 * painter of all drawing things
	 */
	protected Paint mPaint = new Paint();
	/**
	 * color of progress number
	 */
	protected int mTextColor = DEFAULT_TEXT_COLOR;
	/**
	 * size of text (sp)
	 */
	protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);

	/**
	 * offset of draw progress
	 */
	protected int mTextOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);

	/**
	 * height of reached progress bar
	 */
	protected int mReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);

	/**
	 * color of reached bar
	 */
	protected int mReachedBarColor = DEFAULT_TEXT_COLOR;
	/**
	 * color of unreached bar
	 */
	protected int mUnReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
	/**
	 * height of unreached progress bar
	 */
	protected int mUnReachedProgressBarHeight = dp2px(DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
	/**
	 * view width except padding
	 */
	protected int mRealWidth;
	
	protected boolean mIfDrawText = true;

	protected static final int VISIBLE = 0;

	public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs,
			int defStyle)
	{
		super(context, attrs, defStyle);
		
		setHorizontalScrollBarEnabled(true);

		obtainStyledAttributes(attrs);

		mPaint.setTextSize(mTextSize);
		mPaint.setColor(mTextColor);

	}
	
	/**
	 * get the styled attributes
	 * 
	 * @param attrs
	 */
	private void obtainStyledAttributes(AttributeSet attrs)
	{
		// init values from custom attributes
		final TypedArray attributes = getContext().obtainStyledAttributes(
				attrs, R.styleable.HorizontalProgressBarWithNumber);

		mTextColor = attributes
				.getColor(
						R.styleable.HorizontalProgressBarWithNumber_progress_text_color,
						DEFAULT_TEXT_COLOR);
		mTextSize = (int) attributes.getDimension(
				R.styleable.HorizontalProgressBarWithNumber_progress_text_size,
				mTextSize);

		mReachedBarColor = attributes
				.getColor(
						R.styleable.HorizontalProgressBarWithNumber_progress_reached_color,
						mTextColor);
		mUnReachedBarColor = attributes
				.getColor(
						R.styleable.HorizontalProgressBarWithNumber_progress_unreached_color,
						DEFAULT_COLOR_UNREACHED_COLOR);
		mReachedProgressBarHeight = (int) attributes
				.getDimension(
						R.styleable.HorizontalProgressBarWithNumber_progress_reached_bar_height,
						mReachedProgressBarHeight);
		mUnReachedProgressBarHeight = (int) attributes
				.getDimension(
						R.styleable.HorizontalProgressBarWithNumber_progress_unreached_bar_height,
						mUnReachedProgressBarHeight);
		mTextOffset = (int) attributes
				.getDimension(
						R.styleable.HorizontalProgressBarWithNumber_progress_text_offset,
						mTextOffset);

		int textVisible = attributes
				.getInt(R.styleable.HorizontalProgressBarWithNumber_progress_text_visibility,
						VISIBLE);
		if (textVisible != VISIBLE)
		{
			mIfDrawText = false;
		}
		attributes.recycle();
	}
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec)
	{
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		if (heightMode != MeasureSpec.EXACTLY)
		{

			float textHeight = (mPaint.descent() + mPaint.ascent());
			int exceptHeight = (int) (getPaddingTop() + getPaddingBottom() + Math
					.max(Math.max(mReachedProgressBarHeight,
							mUnReachedProgressBarHeight), Math.abs(textHeight)));

			heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight,
					MeasureSpec.EXACTLY);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}
	@Override
	protected synchronized void onDraw(Canvas canvas)
	{
		canvas.save();
		//����ƽ�Ƶ�ָ��paddingLeft�� getHeight() / 2λ�ã�ע���Ժ����궼Ϊ�Դ�Ϊ0��0
		canvas.translate(getPaddingLeft(), getHeight() / 2);

		boolean noNeedBg = false;
		//��ǰ���Ⱥ���ֵ�ı���
		float radio = getProgress() * 1.0f / getMax();
		//�ѵ���Ŀ���
		float progressPosX = (int) (mRealWidth * radio);
		//���Ƶ��ı�
		String text = getProgress() + "%";

		//�õ�����Ŀ��Ⱥ͸߶�
		float textWidth = mPaint.measureText(text);
		float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;

		//������������δ����Ľ���������Ҫ����
		if (progressPosX + textWidth > mRealWidth)
		{
			progressPosX = mRealWidth - textWidth;
			noNeedBg = true;
		}

		// �����ѵ���Ľ���
		float endX = progressPosX - mTextOffset / 2;
		if (endX > 0)
		{
			mPaint.setColor(mReachedBarColor);
			mPaint.setStrokeWidth(mReachedProgressBarHeight);
			canvas.drawLine(0, 0, endX, 0, mPaint);
		}
	
		// �����ı�
		if (mIfDrawText)
		{
			mPaint.setColor(mTextColor);
			canvas.drawText(text, progressPosX, -textHeight, mPaint);
		}

		// ����δ����Ľ�����
		if (!noNeedBg)
		{
			float start = progressPosX + mTextOffset / 2 + textWidth;
			mPaint.setColor(mUnReachedBarColor);
			mPaint.setStrokeWidth(mUnReachedProgressBarHeight);
			canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
		}

		canvas.restore();

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mRealWidth = w - getPaddingRight() - getPaddingLeft();

	}
	/**
	 * dp 2 px
	 * 
	 * @param dpVal
	 */
	protected int dp2px(int dpVal)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, getResources().getDisplayMetrics());
	}

	/**
	 * sp 2 px
	 * 
	 * @param spVal
	 * @return
	 */
	protected int sp2px(int spVal)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, getResources().getDisplayMetrics());

	}
}