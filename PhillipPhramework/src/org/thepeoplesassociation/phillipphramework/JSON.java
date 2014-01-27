package org.thepeoplesassociation.phillipphramework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON {

	public static JSONArray concatArray(JSONArray A,JSONArray B) throws JSONException{
		for(int i=0;i<B.length();i++){
			JSONObject b=B.getJSONObject(i);
			for(int j=0;j<A.length();j++){
				JSONObject a=A.getJSONObject(j);
				if(a.getString("id").equals(b.getString("id"))){
					break;
				}
				if(j==A.length()-1){
					A.put(b);
				}
			}
		}
		return A;
	}
	
	public static JSONObject concatObjects(JSONObject A,JSONObject B)throws JSONException{
		JSONArray keys=B.names();
		for(int i=0;i<keys.length();i++){
			A.put(keys.getString(i),B.get(keys.getString(i)));
		}
		return A;
	}
	
	public static String listMapToJSONString(List<HashMap<String, String>> list){
	    JSONArray json_arr=new JSONArray();
	    for (Map<String, String> map : list) {
	        JSONObject json_obj=new JSONObject();
	        for (Map.Entry<String, String> entry : map.entrySet()) {
	            String key = entry.getKey();
	            Object value = entry.getValue();
	            try {
	                json_obj.put(key,value);
	            } catch (JSONException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }                           
	        }
	        json_arr.put(json_obj);
	    }
	    return json_arr.toString();
	}
	
	public static String mapToJSONString(Map<String, ?> map){
        JSONObject json_obj=new JSONObject();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            try {
                json_obj.put(key,value);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }                           
        }
        return json_obj.toString();
	}
}
