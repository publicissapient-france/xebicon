package fr.xebia.xebicon.model;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class TrackColors {

    public static final int NO_TRACK = Color.parseColor("#ff000000");

    public final static Map<String, Integer> MAP;

    static {
        MAP = new HashMap<>();
        MAP.put("Agile", Color.parseColor("#cccac5")); // light=#D7D5D0 / dark=#c1bfbb
        MAP.put("Back", Color.parseColor("#AA2615"));
        MAP.put("Cloud", Color.parseColor("#06A99C"));
        MAP.put("Craft", Color.parseColor("#AFCD37"));
        MAP.put("Data", Color.parseColor("#DF0075"));
        MAP.put("DevOps", Color.parseColor("#F99B1D"));
        MAP.put("Front", Color.parseColor("#00A0D4"));
        MAP.put("IoT", Color.parseColor("#B26792"));
        MAP.put("Keynote", Color.parseColor("#6A205F"));
        MAP.put("Management", Color.parseColor("#00B249"));
        MAP.put("Marketing", Color.parseColor("#d5d348")); //light=#EDEB50 / dark=#d5d348
        MAP.put("Mobile", Color.parseColor("#1447D3"));
        MAP.put("UX", Color.parseColor("#B25C00"));
    }
}
