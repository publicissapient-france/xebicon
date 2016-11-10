package fr.xebia.xebicon.ui.schedule;

public class FilterScheduleSpinnerItem {

    boolean isHeader;
    String tag, title;
    int color;
    boolean indented;

    FilterScheduleSpinnerItem(boolean isHeader, String tag, String title, boolean indented, int color) {
        this.isHeader = isHeader;
        this.tag = tag;
        this.title = title;
        this.indented = indented;
        this.color = color;
    }
}
