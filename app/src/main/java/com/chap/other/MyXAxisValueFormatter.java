package com.chap.other;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

/**
 * Created by DNJUNGE on 5/1/2017.
 */

public class MyXAxisValueFormatter implements IAxisValueFormatter {

    private ArrayList<String> mValues;

    public MyXAxisValueFormatter(ArrayList values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        int i = (int)value;

        return mValues.get(i);
    }

    /** this is only needed if numbers are returned, else return 0 */

    public int getDecimalDigits() { return 0; }
}