package fr.xebia.xebicon.core.utils;

import java.util.List;

public class SqlUtils {

    private SqlUtils() {

    }

    public static String toSqlArray(List<String> availableTalksIds) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String talkId : availableTalksIds) {
            stringBuilder.append("'").append(talkId).append("'").append(" ,");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 2);
    }
}
