/*******************************************************************************
 *  
 *  Created by iExemplar on 2016.
 *  
 *  Copyright (c) 2016 iExemplar. All rights reserved.
 *  
 *  
 *******************************************************************************/
package ssp.tt.com.ssp.utils;

import android.view.MotionEvent;
import android.view.ViewConfiguration;

public abstract class SimpleThreeFingerDoubleTapDetector {
	private static final int TIMEOUT = ViewConfiguration.getDoubleTapTimeout() + 400;
	private long mFirstDownTime = 0;
	private boolean mSeparateTouches = false;
	private byte mTwoFingerTapCount = 0;

	private void reset(long time) {
		mFirstDownTime = time;
		mSeparateTouches = false;
		mTwoFingerTapCount = 0;
	}

	boolean doubleBackToExitPressedOnce;

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			if (mFirstDownTime == 0
					|| event.getEventTime() - mFirstDownTime > TIMEOUT)
				reset(event.getDownTime());
			break;
		case MotionEvent.ACTION_POINTER_UP:
			if (event.getPointerCount() == 2)
				mTwoFingerTapCount++;
			else
				mFirstDownTime = 0;
			break;
		case MotionEvent.ACTION_UP:
			if (!mSeparateTouches) {
				mSeparateTouches = true;
			} else if (mTwoFingerTapCount == 3) {
				onThreeFingerDoubleTap();
				mFirstDownTime = 0;
				return true;
			}
		}

		return false;
	}

	public abstract void onThreeFingerDoubleTap();
}
