package com.ii.mobile.timers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ii.mobile.transport.R;
import com.ii.mobile.util.L;

public final class Swoosh extends View {

	private final String TAG = Swoosh.class.getSimpleName();

	// drawing tools

	private Paint labelPaint;
	public String title = "Arrival";

	private Paint logoPaint;
	private Bitmap logo = null;
	private Matrix logoMatrix;
	private float logoScale;

	private Paint statPaint;
	private Bitmap stat;
	private Matrix statMatrix;
	private float statScale;

	// private Paint warningPaint;
	// private Bitmap warning;
	// private Matrix warningMatrix;
	// private float warningScale;

	private Paint handPaint;
	private Path handPath;
	private Paint handScrewPaint;

	private Paint backgroundPaint;
	// end drawing tools

	Bitmap background; // holds the cached part

	// scale configuration
	int target = 60 * 1;
	int forecast = 60 * 1;
	public int arrived = 0;

	private final int logoResource = R.drawable.frag_green_swoosh;
	private float maxSeconds = 60 * 1;
	private final float startAngle = -65f;
	private final float endAngle = 170f;

	// hand dynamics -- all are angular expressed in F degrees
	private boolean handInitialized = false;
	private float handPosition = 0;

	private float handVelocity = 0.0f;
	private float handAcceleration = 0.0f;
	private long lastHandMoveTime = -1L;

	float xChange = .03f;

	int skip = 0;

	private Paint redPaint;

	public Swoosh(Context context) {
		super(context);
		init();
	}

	public Swoosh(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Swoosh(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	boolean initialDraw = false;

	@Override
	protected void onDraw(Canvas canvas) {
		drawBackground(canvas);

		float xScale = getWidth();
		float yScale = getHeight();
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(xScale, xScale);
		canvas.translate(0.00f, -0.14f);
		drawLogo(canvas);

		// drawWarning(canvas);

		// drawGrid(canvas);
		drawLabel(canvas);
		drawActiveArc(canvas);
		drawDelayArcs(canvas);
		drawLateArc(canvas);
		drawHand(canvas);
		canvas.restore();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		// attachToSensor();
	}

	@Override
	protected void onDetachedFromWindow() {
		// detachFromSensor();
		super.onDetachedFromWindow();
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		L.out("onRestoreInstanceState");
		Bundle bundle = (Bundle) state;
		Parcelable superState = bundle.getParcelable("superState");
		super.onRestoreInstanceState(superState);

		handInitialized = bundle.getBoolean("handInitialized");
		handPosition = bundle.getFloat("handPosition");

		handVelocity = bundle.getFloat("handVelocity");
		handAcceleration = bundle.getFloat("handAcceleration");
		lastHandMoveTime = bundle.getLong("lastHandMoveTime");
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		Bundle state = new Bundle();
		state.putParcelable("superState", superState);
		state.putBoolean("handInitialized", handInitialized);
		state.putFloat("handPosition", handPosition);

		state.putFloat("handVelocity", handVelocity);
		state.putFloat("handAcceleration", handAcceleration);
		state.putLong("lastHandMoveTime", lastHandMoveTime);
		L.out("onSaveInstanceState");
		return state;
	}

	public void init() {
		initDrawingTools();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			setLayerType(LAYER_TYPE_SOFTWARE, labelPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// L.out("*** " + labelString + " Width spec: " +
		// MeasureSpec.toString(widthMeasureSpec));
		// L.out("Height spec: " + MeasureSpec.toString(heightMeasureSpec));

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);

		int chosenDimension = Math.min(chosenWidth, chosenHeight);
		chosenDimension = (int) (chosenWidth * .86);
		int test = (int) (chosenDimension * .75);
		setMeasuredDimension(chosenDimension, test);
		// L.out("chosenDimension: " + chosenDimension);
		// L.out("chosenWidth: " + chosenWidth);
		// L.out("chosenHeight: " + chosenHeight);
	}

	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		}
	}

	// in case there is no size specified
	private int getPreferredSize() {
		return 600;
	}

	private void drawLogo(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.translate(0.5f - logo.getWidth() * logoScale / 2.0f,
				0.5f - logo.getHeight() * logoScale / 2.0f);
		canvas.drawBitmap(logo, logoMatrix, null);
		canvas.restore();
	}

	private void drawStat(Canvas canvas) {
		float yChange = .12f;
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.translate(0.53f,
				0.5f - yChange - stat.getHeight() * statScale / 2.0f);

		canvas.drawBitmap(stat, statMatrix, null);
		canvas.restore();
	}

	// private void drawWarning(Canvas canvas) {
	// canvas.save(Canvas.MATRIX_SAVE_FLAG);
	// canvas.translate(0.5f + xChange,
	// 0.5f);
	// canvas.drawBitmap(warning, warningMatrix, null);
	// canvas.restore();
	// }

	private void drawHand(Canvas canvas) {
		if (handInitialized) {
			// float handAngle = degreeToAngle(handPosition);
			canvas.save(Canvas.MATRIX_SAVE_FLAG);
			canvas.translate(0.05f, 0.0f);
			canvas.rotate(handPosition, 0.5f, 0.5f);

			canvas.drawPath(handPath, handPaint);

			canvas.drawCircle(0.5f, 0.5f, 0.01f, handScrewPaint);
			canvas.restore();
		}
	}

	private void drawBackground(Canvas canvas) {
		if (background == null) {
			Log.w(TAG, "Background not created");
		} else {
			// canvas.drawBitmap(background, 0, 0, backgroundPaint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		L.out("Size changed to " + w + "x" + h);

		regenerateBackground();
	}

	private void regenerateBackground() {
		// free the old bitmap
		if (background != null) {
			background.recycle();
		}

		background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		// Canvas backgroundCanvas = new Canvas(background);
		Canvas backgroundCanvas = new Canvas();
		float scale = getWidth();
		backgroundCanvas.scale(scale, scale);
	}

	float counter = 0;

	private Paint grayPaint;
	private Paint bigGrayPaint;
	private Paint bigPinkPaint;
	private Paint activeArc;
	private RectF activeRect;

	private Paint delayArc;

	private final int handColor = 0xafab0534;

	private boolean wantSubLabel = true;

	private Paint bluePaint;

	private Paint largePinkPaint;

	private Paint bigBluePaint;

	public boolean started = false;

	int swooshBackground = 0;

	private boolean inited = false;

	public void setWantSubLabel(boolean wantSubLabel) {
		this.wantSubLabel = wantSubLabel;
	}

	private void initDrawingTools() {
		if (inited)
			return;
		inited = true;
		labelPaint = new Paint();
		labelPaint.setColor(0xcf000000);
		labelPaint.setAntiAlias(true);
		labelPaint.setTextScaleX(1.2f);
		labelPaint.setTypeface(Typeface.DEFAULT_BOLD);
		labelPaint.setTextAlign(Paint.Align.CENTER);
		labelPaint.setShadowLayer(0.01f, 0.005f, 0.005f, 0x9f000000);
		labelPaint.setTextSize(0.085f);
		// labelPaint.setTextScaleX(0.8f);

		// labelPaint.setTextScaleX(0.8f);

		grayPaint = new Paint();
		grayPaint.setColor(0xaf999999);
		grayPaint.setTypeface(Typeface.DEFAULT);
		grayPaint.setTextScaleX(1.20f);
		grayPaint.setAntiAlias(true);
		// grayPaint.setTypeface(Typeface.DEFAULT_BOLD);
		grayPaint.setTextAlign(Paint.Align.CENTER);
		// grayPaint.setShadowLayer(0.01f, 0.005f, 0.005f, 0x9f000000);
		grayPaint.setTextSize(0.06f);

		bluePaint = new Paint();
		bluePaint.setColor(0xaf0000FF);
		bluePaint.setTypeface(Typeface.DEFAULT_BOLD);
		bluePaint.setTextScaleX(1.50f);
		bluePaint.setAntiAlias(true);
		bluePaint.setShadowLayer(0.01f, 0.005f, 0.005f, 0x9f000000);
		// grayPaint.setTypeface(Typeface.DEFAULT_BOLD);
		bluePaint.setTextAlign(Paint.Align.CENTER);
		// grayPaint.setShadowLayer(0.01f, 0.005f, 0.005f, 0x9f000000);
		bluePaint.setTextSize(0.06f);

		redPaint = new Paint();
		redPaint.setColor(0xcfcb0534);
		redPaint.setAntiAlias(true);
		redPaint.setTypeface(Typeface.DEFAULT_BOLD);
		redPaint.setTextAlign(Paint.Align.CENTER);
		redPaint.setShadowLayer(0.01f, 0.005f, 0.005f, 0x9f000000);
		redPaint.setTextSize(0.09f);

		bigBluePaint = new Paint();
		bigBluePaint.setColor(0xaf0000FF);
		bigBluePaint.setTypeface(Typeface.DEFAULT_BOLD);
		bigBluePaint.setTextScaleX(1.4f);
		bigBluePaint.setAntiAlias(true);
		bigBluePaint.setShadowLayer(0.01f, 0.005f, 0.005f, 0x9f000000);
		bigBluePaint.setTextAlign(Paint.Align.CENTER);

		bigBluePaint.setTextSize(0.075f);

		bigGrayPaint = new Paint();
		bigGrayPaint.setColor(0xaf999999);
		bigGrayPaint.setAntiAlias(true);
		// grayPaint.setTypeface(Typeface.DEFAULT_BOLD);
		bigGrayPaint.setTextAlign(Paint.Align.CENTER);
		// grayPaint.setShadowLayer(0.01f, 0.005f, 0.005f, 0x9f000000);
		bigGrayPaint.setTextSize(0.09f);

		bigPinkPaint = new Paint();
		bigPinkPaint.setColor(0xafdb93a7);
		bigPinkPaint.setAntiAlias(true);
		// grayPaint.setTypeface(Typeface.DEFAULT_BOLD);
		bigPinkPaint.setTextAlign(Paint.Align.CENTER);
		// grayPaint.setShadowLayer(0.01f, 0.005f, 0.005f, 0x9f000000);
		bigPinkPaint.setTextSize(0.09f);

		largePinkPaint = new Paint();
		largePinkPaint.setColor(0xFFdb93a7);
		largePinkPaint.setAntiAlias(true);
		largePinkPaint.setTypeface(Typeface.DEFAULT_BOLD);
		largePinkPaint.setTextAlign(Paint.Align.CENTER);
		// grayPaint.setShadowLayer(0.01f, 0.005f, 0.005f, 0x9f000000);
		largePinkPaint.setTextSize(0.09f);

		logoPaint = new Paint();
		logoPaint.setFilterBitmap(true);
		logo = BitmapFactory.decodeResource(getContext().getResources(), logoResource);
		logoMatrix = new Matrix();
		logoScale = (1.0f / logo.getWidth()) * 1f;
		logoMatrix.setScale(logoScale, logoScale);

		statPaint = new Paint();
		statPaint.setFilterBitmap(true);
		stat = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.frag_stat);
		statMatrix = new Matrix();
		statScale = (1.0f / stat.getWidth()) * .25f;
		statMatrix.setScale(statScale, statScale);

		// warningPaint = new Paint();
		// warningPaint.setFilterBitmap(true);
		// warning = BitmapFactory.decodeResource(getContext().getResources(),
		// R.drawable.frag_warning);
		// warningMatrix = new Matrix();
		// warningScale = (1.0f / warning.getWidth()) * .38f;
		// warningMatrix.setScale(warningScale, warningScale);

		activeArc = new Paint();
		activeArc.setAntiAlias(true);
		activeArc.setColor(0x44edf1f4);
		activeArc.setShadowLayer(0.01f, 0.005f, 0.005f, 0x9f000000);
		activeRect = new RectF(0.1f, 0.15f, 0.9f, 0.85f);

		delayArc = new Paint();
		delayArc.setAntiAlias(true);
		delayArc.setColor(0x44FFFFFF);
		delayArc.setShadowLayer(0.01f, 0.005f, 0.005f, 0x9f000000);

		handPaint = new Paint();
		handPaint.setAntiAlias(true);
		// handPaint.setColor(0xff392f2c);
		handPaint.setColor(handColor);

		// handPaint.setShadowLayer(0.01f, -0.005f, -0.005f, 0x7f000000);
		handPaint.setShadowLayer(0.015f, 0.015f, 0.01f, 0x9f000000);
		handPaint.setStyle(Paint.Style.FILL);

		handPath = new Path();

		float handLength = .02f;

		handPath.moveTo(0.5f, 0.5f + xChange);
		handPath.lineTo(0.5f - 0.010f, 0.5f + xChange - 0.007f);
		handPath.lineTo(0.5f - 0.002f, 0.5f - 0.32f - handLength);
		handPath.lineTo(0.5f + 0.002f, 0.5f - 0.32f - handLength);
		handPath.lineTo(0.5f + 0.010f, 0.5f + xChange - 0.007f);
		handPath.lineTo(0.5f, 0.5f + xChange);
		handPath.addCircle(0.5f, 0.5f, 0.015f, Path.Direction.CW);

		// handPath.moveTo(0.5f, 0.5f + 0.2f);
		// handPath.lineTo(0.5f - 0.010f, 0.5f + 0.2f - 0.007f);
		// handPath.lineTo(0.5f - 0.002f, 0.5f - 0.32f);
		// handPath.lineTo(0.5f + 0.002f, 0.5f - 0.32f);
		// handPath.lineTo(0.5f + 0.010f, 0.5f + 0.2f - 0.007f);
		// handPath.lineTo(0.5f, 0.5f + 0.2f);
		// handPath.addCircle(0.5f, 0.5f, 0.015f, Path.Direction.CW);

		handScrewPaint = new Paint();
		handScrewPaint.setAntiAlias(true);
		handScrewPaint.setColor(0xff493f3c);
		handScrewPaint.setStyle(Paint.Style.FILL);

		backgroundPaint = new Paint();
		backgroundPaint.setFilterBitmap(true);
	}

	@SuppressWarnings("unused")
	private void drawGrid(Canvas canvas) {
		Paint gridPaint = new Paint();
		gridPaint.setColor(0xaf946109);
		gridPaint.setAntiAlias(true);
		gridPaint.setTypeface(Typeface.DEFAULT_BOLD);
		gridPaint.setTextAlign(Paint.Align.CENTER);
		gridPaint.setTextSize(0.05f);
		gridPaint.setTextScaleX(0.8f);
		canvas.drawText("0", 0.0f, 0.0f, gridPaint);
		canvas.drawText("1", 0.1f, 0.1f, gridPaint);
		canvas.drawText("2", 0.2f, 0.2f, gridPaint);
		canvas.drawText("3", 0.3f, 0.3f, gridPaint);
		canvas.drawText("4", 0.4f, 0.4f, gridPaint);
		canvas.drawText("x", 0.5f, 0.5f, gridPaint);
		canvas.drawText("9", .9f, .9f, gridPaint);
	}

	private float getAngle(float seconds) {

		// seconds = seconds % maxSeconds;
		return (seconds / maxSeconds) * (endAngle - startAngle) + startAngle;
	}

	private float getCompleteAngle(float seconds) {
		return (seconds / maxSeconds) * (endAngle - startAngle) + startAngle;
	}

	// public void onSensorChanged(SensorEvent sensorEvent) {
	// if (sensorEvent.values.length > 0) {
	// // L.out("sensorEvent: " + sensorEvent.values[0] + " " +
	// // sensorEvent.values[1] + " "
	// // + sensorEvent.values[2]);
	// skip += 1;
	// // if (skip % 10 != 0)
	// // return;
	// counter += 1;
	// setHandTarget(getAngle(counter));
	//
	// } else {
	// Log.w(TAG, "Empty sensor event received");
	// }
	// }

	private void setHandTarget(float angle) {
		handPosition = angle;
		// L.out("time: " + counter % 10.0f + " angle: " + angle);
		handInitialized = true;
		invalidate();
	}

	private void drawActiveArc(Canvas canvas) {
		if (handInitialized) {
			activeArc = new Paint();
			activeArc.setAntiAlias(true);
			activeArc.setColor(0x77c1d3e1);
			// activeArc.setStyle(Paint.Style.STROKE);
			// activeArc.setShadowLayer(0.01f, 0.005f, 0.005f, 0xCCedf1f4);
			// activeRect = new RectF(0.0f, 0.15f, .8f, 0.85f);
			activeRect = new RectF(0.1f, 0.1f, .9f, .9f);
			// float handAngle = degreeToAngle(handPosition);
			canvas.save(Canvas.MATRIX_SAVE_FLAG);
			// float yScale = .8f;
			// canvas.scale(1f, yScale);
			// canvas.translate(0.05f, 1 - 1 / yScale);
			canvas.clipRect(0.1f, 0.15f, 1.0f, .85f);
			canvas.translate(0.05f, 0.0f);
			// canvas.rotate(handPosition, 0.5f, 0.5f);
			float maxCount = counter;
			if (maxCount > arrived && arrived != 0)
				maxCount = arrived;
			canvas.drawArc(activeRect, startAngle - 90, getAngle(maxCount) - startAngle, true, activeArc);

			Paint stroke = new Paint();
			stroke.setAntiAlias(true);
			stroke.setStyle(Paint.Style.STROKE);
			stroke.setColor(0xAA333333);
			// stroke.setShadowLayer(0.01f, 0.005f, 0.005f, 0xCCedf1f4);
			canvas.drawArc(activeRect, startAngle - 90, getAngle(maxCount) - startAngle, true, stroke);

			canvas.drawCircle(0.5f, 0.5f, 0.01f, handScrewPaint);
			canvas.restore();
		}
	}

	private void drawLateArc(Canvas canvas) {
		if (handInitialized) {
			if (forecast > target) {
				drawStat(canvas);
				activeArc = new Paint();
				activeArc.setAntiAlias(true);
				activeArc.setColor(0x77ffa8a8);
				// activeArc.setStyle(Paint.Style.STROKE);
				// activeArc.setShadowLayer(0.01f, 0.005f, 0.005f, 0xCCedf1f4);
				// activeRect = new RectF(0.0f, 0.15f, .8f, 0.85f);
				activeRect = new RectF(0.1f, 0.1f, .9f, .9f);
				// float handAngle = degreeToAngle(handPosition);
				canvas.save(Canvas.MATRIX_SAVE_FLAG);
				// float yScale = .8f;
				// canvas.scale(1f, yScale);
				// canvas.translate(0.05f, 1 - 1 / yScale);
				canvas.clipRect(0.1f, 0.15f, 1.0f, .85f);
				canvas.translate(0.05f, 0.0f);
				// canvas.rotate(handPosition, 0.5f, 0.5f);
				int sweep = (int) (getCompleteAngle(target) - endAngle);
				// canvas.drawText(sweep + ":"
				// + ((int) (target / maxSeconds * 100)), 0.225f, 0.525f,
				// labelPaint);

				canvas.drawArc(activeRect, endAngle - 90, sweep, true, activeArc);
				Paint stroke = new Paint();
				stroke.setAntiAlias(true);
				stroke.setStyle(Paint.Style.STROKE);
				stroke.setColor(0xAA333333);
				// stroke.setShadowLayer(0.01f, 0.005f, 0.005f, 0xCCedf1f4);
				canvas.drawArc(activeRect, endAngle - 90, sweep, true, stroke);
				canvas.drawCircle(0.5f, 0.5f, 0.01f, handScrewPaint);
				canvas.restore();
			}
		}
	}

	public String parseDate(int ticks) {
		try {
			int positiveTicks = Math.abs(ticks);
			int minutes = positiveTicks / 60;
			int seconds = positiveTicks % 60;
			Date date = new Date(0, 0, 0, 0, minutes, seconds);

			SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
			String temp = sdf.format(date.getTime());
			if (ticks < 0)
				return "-" + temp;
			return temp;
		} catch (Exception e) {
			L.out("Exception :" + e);
		}
		return "______";
	}

	private void drawLabel(Canvas canvas) {
		labelPaint.setLinearText(true);
		redPaint.setLinearText(true);
		grayPaint.setLinearText(true);
		bigBluePaint.setLinearText(true);

		canvas.drawText(title, 0.230f, 0.525f, labelPaint);
		String arrive = parseDate((int) (target - counter));

		canvas.drawText(arrive, 0.225f, 0.625f, redPaint);

		canvas.drawText("Target", 0.115f, 0.75f, grayPaint);
		canvas.drawText(parseDate(target), 0.115f, 0.8375f, grayPaint);
		if (wantSubLabel) {
			canvas.drawText("Forecast", 0.375f, 0.75f, grayPaint);
			canvas.drawText(parseDate(forecast), 0.375f, 0.8375f, grayPaint);
			canvas.drawText("Target", 0.115f, 0.75f, grayPaint);
			canvas.drawText(parseDate(target), 0.115f, 0.8375f, grayPaint);
		} else {
			canvas.drawText("Target", 0.15f, 0.75f, bigBluePaint);
			canvas.drawText(parseDate(target), 0.15f, 0.8375f, bigBluePaint);
		}
	}

	public void setTitle(String newTitle) {
		title = newTitle;
	}

	public String getTitle() {
		return title;
	}

	public int getTarget() {
		// TODO Auto-generated method stub
		return target;
	}

	public void setTarget(int target) {
		// setTarget(target, false);
		this.target = target;
		maxSeconds = target;
		forecast = target;
		// if (target > maxSeconds)
		// maxSeconds = target;

	}

	// public void setTarget(int target, boolean init) {
	// this.target = target;
	// if (init || target > maxSeconds)
	// maxSeconds = target;
	//
	// }

	public void setForecast(int forecast) {
		this.forecast = forecast;
		if (forecast > maxSeconds)
			maxSeconds = forecast;
	}

	public int getForecast() {
		// side effect if restarting from TimerFragment
		if (forecast > maxSeconds)
			maxSeconds = forecast;
		return forecast;
	}

	public void setHandColor(int handColor) {
		handPaint.setColor(handColor);
	}

	public int getHandcolor() {
		// TODO Auto-generated method stub
		return handPaint.getColor();
	}

	public void setBackground(int background) {
		if (swooshBackground != 0)
			return;
		if (logo != null)
			logo = BitmapFactory.decodeResource(getContext().getResources(), background);
		logoMatrix = new Matrix();
		logoScale = (1.0f / logo.getWidth()) * 1f;
		logoMatrix.setScale(logoScale, logoScale);
		this.swooshBackground = background;
	}

	public int getSwooshBackground() {
		// side effect since initializing timeFragment
		setBackground(swooshBackground);
		return swooshBackground;
	}

	public boolean update(float newCounter) {
		// L.out("update:");
		if (newCounter >= maxSeconds) {
			// counter = maxSeconds;
			maxSeconds = newCounter;
			if (forecast < maxSeconds && started)
				forecast = (int) maxSeconds;
			if (started)
				counter = newCounter;

			setHandTarget(getAngle(counter));
			return true;
		}
		if (started)
			counter = newCounter;
		setHandTarget(getAngle(counter));
		return false;
	}

	public void setStarted(boolean started) {
		L.out("started: " + started);
		this.started = started;
	}

	public boolean getStarted() {
		return started;
	}

	public void initArrived() {
		arrived = 0;
		L.out("initArrived: " + arrived);
	}

	public void setArrived() {
		arrived = (int) counter;
		// arrived = 1;
		// L.out("setArrived: " + counter);
		// if (arrived == 0)
		// arrived = 1;
	}

	public void startDelayed() {
		delay = new Delay(counter);
		delays.add(delay);
	}

	public void stopDelayed() {
		if (delay == null) {
			delay = new Delay(0);
			delays.add(delay);
		}
		delay.stopDelayed(counter);
	}

	public void clearDelayed() {
		delays = new ArrayList<Delay>();
	}

	public void printDelayed() {
		L.out("printing delays");
		for (Delay delay : delays)
			L.out(delay.toString());
	}

	public List<Delay> delays = new ArrayList<Delay>();
	Delay delay = null;

	public long startTime = 0l;

	public int getArrived() {
		return arrived;
	}

	private void drawDelayArcs(Canvas canvas) {
		for (Delay delay : delays)
			drawDelayArc(canvas, delay);
	}

	private void drawDelayArc(Canvas canvas, Delay delay) {
		if (handInitialized) {
			Paint delayPaint = new Paint();
			delayPaint.setAntiAlias(true);
			delayPaint.setColor(0x77ffffa8);

			RectF delayRect = new RectF(0.1f, 0.1f, .9f, .9f);

			canvas.save(Canvas.MATRIX_SAVE_FLAG);

			canvas.clipRect(0.1f, 0.15f, 1.0f, .85f);
			canvas.translate(0.05f, 0.0f);

			float maxCount = counter;
			if (delay.stopDelayed > 0)
				maxCount = delay.stopDelayed;
			float angle = getAngle(delay.startDelay);
			canvas.drawArc(delayRect, -90 + angle,
					getAngle(maxCount - delay.startDelay) - startAngle, true, delayPaint);

			Paint stroke = new Paint();
			stroke.setAntiAlias(true);
			stroke.setStyle(Paint.Style.STROKE);
			stroke.setColor(0xAA333333);
			// stroke.setShadowLayer(0.01f, 0.005f, 0.005f, 0xCCedf1f4);
			canvas.drawArc(delayRect, -90 + angle, getAngle(maxCount - delay.startDelay) - startAngle, true, stroke);

			canvas.restore();
		}
	}

	public void setDelays(Swoosh swoosh, Bundle savedInstanceState, String delayStarts, String delayEnds) {
		int[] starts = savedInstanceState.getIntArray(delayStarts);
		if (starts == null)
			return;
		int[] ends = savedInstanceState.getIntArray(delayEnds);

		for (int i = 0; i < starts.length; i++) {
			swoosh.delays.add(new Delay(starts[i], ends[i]));
		}
	}

	public void onDestroy() {
		if (logo != null)
			logo.recycle();
		logo = null;
		if (background != null)
			background.recycle();
		background = null;
		if (stat != null)
			stat.recycle();
		stat = null;
	}
}

class Delay {
	float startDelay = 0f;
	float stopDelayed = 0f;

	Delay(float delayed) {
		startDelay = delayed;
		L.out("startDelay: " + this.toString());
	}

	public Delay(int start, int stop) {
		startDelay = start;
		stopDelayed = stop;
		L.out("restore Delay: " + this.toString());
	}

	void stopDelayed(float delayed) {
		stopDelayed = delayed;
		L.out("endDelay: " + this);
	}

	@Override
	public String toString() {
		return "Delay: " + startDelay + " <-> " + stopDelayed;
	}
}
