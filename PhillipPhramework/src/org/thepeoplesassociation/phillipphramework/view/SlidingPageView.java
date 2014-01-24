package org.thepeoplesassociation.phillipphramework.view;

import org.thepeoplesassociation.phillipphramework.PhrameworkApplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;


public class SlidingPageView extends ViewGroup{
	
	public static final int PAGE_MAIN = 0;
	public static final int PAGE_MENU = 1;
	
	private int menuPos;
	private int mainVisible;
	private int currentPage;
	private int halfScreen;
	
	private MotionEvent firstEvent;
	private boolean passed=false;
	private boolean down,finished=true;
	private int currentX;

	private int widthSpec,heightSpec;

    static float sViscousFluidScale;
    static float sViscousFluidNormalize;
    static{
        sViscousFluidScale = 8.0f;
        sViscousFluidNormalize = 1.0f;
        sViscousFluidNormalize = 1.0f / viscousFluid(1.0f);
    }
    
	//used for scrolling
	private int mStartX;
    private int mFinalX;
    private long mStartTime;
    private int mDuration;
    private float mDurationReciprocal;
    private float mDeltaX;
    
	public SlidingPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setCurrentItem(int i){
		int finalX = (i == PAGE_MAIN) ? -getScrollX() : (menuPos-getScrollX());
		PhrameworkApplication.logDebug("finalX"+finalX);
		startScroll(getScrollX(), finalX,500);
		currentPage = i;
	}

    
    private void startScroll(int startX, int dx,  int duration) {
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
        }
        scrollTo(currentX, 0); 
        postInvalidate();
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
	
	public void setPages(Context ctx, int[] pageIds){
		for(int page : pageIds)
			LayoutInflater.from(ctx).inflate(page, this);
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));

        widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
        heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() -
                getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);

        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.measure(widthSpec, heightSpec);
                child.requestLayout();
            }
        }
		menuPos=(int) (getWidth()*.85);
		mainVisible=(int) (getWidth()*.15);
		halfScreen=(int) (getWidth()*.5);
		currentX=0;
		setCurrentItem(currentPage);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
    	addViewInLayout(child, index, params);
    	child.measure(widthSpec, heightSpec);
    }
	@Override
	public boolean onInterceptTouchEvent(MotionEvent evt){
		if(currentPage==PAGE_MAIN)return false;
		passed=false;
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev){
		
		
		if(currentPage==PAGE_MAIN)return true;
		if(firstEvent==null){
			firstEvent=ev;
		}
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(ev.getX()>mainVisible){
				return false;
			}
			down=true;
			firstEvent=ev;
			break;
		case MotionEvent.ACTION_MOVE:
			
			currentX=-((int) ev.getX()-menuPos);
			if(currentX<0)currentX=0;
			else if(currentX>menuPos)currentX=menuPos;
			this.scrollTo(currentX, 0);
			if(ev.getX()>mainVisible)passed=true;
			break;
		case MotionEvent.ACTION_UP:
			down=false;
			int firstX=(int) firstEvent.getX();
			firstEvent=null;
			
			//check to see if the user simply pressed the main screen
			if(firstX<mainVisible&&ev.getX()<mainVisible){
				if(passed){
					setCurrentItem(PAGE_MENU);
				}
				else{
					setCurrentItem(PAGE_MAIN);
				}
			}
			else if(ev.getX()<=halfScreen){
				setCurrentItem(PAGE_MENU);
			}
			else{
				setCurrentItem(PAGE_MAIN);
			}
			break;
		}
		return true;
	}
	
	public int getCurrentItem(){
		return currentPage;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int loff=0;
            if(i==1)loff = getWidth();
            int childLeft = getPaddingLeft() +loff;
            int childTop = getPaddingTop();
            child.layout(childLeft, childTop,
            		childLeft + child.getMeasuredWidth(),
            		childTop + child.getMeasuredHeight());
        }
	}
}
