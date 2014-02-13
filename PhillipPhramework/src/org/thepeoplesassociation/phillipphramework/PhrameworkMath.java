package org.thepeoplesassociation.phillipphramework;

import com.google.android.gms.maps.model.LatLng;

public class PhrameworkMath {
	
	/**
	 * method that, per the pythagorean thereom, accepts two points, creates a third, and figures out the distance between the two original points<br>
	 * a^2+b^2 = c^2
	 * @param A = the first point. A's latitude will be used to create point C
	 * @param B = the second point. B's longitude will be used to create point C
	 * @return the distance between point A and point B in degrees
	 */
	public static double pythagorean(LatLng A, LatLng B){
		double a = B.longitude - A.longitude;
		double b = A.latitude - B.latitude;
		double c = Math.sqrt((a*a)+(b*b));
		return c;
	}
	
}
