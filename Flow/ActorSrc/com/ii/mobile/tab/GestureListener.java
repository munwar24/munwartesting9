package com.ii.mobile.tab;

import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.ii.mobile.util.L;

public class GestureListener implements OnGestureListener, OnDoubleTapListener {

	private final SelfTaskActivity activity;

	public GestureListener(SelfTaskActivity taskActivity) {
		this.activity = taskActivity;
	}

	public boolean onTouchEvent(MotionEvent me) {
		activity.detector.onTouchEvent(me);
		return activity.onTouchEvent(me);
	}

	public boolean onDown(MotionEvent e) {
		L.out("---onDown----" + e.toString());
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		L.out("---onFling---" + e1.toString() + e2.toString());
		return false;
	}

	public void onLongPress(MotionEvent e) {
		L.out("---onLongPress---" + e.toString());
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		L.out("---onScroll---" + e1.toString() + e2.toString());
		return false;
	}

	public void onShowPress(MotionEvent e) {
		L.out("---onShowPress---" + e.toString());
	}

	public boolean onSingleTapUp(MotionEvent e) {
		L.out("---onSingleTapUp---" + e.toString());
		return false;
	}

	public boolean onDoubleTap(MotionEvent e) {
		L.out("---onDoubleTap---" + e.toString());
		return false;
	}

	public boolean onDoubleTapEvent(MotionEvent e) {
		L.out("---onDoubleTapEvent---" + e.toString());
		return false;
	}

	public boolean onSingleTapConfirmed(MotionEvent e) {
		L.out("---onSingleTapConfirmed---" + e.toString());
		return false;
	}

}
