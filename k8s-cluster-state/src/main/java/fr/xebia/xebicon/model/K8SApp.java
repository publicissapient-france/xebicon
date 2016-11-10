package fr.xebia.xebicon.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class K8SApp {

    public final String name;

    @JsonCreator
    public K8SApp(@JsonProperty("name") String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        K8SApp k8SApp = (K8SApp) o;

        return name.equals(k8SApp.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "K8SApp{" +
                "name='" + name + '\'' +
                '}';
    }
}
