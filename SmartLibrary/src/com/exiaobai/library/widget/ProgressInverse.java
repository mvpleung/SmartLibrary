package com.exiaobai.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * 圆形滚动条
 * 
 * @author LiangZiChao
 * @2013-4-12
 * @下午02:55:44
 */
@SuppressLint("DrawAllocation")
public class ProgressInverse extends ProgressBar {
	String text;
	Paint mPaint;

	public ProgressInverse(Context context) {
		super(context);
		initText();
	}

	public ProgressInverse(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initText();
	}

	public ProgressInverse(Context context, AttributeSet attrs) {
		super(context, attrs);
		initText();
	}

	@Override
	public synchronized void setProgress(int progress) {
		// TODO Auto-generated method stub
		setText(progress);
		super.setProgress(progress);

	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// this.setText();
		this.setBackgroundColor(Color.TRANSPARENT);
		Rect rect = new Rect();
		this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
		int x = (getWidth() / 2) - rect.centerX();
		int y = (getHeight() / 2) - rect.centerY();
		canvas.drawText(this.text, x, y, this.mPaint);
		getBackground().draw(canvas);
	}

	private void initText() {
		this.mPaint = new Paint();
		this.mPaint.setColor(Color.BLACK);
		this.mPaint.setTextSize(15);
		// 去掉锯齿
		this.mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

	}

	private void setText(int progress) {
		int i = this.getMax() != 0 ? (progress * 100) / this.getMax() : 0;
		this.text = i == 0 ? "" : String.valueOf(i) + "%";
	}

}
