package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseEvent {
    public static enum EventTypes{A,B,C,D,E,F}

    @JsonProperty("EventType")
    public EventTypes EventType;

    @JsonProperty("Message")
    public String Message;
}
