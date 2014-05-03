package org.axolotlinteractive.android.phillipphramework.datamanipulation;


import java.util.Map;

import org.axolotlinteractive.android.phillipphramework.PhrameworkApplication;
import org.axolotlinteractive.android.phillipphramework.error.PhrameworkException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;


public class PhrameworkPreferences{

	private SharedPreferences prefs;
	
	public PhrameworkPreferences(PhrameworkApplication ma){
		prefs=ma.getSharedPreferences(ma.getPreferencesName(), Context.MODE_PRIVATE);
	}
	
	public void put(String key, Object obj){
		put(new String[]{key}, new Object[] {obj});
	}
	
	public void put(String[] keys, Object[] objs){
		if(keys.length!=objs.length)
			throw new PhrameworkException("Your must pass the same amount of keys and objects");
		SharedPreferences.Editor edit=edit();
		for(int i=0;i<keys.length;i++){
			if(objs[i] instanceof String){
				edit.putString(keys[i], (String)objs[i]);
			}
			else if(objs[i] instanceof Boolean){
				edit.putBoolean(keys[i], (Boolean)objs[i]);
			}
			else if(objs[i] instanceof Integer){
				edit.putInt(keys[i], (Integer)objs[i]);
			}
			else if(objs[i] instanceof Float){
				edit.putFloat(keys[i], (Float)objs[i]);
			}
			else if(objs[i] instanceof Long){
				edit.putLong(keys[i], (Long)objs[i]);
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
		return prefs.getAll();
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
	/**
	 * clear a specific set of preferences
	 * @param prefs the array of preferences you want to delete
	 */
	public void deletePrefs(String[] prefs){
		Editor edit = edit();
		for(String pref: prefs){
			edit.remove(pref);
		}
		edit.commit();
	}
	
	public void clearPrefereneces(){
		SharedPreferences.Editor edit = prefs.edit();
		edit.clear();
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