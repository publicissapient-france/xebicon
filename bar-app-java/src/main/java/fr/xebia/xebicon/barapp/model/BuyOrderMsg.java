package fr.xebia.xebicon.barapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BuyOrderMsg {

    public final String type = "BUY";
    public final ProductType payload;

    @JsonCreator
    public BuyOrderMsg(@JsonProperty("payload") ProductType payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "BuyOrderMsg{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }


}
