package org.axolotlinteractive.android.phillipphramework.datamanipulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.axolotlinteractive.android.phillipphramework.PhrameworkApplication;
import org.axolotlinteractive.android.phillipphramework.error.PhrameworkException;

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



    /**
     * gets a row from the database
     * @param table the table we want to get the row from
     * @param column the column we want to use in a where clause
     * @param value the value we want to match
     * @return the row from the database null if the row does not exist
     */

    public HashMap<String,String> getRow(String table, String[] whereColumns, String[] whereValues){
        HashMap<String,String> map=new HashMap<String,String>();
        Cursor c;
        c=DB.query(table, null, getParamatarizedWhere(whereColumns), whereValues,null, null, null);
        String[] columns =c.getColumnNames();
        c.moveToFirst();
        if(c.isAfterLast()) return null;
        for(int i=0;i<columns.length;i++){
            map.put(columns[i], c.getString(i));
        }
        return map;
    }

    /**
     * gets a certain value from a row
     * @param table the table we want to get the row from
     * @param column the column that we have a where argument for
     * @param value the where argument
     * @param selection the column that we want
     * @return the values of the selected value at the first row or null if there is no value
     */
    public String getValue(String table,String[] columns,String[] values, String selection){
        if(columns.length!=values.length)throw new PhrameworkException("You must pass the same amount of columns as values");
        String column=getParamatarizedWhere(columns);
        Cursor c=DB.query(table, new String[]{selection}, column, values, null, null, null, "1");
        if(!c.moveToFirst())return null;
        return c.getString(0);
    }
    private String getParamatarizedWhere(String[] columns){
        String column="`";
        for(int i=0;i<columns.length;i++){
            column+=columns[i];
            column+="` = ?";
            if(i!=columns.length-1)column+=" AND `";
        }
        return column;
    }
    /**
     * turns an array into a string with parameters
     * @param columns
     * @return
     */
    private String getParameterizedWhere(String[] columns){
        String column="`";
        for(int i=0;i<columns.length;i++){
            column+=columns[i];
            column+="` = ?";
            if(i!=columns.length-1)column+=" AND `";
        }
        return column;
    }
    /**
     * gets a certain value from a row
     * @param table the table we want to get the row from
     * @param column the column that we have a where argument for
     * @param value the where argument
     * @param selection the column that we want
     * @return the values of the selected value at the first row or null if there is no value
     */
    public String getValue(String table,String column,String value,String selection){
        return getValue(table,new String[]{column},new String[]{value},selection);
    }
    public List<HashMap<String,String>> getTable(String table){
        return getTable(table,null,null);
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

    /****************************************************************************************
     * where stuff for an entire table
     ***************************************************************************************/
    /**
     * gets a table where one column is equal to a value
     * @param table the table we are querying
     * @param whereColumn the column we want to check
     * @param whereValue the value that will be in the column of the result rows
     * @return an entire list of rows in HashMap form
     */
    public List<HashMap<String,String>> getTableWhere(String table,String whereColumn,String whereValue){
        return getTableSpecialWhere(table,new String[]{whereColumn},new String[]{whereValue},null);
    }
    /**
     * gets a table where multiple columns are equal to a value
     * @param table the table we are querying
     * @param whereColumns the column we want to check
     * @param whereValues the value of those columns
     * @return an entire list of rows in HashMap form
     */
    public List<HashMap<String,String>> getTableWhere(String table,String[] whereColumns, String[] whereValues){
        return getTableSpecialWhere(table,whereColumns,whereValues,null);
    }
    /**
     * gets a table when we want to run a special query
     * @param table the table we are querying
     * @param specialWhere the special query that we cannot simply pass into an array
     * @return an entire list of rows in HashMap form
     */
    public List<HashMap<String,String>> getTableSpecialWhere(String table,String specialWhere){
        return getTableSpecialWhere(table,null,new String[]{},specialWhere);
    }
    /**
     * gets a table where one column can be easily added to a where, but we also need to run a special query
     * @param table the table we are querying
     * @param column the column that we want to get
     * @param value the value that we want to be in that column
     * @param specialWhere the special where that we need to have included in the query
     * @return an entire list of rows in HashMap form
     */
    public List<HashMap<String,String>> getTableSpecialWhere(String table,String column, String value, String specialWhere){
        return getTableSpecialWhere(table,new String[]{column},new String[]{value},specialWhere);
    }
    /**
     * gets an table where multiple columns can be easily added to a where, and where we also need to run a special query
     * @param table the table we are querying
     * @param whereColumns the columns that we want to get
     * @param whereValues the values that we want to be in the columns
     * @param specialWhere the special where that we need to have included in the query
     * @return an entire list of rows in HashMap form
     */
    public List<HashMap<String,String>> getTableSpecialWhere(String table,String[] whereColumns, String[] whereValues, String specialWhere){
        String where=null;
        if(whereColumns!=null)where=getParamatarizedWhere(whereColumns);
        if(where!=null&&specialWhere!=null)where+=" AND ";
        else if(specialWhere!=null) where="";
        if(specialWhere==null)specialWhere="";
        where+=specialWhere;
        return getTable(table,where,whereValues);
    }
    /**
     * gets an entire table from the database
     * @param table the table you want to retrieve
     * @param where optional where clause if you do not need to use a where then you must pass null
     * @param sort optional sort clause if you do not need to sort then pass null and the rows will be sorted by id
     * @return returns an ArrayList&lt;HashMap&lt;String,String&gt;&gt; that is a clone of what is in the database
     */
    public List<HashMap<String,String>> getTable(String table,String where,String[] whereColumns){
        List<HashMap<String,String>> r=new ArrayList<HashMap<String,String>>();
        Cursor c=DB.query(table, null, where, whereColumns, null, null, null);
        if(c.getCount()==0)return r;
        c.moveToFirst();
        do
        {
            HashMap<String,String> map=new HashMap<String,String>();
            String[] columns=c.getColumnNames();
            for(int i=0;i<columns.length;i++){
                map.put(columns[i], c.getString(i));
            }
            r.add(map);
        }
        while(c.moveToNext());
        return r;
    }
	
	public List<HashMap<String,String>> getTableRaw(String query){
		Cursor c = DB.rawQuery(query, null);
		
		return toList(c);
	}
	
	public String getField(String table,String field,String whereColumn,String whereValue){
		Cursor c=DB.query(table, new String[]{field}, whereColumn+"=?", new String[]{whereValue}, null, null, null);
		c.moveToFirst();
		if(c.isAfterLast())return null;
		String returnVal = c.getString(0);
		c.close();
		return returnVal;
	}

    public List<HashMap<String,String>> rawSelect(String select){
        List<HashMap<String,String>> r=new ArrayList<HashMap<String,String>>();
        Cursor c = DB.rawQuery(select, null);

        if( c.isAfterLast())return r;
        c.moveToFirst();
        do
        {
            HashMap<String,String> map=new HashMap<String,String>();
            String[] columns=c.getColumnNames();
            for(int i=0;i<columns.length;i++){
                map.put(columns[i], c.getString(i));
            }
            r.add(map);
        }
        while(c.moveToNext());
        return r;
    }

	
	/**
	 * Calls an update that updates based on the local id
	 * @param table the table we want to update
	 * @param whereValue the local id of the row in the table that we want to update
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
	 * @param whereField the field that will be
	 * @param whereValue the value of the row in the table that we want to update
	 * @param columns the columns that we want to update
	 * @param values the values asociated with the columns
	 * @throws SQLException if the values and columns are not the same length then this exception will be thrown
	 * @return number of rows effected
	 */
	public int update(String table, String whereField, String whereValue, String[] columns, String[] values) throws SQLException{
		if(columns.length!=values.length){
			throw new SQLException("columns and values must be the same amount when update a field");
		}
		ContentValues updateVal=new ContentValues();
		for(int i=0;i<values.length;i++){
			updateVal.put(columns[i],values[i]);
		}
		return DB.update(table, updateVal, whereField+"= ?", new String[]{whereValue});
	}

    public int emptyTable(String table,String whereClause,String whereArg){
        return DB.delete(table, whereClause+"= ?", new String[]{whereArg});
    }

    public void emptyTable(String table){
        DB.delete(table, null, null);
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

    public long insertWithDatetime(String table,String[] columns,String[] values,String dateField){
        long row=insert(table,columns,values);
        String sql="UPDATE "+table+" SET `"+dateField+"`=datetime('now') WHERE `id`="+row;
        DB.execSQL(sql);
        return row;
    }
	


	public List<HashMap<String,String>> toList(Cursor c){
		List<HashMap<String,String>> r=new ArrayList<HashMap<String,String>>();
		if(c.getCount()==0)return r;
		c.moveToFirst();
		do
		{
			HashMap<String,String> map=new HashMap<String,String>();
			String[] columns=c.getColumnNames();
			for(int i=0;i<columns.length;i++){
				map.put(columns[i], c.getString(i));
			}
			r.add(map);
		}
		while(c.moveToNext());
		c.close();
		return r;
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

