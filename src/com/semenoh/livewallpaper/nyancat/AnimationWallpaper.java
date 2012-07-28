package com.semenoh.livewallpaper.nyancat;

import java.util.Vector;

import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public abstract class AnimationWallpaper extends WallpaperService {
	final String TAG = "nyan";

	int mOffset = 0;
	int mWidth = 0;
	int mHeight = 0;
	int mXTouched = -1;
	int mYTouched = -1;
	boolean mTouched = false;
	
	Vector<Star> mTouchedStars = new Vector<Star>();

	protected abstract class AnimationEngine extends Engine {
		private Handler mHandler = new Handler();

		private Runnable mIteration = new Runnable() {
			public void run() {
				iteration();
				drawFrame();
			}
		};

		private boolean mVisible;

		@Override
		public void onDestroy() {
			super.onDestroy();
			// stop the animation
			mHandler.removeCallbacks(mIteration);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				iteration();
				drawFrame();
			} else {
				// stop the animation
				mHandler.removeCallbacks(mIteration);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			mWidth = width;
			mHeight = height;
			iteration();
			drawFrame();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			// stop the animation
			mHandler.removeCallbacks(mIteration);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			mOffset = xPixelOffset;
			iteration();
			drawFrame();
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN){
				mTouchedStars.add( new Star((int)event.getX(), (int)event.getY(), 0) );
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE){
				mTouched = true;
				mXTouched = (int)event.getX();
				mYTouched = (int)event.getY();
			} else {
				mTouched = false;
			}
			super.onTouchEvent(event);
		}
		
		protected abstract void drawFrame();

		protected void iteration() {
			// Reschedule the next redraw in 40ms
			mHandler.removeCallbacks(mIteration);
			if (mVisible) {
				mHandler.postDelayed(mIteration, 1000 / 25);
			}
		}
	}
}
