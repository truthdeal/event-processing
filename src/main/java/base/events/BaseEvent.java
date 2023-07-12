package base.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseEvent {
    @JsonProperty("EventType")
    public String EventType;

    @JsonProperty("Message")
    public String Message;

    public String getEventType() {
        return EventType;
    }
    public String getMessage() {
        return Message;
    }
}
