package org.thepeoplesassociation.phillipphramework.imagemanipulation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.thepeoplesassociation.phillipphramework.PhrameworkApplication;
import org.thepeoplesassociation.phillipphramework.error.PhrameworkException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;


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
	/**
	 * get a Bitmap from a remote server at its size on the server
	 * @param url the remote url of the image
	 * @return the decoded Bitmap
	 * @throws ClientProtocolException 
	 * @throws IOException
	 */
	public static final Bitmap createFromWebAddress(String url) throws ClientProtocolException, IOException{
		return createScaledFromWebAddress(url,-1,-1);
	}
	/**
	 * get a Bitmap from a remote server at a target width and height
	 * @param url the remote url of the image
	 * @param width the target width of the image that we want
	 * @param height the target height of the image that we want
	 * @return the decoded Bitmap
	 * @throws ClientProtocolException 
	 * @throws IOException
	 */
	public static final Bitmap createScaledFromWebAddress(String url,int width, int height) throws ClientProtocolException, IOException{
		DefaultHttpClient httpClient;
		httpClient=new DefaultHttpClient();
		HttpGet request=new HttpGet(url);
		HttpResponse response=httpClient.execute(request);
		InputStream stream=response.getEntity().getContent();
		return BitmapFactory.decodeStream(stream,null,PhrameworkDecodeOptions.getSizedOptions(width, height));
	}
	/**
	 * saves a remote image to the specified localFolder with a timestamp as the image name
	 * @param url the url of the remote image
	 * @param width the target width of the out image
	 * @param height the target height of the out image
	 * @param localFolder the local folder to save to
	 * @return the final location of the file on the local device
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static final String saveToSd(String url,int width,int height,String localFolder) throws ClientProtocolException, IOException{
		Bitmap image=createScaledFromWebAddress(url,width,height);
		File saveToFolder=new File(Environment.getExternalStorageDirectory()+localFolder);
		saveToFolder.mkdirs();
		
		String imagePath=Long.toString(System.currentTimeMillis());
		String extension=null;
		CompressFormat format=null;
		if(url.endsWith("jpg")||url.endsWith("JPG")||url.endsWith("jpeg")){
			extension=".jpg";
			format=CompressFormat.JPEG;
		}
		else if(url.endsWith("png")||url.endsWith("PNG")||url.endsWith("gif")){
			extension=".png";
			format=CompressFormat.PNG;
		}
		else{
			throw new PhrameworkException("The remote image you are trying to save must be a jpg, png, or gif");
		}
		File savedImage=new File(saveToFolder,imagePath+extension);
		return saveImage(image,savedImage,format, true);
	}
	/**
	 * saves a remote image to the specified localFolder with a timestamp as the image name
	 * @param url the url of the remote image
	 * @param localFolder the local folder to save to
	 * @return the final location of the file on the local device
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static final String saveToSd(String url,String localFolder) throws ClientProtocolException, IOException{
		return saveToSd(url,-1,-1,localFolder);
	}
	/**
	 * saves a remote image to the cache with a timestamp as the image name as well as a random number
	 * @param url the url of the remote image
	 * @param width the target width of the out image
	 * @param height the target height of the out image
	 * @param ctx the application context so we can have access to its cache
	 * @return the final location of the file on the local device
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static final String saveToCache(String url,int width,int height,Context ctx) throws ClientProtocolException, IOException{
		Bitmap image=createScaledFromWebAddress(url,width,height);
		File saveToFolder=ctx.getCacheDir();
		
		String imagePath=Long.toString(System.currentTimeMillis())+(int)(Math.random()*10000);
		String extension=null;
		CompressFormat format=null;
		if(url.endsWith("jpg")||url.endsWith("JPG")||url.endsWith("jpeg")){
			extension=".jpg";
			format=CompressFormat.JPEG;
		}
		else if(url.endsWith("png")||url.endsWith("PNG")){
			extension=".png";
			format=CompressFormat.PNG;
		}
		else{
			throw new PhrameworkException("The remote image you are trying to save must be a jpg, or png");
		}
		if(image==null) return null;
		File savedImage=new File(saveToFolder,imagePath+extension);
		return saveImage(image,savedImage,format, true);
	}
	/**
	 * saves a remote image to the cache with a timestamp as the image name as well as a random number
	 * @param url the url of the remote image
	 * @param localFolder the local folder to save to
	 * @return the final location of the file on the local device
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static final String saveToCache(String url,Context ctx) throws ClientProtocolException, IOException{
		return saveToCache(url,-1,-1,ctx);
	}
	public static final String saveToCache(String url, Context ctx, CompressFormat format) throws ClientProtocolException, IOException{
		return saveToCache(url, -1, -1, ctx, format);
	}
	public static final String saveToCache(String url, int width, int height, Context ctx, CompressFormat format) throws ClientProtocolException, IOException{
		Bitmap image=createScaledFromWebAddress(url, width, height);
		File saveToFolder=ctx.getCacheDir();
		saveToFolder.mkdirs();

		String imagePath=Long.toString(System.currentTimeMillis());
		String extension=null;
		if(format == CompressFormat.JPEG)
			extension = ".jpg";
		else if(format == CompressFormat.PNG)
			extension = ".png";

		File savedImage=new File(saveToFolder,imagePath+extension);
		return saveImage(image,savedImage,format, true);
	}
	public static final String saveToCache(Bitmap image, CompressFormat format) throws IOException{
		return saveToCache(image,format, true);
	}
	
	public static final String saveToCache(Bitmap image, CompressFormat format, boolean destroy) throws IOException{

		File saveToFolder=PhrameworkApplication.instance.getCacheDir();
		saveToFolder.mkdirs();

		String imagePath=Long.toString(System.currentTimeMillis());
		String extension=null;
		if(format == CompressFormat.JPEG)
			extension = ".jpg";
		else if(format == CompressFormat.PNG)
			extension = ".png";
		
		File savedImage=new File(saveToFolder,imagePath+extension);
		return saveImage(image,savedImage,format, destroy);
	}
	
	/**
	 * saves an image
	 * @param image the bitmap we want to save
	 * @param savedImage the out file
	 * @param formatthe format of the image
	 * @return the path of the saved image
	 * @throws IOException
	 */
	public static final String saveImage(Bitmap image, File savedImage, CompressFormat format, boolean destroy) throws IOException{
		FileOutputStream out=new FileOutputStream(savedImage);
		image.compress(format, 85, out);
		out.flush();
		out.close();
		if(destroy)
			image.recycle();
		return savedImage.getAbsolutePath();
	}
}