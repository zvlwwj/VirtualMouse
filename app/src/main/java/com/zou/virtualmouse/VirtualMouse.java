package com.zou.virtualmouse;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import com.zou.virtualmouse.R;

public class VirtualMouse extends View {
	private Context context;

	/**
	 * 外轮廓，边框1
	 */
	private Paint paint0;

	/**
	 * 外轮廓,边框2
	 */
	private Paint paint1;
	/**
	 * 普通状态，填充
	 */
	private Paint paint2;
	/**
	 * 点击状态，内部黄色区域
	 */
	private Paint paint3;
	/**
	 * 点击状态白色区域
	 */
	private Paint paint4;
	/**
	 * 用于判断点击状态
	 */
	private boolean leftClicked, rightClicked, centerClicked, bottomClicked,
			topClicked, smallMouseClicked;

	/**
	 * 顶部圆的Y值
	 */
	private int topY;
	/**
	 * 底部圆的Y值
	 */
	private int bottomY;

	/**
	 * 顶部和底部圆的半径
	 */
	private int topRad;

	/**
	 * 中心圆的半径
	 */
	private float centerRad;

	/**
	 * 左右键宽度半径
	 */
	private float left_with;

	/**
	 * 左键中心离中心圆的距离
	 */
	private int left_center_x;

	/**
	 * 对外抛出的监听
	 */
	private MouseListener mouseListener;

	/**
	 * 边框宽度，dp
	 */
	public static final int STROKE_WITH = 4;

	/**
	 * 中心圆的半径，dp
	 */
	public static final int RAD_CENTER = 30;

	/**
	 * 左右键与中心圆之间的间隙宽度，dp
	 */
	public static final int SPACE_WITH = 10;

	/**
	 * 左键左弧度的半径,dp
	 */
	public static final int RAD1 = RAD_CENTER * 2 + SPACE_WITH + STROKE_WITH
			* 2;

	/**
	 * 左键右弧度的半径,dp
	 */
	public static final int RAD2 = RAD_CENTER + SPACE_WITH + STROKE_WITH;

	/**
	 * View的宽高，dp
	 */
	public static final int VIEW_SIZE = 156;

	/**
	 * 小鼠标的宽高，dp
	 */
	public static final int SMALL_MOUSE_SIZE = 35;

	/**
	 * 默认中心圆半径
	 */
	public static final int DEFAULT_CENTER_RAD = 30;

	/**
	 * 默认顶部圆半径
	 */
	public static final int DEFAULT_TOP_RAD = 13;

	/**
	 * 默认左键中心离中心圆的距离
	 */
	public static final int DEFAULT_LEFT_CENTER_X = 59;

	/**
	 * 左右键角度的1/2
	 */
	public static final int DEFAULT_ANGLE = 35;

	private static final String TAG = "MOUSE";

	/**
	 * 是否是小鼠标
	 */
	private boolean isSmall = false;

	private Handler handler;

	private Timer timer;

	private TimerTask task;

	private long delay = 10000;

	private final int MSG_TO_SIDE = 1;

	private final int MSG_TO_CENTER = 2;

	private GestureDetector detector;
	
	public VirtualMouse(Context context) {
		super(context);
		init(context);
	}

	public VirtualMouse(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VirtualMouse(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public void setMouseListener(MouseListener mouseListener) {
		this.mouseListener = mouseListener;
	}

	private void init(Context context) {
		this.context = context;
		detector = new GestureDetector(getContext(), new GestureListener());
		detector.setOnDoubleTapListener(new DoubleTapListener());
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_TO_SIDE:
					if (mouseListener != null) {
						// 从大鼠标变成小鼠标时，吸边效果
						mouseListener.toSide();
						clearClick();
					}
					break;

				case MSG_TO_CENTER:
					if (mouseListener != null) {
						// 从小鼠标变成大鼠标时，大鼠标移动一段距离
						mouseListener.toCenter();
						clearClick();
					}
					break;
				}
				super.handleMessage(msg);
			}
		};

		topY = DEFAULT_TOP_RAD + STROKE_WITH;
		bottomY = VIEW_SIZE - DEFAULT_TOP_RAD - STROKE_WITH;
		topRad = DEFAULT_TOP_RAD;
		centerRad = DEFAULT_CENTER_RAD;
		left_with = DEFAULT_TOP_RAD;
		left_center_x = DEFAULT_LEFT_CENTER_X;

		paint0 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint0.setStyle(Paint.Style.STROKE);
		paint0.setColor(Color.parseColor("#999999"));
		paint0.setStrokeWidth(dp2px(STROKE_WITH / 2, context));

		paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setColor(Color.parseColor("#59000000"));
		paint1.setStrokeWidth(dp2px(STROKE_WITH / 2, context));

		paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint2.setStyle(Paint.Style.FILL);
		paint2.setColor(Color.parseColor("#cce5e5e5"));

		paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint3.setStyle(Paint.Style.FILL);
		paint3.setColor(Color.parseColor("#faba5a"));

		paint4 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint4.setColor(Color.parseColor("#ffffff"));
		paint4.setStyle(Paint.Style.FILL);

	}

	class DoubleTapListener implements OnDoubleTapListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			float x = e.getX();
			 float y = e.getY();
			 float x1 = dp2px(VIEW_SIZE/2, getContext());
			 float y1 = dp2px(VIEW_SIZE/2, getContext());
			 float rad1 = dp2px(RAD_CENTER+STROKE_WITH, getContext());
			 if ((x - x1) * (x - x1) + (y - y1) * (y - y1) <= rad1 * rad1) {
				 if (mouseListener != null) {
						mouseListener.OnCenterDoubleCLick();
					}
			 }
			
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			
			return false;
		}

		
	}

	class GestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if (mouseListener != null) {
				mouseListener.OnSmallMouseClick();
			}
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureDimension((int) dp2px(VIEW_SIZE, context),
				widthMeasureSpec);
		int height = measureDimension((int) dp2px(VIEW_SIZE, context),
				heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	public int measureDimension(int defaultSize, int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = defaultSize; // UNSPECIFIED
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();

		if (isSmall) {
			drawSmallMouse(canvas);
			if (smallMouseClicked) {
				drawSmallMouseClicked(canvas);
			}
		} else {
			// 画左上角的鼠标
			drawCursor(canvas);
			// 画左键，三种状态：1.普通状态 2.点击高亮状态 3.隐藏状态
			if (!topClicked) {
				drawLeft(canvas, paint0, left_with, left_center_x,
						-STROKE_WITH / 4);
				drawLeft(canvas, paint1, left_with, left_center_x,
						-STROKE_WITH / 4 * 3);
				if (!leftClicked) {
					drawLeft(canvas, paint2, left_with, left_center_x, 0);
				} else {
					drawLeft(canvas, paint4, left_with, left_center_x, 0);
					drawLeftClicked(canvas, paint3, STROKE_WITH / 2);
				}
			}

			// 画右键，三种状态：1.普通状态 2.点击高亮状态 3.隐藏状态
			if (!topClicked) {
				drawRight(canvas, paint0, left_with, left_center_x,
						-STROKE_WITH / 4);
				drawRight(canvas, paint1, left_with, left_center_x,
						-STROKE_WITH / 4 * 3);
				if (!rightClicked) {
					drawRight(canvas, paint2, left_with, left_center_x, 0);
				} else {
					drawRight(canvas, paint4, left_with, left_center_x, 0);
					drawRightClicked(canvas, paint3, STROKE_WITH / 2);
				}
			}

			// 画中心圆，三种状态：1.普通状态 2.点击高亮状态 3.隐藏状态
			if (!topClicked) {
				drawCenter(canvas, paint0, centerRad, STROKE_WITH / 4);
				drawCenter(canvas, paint1, centerRad, STROKE_WITH / 4 * 3);
				if (!centerClicked) {
					drawCenter(canvas, paint2, centerRad, 0);
				} else {
					drawCenter(canvas, paint4, centerRad, 0);
					drawCenterClicked(canvas, paint3, STROKE_WITH / 2);
				}
			}

			// 画底部圆，位置根据Y变化,三种状态：1.普通状态 2.点击高亮状态 3.隐藏状态
			if (!topClicked) {
				drawBottom(canvas, bottomY, topRad + STROKE_WITH);
			}

			// 画顶部圆,单击效果由CircleScroll显示
			drawTop(canvas, paint0, topY, topRad, STROKE_WITH / 4);
			drawTop(canvas, paint1, topY, topRad, STROKE_WITH / 4 * 3);
			drawTop(canvas, paint2, topY, topRad, 0);
		}

		// 十秒内没有刷新过，则变成小鼠标
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				toSmallMouse();
			}
		};
		timer.schedule(task, delay);

		canvas.restore();

	}

	private void drawCursor(Canvas canvas) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				R.mipmap.cursor);
		canvas.drawBitmap(bm, 0, 0, null);
	}

	private void drawSmallMouse(Canvas canvas) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				R.mipmap.small_mouse);
		RectF dst = new RectF(
				dp2px((VIEW_SIZE - SMALL_MOUSE_SIZE) / 2, context), dp2px(
						(VIEW_SIZE - SMALL_MOUSE_SIZE) / 2, context), dp2px(
						(VIEW_SIZE + SMALL_MOUSE_SIZE) / 2, context), dp2px(
						(VIEW_SIZE + SMALL_MOUSE_SIZE) / 2, context));
		canvas.drawBitmap(bm, null, dst, null);
	}

	private void drawSmallMouseClicked(Canvas canvas) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				R.mipmap.small_mouse_clicked);
		RectF dst = new RectF(
				dp2px((VIEW_SIZE - SMALL_MOUSE_SIZE) / 2, context), dp2px(
						(VIEW_SIZE - SMALL_MOUSE_SIZE) / 2, context), dp2px(
						(VIEW_SIZE + SMALL_MOUSE_SIZE) / 2, context), dp2px(
						(VIEW_SIZE + SMALL_MOUSE_SIZE) / 2, context));
		canvas.drawBitmap(bm, null, dst, null);
	}

	public boolean isSmall() {
		return this.isSmall;
	}

	/**
	 * 画鼠标左键
	 * 
	 * @param canvas
	 * @param paint
	 * @param rad
	 *            上下两圆弧的半径（用于缩小）默认15
	 * @param x
	 *            圆弧中心到中心点的x距离（用于缩小）默认65
	 * 
	 * @param dx
	 *            用于绘制点击时的高亮区域 dx表示空隙距离
	 */
	private void drawLeft(Canvas canvas, Paint paint, float rad, int x, int dx) {
		Path p = new Path();
		Double angleRadians = DEFAULT_ANGLE * Math.PI / 180;
		// 上圆弧的圆心位置
		float topx = Float.parseFloat(String.valueOf(VIEW_SIZE / 2 - x
				* Math.cos(angleRadians)));
		float topy = Float.parseFloat(String.valueOf(VIEW_SIZE / 2 - x
				* Math.sin(angleRadians)));
		// 绘制左键左圆弧
		RectF f1 = new RectF(dp2px(VIEW_SIZE / 2 - (x + rad) + dx, context),
				dp2px(VIEW_SIZE / 2 - (x + rad) + dx, context), dp2px(VIEW_SIZE
						/ 2 + (x + rad) - dx, context), dp2px(VIEW_SIZE / 2
						+ (x + rad) - dx, context));
		// 绘制左键上圆弧
		RectF f2 = new RectF(dp2px(topx - rad + dx, context), dp2px(topy - rad
				+ dx, context), dp2px(topx + rad - dx, context), dp2px(topy
				+ rad - dx, context));
		// 绘制左键右圆弧
		RectF f3 = new RectF(dp2px(VIEW_SIZE / 2 - (x - rad) - dx, context),
				dp2px(VIEW_SIZE / 2 - (x - rad) - dx, context), dp2px(VIEW_SIZE
						/ 2 + (x - rad) + dx, context), dp2px(VIEW_SIZE / 2
						+ (x - rad) + dx, context));
		// 绘制左键下圆弧
		RectF f4 = new RectF(dp2px(topx - rad + dx, context), dp2px(VIEW_SIZE
				- topy - rad + dx, context), dp2px(topx + rad - dx, context),
				dp2px(VIEW_SIZE - topy + rad - dx, context));
		p.arcTo(f1, 180 - DEFAULT_ANGLE, DEFAULT_ANGLE * 2);
		p.arcTo(f2, -(180 - DEFAULT_ANGLE), 180);
		p.arcTo(f3, -(180 - DEFAULT_ANGLE), -DEFAULT_ANGLE * 2);
		p.arcTo(f4, -DEFAULT_ANGLE, 180);
		p.close();
		canvas.drawPath(p, paint);
	}

	/**
	 * 画点击时左键的高亮区域
	 */
	private void drawLeftClicked(Canvas canvas, Paint paint, int dx) {
		drawLeft(canvas, paint, DEFAULT_TOP_RAD, DEFAULT_LEFT_CENTER_X, dx);
	}

	/**
	 * 画鼠标右键
	 * 
	 * @param canvas
	 * @param paint
	 * @param rad
	 *            上下两圆弧的半径（用于缩小）默认15
	 * @param x
	 *            圆弧中心到中心点的x距离（用于缩小）默认65
	 * 
	 * @param dx
	 *            用于绘制点击时的高亮区域 dx表示空隙距离
	 */
	private void drawRight(Canvas canvas, Paint paint, float rad, int x, int dx) {
		Path p = new Path();
		Double angleRadians = DEFAULT_ANGLE * Math.PI / 180;
		// 左键上圆弧的圆心位置
		float topx = Float.parseFloat(String.valueOf(VIEW_SIZE / 2 - x
				* Math.cos(angleRadians)));
		float topy = Float.parseFloat(String.valueOf(VIEW_SIZE / 2 - x
				* Math.sin(angleRadians)));
		// 绘制右键右圆弧
		RectF f1 = new RectF(dp2px(VIEW_SIZE / 2 - (x + rad) + dx, context),
				dp2px(VIEW_SIZE / 2 - (x + rad) + dx, context), dp2px(VIEW_SIZE
						/ 2 + (x + rad) - dx, context), dp2px(VIEW_SIZE / 2
						+ (x + rad) - dx, context));
		// 绘制右键上圆弧
		RectF f2 = new RectF(dp2px(VIEW_SIZE - topx - rad + dx, context),
				dp2px(topy - rad + dx, context), dp2px(VIEW_SIZE - topx + rad
						- dx, context), dp2px(topy + rad - dx, context));
		// 绘制右键左圆弧
		RectF f3 = new RectF(dp2px(VIEW_SIZE / 2 - x + rad - dx, context),
				dp2px(VIEW_SIZE / 2 - x + rad - dx, context), dp2px(VIEW_SIZE
						/ 2 + x - rad + dx, context), dp2px(VIEW_SIZE / 2 + x
						- rad + dx, context));
		// 绘制右键下圆弧
		RectF f4 = new RectF(dp2px(VIEW_SIZE - topx - rad + dx, context),
				dp2px(VIEW_SIZE - topy - rad + dx, context), dp2px(VIEW_SIZE
						- topx + rad - dx, context), dp2px(VIEW_SIZE - topy
						+ rad - dx, context));
		p.arcTo(f1, DEFAULT_ANGLE, -DEFAULT_ANGLE * 2);
		p.arcTo(f2, -DEFAULT_ANGLE, -180);
		p.arcTo(f3, -DEFAULT_ANGLE, DEFAULT_ANGLE * 2);
		p.arcTo(f4, 180 + DEFAULT_ANGLE, -180);
		p.close();
		canvas.drawPath(p, paint);
	}

	/**
	 * 画点击时右键的高亮区域
	 *
	 * @param paint
	 */
	private void drawRightClicked(Canvas canvas, Paint paint, int dx) {
		drawRight(canvas, paint, DEFAULT_TOP_RAD, DEFAULT_LEFT_CENTER_X, dx);
	}

	/**
	 * 画鼠标中心圆
	 * 
	 * @param canvas
	 * @param paint
	 * @param rad
	 *            默认32
	 * @param dx
	 *            用于绘制边框
	 */
	private void drawCenter(Canvas canvas, Paint paint, float rad, int dx) {
		canvas.drawCircle(dp2px(VIEW_SIZE / 2, context),
				dp2px(VIEW_SIZE / 2, context), dp2px(rad + dx, context), paint);
	}

	/**
	 * 画点击时鼠标中心圆的高亮区域
	 *
	 * @param dx
	 *            表示点击时的高亮区域离边界的距离
	 */
	private void drawCenterClicked(Canvas canvas, Paint paint, int dx) {
		canvas.drawCircle(dp2px(VIEW_SIZE / 2, context),
				dp2px(VIEW_SIZE / 2, context), dp2px(RAD_CENTER - dx, context),
				paint);
	}

	/**
	 * 画鼠标底部圆,根据Y值变化
	 * 
	 * @param canvas
	 * @param
	 * @param y
	 * @param rad
	 *            默认15
	 */
	private void drawBottom(Canvas canvas, int y, int rad) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				R.mipmap.mouse_more);
		RectF dst = new RectF(dp2px(VIEW_SIZE / 2 - rad, context), dp2px(
				VIEW_SIZE - rad * 2, context), dp2px(VIEW_SIZE / 2 + rad,
				context), dp2px(VIEW_SIZE, context));
		canvas.drawBitmap(bm, null, dst, null);
	}

	/**
	 * 画鼠标顶部圆，根据Y值变化
	 * 
	 * @param canvas
	 * @param paint
	 * @param y
	 * @param rad
	 *            默认15
	 * @param
	 */
	private void drawTop(Canvas canvas, Paint paint, int y, int rad, int dx) {
		canvas.drawCircle(dp2px(VIEW_SIZE / 2, context), dp2px(y, context),
				dp2px(rad + dx, context), paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isSmall) {
			// 点击事件
			clickEvent(event);
			// 出现滚轮事件
			appearRoller(event);
			// 双击中心圆
			doubleClickCenter(event);
			// 移动鼠标
			moveMouse(event);
		} else {
			// 小鼠标的事件处理
			return smallMouseEvent(event);
		}
		return true;
	}

	/**
	 * 小鼠标事件
	 */
	private float prex, prey;
	private long downTime;

	private boolean smallMouseEvent(MotionEvent event) {
		
		float x = event.getX();
		float y = event.getY();
		float x1 = dp2px(VIEW_SIZE / 2, context);
		float y1 = dp2px(VIEW_SIZE / 2, context);
		
			detector.onTouchEvent(event);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (x > x1 - dp2px(SMALL_MOUSE_SIZE / 2, context)
						&& x < x1 + dp2px(SMALL_MOUSE_SIZE / 2, context)
						&& y > y1 - dp2px(SMALL_MOUSE_SIZE / 2, context)
						&& y < y1 + dp2px(SMALL_MOUSE_SIZE / 2, context)) {
				prex = event.getRawX();
				prey = event.getRawY();
				smallMouseClicked = true;
				downTime = System.currentTimeMillis();
				invalidate();
				} else {
					return false;
				}
				break;

			case MotionEvent.ACTION_MOVE:
				if(smallMouseClicked){
				float dx = event.getRawX() - prex;
				float dy = event.getRawY() - prey;
				if (this.mouseListener != null) {
					this.mouseListener.OnSmallMouseMove(dx, dy);
				}
				prex = event.getRawX();
				prey = event.getRawY();
				}
				break;

			case MotionEvent.ACTION_UP:
				smallMouseClicked = false;
				invalidate();
				if (this.mouseListener != null) {
					mouseListener.toSide();
				}
				break;
			}
			return true;
		
	}

	/**
	 * 鼠标移动事件
	 */
	private float xPre, yPre;// 绝对位置

	private void moveMouse(MotionEvent event) {
		if (centerClicked || leftClicked || rightClicked) {
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				xPre = event.getRawX();
				yPre = event.getRawY();
			}
			if (MotionEvent.ACTION_MOVE == event.getAction()) {
				float dx = event.getRawX() - xPre;
				float dy = event.getRawY() - yPre;
				mouseListener.OnMouseMove(dx, dy);
				xPre = event.getRawX();
				yPre = event.getRawY();
			}
		}
	}

	/**
	 * 单击事件
	 * 
	 * @param event
	 */
	private void clickEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		float x1 = dp2px(VIEW_SIZE / 2, context);
		float y1 = dp2px(VIEW_SIZE / 2, context);
		float rad1 = dp2px(RAD_CENTER + STROKE_WITH, context);
		float x2 = dp2px(VIEW_SIZE / 2, context);
		float y2 = dp2px(DEFAULT_TOP_RAD + STROKE_WITH, context);
		float rad2 = dp2px(DEFAULT_TOP_RAD + STROKE_WITH, context);

		float x3 = dp2px(VIEW_SIZE / 2, context);
		float y3 = dp2px(VIEW_SIZE - (DEFAULT_TOP_RAD + STROKE_WITH), context);
		float rad3 = dp2px(DEFAULT_TOP_RAD + STROKE_WITH, context);
		if (MotionEvent.ACTION_UP == event.getAction()) {
			clearClick();
		}
		if ((x - x1) * (x - x1) + (y - y1) * (y - y1) <= rad1 * rad1) {
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				centerClicked = true;
			}
			invalidate();
		} else if ((x - x2) * (x - x2) + (y - y2) * (y - y2) <= rad2 * rad2) {
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				topClicked = true;
			}
			invalidate();
		} else if ((x - x3) * (x - x3) + (y - y3) * (y - y3) <= rad3 * rad3) {
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				bottomClicked = true;
			}
			invalidate();
		} else if (x < dp2px(VIEW_SIZE / 2 - DEFAULT_CENTER_RAD / 2 - 15,
				context)) {
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				leftClicked = true;
				if (mouseListener != null) {
					mouseListener.OnLeftDown();
				}
			}
			invalidate();
		} else if (x > dp2px(VIEW_SIZE / 2 + DEFAULT_CENTER_RAD / 2 + 15,
				context)) {
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				rightClicked = true;
				if (mouseListener != null) {
					mouseListener.OnRightDown();
				}
			}
			invalidate();
		}
	}

	/**
	 * 变成小鼠标
	 */
	public void toSmallMouse() {
		if (!isSmall) {
			new ReduceAnimateThread().start();
		}
	}

	/**
	 * 变成大鼠标
	 */
	public void toBigMouse() {
		if (isSmall) {
			new ExpandAnimateThread().start();
		}
	}

	/**
	 * 变成小鼠标的动画线程
	 * 
	 * @author zou
	 *
	 */
	class ReduceAnimateThread extends Thread {
		@Override
		public void run() {
			for (int i = 0; i < 100; i++) {
				if (centerRad >= 25) {
					centerRad -= 0.2;
				}
				if (i <= 25) {
					topY += 2;
					bottomY -= 2;
				}
				if (i >= 10 && i <= 25) {
					topRad--;
				}
				if (i >= 25) {
					left_with -= 0.2;
					left_center_x--;
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				postInvalidate();
			}
			centerRad = 25;
			topY = 71;
			bottomY = 97;
			topRad = -1;
			left_with = 2;
			left_center_x = -10;
			isSmall = true;
			smallMouseClicked = false;
			Message msg = Message.obtain(handler);
			msg.what = MSG_TO_SIDE;
			msg.sendToTarget();
		}

	}

	/**
	 * 变成大鼠标的线程
	 * 
	 * @author zou
	 *
	 */
	class ExpandAnimateThread extends Thread {
		@Override
		public void run() {
			isSmall = false;
			for (int i = 0; i < 100; i++) {
				if (centerRad <= RAD_CENTER) {
					centerRad += 0.2;
				}
				if (topY > DEFAULT_TOP_RAD + STROKE_WITH) {
					topY -= 2;

				}
				if (bottomY < VIEW_SIZE - (DEFAULT_TOP_RAD + STROKE_WITH)) {
					bottomY += 2;
				}

				if (topRad < DEFAULT_TOP_RAD) {
					topRad++;
				}
				if (left_with < DEFAULT_TOP_RAD) {
					left_with += 0.2;
				}
				if (left_center_x < DEFAULT_LEFT_CENTER_X) {
					left_center_x++;
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				postInvalidate();
			}
			Message msg = Message.obtain(handler);
			msg.what = MSG_TO_CENTER;
			msg.sendToTarget();
		}
	}

	public boolean getIsSmall() {
		return isSmall;
	}

	/**
	 * 出现滚轮的事件处理
	 *
	 * @param event
	 */
	private void appearRoller(MotionEvent event) {
		if (topClicked) {
			if (mouseListener != null) {
				mouseListener.OnScrollMove(event);
			}
		}
	}

	/**
	 * 双击中心圆
	 *
	 * @param event
	 */
	private void doubleClickCenter(MotionEvent event) {
		detector.onTouchEvent(event);
		// float x = event.getX();
		// float y = event.getY();
		// float x1 = dp2px(VIEW_SIZE/2, context);
		// float y1 = dp2px(VIEW_SIZE/2, context);
		// float rad1 = dp2px(RAD_CENTER+STROKE_WITH, context);
		// if ((x - x1) * (x - x1) + (y - y1) * (y - y1) <= rad1 * rad1) {
		// if (MotionEvent.ACTION_UP == event.getAction()) {
		// centerClickCount++;
		// if (centerClickCount == 1) {
		// firClick = System.currentTimeMillis();
		//
		// } else if (centerClickCount == 2) {
		// secClick = System.currentTimeMillis();
		// if (secClick - firClick < 800) {
		// // 双击事件
		// if (this.mouseListener != null) {
		// this.mouseListener.OnCenterDoubleCLick();
		// }
		// }
		// centerClickCount = 0;
		// firClick = 0;
		// secClick = 0;
		// }
		// }
		// }
	}

	private void clearClick() {
		if (leftClicked) {
			leftClicked = false;
			if (mouseListener != null) {
				mouseListener.OnLeftUp();
			}
		}
		if (rightClicked) {
			rightClicked = false;
			if (mouseListener != null) {
				mouseListener.OnRightUp();
			}
		}
		if (centerClicked) {
			centerClicked = false;
		}
		if (bottomClicked) {
			bottomClicked = false;
			if (mouseListener != null) {
				mouseListener.OnBottomClick();
			}
		}
		if (topClicked) {
			topClicked = false;
			if (mouseListener != null) {
				mouseListener.OnScrollUp();
			}
		}
		invalidate();
	}

	public static float dp2px(float dipValue, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return dipValue * scale;
	}

	public static float px2dp(float pxValue, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return pxValue / scale;
	}

	public interface MouseListener {
		void OnLeftDown();

		void OnLeftUp();

		void OnRightUp();

		void OnRightDown();

		void OnBottomClick();

		void OnCenterDoubleCLick();

		void OnMouseMove(float dx, float dy);

		void OnScrollMove(MotionEvent event);

		void OnScrollUp();

		void OnSmallMouseClick();

		void OnSmallMouseMove(float dx, float dy);

		void toSide();

		void toCenter();
	}
}
