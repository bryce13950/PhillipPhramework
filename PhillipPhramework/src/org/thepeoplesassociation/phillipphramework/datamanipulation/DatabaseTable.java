package org.thepeoplesassociation.phillipphramework.datamanipulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thepeoplesassociation.phillipphramework.PhrameworkApplication;
import org.thepeoplesassociation.phillipphramework.error.PhrameworkException;

import android.database.sqlite.SQLiteDatabase;


/**
 * @author Bryce Meyer, bryce13950@gmail.com
 *
 * This class stores the structure of a database table<br>
 * it also handles the creation of the database
 */
public class DatabaseTable {

	private String Name;
	private String[] Columns,Structures;
	private boolean isAssoc;
	/**
	 * INTEGER AUTO INCRMENT = the primary key of a row, if the table is associative this will not be generated
	 */
	public static final String COLUMN_ID = "id";
	/**
	 * INTEGER = the primary key of this entry on the server, this needs to be passed into its constructor if this field is nesscessary
	 */
	public static final String COLUMN_SERVER = "server_id";
	/**
	 * @param name the name of the Database Table
	 * @param columns the name of the columns in the database
	 * @param structures the structure of the columns in the database
	 */
	public DatabaseTable(String name, String[] columns, String[] structures, boolean assoc){
		Name=name;
		isAssoc = assoc;
		if(Name==null){
			throw new PhrameworkException("Error while creating Database Table you must specify the name of the table");
		}
		Columns=columns;
		Structures=structures;
		if(Columns.length!=Structures.length){
			throw new PhrameworkException("Error while creating Database Table "+Name+" you must pass the same amount of structures as you are passing for columns");
		}
	}
	/**
	 * creates the table in the database
	 * @param db the database that we are going to create the table in
	 */
	public void createTable(SQLiteDatabase db){
		String sql="CREATE TABLE `"+Name+"`(";
		if(!isAssoc)
			sql+="`id` INTEGER PRIMARY KEY, `";
		else
			sql+="`";
		for(int i=0;i<Columns.length;i++){
			sql+=Columns[i];
			sql+="` ";
			sql+=Structures[i];
			if(i!=Columns.length-1)sql+=", `";
		}
		sql+=");";
		db.execSQL(sql);
	}
	/**
	 * Gets a HashMap of the local ids of this table with the server id as the key
	 */
	public static HashMap<String, String> getLocalIds(String tableName){

		String select;
		select = "SELECT `"+COLUMN_ID+"`, `"+COLUMN_SERVER+"` ";
		select+= "FROM `"+tableName+"` ";
		select+= "WHERE `"+COLUMN_SERVER+"` IS NOT NULL";
		
		List<HashMap<String, String>> rows = PhrameworkApplication.instance.getDatabase().getTableRaw(select);
		
		HashMap<String, String> results = new HashMap<String, String>();
		
		for(HashMap<String, String> row : rows){
			results.put(row.get(COLUMN_SERVER), row.get(COLUMN_ID));
		}
		
		return results;
	}
	
	public void applicationUpdated(int previousVersion){
		PhrameworkApplication.logDebug("tableUpdated" + Name);
		HashMap<String, String> newColumns = getNewColumns(previousVersion);
		if(newColumns != null){
			for(Map.Entry<String, String> entry : newColumns.entrySet()){
				String alter;
				alter = "ALTER TABLE `";
				alter+= Name;
				alter+= "` ADD COLUMN `";
				alter+= entry.getKey();
				alter+= "` ";
				alter+= entry.getValue();
				PhrameworkApplication.getInstance().getDatabase().DB.execSQL(alter);
			}
		}
	}
	/**
	 * gets any new columns that were added in a update override this if there are new columns in a table
	 * @param previousVersion the version we are updating from
	 * @return returns a list of new columns with the key being the name of the column, and value being the structure of the column
	 */
	protected HashMap<String, String> getNewColumns(int previousVersion){
		return null;
	}
	
	public static String getInValues(List<HashMap<String,String>> rows, String key){
		String in = "(";
		for(HashMap<String, String> data : rows)
			if(!in.contains(","+data.get(key)+",")
					&& !in.contains("("+data.get(key)+","))
				in+= data.get(key)+",";
		if(!in.equals("("))
			in = in.substring(0, in.length() - 1);
		return in+")";
	}
}
