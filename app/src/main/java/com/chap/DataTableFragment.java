package com.chap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;


public class DataTableFragment extends Fragment{

    private DatabaseHelper myDB;
    String currentTable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDB = new DatabaseHelper(getActivity());
        currentTable = getArguments().getString("currentTable");

     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_table, container, false);
       return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        final TableRow tableHeader = new TableRow(getActivity());

        tableHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        String[] headerTxt = myDB.getColumnHeaders(currentTable);

        //Remove _id column
        for (int i = 0; i < headerTxt.length; i++)
        {
            if (headerTxt[i].equals("_id"))
            {
                headerTxt[i] = null;
                break;
            }
        }


        // Reference to TableLayout
        TableLayout tableHeaderLayout=(TableLayout) getView().findViewById(R.id.tbl_header);
        TableLayout tableLayout=(TableLayout) getView().findViewById(R.id.data_table_layout);


        //Add data from database
        SQLiteDatabase database = myDB.getReadableDatabase();

        database.beginTransaction();

        try{
            String query = "SELECT * FROM " + currentTable;
            Cursor cursor = database.rawQuery(query, null);
            if(cursor.getCount() > 0)
            {
                while (cursor.moveToNext()) {

                    ArrayList<String> colText = new ArrayList<String>();
                    String[] columnNames = headerTxt;
                            //myDB.getColumnHeaders(currentTable);

                    for(String element : columnNames) {
                        if(element !=null && !element.equals("")) {
                            colText.add(cursor.getString(cursor.getColumnIndex(element)));
                        }
                    }

                    // data rows
                    TableRow row = new TableRow(getActivity());
                    row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    for(String text:colText) {
                        TextView tv = new TextView(getActivity());
                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(16);
                        tv.setPadding(5, 5, 5, 5);
                        tv.setBackground(getResources().getDrawable(R.drawable.table_view_backgrd));
                        tv.setText(text);
                        row.addView(tv);
                    }
                    tableLayout.addView(row);

                }

            }
            database.setTransactionSuccessful();

        }
        catch (SQLiteException e)
        {
            e.printStackTrace();

        }
        finally
        {
            database.endTransaction();
            // End the transaction.
            database.close();
            // Close database
        }


        final TableRow firstRow = (TableRow)tableLayout.getChildAt(0);
        int i = 0;
        for(String c:headerTxt){
            if(c !=null && !c.equals("")) {
                TextView txtView = new TextView(getActivity());
                txtView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                txtView.setGravity(Gravity.CENTER);
                txtView.setTextSize(18);
                txtView.setPadding(5, 5, 5, 5);
                txtView.setText(c);
                txtView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                txtView.setBackground(getResources().getDrawable(R.drawable.table_view_backgrd));
                tableHeader.addView(txtView);

                final int finalI = i;
                tableLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        tableHeader.getChildAt(finalI).setLayoutParams(new TableRow.LayoutParams(firstRow.getChildAt(finalI).getMeasuredWidth(),
                                firstRow.getChildAt(finalI).getMeasuredHeight()));
                    }
                });
                i++;
            }
        }

        tableHeaderLayout.addView(tableHeader);

    }


}






