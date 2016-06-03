package com.zou.virtualmouse;

import com.zou.virtualmouse.VirtualMouse;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;

public class CircleScroll extends View{
	private Context context; 
	private float circleX,circleY;
	//VirtualMouse的中心点的Y值
	private float centerY;
	private float maxDesY;
	public CircleScroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public CircleScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public CircleScroll(Context context) {
		super(context);
		this.context = context;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		//轮廓
		Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setColor(Color.parseColor("#3f000000"));
        paint1.setStrokeWidth(dp2px(VirtualMouse.STROKE_WITH, context));
        
        //常态，填充
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint2.setStyle(Paint.Style.FILL);
		paint2.setColor(Color.parseColor("#cce5e5e5"));
        
        //高亮，填充
        Paint paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3.setStyle(Paint.Style.FILL);
        paint3.setColor(Color.parseColor("#faba5a"));

        //白底
        Paint paint4 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint4.setColor(Color.parseColor("#ffffff"));
        paint4.setStyle(Paint.Style.FILL);
        mDrawCircle(canvas,paint1,circleX,circleY);
        mDrawCircle(canvas,paint4,circleX,circleY);
        mDrawCircleClicked(canvas,paint3,circleX,circleY);
        
        if(maxDesY<dp2px(-32, context)){
        	for(int i=1;i<=9;i++){
        		float y = (maxDesY+dp2px(39, context))/9;
        		drawUp(canvas, paint1, 2, 9,y*i);
        		drawUp(canvas, paint2, 2, 9,y*i);
        	}
        }
        if(maxDesY>dp2px(26, context)){
        	for(int i=1;i<=9;i++){
        		float y = (maxDesY-dp2px(39, context))/9;
        		drawDown(canvas, paint1, 2, 9,y*i);
        		drawDown(canvas, paint2, 2, 9,y*i);
        	}
        }
        canvas.restore();
	}
	
	private void mDrawCircle(Canvas canvas,Paint paint,float x,float y){
		canvas.drawCircle(x, y, dp2px(VirtualMouse.DEFAULT_TOP_RAD, context), paint);
	}
	
	private void mDrawCircleClicked(Canvas canvas,Paint paint,float x ,float y){
		canvas.drawCircle(x, y, dp2px(VirtualMouse.DEFAULT_TOP_RAD-2, context), paint);
	}
	
	public void setCenterY(float y){
		this.centerY = y;
	}
	
	public float getMaxDesY(){
		return this.maxDesY;
	}
	
	private void drawUp(Canvas canvas, Paint paint, float rad, int x,float desY) {
		Path p = new Path();
		RectF f1 = new RectF(circleX-dp2px( x + rad, context),
				dp2px(VirtualMouse.VIEW_SIZE/2 - (x + rad)-150, context)+centerY+desY, circleX+dp2px(x + rad, context), dp2px(
						VirtualMouse.VIEW_SIZE/2 + (x + rad)-150, context)+centerY+desY);
		Double angleRadians = VirtualMouse.DEFAULT_ANGLE * Math.PI / 180;
		float topx = Float.parseFloat(String.valueOf(VirtualMouse.VIEW_SIZE/2 - x * Math.sin(angleRadians)));
		float topy = Float.parseFloat(String.valueOf(VirtualMouse.VIEW_SIZE/2 - x * Math.cos(angleRadians)));
		RectF f2 = new RectF( circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+dp2px(VirtualMouse.VIEW_SIZE - topx - rad, context),
				 dp2px(topy - rad-150, context)+centerY+desY,  circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+dp2px(
						VirtualMouse.VIEW_SIZE - topx + rad, context),  dp2px(
						topy + rad-150, context)+centerY+desY);
		RectF f3 = new RectF(circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+dp2px(VirtualMouse.VIEW_SIZE/2 - x + rad, context),
				dp2px(VirtualMouse.VIEW_SIZE/2 - x + rad-150, context)+centerY+desY, circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+dp2px(
						VirtualMouse.VIEW_SIZE/2 + x - rad, context), dp2px(
						VirtualMouse.VIEW_SIZE/2 + x - rad-150, context)+centerY+desY);
		RectF f4 = new RectF(circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+dp2px(topx - rad, context),
				dp2px(topy - rad-150, context)+centerY+desY, circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+dp2px(
						topx + rad, context), dp2px(
						topy + rad-150, context)+centerY+desY);
		p.arcTo(f1, 235, 60);
		p.arcTo(f2, -55, 180);
		p.arcTo(f3, -55, -70);
		p.arcTo(f4, 55, 180);
		p.close();
		
		canvas.drawPath(p, paint);
	}
	
	private void drawDown(Canvas canvas, Paint paint, float rad, int x,float desY) {
		Path p = new Path();
		RectF f1 = new RectF(circleX-dp2px((x + rad), context),
				dp2px(VirtualMouse.VIEW_SIZE/2 - (x + rad)-135, context)+centerY+desY, circleX+dp2px((x + rad), context), dp2px(
						VirtualMouse.VIEW_SIZE/2 + (x + rad)-135, context)+centerY+desY);
		Double angleRadians = VirtualMouse.DEFAULT_ANGLE * Math.PI / 180;
		float topx = Float.parseFloat(String.valueOf(VirtualMouse.VIEW_SIZE/2 - x * Math.sin(angleRadians)));
		float topy = Float.parseFloat(String.valueOf(VirtualMouse.VIEW_SIZE/2 - x * Math.cos(angleRadians)));
		RectF f2 = new RectF( circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+dp2px(topx - rad, context),
				dp2px(VirtualMouse.VIEW_SIZE - topy - rad-135, context)+centerY+desY, circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+dp2px(
						topx + rad, context), dp2px(
						VirtualMouse.VIEW_SIZE - topy + rad-135, context)+centerY+desY);
		RectF f3 = new RectF(circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+dp2px(VirtualMouse.VIEW_SIZE/2 - x + rad, context),
				dp2px(VirtualMouse.VIEW_SIZE/2 - x + rad-135, context)+centerY+desY,circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+ dp2px(
						VirtualMouse.VIEW_SIZE/2 + x - rad, context), dp2px(
						VirtualMouse.VIEW_SIZE/2 + x - rad-135, context)+centerY+desY);
		RectF f4 = new RectF(circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+dp2px((int) Math.rint(VirtualMouse.VIEW_SIZE - topx - rad), context),
				dp2px(VirtualMouse.VIEW_SIZE - topy - rad-135, context)+centerY+desY, circleX-dp2px(VirtualMouse.VIEW_SIZE/2,context)+dp2px(
						VirtualMouse.VIEW_SIZE - topx + rad, context), dp2px(
						VirtualMouse.VIEW_SIZE - topy + rad-135, context)+centerY+desY);
		p.arcTo(f1, 65, 70);
		p.arcTo(f2, 125, 180);
		p.arcTo(f3, 125, -70);
		p.arcTo(f4, -55, 180);
		p.close();
		
		canvas.drawPath(p, paint);
	}
	
	public void moveScoller(float x ,float y,MotionEvent event){
		this.circleX = x;
		this.circleY = y-dp2px(66, context);
		this.setVisibility(View.VISIBLE);
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			maxDesY = event.getRawY()-centerY;
			invalidate();
			break;
		}
	}
	
	public static float dp2px(float dipValue, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return dipValue * scale;
	}
}
