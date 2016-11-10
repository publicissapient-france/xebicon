package fr.xebia.xebicon.barapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductType {

    public final String type;

    @JsonCreator
    public ProductType(@JsonProperty("type") String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ProductType{" +
                "type=" + type +
                '}';
    }

}
