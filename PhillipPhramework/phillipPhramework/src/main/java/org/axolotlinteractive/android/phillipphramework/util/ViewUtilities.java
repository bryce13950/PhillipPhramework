package org.axolotlinteractive.android.phillipphramework.util;

import android.view.View;

public class ViewUtilities {

	public static int getRelativeTop(View myView) {
	    if (myView.getParent() == myView.getRootView())
	        return myView.getTop();
	    else
	        return myView.getTop() + getRelativeTop((View) myView.getParent());
	}
}
