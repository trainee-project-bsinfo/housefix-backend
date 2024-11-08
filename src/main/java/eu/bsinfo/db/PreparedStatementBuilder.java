package eu.bsinfo.db;

import eu.bsinfo.db.enums.SQLSortingOrder;
import eu.bsinfo.db.enums.Tables;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreparedStatementBuilder {
    private String sql;

    public PreparedStatementBuilder select(String[] columns) {
        sql = "SELECT " + String.join(", ", columns);
        return this;
    }

    public PreparedStatementBuilder insertInto(Tables table, String[] columns) {
        sql = "INSERT INTO "+table.toString()+
                " ("+String.join(", ", columns)+")"+
                " VALUES ("+String.join(", ", Arrays.stream(columns).map(c -> "?").toArray(String[]::new))+")";
        return this;
    }

    public PreparedStatementBuilder update(Tables table, String[] columns) {
        sql = "UPDATE " + table.toString() + " SET "+
                String.join(", ", Arrays.stream(columns).map(c -> c + " = ?").toArray(String[]::new));
        return this;
    }

    public PreparedStatementBuilder delete() {
        sql = "DELETE";
        return this;
    }

    public PreparedStatementBuilder from(Tables table) {
        sql += " FROM " + table.toString();
        return this;
    }

    public PreparedStatementBuilder where(String column, String operator) {
        sql += " WHERE " + column + " "+operator+" ?";
        return this;
    }

    public PreparedStatementBuilder and(String column, String operator) {
        sql += " AND " + column + " "+operator+" ?";
        return this;
    }

    public PreparedStatementBuilder or(String column, String operator) {
        sql += " OR " + column + " "+operator+" ?";
        return this;
    }

    public PreparedStatementBuilder groupBy(String[] columns) {
        sql += " GROUP BY " + String.join(", ", columns);
        return this;
    }

    public PreparedStatementBuilder orderBy(Object... columnsWithOrder) {
        List<String> concated = new ArrayList<>();

        for (int i = 0; i < columnsWithOrder.length; i++) {
            if (!(columnsWithOrder[i] instanceof String) && !(columnsWithOrder[i] instanceof SQLSortingOrder)) {
                throw new IllegalArgumentException("Invalid column type \""+ columnsWithOrder[i] +"\" only String and SQLSortingOrder");
            }
            if (columnsWithOrder[i] instanceof SQLSortingOrder && columnsWithOrder[i+1] instanceof SQLSortingOrder) {
                throw new IllegalArgumentException("SQLSortingOrder values cannot appear consecutively");
            }

            if (columnsWithOrder[i] instanceof String && i+1 < columnsWithOrder.length && columnsWithOrder[i+1] instanceof SQLSortingOrder) {
                concated.add(columnsWithOrder[i] + " " + columnsWithOrder[i+1].toString());
                i++;
                continue;
            }
            concated.add(columnsWithOrder[i].toString());
        }

        sql += " ORDER BY " + String.join(", ", concated);
        return this;
    }

    public PreparedStatement build(DatabaseConnection conn) throws SQLException {
        return conn.getConnection().prepareStatement(sql+=";");
    }

    public String getSql() {
        return sql;
    }
}
