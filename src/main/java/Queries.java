public class Queries {

    public static String QUERY_1 = "SELECT SEQ(A, F, C) FROM A, F, C ON {0}";
    public static String QUERY_2 = "SELECT AND(E, SEQ(C, J, A)) FROM AND(E, SEQ(J, A)), C ON {5, 9}";
    public static String QUERY_3 = "SELECT AND(E, SEQ(J, A)) FROM E, SEQ(J, A) ON {9}";
    public static String QUERY_4 = "SELECT SEQ(J, A) FROM J, A ON {4}";
    public static String QUERY_5 = "SELECT AND(C, E, B, D, F) FROM B, AND(C, E, D, F) ON {0, 1, 2, 3, 4, 5}";
    public static String QUERY_6 = "SELECT AND(C, E, D, F) FROM C, E, D, F ON {2, 4}";
}
