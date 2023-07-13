package base.queries;

public class QueryContainer {
    public String Query;
    public int[] TargetNodeIds;
    public String[] EventFilters;

    public QueryContainer(String query, int[] targetNodeIds, String[] eventFilters ){
        Query = query;
        TargetNodeIds = targetNodeIds;
        EventFilters = eventFilters;
    }
}
