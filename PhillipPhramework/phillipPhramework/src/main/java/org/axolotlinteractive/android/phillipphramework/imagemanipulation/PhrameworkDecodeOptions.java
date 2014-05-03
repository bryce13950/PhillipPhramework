package org.axolotlinteractive.android.phillipphramework.imagemanipulation;

import org.axolotlinteractive.android.phillipphramework.error.PhrameworkException;

import android.graphics.BitmapFactory;

class PhrameworkDecodeOptions {

	/**
	 * gets a BitmapFactory.Options that will only decode the real bounds of a bitmap
	 * @return the options with inJustDecodeBounds set to true
	 */
	static final BitmapFactory.Options getBoundsOptions(){
		BitmapFactory.Options boundsOptions=new BitmapFactory.Options();
    	boundsOptions.inJustDecodeBounds=true;
    	return boundsOptions;
	}
	/**
	 * gets a BitmapFactory.Options that will scale a raw image
	 * @param boundsOptions the BitmapFactory.Options that already have the bounds of raw image
	 * @param width the target width of the final Bitmap
	 * @param height the target height of the final Bitmap
	 * @return the options with inSampleSize set to the scale
	 * @throws org.axolotlinteractive.android.phillipphramework.error.PhrameworkException if the bounds have not already been calculated or either the width and height are less then zero
	 */
	static final BitmapFactory.Options getScaledOptions(BitmapFactory.Options boundsOptions, int width,int height){
		if(boundsOptions.outHeight<=0||boundsOptions.outWidth<=0){
			throw new PhrameworkException("Please decode the bounds of the raw image before you attempt to get the scaled options");
		}
		if(width<=0){
			throw new PhrameworkException("Please pass in a width that is greater then 0");
		}
		if(height<=0){
			throw new PhrameworkException("Please pass in a height that is greater then 0");
		}
		int scale=1;
		while(boundsOptions.outWidth/scale/2>=width||boundsOptions.outHeight/scale/2>=height){
			scale*=2;
		}
		BitmapFactory.Options scaleOptions=new BitmapFactory.Options();
		scaleOptions.inSampleSize=scale;
		return scaleOptions;
	}
	/**
	 * gets a BitmapFactory.Options that will scale a bitmap to the given size
	 * @param width the target width of the final bitmap
	 * @param height the target height of the final bitmap
	 * @return the BitmapFactory.Options with the out height and width set
	 */
	static final BitmapFactory.Options getSizedOptions(int width,int height){
		if(width<=0||height<=0)return null;
		BitmapFactory.Options sizedOptions=new BitmapFactory.Options();
		sizedOptions.outWidth=width;
		sizedOptions.outHeight=height;
		return sizedOptions;
	}
}
