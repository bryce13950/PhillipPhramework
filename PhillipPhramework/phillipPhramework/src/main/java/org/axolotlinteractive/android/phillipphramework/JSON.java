package org.axolotlinteractive.android.phillipphramework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class contains a number of json helpers that make manipulating JSON data a bit easier
 */
public class JSON 
{
	/**
	 * This method will take two JSONArray's and concatenate them one after another
	 * @param A the JSONArray that will be concatenated onto
	 * @param B the JSONArray that will be added to A
	 * @return the fully concatenated JSONArray
	 */
	public static JSONArray concatArray(JSONArray A, JSONArray B) throws JSONException
	{
		for(int i = 0; i < B.length(); i++)
		{
			JSONObject b = B.getJSONObject(i);
			for(int j = 0; j < A.length(); j++)
			{
				JSONObject a=A.getJSONObject(j);
				if(a.getString("id").equals(b.getString("id")))
				{
					break;
				}
				if(j==A.length()-1)
				{
					A.put(b);
				}
			}
		}
		return A;
	}

	/**
	 * This method will take two JSONObject's and concatenate them together
	 * Note that keys in the first object that are also in the second object will be overwritten
	 * @param A the JSONObject that the second object will be added to
	 * @param B the JSONObject that will be added the first one
	 * @return a fully concatenated JSONObject of the two objects
	 * @throws JSONException
	 */
	public static JSONObject concatObjects(JSONObject A,JSONObject B) throws JSONException
	{
		JSONArray keys=B.names();
		for(int i=0;i<keys.length();i++)
		{
			A.put(keys.getString(i),B.get(keys.getString(i)));
		}
		return A;
	}

	/**
	 * This will take a List and turn it into a JSONArray
	 * @param list the List that we are converting into a JSONArray
	 * @return a JSONArray identical to the list passed in
	 */
	public static JSONArray convertListToJSON(List<Map<String, ?>> list)
	{
		JSONArray jsonArray = new JSONArray();
		for (Map<String, ?> map : list)
		{
			jsonArray.put(convertHashMapToJSON(map));
		}
		return jsonArray;
	}

	/**
	 * This will take a Map and turn it into a JSONObject
	 * @param map the Map that we are converting into a JSONObject
	 * @return a JSONObject identical to the map passed in
	 */
	public static JSONObject convertHashMapToJSON(Map<String, ?> map)
	{
		JSONObject jsonObject = new JSONObject();
		for (Map.Entry<String, ?> entry : map.entrySet())
		{
			String key = entry.getKey();
			Object value = entry.getValue();
			try
			{
				jsonObject.put(key,value);
			}
			catch (JSONException e)
			{
				PhrameworkApplication.handleCaughtException(e, "JSON.listMapToJSONString");
			}
		}
		return jsonObject;
	}

	/**
	 * This will convert a JSONArray to a List
	 * @param array the array that we are converting
	 * @return a generic list that is identical to the array that was passed hin
	 */
	public static List<Map<String,?>> convertJSONArrayToList(JSONArray array)
	{
	    List<Map<String, ?>> list = new ArrayList<Map<String,?>>();
		for(int i=0;i<array.length();i++)
		{
    		JSONObject obj = array.optJSONObject(i);
    		Iterator<String> iter = obj.keys();
			Map<String,Object> hash = new HashMap<String,Object>();
    		while (iter.hasNext())
    		{
    			String key = iter.next();
    			Object value = obj.opt(key);
    			hash.put(key, value);
    		}
    		list.add(hash);
		}
	    return list;
	}
}
