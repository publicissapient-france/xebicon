package fr.xebia.xebicon.model;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

@Table("Talks")
public class Track extends Model {

    @Column("track") @Key String title;
    @Column("count") int count;
    @Column("color") int color;

    public String getTitle() {
        return title;
    }

    public int getCount() {
        return count;
    }

    public int getColor() {
        return color;
    }
}
