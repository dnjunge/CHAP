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
                    String[] columnNames = myDB.getColumnHeaders(currentTable);

                    for(String element : columnNames) {
                        colText.add(cursor.getString(cursor.getColumnIndex(element)));
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

            TextView txtView = new TextView(getActivity());
            txtView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            txtView.setGravity(Gravity.CENTER);
            txtView.setTextSize(18);
            txtView.setPadding(5,5,5,5);
            txtView.setText(c);
            tableHeader.addView(txtView);

            final int finalI = i;
            tableLayout.post(new Runnable(){
                @Override
                public void run(){
                    tableHeader.getChildAt(finalI).setLayoutParams(new TableRow.LayoutParams(firstRow.getChildAt(finalI).getMeasuredWidth(),
                            firstRow.getChildAt(finalI).getMeasuredHeight()));
                }
            });
            i++;
        }

        tableHeaderLayout.addView(tableHeader);

    }


}








/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DataTableFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DataTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
/*
public class DataTableFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DataTableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataTableFragment.
     */
    // TODO: Rename and change types and number of parameters
/*
    public static DataTableFragment newInstance(String param1, String param2) {
        DataTableFragment fragment = new DataTableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }





    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
} */
