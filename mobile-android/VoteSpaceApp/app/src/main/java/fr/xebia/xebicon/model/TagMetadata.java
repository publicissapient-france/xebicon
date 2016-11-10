package fr.xebia.xebicon.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagMetadata {

    Map<String, ArrayList<Tag>> mTagsInCategory = new HashMap<String, ArrayList<Tag>>();

    Map<String, Tag> mTagsById = new HashMap<String, Tag>();

    private TagMetadata() {

    }

    public static TagMetadata fromSchedule(Schedule schedule) {
        return new TagMetadata().loadSchedule(schedule);
    }

    private TagMetadata loadSchedule(Schedule schedule) {
        mTagsInCategory.put(Tags.CATEGORY_DAY, extractTags(schedule, schedule.getFormattedDays(), Tags.CATEGORY_DAY));
        mTagsInCategory.put(Tags.CATEGORY_TOPIC, extractTags(schedule, schedule.getAvailableTopics(), Tags.CATEGORY_TOPIC));
        mTagsInCategory.put(Tags.CATEGORY_TYPE, extractTags(schedule, schedule.getAvailableTypes(), Tags.CATEGORY_TYPE));
        return this;
    }

    private ArrayList<Tag> extractTags(Schedule schedule, List<String> availableTags, String category) {
        ArrayList<Tag> tags = new ArrayList<>();
        for (int i = 0; i < availableTags.size(); i++) {
            String tagKey = availableTags.get(i);
            Tag newTag = new Tag(tagKey, tagKey, category, i, null,
                    Tags.CATEGORY_TOPIC.equals(category) ? schedule.getColorForTrack(tagKey) : 0);
            tags.add(newTag);
            mTagsById.put(tagKey, newTag);
        }
        return tags;
    }

    public List<Tag> getTagsInCategory(String category) {
        return mTagsInCategory.get(category);
    }

    public Tag getTag(String tagId) {
        return mTagsById.containsKey(tagId) ? mTagsById.get(tagId) : null;
    }

    public static class Tag implements Comparable<Tag> {
        private String mId;
        private String mName;
        private String mCategory;
        private int mOrderInCategory;
        private String mAbstract;
        private int mColor;

        public Tag(String id, String name, String category, int orderInCategory, String _abstract,
                   int color) {
            mId = id;
            mName = name;
            mCategory = category;
            mOrderInCategory = orderInCategory;
            mAbstract = _abstract;
            mColor = color;
        }

        public String getId() {
            return mId;
        }

        public String getName() {
            return mName;
        }

        public String getCategory() {
            return mCategory;
        }

        public int getOrderInCategory() {
            return mOrderInCategory;
        }

        public String getAbstract() {
            return mAbstract;
        }

        public int getColor() {
            return mColor;
        }

        @Override
        public int compareTo(Tag another) {
            return mOrderInCategory - another.mOrderInCategory;
        }
    }
}
