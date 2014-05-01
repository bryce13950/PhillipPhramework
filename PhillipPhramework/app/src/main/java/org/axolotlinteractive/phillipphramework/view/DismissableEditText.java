package org.axolotlinteractive.phillipphramework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class DismissableEditText extends EditText {
	 
	private OnKeyboardDismissedListener listener;
	
    public DismissableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    public DismissableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public DismissableEditText(Context context) {
        super(context);
    }
    
    public void setOnKeyboardDismissedListener(OnKeyboardDismissedListener dismiss){
    	listener = dismiss;
    }
 
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && listener != null) 
            listener.onKeyboardDismissed(this);
        return super.onKeyPreIme(keyCode, event);
    }
    
    public interface OnKeyboardDismissedListener{
    	
    	public abstract void onKeyboardDismissed(DismissableEditText editText);
    }
 
}