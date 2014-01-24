package org.thepeoplesassociation.phillipphramework.view;

import com.bryce13950.framework.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SlidingMenu extends FrameLayout{

        public static final int PAGE_MAIN=0;
        public static final int PAGE_RIGHT=1;
        public static final int PAGE_LEFT=2;

        private int leftPos,rightPos;
        private int menuMargin;
        private int currentPage;
        private int halfScreen;

        private boolean passed=false;
        private boolean down,finished=true;
        private int currentX;

        private int Width, Height;

    static float sViscousFluidScale;
    static float sViscousFluidNormalize;
    static{
        sViscousFluidScale = 8.0f;
        sViscousFluidNormalize = 1.0f;
        sViscousFluidNormalize = 1.0f / viscousFluid(1.0f);
    }

    private LinearLayout mainPage=null;
    private View leftPage=null;
    private View rightPage=null;

        //used for scrolling
        private int mStartX;
    private int mFinalX;
    private long mStartTime;
    private int mDuration;
    private float mDurationReciprocal;
    private float mDeltaX;

    private LayoutInflater inflater;

    private float visibleWidth;
    private float rightImageWidth;

    private OnPageChangedListener listener;

    /**
     * the applications context
     */
    private Context CTX;

        public SlidingMenu(Context context, AttributeSet attrs) {
                super(context, attrs);
                CTX=context;
                inflater=(LayoutInflater)CTX.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.SlidingMenu);

                visibleWidth=array.getFloat(R.styleable.SlidingMenu_visible_menu,.85f);
                rightImageWidth = array.getFloat(R.styleable.SlidingMenu_rightShadowWidth, .15f);
                loadWidth();

                int leftLayout=array.getResourceId(R.styleable.SlidingMenu_left,-1);
                int rightLayout=array.getResourceId(R.styleable.SlidingMenu_right, -1);
                setMenus(leftLayout,rightLayout);

                int mainLayout=array.getResourceId(R.styleable.SlidingMenu_main,-1);
                int rightImageSource = array.getResourceId(R.styleable.SlidingMenu_rightShadowSrc, -1);

                setMainPage(mainLayout, rightImageSource);

                int buttonRight=array.getResourceId(R.styleable.SlidingMenu_rightButton, -1);
                if(buttonRight!=-1)setRightButton(buttonRight);

                int buttonLeft=array.getResourceId(R.styleable.SlidingMenu_leftButton, -1);
                if(buttonLeft!=-1)setLeftButton(buttonLeft);
                array.recycle();
        }

        @SuppressLint("NewApi")
        @SuppressWarnings("deprecation")
        private void loadWidth(){
                WindowManager window=(WindowManager)CTX.getSystemService(Context.WINDOW_SERVICE);
                //begin api version 13 compatibility test
                if(VERSION.SDK_INT>=13){
                        Point outSize=new Point();
                        window.getDefaultDisplay().getSize(outSize);
                        Width=outSize.x;
                        Height = outSize.y;
                }
                else{
                        Width=window.getDefaultDisplay().getWidth();
                        Height=window.getDefaultDisplay().getHeight();
                }
                //end api compatibility stuff
                rightPos=(int) (Width*visibleWidth);
                leftPos=-rightPos;
                menuMargin=Width-rightPos;
                rightImageWidth *= Width;
                halfScreen=(int) (Width*.5);
                currentX=0;
        }

        public void setMenus(int left,int right){
                if(right==-1&&left==-1){
                        throw new InflateException("You must supply at least one menu to your Sliding Menu");
                }
                if(rightPage!=null){
                        removeView(rightPage);
                }
                if(leftPage!=null){
                        removeView(leftPage);
                }
                FrameLayout menuFrame=new FrameLayout(CTX);
                menuFrame.setBackgroundResource(android.R.color.transparent);
                if(right!=-1){
                        rightPage=inflater.inflate(right,null);
                        LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
                        params.setMargins(menuMargin, 0, 0, 0);
                        menuFrame.addView(rightPage,params);
                }
                if(left!=-1){
                        leftPage=inflater.inflate(left, null);
                        LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
                        params.setMargins(0, 0, menuMargin, 0);
                        menuFrame.addView(leftPage,params);
                }
                addView(menuFrame);
                if(mainPage!=null)mainPage.bringToFront();
        }

        public void setMainPage(int layout, int rightImageSource){
                if(layout==-1){
                        throw new InflateException("You must supply a main page to your sliding menu");
                }
                ViewGroup.LayoutParams params;
                if(mainPage!=null){
                        mainPage.removeAllViews();
                }
                else{
                        mainPage=new LinearLayout(CTX);
                        params = new ViewGroup.LayoutParams((int) (Width + rightImageWidth), ViewGroup.LayoutParams.MATCH_PARENT);
                        mainPage.setLayoutParams(params);
                }
                inflater.inflate(layout, mainPage);
                ViewGroup inflatedLayout = (ViewGroup) mainPage.getChildAt(mainPage.getChildCount() - 1);
                params = inflatedLayout.getLayoutParams();
                params.width = Width;
                inflatedLayout.setLayoutParams(params);
                if(rightImageSource != -1){
                        View v = new ImageView(CTX);
                        v.setMinimumHeight(Height);
                        v.setMinimumWidth((int) rightImageWidth);
                        v.setBackgroundResource(rightImageSource);
                        mainPage.addView(v);
                }
                addView(mainPage);
                mainPage.bringToFront();
        }

        public void setRightButton(int id){
                findViewById(id).setOnClickListener(new OnClickListener(){
                        public void onClick(View v){
                                if(leftPage != null)
                                        leftPage.setVisibility(View.GONE);
                                setCurrentPage(PAGE_RIGHT);
                        }
                });
        }

        public void setLeftButton(int id){
                findViewById(id).setOnClickListener(new OnClickListener(){
                        public void onClick(View v){
                                rightPage.setVisibility(View.GONE);
                                setCurrentPage(PAGE_LEFT);
                        }
                });
        }

        public void setCurrentPage(int page){
                currentPage=page;
                down = false;
                finished = false;
                switch(currentPage){
                case PAGE_MAIN:
                        startScroll(mainPage.getScrollX(),-mainPage.getScrollX(), 500);
                        break;
                case PAGE_LEFT:
                        startScroll(mainPage.getScrollX(), (leftPos-mainPage.getScrollX()),500);
                        break;
                case PAGE_RIGHT:
                        startScroll(mainPage.getScrollX(), (rightPos-mainPage.getScrollX()),500);
                        break;
                }
        }

    private void startScroll(int startX, int dx,  int duration) {
        if(currentPage==PAGE_MAIN&&listener!=null)listener.onPageChangeBegin();
                finished=false;
        mDuration = duration;
        mStartTime = AnimationUtils.currentAnimationTimeMillis();
        mStartX = startX;
        mFinalX = startX + dx;
        mDeltaX = dx;
        mDurationReciprocal = 1.0f / (float) mDuration;
        computeScroll();
    }

        @Override
    public void computeScroll() {
                if(down||finished){
                return;
        }
        int timePassed = (int)(AnimationUtils.currentAnimationTimeMillis() - mStartTime);

        if (timePassed < mDuration) {
                float x = (float)timePassed * mDurationReciprocal;
                x = viscousFluid(x);
                currentX = mStartX + Math.round(x * mDeltaX);
        }
        else {
            currentX = mFinalX;
            finished=true;
            if(currentPage==PAGE_MAIN){
                if(leftPage!=null)leftPage.setVisibility(View.VISIBLE);
                if(rightPage!=null)rightPage.setVisibility(View.VISIBLE);

                if(listener!=null)listener.onPageChangeComplete();
            }
        }
        mainPage.scrollTo(currentX, 0);
        invalidate();
        }

        static float viscousFluid(float x){
                x *= sViscousFluidScale;
                if (x < 1.0f) {
                        x -= (1.0f - (float)Math.exp(-x));
                } else {
                        float start = 0.36787944117f;
                        x = 1.0f - (float)Math.exp(1.0f - x);
                        x = start + x * (1.0f - start);
                }
                x *= sViscousFluidNormalize;
                return x;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent evt){
                if(currentPage==PAGE_MAIN)return false;
                else if(evt.getX()>menuMargin&&currentPage==PAGE_RIGHT){
                        return false;
                }
                else if(evt.getX()<rightPos&&currentPage==PAGE_LEFT){
                        return false;
                }
                passed=false;
                return true;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev){

                if(currentPage==PAGE_MAIN || !finished )return false;
                switch(ev.getAction()){
                case MotionEvent.ACTION_DOWN:
                        down=true;
                        if(ev.getX()>menuMargin&&currentPage==PAGE_RIGHT){
                                return false;
                        }
                        else if(ev.getX()<rightPos&&currentPage==PAGE_LEFT){
                                return false;
                        }
                        break;
                case MotionEvent.ACTION_MOVE:
                        switch(currentPage){
                        case PAGE_RIGHT:
                                currentX=-((int) ev.getX()-rightPos);
                                if(currentX<0)currentX=0;
                                else if(currentX>rightPos)currentX=rightPos;
                                mainPage.scrollTo(currentX, 0);
                                if(ev.getX()>menuMargin)passed=true;
                                break;
                        case PAGE_LEFT:
                                currentX=-((int) ev.getX());
                                if(currentX<leftPos)currentX=leftPos;
                                mainPage.scrollTo(currentX, 0);
                                if(ev.getX()<rightPos)passed=true;
                                break;
                        }
                        if(listener!=null)listener.onUserDrag();
                        break;
                case MotionEvent.ACTION_UP:
                        down=false;

                        //check to see if the user simply pressed the main screen
                        if(ev.getX()<menuMargin&&currentPage==PAGE_RIGHT){
                                if(passed){
                                        setCurrentPage(PAGE_RIGHT);
                                }
                                else{
                                        setCurrentPage(PAGE_MAIN);
                                }
                        }
                        else if(ev.getX()>rightPos&&currentPage==PAGE_LEFT){
                                if(passed){
                                        setCurrentPage(PAGE_LEFT);
                                }
                                else{
                                        setCurrentPage(PAGE_MAIN);
                                }
                        }
                        else if(ev.getX()<=halfScreen&&currentPage==PAGE_RIGHT){
                                setCurrentPage(PAGE_RIGHT);
                        }
                        else if(ev.getX()>=halfScreen&&currentPage==PAGE_LEFT){
                                setCurrentPage(PAGE_LEFT);
                        }
                        else{
                                setCurrentPage(PAGE_MAIN);
                        }
                        break;
                }
                return true;
        }

        public void setOnPageChangedListener(OnPageChangedListener list){
                listener=list;
        }

        public int getCurrentPage(){
                return currentPage;
        }

        public interface OnPageChangedListener{
                public abstract void onPageChangeBegin();
                public abstract void onPageChangeComplete();
                public abstract void onUserDrag();
        }
}