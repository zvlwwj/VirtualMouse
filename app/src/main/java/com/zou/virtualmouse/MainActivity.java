package com.zou.virtualmouse;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    private VirtualMouse virtualMouse;
    private CircleScroll circleScroll;
    private Animator.AnimatorListener aListener;
    private boolean smallMouseClickable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circleScroll = (CircleScroll) findViewById(R.id.circle);
        circleScroll.setVisibility(View.INVISIBLE);
        aListener = new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                smallMouseClickable = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        };
        virtualMouse = (VirtualMouse) findViewById(R.id.virtual_mouse);
        virtualMouse.setMouseListener(new VirtualMouse.MouseListener() {

            @Override
            public void OnScrollMove(MotionEvent event) {
                //滚轮弹簧动画
                circleScroll.setCenterY(virtualMouse.getTranslationY()+dp2px(VirtualMouse.VIEW_SIZE/2,MainActivity.this));
                circleScroll.moveScoller(virtualMouse.getTranslationX()+dp2px(VirtualMouse.VIEW_SIZE/2, MainActivity.this),event.getRawY(),event);

                //发送滚轮事件
//                float[] pt = new float[] { virtualMouse.getTranslationX(), virtualMouse.getTranslationY() };
//                mDesktopView.transformPointToDesktop(pt);
//                mDesktopJni.SendMouseHover("", (int) pt[0], (int) pt[1], true);
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        y1 = event.getRawY();
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//                        y2 = event.getRawY();
//                        if(y2-y1>30){
//                            mDesktopJni.SendMouseWheel("", (int) pt[0], (int) pt[1],
//                                    (int) 100, true);
//                            y1 = y2;
//                        }else if(y2-y1<-30){
//                            mDesktopJni.SendMouseWheel("", (int) pt[0], (int) pt[1],
//                                    (int) -100, true);
//                            y1 = y2;
//                        }
//                        break;
            }

            @Override
            public void OnRightUp() {
                float[] pt = new float[2];
                pt[0] = virtualMouse.getTranslationX();
                pt[1] = virtualMouse.getTranslationY();
//                if (mDesktopView.transformPointToDesktop(pt)) {
//                    mDesktopJni.SendMouseRBUp("", (int) pt[0], (int) pt[1],
//                            true);
//                }
            }

            @Override
            public void OnRightDown() {
                float[] pt = new float[2];
                pt[0] = virtualMouse.getTranslationX();
                pt[1] = virtualMouse.getTranslationY();
//                if (mDesktopView.transformPointToDesktop(pt)) {
//                    mDesktopJni.SendMouseRBDown("", (int) pt[0], (int) pt[1],
//                            true);
//                }
            }

            @Override
            public void OnLeftUp() {
                float[] pt = new float[2];
                pt[0] = virtualMouse.getTranslationX();
                pt[1] = virtualMouse.getTranslationY();
//                if (mDesktopView.transformPointToDesktop(pt)) {
//                    mDesktopJni.SendMouseLBUp("", (int) pt[0], (int) pt[1],
//                            true);
//                }
            }


            @Override
            public void OnLeftDown() {
                float[] pt = new float[2];
                pt[0] = virtualMouse.getTranslationX();
                pt[1] = virtualMouse.getTranslationY();
//                if (mDesktopView.transformPointToDesktop(pt)) {
//                    mDesktopJni.SendMouseLBDown("", (int) pt[0], (int) pt[1],
//                            true);
//                }
            }

            @Override
            public void OnMouseMove(float dx,float dy) {
                float[] temp = new float[2];
                temp[0] = virtualMouse.getTranslationX();
                temp[1] = virtualMouse.getTranslationY();
//                mDesktopView.transformPointToDesktop(temp);

                circleScroll.setCenterY(virtualMouse.getTranslationY()+dp2px(VirtualMouse.VIEW_SIZE/2, MainActivity.this));

                if(virtualMouse.getTranslationX()>getScreenWidth(MainActivity.this)-dp2px(VirtualMouse.VIEW_SIZE-25, MainActivity.this)){
//					if(dx>0){
                    //向右滑动，而且滑出屏幕右边
//                    mDesktopView.setStopMoveDesktop(false);
//                    mDesktopView.moveDesktopView(-10,0,(int)virtualMouse.getTranslationX(),(int)virtualMouse.getTranslationY());
                    virtualMouse.setTranslationX(virtualMouse.getTranslationX()+dx);
                    virtualMouse.setTranslationY(virtualMouse.getTranslationY()+dy);
//					}else{
//						//向左滑动，而且滑出屏幕右边
//						mouse.setTranslationX(mouse.getTranslationX()+dx);
//						mouse.setTranslationY(mouse.getTranslationY()+dy);
//					}

                }else if(virtualMouse.getTranslationX()<0){
//					if(dx<0){
                    //向左滑动，而且滑出屏幕左边
//                    mDesktopView.setStopMoveDesktop(false);
//                    mDesktopView.moveDesktopView(10,0,(int)virtualMouse.getTranslationX(),(int)virtualMouse.getTranslationY());
                    virtualMouse.setTranslationX(virtualMouse.getTranslationX()+dx);
                    virtualMouse.setTranslationY(virtualMouse.getTranslationY()+dy);
//					}else{
//						//向右滑动，而且滑出屏幕左边
//						mouse.setTranslationX(mouse.getTranslationX()+dx);
//						mouse.setTranslationY(mouse.getTranslationY()+dy);
//					}
                }else if(virtualMouse.getTranslationY()>getScreenHeight(MainActivity.this)-dp2px(VirtualMouse.VIEW_SIZE, MainActivity.this)){
//					if(dy>0){
                    //向下滑动，而且滑出屏幕下方
//                    mDesktopView.setStopMoveDesktop(false);
//                    mDesktopView.moveDesktopView(0,-10,(int)virtualMouse.getTranslationX(),(int)virtualMouse.getTranslationY());
//					}else{
                    //向上滑动，而且滑出屏幕下方
                    virtualMouse.setTranslationX(virtualMouse.getTranslationX()+dx);
                    virtualMouse.setTranslationY(virtualMouse.getTranslationY()+dy);
//					}
                }else if(virtualMouse.getTranslationY()<0){
//					if(dy<0){
                    //向上滑动，而且滑出屏幕上方
//                    mDesktopView.setStopMoveDesktop(false);
//                    mDesktopView.moveDesktopView(0,10,(int)virtualMouse.getTranslationX(),(int)virtualMouse.getTranslationY());
//					}else{
                    //向下滑动，而且滑出屏幕上方
                    virtualMouse.setTranslationX(virtualMouse.getTranslationX()+dx);
                    virtualMouse.setTranslationY(virtualMouse.getTranslationY()+dy);
//					}
                }else{
                    //左右滑动都在屏幕范围内
                    virtualMouse.setTranslationX(virtualMouse.getTranslationX()+dx);
                    //上下滑动都在屏幕范围内
                    virtualMouse.setTranslationY(virtualMouse.getTranslationY()+dy);

//                    mDesktopView.setStopMoveDesktop(true);
                }

                //发送移动事件
//                mDesktopJni.SendMouseMove("LBUTTON", (int) temp[0],
//                        (int) temp[1], true);
            }

            @Override
            public void OnCenterDoubleCLick() {
                virtualMouse.toSmallMouse();
//                mDesktopView.setStopMoveDesktop(true);
                smallMouseClickable = false;
            }
            @Override
            public void OnBottomClick() {
//                if(virtualMouse.isShown()){
//                    virtualMouse.setVisibility(View.INVISIBLE);
//                }
//                showMouseMenuPopupWindow();
            }
            @Override
            public void OnScrollUp() {
                circleScroll.setVisibility(View.INVISIBLE);
            }

            @Override
            public void OnSmallMouseClick() {
                if(smallMouseClickable){
                    virtualMouse.toBigMouse();
                }
            }

            @Override
            public void OnSmallMouseMove(float dx,float dy) {
                virtualMouse.setTranslationX(virtualMouse.getTranslationX()+dx);
                if((dy<0&&virtualMouse.getTranslationY()+dp2px(VirtualMouse.VIEW_SIZE/2-VirtualMouse.SMALL_MOUSE_SIZE/2, MainActivity.this)>0)||(dy>0&&virtualMouse.getTranslationY()<getScreenHeight(MainActivity.this)-dp2px(VirtualMouse.VIEW_SIZE/2+VirtualMouse.SMALL_MOUSE_SIZE/2+54, MainActivity.this))){
                    virtualMouse.setTranslationY(virtualMouse.getTranslationY()+dy);
                }
            }

            @Override
            public void toSide() {
                if(virtualMouse.getTranslationY()+dp2px(VirtualMouse.VIEW_SIZE/2-VirtualMouse.SMALL_MOUSE_SIZE/2, MainActivity.this)<0){
                    virtualMouse.setTranslationY(-dp2px(VirtualMouse.VIEW_SIZE/2-VirtualMouse.SMALL_MOUSE_SIZE/2, MainActivity.this));
                }else if(virtualMouse.getTranslationY()+dp2px(VirtualMouse.VIEW_SIZE/2+VirtualMouse.SMALL_MOUSE_SIZE/2, MainActivity.this)>getScreenHeight(MainActivity.this)){
                    virtualMouse.setTranslationY(getScreenHeight(MainActivity.this) - dp2px(VirtualMouse.VIEW_SIZE/2+VirtualMouse.SMALL_MOUSE_SIZE/2, MainActivity.this));
                }

                if (virtualMouse.getTranslationX()+dp2px(VirtualMouse.VIEW_SIZE/2, MainActivity.this) < getScreenWidth(MainActivity.this) / 2) {
                    ObjectAnimator oa = new ObjectAnimator().ofFloat(virtualMouse, "translationX", dp2px(-VirtualMouse.VIEW_SIZE/2+VirtualMouse.SMALL_MOUSE_SIZE/2,MainActivity.this));
                    oa.setDuration(500);
                    oa.addListener(aListener);
                    oa.start();
                } else {
                    ObjectAnimator oa = new ObjectAnimator().ofFloat(virtualMouse, "translationX", getScreenWidth(MainActivity.this)-dp2px(VirtualMouse.VIEW_SIZE/2+VirtualMouse.SMALL_MOUSE_SIZE/2, MainActivity.this));;
                    oa.setDuration(500);
                    oa.addListener(aListener);
                    oa.start();
                }
            }

            @Override
            public void toCenter() {
                if(virtualMouse.getTranslationX()<getScreenWidth(MainActivity.this) / 2){
                    ObjectAnimator.ofFloat(virtualMouse, "translationX",0).setDuration(400).start();
                }else{
                    ObjectAnimator.ofFloat(virtualMouse, "translationX",getScreenWidth(MainActivity.this)-dp2px(VirtualMouse.VIEW_SIZE, MainActivity.this)).setDuration(400).start();
                }

                if(virtualMouse.getTranslationY()<0){
                    ObjectAnimator.ofFloat(virtualMouse, "translationY",0).setDuration(400).start();
                }else if(virtualMouse.getTranslationY()>getScreenHeight(MainActivity.this)-dp2px(VirtualMouse.VIEW_SIZE, MainActivity.this)){
                    ObjectAnimator.ofFloat(virtualMouse, "translationY",getScreenHeight(MainActivity.this)-dp2px(VirtualMouse.VIEW_SIZE, MainActivity.this)).setDuration(400).start();
                }
            }
        });

        virtualMouse.setTranslationX(getScreenWidth(MainActivity.this)/2-dp2px(VirtualMouse.VIEW_SIZE/2, MainActivity.this));
        virtualMouse.setTranslationY(getScreenHeight(MainActivity.this)/2-dp2px(VirtualMouse.VIEW_SIZE/2, MainActivity.this));
    }
    /**
     * dp转px
     * @param dp
     * @param context
     * @return
     */
    public static int dp2px(int dp,Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }
    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }
}
