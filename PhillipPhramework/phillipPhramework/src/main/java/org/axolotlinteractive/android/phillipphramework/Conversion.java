package org.axolotlinteractive.android.phillipphramework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brycemeyer on 5/12/14.
 */
public class Conversion
{
	public static List<Map<String, ?>> makeHashMapListGeneric(List<HashMap<String, String>> rows)
	{
		List<Map<String, ?>> genericList = new ArrayList<Map<String, ?>>();
		for(HashMap<String, String> map : rows)
		{
			genericList.add(map);
		}
		return genericList;
	}
}
