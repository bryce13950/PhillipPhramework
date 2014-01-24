package org.thepeoplesassociation.phillipphramework.imagemanipulation;

import org.thepeoplesassociation.phillipphramework.PhrameworkApplication;
import org.thepeoplesassociation.phillipphramework.error.PhrameworkException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class AutomaticScaledBitmap {

	public static final Bitmap createFromResource(int id, int width,int height){
		if(id==0){
			throw new PhrameworkException("Please pass in a valid resource id");
		}
		if(width==0){
			throw new PhrameworkException("Please pass in a width that is greater then 0");
		}
		if(height==0){
			throw new PhrameworkException("Please pass in a height that is greater then 0");
		}
		Resources res=PhrameworkApplication.instance.getResources();
    	BitmapFactory.Options boundsOptions=new BitmapFactory.Options();
    	boundsOptions.inJustDecodeBounds=true;
		BitmapFactory.decodeResource(res, id,boundsOptions);
		int scale=1;
		if (boundsOptions.outHeight > height || boundsOptions.outWidth > width) {

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) boundsOptions.outHeight / (float) height);
	        final int widthRatio = Math.round((float) boundsOptions.outWidth / (float) width);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        scale = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
		BitmapFactory.Options scaleOptions=new BitmapFactory.Options();
		scaleOptions.inSampleSize=scale;
    	Bitmap bm=BitmapFactory.decodeResource(res, id,scaleOptions);
		return bm;
	}
	
	public static final Bitmap createFromResourceExactSize(int id, int width, int height){
		if(id==0){
			throw new PhrameworkException("Please pass in a valid resource id");
		}
		if(width == 0 && height == 0){
			throw new PhrameworkException("Please pass in a width or height that is greater then 0");
		}
		Resources res=PhrameworkApplication.instance.getResources();
    	BitmapFactory.Options boundsOptions=new BitmapFactory.Options();
    	boundsOptions.inJustDecodeBounds=true;
		BitmapFactory.decodeResource(res, id,boundsOptions);
		if(width == 0) width = (height * boundsOptions.outWidth) / boundsOptions.outHeight;
		if(height == 0) height = (width * boundsOptions.outHeight) / boundsOptions.outWidth;
		int scale=1;
		if (boundsOptions.outHeight > height || boundsOptions.outWidth > width) {

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) boundsOptions.outHeight / (float) height);
	        final int widthRatio = Math.round((float) boundsOptions.outWidth / (float) width);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        scale = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
		BitmapFactory.Options scaleOptions=new BitmapFactory.Options();
		scaleOptions.inSampleSize=scale;
    	Bitmap src=BitmapFactory.decodeResource(res, id,scaleOptions);
		return Bitmap.createScaledBitmap(src, width, height, true);
	}
}