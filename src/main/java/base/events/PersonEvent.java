package base.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonEvent {
    @JsonProperty("name")
    public String name;

    @JsonProperty("age")
    public int age;
    public PersonEvent() {}

    public PersonEvent(String name, int age) {
        this.name = name;
        this.age = age;
    }


    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}