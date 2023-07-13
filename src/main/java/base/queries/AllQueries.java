package base.queries;

import javax.management.Query;

public class AllQueries {

    public static final QueryContainer QUERY_1 = new QueryContainer(
            "SELECT * FROM pattern [every (eventB = BaseEvent(eventType='B') -> (eventC= BaseEvent(eventType='C') AND eventE=BaseEvent(eventType='E') AND eventD=BaseEvent(eventType='D') AND eventF=BaseEvent(eventType='F')))]"+
                    "WHERE (eventB.nodeId IN (5, 9) AND eventC.nodeId IN (5,9) AND eventE.nodeId IN (5, 9) AND eventD.nodeId IN (5,9) AND eventF.nodeId IN (5,9))",
            new int[]{5,9},
            new String[]{"eventB", "eventC", "eventD", "eventE", "eventF"});
    public static final QueryContainer QUERY_2 = new QueryContainer(
            "SELECT * FROM BaseEvent WHERE eventType='D' AND (message='2' OR nodeId IN (6,7))",
            //"SELECT * FROM BaseEvent WHERE eventType='E' AND nodeId IN (1)",
            new int[]{1},
            new String[0]
    );
    public static final QueryContainer QUERY_3 = new QueryContainer(
            "SELECT * FROM pattern [every(eventA=BaseEvent(eventType = 'A') -> eventF=BaseEvent(eventType = 'F'))] WHERE (eventA.nodeId IN (2,3) AND eventF.nodeId IN (2,3))",
            new int[]{2,3},
            new String[]{"eventA", "eventF"});
    public static final QueryContainer QUERY_4 = new QueryContainer(
            "SELECT * FROM pattern [every((eventB=BaseEvent(eventType = 'B')) -> eventA=BaseEvent(eventType = 'A'))] WHERE (eventB.nodeId IN (5,9) AND eventA.nodeId IN (5,9))",
            new int[]{5,9},
            new String[]{"eventB", "eventA"}
    );
    public static final QueryContainer QUERY_5 = new QueryContainer(
            "SELECT * FROM pattern [every (eventA = BaseEvent(eventType='A') -> (eventC= BaseEvent(eventType='C') AND eventE=BaseEvent(eventType='E') AND eventD=BaseEvent(eventType='D') AND eventF=BaseEvent(eventType='F')))]" +
            "WHERE (eventA.nodeId IN (1, 2, 3, 4, 5, 6, 7) AND eventC.nodeId IN (1, 2, 3, 4, 5, 6, 7) AND eventE.nodeId IN (1, 2, 3, 4, 5, 6, 7) AND eventD.nodeId IN (1, 2, 3, 4, 5, 6, 7) AND eventF.nodeId IN (1, 2, 3, 4, 5, 6, 7))",
            new int[]{1,2,3,4,5,6,7},
            new String[]{"eventA", "eventC", "eventD", "eventE", "eventF"}
    );
    public static final QueryContainer QUERY_6 = new QueryContainer(
            "SELECT * FROM pattern [every( eventA = BaseEvent(eventType='E') -> eventC= BaseEvent(eventType='D'))]",
            new int[]{6},
            new String[]{"eventA", "eventC"}
    );

    public static final QueryContainer QUERY_7 = new QueryContainer(
            "SELECT * FROM BaseEvent WHERE ( eventType='F' AND message='7' AND nodeId IN (2,6,7))",
            new int[]{2,6,7},
            new String[0]
    );

    public static final QueryContainer QUERY_8 = new QueryContainer(
            "SELECT * FROM pattern [every((eventB=BaseEvent(eventType = 'B')) -> eventA=BaseEvent(eventType = 'A'))] WHERE (eventB.nodeId IN (6,8) AND eventA.nodeId IN (6,8))",
            new int[]{6,8},
            new String[]{"eventA", "eventB"}
    );

    public static final QueryContainer QUERY_9 = new QueryContainer(
            "SELECT * FROM BaseEvent WHERE eventType='A' AND nodeId IN (7)",
            new int[]{7},
            new String[0]
    );

    public static final QueryContainer[] QUERY_CONTAINERS = new QueryContainer[]{
            QUERY_1,
            QUERY_2,
            QUERY_3,
            QUERY_4,
            QUERY_5,
            QUERY_6,
            QUERY_7,
            QUERY_8,
            QUERY_9,
    };
}
