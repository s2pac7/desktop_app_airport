package Patterns.builders;

import Pojo.Aircraft;

public class AircraftBuilder {
    private int id;
    private String name;
    private String type;

    public AircraftBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public AircraftBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public AircraftBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public Aircraft build() {
        if (name == null || type == null) {
            throw new IllegalArgumentException("Name and Type must be provided");
        }
        return new Aircraft(id, name, type);
    }
}