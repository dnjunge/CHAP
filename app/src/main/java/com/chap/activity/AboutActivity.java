package com.chap.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.chap.R;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about);

        Element versionElement = new Element();
        versionElement.setTitle("Version 2.1");


        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("This app was developed by Danson Njunge. It provides easy access to data on the state of population health within all of Kenya's counties. " +
                        "All data is obtained from the Humanitarian Data Exchange (HDX). The App will be updated periodically to add new data sets and features.")
                .addItem(versionElement)
                .addGroup("Connect with me")
                .addEmail("dnjunge@gmail.com")
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }
    private Element createCopyright(){
       final Element copyright = new Element();
       final String copyrightString = String.format("Copyright %d by Danson Njunge", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setIconDrawable(R.mipmap.ic_launcher);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(AboutActivity.this, copyrightString, Toast.LENGTH_SHORT).show();
            }
        });
                return copyright;

    }

}
