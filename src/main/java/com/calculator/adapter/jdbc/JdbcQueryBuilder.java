package com.calculator.adapter.jdbc;

public class JdbcQueryBuilder {

    public static String buildCountQuery(String query) {
        return "SELECT COUNT(*) FROM " + query;
    }

    public static String buildSelectQuery(String query) {
        return "SELECT * FROM " + query;
    }

    public static String buildPagedSelectQuery(String query, int page, int size) {
        StringBuilder sb = new StringBuilder(query);
        int offset = page * size;
        sb.append(" OFFSET ").append(offset).append(" ROWS");
        sb.append(" FETCH NEXT ").append(size).append(" ROWS ONLY");

        return "SELECT * FROM " + sb;
    }

}
