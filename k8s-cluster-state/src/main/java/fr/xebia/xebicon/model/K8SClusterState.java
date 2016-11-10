package fr.xebia.xebicon.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class K8SClusterState {

    public final String type = "K8S_STATUS";
    public final List<K8SNode> payload;

    @JsonCreator
    public K8SClusterState(@JsonProperty("payload") List<K8SNode> payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        K8SClusterState that = (K8SClusterState) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, payload);
    }

    @Override
    public String toString() {
        return "K8SClusterState{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }
}
