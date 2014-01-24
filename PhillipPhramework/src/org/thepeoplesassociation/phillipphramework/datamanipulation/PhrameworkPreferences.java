package org.thepeoplesassociation.phillipphramework.datamanipulation;


import java.util.ArrayList;
import java.util.Map;

import org.thepeoplesassociation.phillipphramework.PhrameworkApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;


public class PhrameworkPreferences{

	private SharedPreferences prefs;
	
	public PhrameworkPreferences(PhrameworkApplication ma){
		prefs=ma.getSharedPreferences(ma.getPreferencesName(), Context.MODE_PRIVATE);
	}
	
	public void put(String k, Object o){
		ArrayList<String> keys = new ArrayList<String>();
		keys.add(k);
		ArrayList<Object> objs = new ArrayList<Object>();
		objs.add(o);
		put(keys, objs);
	}
	
	public void put(ArrayList<String> keys, ArrayList<Object> objs){
		if(keys.size()!=objs.size())throw new RuntimeException("Your must pass the same amount of keys and objects");
		SharedPreferences.Editor edit=edit();
		for(int i=0;i<keys.size();i++){
			if(objs.get(i) instanceof String){
				edit.putString(keys.get(i), (String)objs.get(i));
			}
			else if(objs.get(i) instanceof Boolean){
				edit.putBoolean(keys.get(i), (Boolean)objs.get(i));
			}
			else if(objs.get(i) instanceof Integer){
				edit.putInt(keys.get(i), (Integer)objs.get(i));
			}
			else if(objs.get(i) instanceof Float){
				edit.putFloat(keys.get(i), (Float)objs.get(i));
			}
			else if(objs.get(i) instanceof Long){
				edit.putLong(keys.get(i), (Long)objs.get(i));
			}
		}
		edit.commit();
	}
	
	public boolean contains(String key) {
		return prefs.contains(key);
	}

	public Editor edit() {
		return prefs.edit();
	}

	public Map<String, ?> getAll() {
		return null;
	}

	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	public float getFloat(String key, float defValue) {
		return prefs.getFloat(key, defValue);
	}

	public int getInt(String key,int def) {
		return prefs.getInt(key, def);
	}

	public long getLong(String key, long defValue) {
		return prefs.getLong(key, defValue);
	}

	public String getString(String key, String defValue) {
		return prefs.getString(key, defValue);
	}
	public void deletePref(String pref){
		deletePrefs(new String[]{pref});
	}
	public void deletePrefs(String[] prefs){
		Editor edit = edit();
		for(String pref: prefs){
			edit.remove(pref);
		}
		edit.commit();
	}

	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		// TODO Auto-generated method stub
		
	}
}