package chris.test.nfcproject;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;



class MyView extends View {
	
	Paint paint;
	Path path;
	
	RectF mainRectF;
	final RectF oval = new RectF();
	
	boolean playing = false;
	
	int backgroundColor = ContextCompat.getColor(this.getContext(), R.color.background);
	float circleX = 265.0f + 16.0f - 12.0f;;
	int opacity = 0;
	float leftPart = 0.0f;
	
	
	float smallCircleRad = 12.0f;
	float innerCircleRad = 16.0f;
	
	float pointX = 280.0f;
	float pointY = 280.0f;
	
	float bigLockRad = 70.0f;
	float smallLockRad = 40.0f;
	
	float lockRotationX = 320.0f;
	float lockRotationY = 290.0f;
	
	float lockRotation = 0.0f;
	
	float verticalLine = 50.0f;
	
	boolean entered = false;
	boolean enteredRight1 = false;
	boolean enteredLeft1 = false;
	boolean enteredRight2 = false;
	boolean enteredLeftPart2 = false;
	
	
	public MyView(Context context) {
		super(context);
		
		paint = new Paint();
		path = new Path();
		mainRectF = new RectF(180, 280, 380, 480);
		
		paint.setAntiAlias(true);
		
	}
	
	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		paint = new Paint();
		path = new Path();
		mainRectF = new RectF(180, 280, 380, 480);
		
		paint.setAntiAlias(true);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}
	
	protected void playAnimation () {
		
		if (!playing) {
			playing = true;
			// invalidate();
			animateCircle( );
		}
	}
	
	protected void resetAnimation () {
		
		circleX = 265.0f + 16.0f - 12.0f;
		
		lockRotation = 0.0f;
		
		opacity = 0;
		
		leftPart = 0;
		
		playing = false;
		
		entered = false;
		enteredLeft1 = false;
		enteredRight1 = false;
		enteredRight2 = false;
		enteredLeftPart2 = false;
		
		invalidate();
		
	}
	
	
	protected void animateCircle () {
		
		ValueAnimator translateAnimator = ValueAnimator.ofFloat(265.0f + 16.0f - 12.0f, 290.0f - 16.0f + 12.0f);
		translateAnimator.setDuration(500);
		translateAnimator.setInterpolator(new LinearInterpolator());
		translateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator animation) {
				circleX = (float) animation.getAnimatedValue();
				if (circleX == 286.0f && !entered) {
					
					animateOpacity();
					animateLeftPart();
					animateLockRotationRight1();
					
					entered = true;
				}
				invalidate();
			}
		});
		translateAnimator.start();
		
	}
	
	protected void animateLockRotationRight1 () {
		
		ValueAnimator rotationAnimator = ValueAnimator.ofFloat(0.0f, 50.0f);
		rotationAnimator.setDuration(250);
		rotationAnimator.setInterpolator(new LinearInterpolator());
		rotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator animation) {
				lockRotation = (float) animation.getAnimatedValue();
				
				if (lockRotation == 50.0f && !enteredRight1) {
					animateLockRotationLeft1();
					
					enteredRight1 = true;
				}
				
				invalidate();
			}
		});
		rotationAnimator.start();
	}
	
	protected void animateLockRotationLeft1 () {
		
		ValueAnimator rotationAnimator = ValueAnimator.ofFloat(50.0f, 38.0f);
		rotationAnimator.setDuration(250);
		rotationAnimator.setInterpolator(new LinearInterpolator());
		rotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator animation) {
				lockRotation = (float) animation.getAnimatedValue();
				if (lockRotation == 38.0f && !enteredLeft1) {
					animateLockRotationRight2();
					
					enteredLeft1 = true;
				}
				invalidate();
			}
		});
		rotationAnimator.start();
		
	}
	
	protected void animateLockRotationRight2 () {
		
		ValueAnimator rotationAnimator = ValueAnimator.ofFloat(38.0f, 43.0f);
		rotationAnimator.setDuration(250);
		rotationAnimator.setInterpolator(new LinearInterpolator());
		rotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator animation) {
				lockRotation = (float) animation.getAnimatedValue();
				if (lockRotation == 43.0f && !enteredRight2) {
					animateLockRotationLeft2();
					
					enteredRight2 = true;
				}
				invalidate();
			}
		});
		rotationAnimator.start();
		
	}
	
	protected void animateLockRotationLeft2 () {
		
		ValueAnimator rotationAnimator = ValueAnimator.ofFloat(43.0f, 41.0f);
		rotationAnimator.setDuration(250);
		rotationAnimator.setInterpolator(new LinearInterpolator());
		rotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator animation) {
				lockRotation = (float) animation.getAnimatedValue();
				invalidate();
				if (lockRotation == 41.0f) {
					playing = false;
				}
			}
		});
		rotationAnimator.start();
		
	}
	
	protected void animateOpacity () {
		
		Log.d("opacity", "opacity");
		ValueAnimator fadeAnimator = ValueAnimator.ofInt(0, 255);
		fadeAnimator.setDuration(80);
		fadeAnimator.setInterpolator(new AccelerateInterpolator( ));
		fadeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener( ) {
			public void onAnimationUpdate (ValueAnimator animation) {
				opacity = (int) animation.getAnimatedValue( );
				invalidate( );
			}
		});
		fadeAnimator.start( );
		
	}
	
	protected void animateLeftPart () {
		
		ValueAnimator translateAnimator = ValueAnimator.ofFloat(0, 7);
		translateAnimator.setDuration(100);
		translateAnimator.setInterpolator(new LinearInterpolator( ));
		translateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener( ) {
			public void onAnimationUpdate (ValueAnimator animation) {
				leftPart = (float) animation.getAnimatedValue( );
				if (leftPart == 7 && !enteredLeftPart2) {
					animateLeftPart2();
					enteredLeftPart2 = true;
				}
				invalidate( );
			}
		});
		translateAnimator.start( );
		
	}
	
	protected void animateLeftPart2 () {
		
		Log.d("aaa","aaa");
		ValueAnimator translateAnimator = ValueAnimator.ofFloat(7, 0);
		translateAnimator.setDuration(100);
		translateAnimator.setInterpolator(new LinearInterpolator( ));
		translateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener( ) {
			public void onAnimationUpdate (ValueAnimator animation) {
				leftPart = (float) animation.getAnimatedValue( );
				invalidate( );
			}
		});
		translateAnimator.start( );
		
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		paint.setColor(backgroundColor);
		paint.setStyle(Paint.Style.FILL);
		
		canvas.save();
		
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		
		
		paint.setStyle(Paint.Style.STROKE);
		
		oval.set(pointX - bigLockRad, pointY - bigLockRad, pointX + bigLockRad, pointY + bigLockRad);
		path.arcTo(oval, 0, -(float) 180, true);
		
		path.moveTo(pointX - bigLockRad, pointY);
		path.lineTo(pointX - smallLockRad, pointY);
		oval.set(pointX - smallLockRad, pointY - smallLockRad, pointX + smallLockRad, pointY + smallLockRad);
		path.arcTo(oval, 0, -(float) 180, true);
		
		paint.setColor(Color.WHITE);
		path.moveTo(pointX + smallLockRad, pointY);
		path.lineTo(pointX + smallLockRad, pointY + smallLockRad);
		
		path.moveTo(pointX + bigLockRad, pointY);
		path.lineTo(pointX + bigLockRad, pointY + verticalLine);
		
		
		canvas.rotate(lockRotation, lockRotationX, lockRotationY);
		canvas.drawPath(path, paint);
		canvas.restore();
		
		
		
		paint.setStrokeWidth(4);
		paint.setColor(backgroundColor);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRoundRect(mainRectF, 12, 12, paint);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRoundRect(mainRectF, 12, 12, paint);
		
		
		// LEFT CIRCLE
		
		canvas.drawCircle(265.0f, 375.0f, smallCircleRad, paint);
		
		
		// RIGHT CIRCLE
		
		canvas.drawCircle(290.0f, 375.0f, smallCircleRad, paint);
		
		
		// RECTANGLE TO HIDE THE INSIDE HALF CIRCLES
		
		paint.setColor(backgroundColor);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(265.0f, 363.0f, 289.0f, 387.0f, paint);
		
		
		// TOP & BOTTOM WHITE LINES
		
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		
		canvas.drawLine(265.0f, 363.0f, 290.0f, 363.0f, paint);
		canvas.drawLine(265.0f, 387.0f, 290.0f, 387.0f, paint);
		
		
		// INNER MOVING CIRCLE RED CIRCLE
		
		paint.setColor(backgroundColor);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawOval(circleX - innerCircleRad + leftPart, 359.0f, circleX + innerCircleRad, 391.0f, paint);
		
		
		// INNER MOVING CIRCLE WHITE CIRCLE
		
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		paint.setAlpha(opacity);
		canvas.drawOval(circleX - innerCircleRad + leftPart, 359.0f, circleX + innerCircleRad, 391.0f, paint);
		
		
		// OUTER MOVING CIRCLE
		
		paint.setAlpha(255);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawOval(circleX - innerCircleRad + leftPart, 359.0f, circleX + innerCircleRad, 391.0f, paint);
		
		
		path.close();
		super.onDraw(canvas);
		
	}
	
}