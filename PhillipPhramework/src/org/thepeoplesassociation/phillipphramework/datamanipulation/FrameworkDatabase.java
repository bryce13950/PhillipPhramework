package org.thepeoplesassociation.phillipphramework.datamanipulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.thepeoplesassociation.phillipphramework.FrameworkApplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * This class is designed to help with the implementation of 
 * databases inside of an android application
 */
public class FrameworkDatabase{
	
	public SQLiteDatabase DB;

	public FrameworkDatabase(FrameworkApplication ma){
		Helper helper=new Helper(ma,ma.getDatabaseName(),ma.getDatabaseVersion(),ma.getAllDatabaseTables());
		DB=helper.getWritableDatabase();
	}
	/**
	 * This method returns an entire table
	 * 
	 * @param table= the table you are querying
	 * 
	 * @return the return value is the entire table in an ArrayList
	 * <br>
	 * 	every item in the List is a different row
	 * 	<br>
	 * 	each row is returned inside a HashMap
	 */
	public List<HashMap<String,String>> getTable(String table){
		return getTable(table,null,null);
	}
	public List<HashMap<String,String>> getTableWhere(String table,String whereColumn,String whereArg){
		return getTableWhere(table,new String[]{whereColumn},new String[]{whereArg});
	}
	public List<HashMap<String,String>> getTableWhere(String table,String[] whereColumns,String[] whereArgs){
		String where="";
		for(String column:whereColumns){
			where+=column+"=? ";
		}
		return getTable(table,where,whereColumns);
	}
	public	List<HashMap<String,String>> getTable(String table,String where,String[] whereArgs){
		List<HashMap<String,String>> returnValue=new ArrayList<HashMap<String,String>>();
		Cursor c=DB.query(table, null,where,whereArgs, null,null, null);
		while(c.moveToNext()){
			HashMap<String,String> data=new HashMap<String,String>();
			for(int i=0;i<c.getColumnCount();i++){
				data.put(c.getColumnName(i), c.getString(i));
			}
			returnValue.add(data);
		}
		c.close();
		return returnValue;
	}
	
	public List<HashMap<String,String>> getTableRaw(String query){
		List<HashMap<String,String>> returnValue=new ArrayList<HashMap<String,String>>();
		Cursor c = DB.rawQuery(query, null);
		while(c.moveToNext()){
			HashMap<String,String> data=new HashMap<String,String>();
			for(int i=0;i<c.getColumnCount();i++){
				data.put(c.getColumnName(i), c.getString(i));
			}
			returnValue.add(data);
		}
		c.close();
		return returnValue;
	}
	
	public String getField(String table,String field,String whereColumn,String whereValue){
		Cursor c=DB.query(table, new String[]{field}, whereColumn+"=?", new String[]{whereValue}, null, null, null);
		c.moveToFirst();
		if(c.isAfterLast())return null;
		String returnVal = c.getString(0);
		c.close();
		return returnVal;
	}
	
	
	public void update(String table,String updateField,String updateArg,String where,String whereArg){
		ContentValues values=new ContentValues();
		values.put(updateField, updateArg);
		DB.update(table, values, where+"=?", new String[]{whereArg});
	}
	
	public void update(String table, String[] updateFields, String[] updateArgs, String where, String whereArg){
		ContentValues values = new ContentValues();
		for(int i = 0; i < updateFields.length; i++){
			values.put(updateFields[i], updateArgs[i]);
		}
		DB.update(table, values, where + " = ?", new String[]{whereArg});
	}
	
	public long Insert(String table,String column,String value){
		return Insert(table,new String[]{column},new String[]{value});
	}
	/**
	 * this method builds and executes insert statements
	 * @param table = the table you are trying to insert into
	 * 
	 * @param columns = the columns that you have values to insert into
	 * 
	 * @param values = the values for the columns
	 */
	public long Insert(String table, String[] columns, String[] values){
		if(columns.length!=values.length){
			throw new SQLiteException("values and columns must be the same size");
		}
		ContentValues data=new ContentValues();
		for(int i=0;i<columns.length;i++){
			data.put(columns[i], values[i]);
		}
		return DB.insert(table, null, data);
	}
	
	public HashMap<String,String> getLastEntry(String table){
		Cursor c=DB.query( table, null, null, null, null, null, "id DESC", "1");

		while(c.moveToNext()){
			HashMap<String,String> data=new HashMap<String,String>();
			for(int i=0;i<c.getColumnCount();i++){
				data.put(c.getColumnName(i), c.getString(i));
			}
			c.close();
			return data;
		}
		c.close();
		return null;
	}
	
	public HashMap<String,String> getRow(String table, String name, String value){
		Cursor c=DB.query( table, null, name+"=?", new String[]{value}, null, null, "id DESC", "1");

		while(c.moveToNext()){
			HashMap<String,String> data=new HashMap<String,String>();
			for(int i=0;i<c.getColumnCount();i++){
				data.put(c.getColumnName(i), c.getString(i));
			}
			c.close();
			return data;
		}
		c.close();
		return null;
	}
	
	public boolean delete(String table, String column, String value){
		return DB.delete(table, column+"=?", new String[]{value}) > 0;
	}
	
	private static final class Helper extends SQLiteOpenHelper{

		private final List<DatabaseTable> Tables;
		private final FrameworkApplication MA;
		public Helper(FrameworkApplication ma,String name,int version,List<DatabaseTable> tables){ 
			super(ma,name,null,version);
			Tables=tables;
			MA=ma;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			for(DatabaseTable Table: Tables){
				Table.createTable(db);
			}
			MA.setFirstCreate(true);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onCreate(db);
		}
	}
}

