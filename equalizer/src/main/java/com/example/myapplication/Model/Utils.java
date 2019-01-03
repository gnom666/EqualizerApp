package com.example.myapplication.Model;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static <T extends Entity> T byId(List<? extends Entity> content, long id, Class<T> type) {
        for (Entity e : content) {
            if (((T)e).id == id) return (T) e; /*if (type == Person.class) {if (((Person)e).id == id) return (T) e;}*/
        }
        Log.i("COINCIDENCE", "NOT FOUND! (still in test)");
        return null;
    }

    public static String amount2string (double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat decim = (DecimalFormat)nf;
        decim.applyPattern("0.00");
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        decim.setDecimalFormatSymbols(otherSymbols);
        decim.setDecimalSeparatorAlwaysShown(true);
        return decim.format(amount);
    }

    public static double string2amount (String amountText) {
        return Double.parseDouble(amountText.replace(",", "."));
    }
}
