package com.chap.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.chap.R;
import com.chap.activity.HealthIndicatorViewActivity;
import com.mapbox.mapboxsdk.maps.MapView;

public class AnalyticsFragment extends Fragment {
    private Spinner mapSelectSpinner;
    WebView mapVIEW;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }



    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapVIEW = (WebView)view.findViewById(R.id.map_webview);
        mapVIEW.getSettings().setJavaScriptEnabled(true);
        mapVIEW.loadUrl("http://kenyachap.info/index.html");

        mapSelectSpinner = (Spinner) view.findViewById(R.id.map_indicator_spinner);
        mapSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // selectedIndicator = (String) (indicatorsList.getItemAtPosition(position));
             String  selectedIndicator = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(), selectedIndicator, Toast.LENGTH_SHORT).show();

            }

            @SuppressWarnings("unused")
            public void onClick(View v) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

            ;
        });

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

