package fr.xebia.xebicon.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum K8SNodeType {

    @JsonProperty("cloud")
    CLOUD,
    @JsonProperty("local")
    LOCAL
}
