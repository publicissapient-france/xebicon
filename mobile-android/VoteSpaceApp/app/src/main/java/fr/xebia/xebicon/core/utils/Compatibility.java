package fr.xebia.xebicon.core.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import fr.xebia.xebicon.R;

public class Compatibility {

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public static String getLocalizedTitle(Context context, String title) {
        return context.getResources().getBoolean(R.bool.french) ? title : translateTitle(title);
    }

    private static String translateTitle(String title) {
        if (title.startsWith("Accueil")) {
            return "Registration, Welcome and Breakfast";
        } else if (title.startsWith("Pause déjeuner")) {
            return "Lunch";
        } else if (title.startsWith("Pause café")) {
            return "Coffee Break";
        } else if (title.startsWith("Pause courte")) {
            return "Short Break";
        } else if (title.startsWith("Soirée")) {
            return "Night at Noxx";
        }
        return title;
    }

    public static boolean isCompatible(int versionCode) {
        return Build.VERSION.SDK_INT >= versionCode;
    }

    public static int darker(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.7f;
        return Color.HSVToColor(hsv);
    }
}
