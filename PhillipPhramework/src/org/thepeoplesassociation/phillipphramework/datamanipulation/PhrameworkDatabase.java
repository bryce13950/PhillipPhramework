package org.thepeoplesassociation.phillipphramework.datamanipulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.thepeoplesassociation.phillipphramework.PhrameworkApplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * This class is designed to help with the implementation of 
 * databases inside of an android application
 */
public class PhrameworkDatabase{
	
	public SQLiteDatabase DB;

	public PhrameworkDatabase(PhrameworkApplication ma){
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
	

	
	/**
	 * calls the other update method in this class
	 * @param table the table we want to update
	 * @param id the id of the row in the table that we want to update
	 * @param column the column that we want to update
	 * @param value the value we want to update the column with
	 */
	public int update(String table,String whereValue,String column,String value){
		return update(table,whereValue,new String[]{column},new String[]{value});
	}
	
	public int update(String table,String whereValue,String[] column,String[] value){
		return update(table,"id",whereValue,column,value);
	}
	public int update(String table,String whereField,String whereValue,String column,String value)throws SQLException{
		return update(table,whereField,whereValue,new String[]{column},new String[]{value});
	}
		
	/**
	 * build and constructs the update values and executes the update
	 * @param table the table we want to update
	 * @param clumnId the column that will hold the id that is being passed
	 * @param id the id of the row in the table that we want to update
	 * @param columns the columns that we want to update
	 * @param values the values asociated with the columns
	 * @throws SQLException if the values and columns are not the same length then this exception will be thrown
	 * @return number of rows effected
	 */
	public int update(String table,String whereField,String whereValue,String[] columns,String[] values)throws SQLException{
		if(columns.length!=values.length){
			throw new SQLException("columns and values must be the same amount when update a field");
		}
		ContentValues updateVal=new ContentValues();
		for(int i=0;i<values.length;i++){
			updateVal.put(columns[i],values[i]);
		}
		return DB.update(table, updateVal, whereField+"= ?", new String[]{whereValue});
	}
	
	public long insert(String table,String column,String value){
		return insert(table,new String[]{column},new String[]{value});
	}
	/**
	 * this method builds and executes insert statements
	 * @param table = the table you are trying to insert into
	 * 
	 * @param columns = the columns that you have values to insert into
	 * 
	 * @param values = the values for the columns
	 */
	public long insert(String table, String[] columns, String[] values){
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
		private final PhrameworkApplication MA;
		public Helper(PhrameworkApplication ma,String name,int version,List<DatabaseTable> tables){ 
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

