package com.dansl.DrawerPro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class BrushSize extends AlertDialog implements OnSeekBarChangeListener, OnClickListener {

    public interface OnClickListener {
        public void onClick(View view, int color);
    }
    
	private SeekBar SizeBar;
	private TextView SizeText;
	private int brushSize;
	private DrawView drawViewColorPath;

	public BrushSize(Context context, View view, int _brushSize, DrawView drawViewPath) {
		super(context);

		drawViewColorPath = drawViewPath;
		brushSize = _brushSize;
		
		Resources res = context.getResources();
		setTitle("Choose Brush Size");
		setButton(BUTTON1, "Set Size", this);
		setButton(BUTTON3, "Cancel", this);
		
		View root = LayoutInflater.from(context).inflate(R.layout.brush_size, null);
		setView(root);
		
		SizeBar = (SeekBar) root.findViewById(R.id.mSize);
		SizeText = (TextView) root.findViewById(R.id.amount);

		setupSeekBar(SizeBar, R.string.size_brush, brushSize, res);
	}
	
	private void setupSeekBar(SeekBar seekBar, int id, int value, Resources res) {
		seekBar.setProgressDrawable(new TextSeekBarDrawable(res, id, value < seekBar.getMax() / 2));
		seekBar.setProgress(value);
		seekBar.setOnSeekBarChangeListener(this);
		SizeText.setText((String)("Size: "+value));
	}

	private void update() {
		brushSize = (int)(SizeBar.getProgress());
		SizeText.setText((String)("Size: "+brushSize));
	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		update();
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON1) {
			//Log.v("COLOR","COLOR: "+mColor);
			drawViewColorPath.setBrushSize(brushSize);
		}
		dismiss();
	}
	
	static final int[] STATE_FOCUSED = {android.R.attr.state_focused};
	static final int[] STATE_PRESSED = {android.R.attr.state_pressed};
	
	class TextSeekBarDrawable extends Drawable implements Runnable {
		private static final long DELAY = 25;
		private String mText;
		private Drawable mProgress;
		private Paint mPaint;
		private Paint mOutlinePaint;
		private float mTextWidth;
		private boolean mActive;
		private float mTextX;
		private float mDelta;

		public TextSeekBarDrawable(Resources res, int id, boolean labelOnRight) {
			mText = res.getString(id);
			mProgress = res.getDrawable(android.R.drawable.progress_horizontal);
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setTypeface(Typeface.DEFAULT_BOLD);
			mPaint.setTextSize(16);
			mPaint.setColor(0xff000000);
			mOutlinePaint = new Paint(mPaint);
			mOutlinePaint.setStyle(Style.STROKE);
			mOutlinePaint.setStrokeWidth(3);
			mOutlinePaint.setColor(0xbbffc300);
			mOutlinePaint.setMaskFilter(new BlurMaskFilter(1, Blur.NORMAL));
			mTextWidth = mOutlinePaint.measureText(mText);
			mTextX = labelOnRight? 1 : 0;
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			mProgress.setBounds(bounds);
		}
		
		@Override
		protected boolean onStateChange(int[] state) {
			mActive = StateSet.stateSetMatches(STATE_FOCUSED, state) | StateSet.stateSetMatches(STATE_PRESSED, state);
			invalidateSelf();
			return false;
		}
		
		@Override
		public boolean isStateful() {
			return true;
		}
		
		@Override
		protected boolean onLevelChange(int level) {
//			Log.d(TAG, "onLevelChange " + level);
			if (level < 4000 && mDelta <= 0) {
				mDelta = 0.05f;
//				Log.d(TAG, "onLevelChange scheduleSelf ++");
				scheduleSelf(this, SystemClock.uptimeMillis() + DELAY);
			} else
			if (level > 6000 && mDelta >= 0) {
//				Log.d(TAG, "onLevelChange scheduleSelf --");
				mDelta = -0.05f;
				scheduleSelf(this, SystemClock.uptimeMillis() + DELAY);
			}
			return mProgress.setLevel(level);
		}
		
		@Override
		public void draw(Canvas canvas) {
			mProgress.draw(canvas);
			Rect bounds = getBounds();

			float x = 6 + mTextX * (bounds.width() - mTextWidth - 6 - 6);
			float y = (bounds.height() + mPaint.getTextSize()) / 2;
			mOutlinePaint.setAlpha(mActive? 255 : 255 / 2);
			mPaint.setAlpha(mActive? 255 : 255 / 2);
			canvas.drawText(mText, x, y, mOutlinePaint);
			canvas.drawText(mText, x, y, mPaint);
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSLUCENT;
		}

		@Override
		public void setAlpha(int alpha) {
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
		}

		public void run() {
			mTextX += mDelta;
			if (mTextX >= 1) {
				mTextX = 1;
				mDelta = 0;
			} else
			if (mTextX <= 0) {
				mTextX = 0;
				mDelta = 0;
			} else {
				scheduleSelf(this, SystemClock.uptimeMillis() + DELAY);
			}
			invalidateSelf();
//			Log.d(TAG, "run " + mTextX + " " + SystemClock.uptimeMillis());
		}
	}
}
