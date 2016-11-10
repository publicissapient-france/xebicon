package fr.xebia.xebicon.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class K8SNode {

    public final K8SNodeType type;
    public final boolean active;
    public final List<K8SApp> apps;

    @JsonCreator
    public K8SNode(@JsonProperty("type") K8SNodeType type, @JsonProperty("active") boolean active, @JsonProperty("apps") List<K8SApp> apps) {
        this.type = type;
        this.apps = apps;
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        K8SNode k8SNode = (K8SNode) o;
        return active == k8SNode.active &&
                type == k8SNode.type &&
                Objects.equals(apps, k8SNode.apps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, active, apps);
    }

    @Override
    public String toString() {
        return "K8SNode{" +
                "type=" + type +
                ", active=" + active +
                ", apps=" + apps +
                '}';
    }
}
