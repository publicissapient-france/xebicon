package fr.xebia.xebicon.barapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ShopStateMsg {

    public final String type = "SHOP_STATE";
    public final Map<String, Long> payload;

    @JsonCreator
    public ShopStateMsg(@JsonProperty("payload") Map<String, Long> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "ShopStateMsg{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }


}
