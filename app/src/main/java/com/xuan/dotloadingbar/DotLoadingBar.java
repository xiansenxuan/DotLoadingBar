package com.xuan.dotloadingbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * com.xuan.dotloadingbar
 *
 * @author by xuan on 2018/6/4
 * @version [版本号, 2018/6/4]
 * @update by xuan on 2018/6/4
 * @descript
 */
public class DotLoadingBar extends RelativeLayout {

    private CircleView leftView,rightView,middleView;

    private float translationDistance=60f;
    private int duration=500;

    private boolean isStopAnim=false;
    private boolean isExecutingAnim=false;

    public DotLoadingBar(Context context) {
        this(context,null);
    }

    public DotLoadingBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DotLoadingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
        setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));
    }

    private void initView() {
        //创建3个圆点重叠在界面中间 然后后续动画平移其中2个
        leftView=getCircleView();
        leftView.exchangeColor(R.color.holo_red_light);

        rightView=getCircleView();
        rightView.exchangeColor(R.color.holo_blue_light);

        middleView=getCircleView();
        middleView.exchangeColor(R.color.holo_green_light);

        addView(leftView);
        addView(rightView);
        addView(middleView);

        post(new Runnable() {
            @Override
            public void run() {
                isStopAnim=false;
                //布局实例化之后开启动画
                executeAnim();
            }
        });
    }

    private void executeAnim() {
        if(isStopAnim || isExecutingAnim)return;

        ObjectAnimator leftTranslationXAnim=ObjectAnimator.ofFloat(
                leftView,"TranslationX",0,-translationDistance);

        ObjectAnimator rightTranslationXAnim=ObjectAnimator.ofFloat(
                rightView,"TranslationX",0,translationDistance);

        AnimatorSet animSet=new AnimatorSet();
        animSet.playTogether(leftTranslationXAnim,rightTranslationXAnim);
        animSet.setDuration(duration);
        animSet.setInterpolator(new DecelerateInterpolator());

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isExecutingAnim=false;

                executeBackAnim();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isExecutingAnim=true;
            }
        });

        animSet.start();
    }

    private void executeBackAnim() {
        if(isStopAnim || isExecutingAnim)return;

        ObjectAnimator leftTranslationXAnim=ObjectAnimator.ofFloat(
                leftView,"TranslationX",-translationDistance,0);

        ObjectAnimator rightTranslationXAnim=ObjectAnimator.ofFloat(
                rightView,"TranslationX",translationDistance,0);

        AnimatorSet animSet=new AnimatorSet();
        animSet.playTogether(leftTranslationXAnim,rightTranslationXAnim);
        animSet.setDuration(duration);
        animSet.setInterpolator(new AccelerateInterpolator());

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isExecutingAnim=false;

                exchangeColor();

                executeAnim();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isExecutingAnim=true;
            }
        });

        animSet.start();
    }

    @Override
    public void setVisibility(int visibility) {

        if(visibility==VISIBLE){
            super.setVisibility(visibility);

            resetAnim();
        }else{
            super.setVisibility(INVISIBLE);//避免重新绘制 摆放等流程

            clearAnim();
        }
    }

    private void resetAnim(){
        //重启开启需要先清除之前的动画
        clearAnim();

        //重新添加布局
        initView();

        isStopAnim=false;
        post(new Runnable() {
            @Override
            public void run() {
                executeAnim();
            }
        });
    }

    private void clearAnim(){
        //清除动画
        leftView.clearAnimation();
        middleView.clearAnimation();
        rightView.clearAnimation();
        //移除loadingView
        if(getParent()!=null){
            //从父布局移除自己 (如果需要清除这个界面内存需要这一行代码 目前为了测试 暂时取消)
//                ((ViewGroup)getParent()).removeView(this);
            //移除自己的子布局
            removeAllViews();
        }

        isStopAnim=true;
    }


    private void exchangeColor(){
        int leftColor=leftView.getCircleColor();
        int middleColor=middleView.getCircleColor();
        int rightColor=rightView.getCircleColor();
        //修改颜色
        middleView.exchangeColor(leftColor);
        rightView.exchangeColor(middleColor);
        leftView.exchangeColor(rightColor);
    }

    public CircleView getCircleView() {
        CircleView circleView=new CircleView(getContext());
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(dipFormPx(12),dipFormPx(12));
        params.addRule(CENTER_IN_PARENT);
        circleView.setLayoutParams(params);
        return circleView;
    }

    private int dipFormPx(int dip){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,getResources().getDisplayMetrics());
    }
}
