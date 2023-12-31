package base.events;

import com.espertech.esper.common.client.util.DateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class BaseEvent {
    @JsonProperty("EventType")
    public String EventType = "";

    @JsonProperty("Message")
    public String Message = "";

    @JsonProperty("NodeId")
    public int NodeId = 0;

    @JsonProperty("TimeStamp")
    public String TimeStamp = "";

    @JsonIgnore
    public String getEventType() { return EventType; }
    @JsonIgnore
    public String getMessage() {return Message;   }
    @JsonIgnore
    public int getNodeId() {return NodeId;    }
    @JsonIgnore
    public String getTimeStamp(){return TimeStamp;}
}
