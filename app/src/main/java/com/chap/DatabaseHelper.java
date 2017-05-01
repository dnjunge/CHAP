//Danson Njunge
//4/27/2017
// Helper class, extends SQLiteAssetHelper which extends SQLiteOpenHelper. Provides simple means of moving the pre-populated
//countyHealthData database from the assets folder.
package com.chap;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;

/**
 * Created by DNJUNGE on 4/27/2017.
 */

public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "countyHealthData.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Method to get list of table names in the database return as arraylist
    public ArrayList getTableNames(){
        SQLiteDatabase countyDB = getReadableDatabase();
        SQLiteQueryBuilder queryDB = new SQLiteQueryBuilder();

        ArrayList<String> tblNames = new ArrayList<String>();
        Cursor c = countyDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        try{
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    tblNames.add( c.getString( c.getColumnIndex("name")) );
                    c.moveToNext();
                }
            }
        }
        finally {
            c.close();
        }

        return tblNames;
    }

    //Method to get all entries in a column and return as arraylist
    public ArrayList getEntriesInCol(String column, String table){

        String query;
        SQLiteDatabase countyDB = getReadableDatabase();
        SQLiteQueryBuilder queryDB = new SQLiteQueryBuilder();

        ArrayList<String> colEntries = new ArrayList<String>();
        query = String.format("SELECT %s FROM %s", column, table);
        Cursor c = countyDB.rawQuery(query, null);

        try{
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    colEntries.add( c.getString( c.getColumnIndex(column)) );
                    c.moveToNext();
                }
            }
        }
        finally {
            c.close();
        }

        return colEntries;
    }

    //Method to get all column headers for a table
    public String[] getColumnHeaders(String table){

        String query;
        String[] colHeaders;
        SQLiteDatabase countyDB = getReadableDatabase();
        SQLiteQueryBuilder queryDB = new SQLiteQueryBuilder();

        query = String.format("SELECT * FROM %s WHERE 0", table);
        Cursor c = countyDB.rawQuery(query, null);

        try{
            colHeaders = c.getColumnNames();
        }
        finally {
            c.close();
        }

        return colHeaders;

    }

    public String getChartType(String table){
        String query;
        String chartType = null;
        String column ="ChartType";
        SQLiteDatabase countyDB = getReadableDatabase();
        SQLiteQueryBuilder queryDB = new SQLiteQueryBuilder();
        table = String.format("'%s'", table);
        query = String.format("SELECT Chart_Type FROM Chart_Type WHERE Ref_Table = %s", table);
        Cursor c = countyDB.rawQuery(query, null);

        try{
                c.moveToFirst();
                chartType = ( c.getString(0) );
        }
        finally {
            c.close();
        }
        return chartType;
    }


}
