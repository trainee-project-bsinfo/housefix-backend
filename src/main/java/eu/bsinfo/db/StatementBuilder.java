package eu.bsinfo.db;

import eu.bsinfo.db.enums.SQLSortingOrder;
import eu.bsinfo.db.enums.Tables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class StatementBuilder {
    private String sql;

    public StatementBuilder select(String[] columns) {
        sql = "SELECT " + String.join(", ", columns);
        return this;
    }

    @SafeVarargs
    public final <T> StatementBuilder insertInto(Tables table, String[] columns, T... values) {
        this.sql = "INSERT INTO " + table.toString() + " (" + String.join(", ", columns) + ") VALUES (";
        String[] valueStrings = Arrays.stream(values)
                .map(value -> value instanceof String ? "'" + value + "'" : value.toString())
                .toArray(String[]::new);
        this.sql += String.join(", ", valueStrings) + ")";
        return this;
    }

    @SafeVarargs
    public final <T> StatementBuilder update(Tables table, String[] columns, T... values) {
        String[] valueStrings = Arrays.stream(values)
                .map(value -> value instanceof String ? "'" + value + "'" : value.toString())
                .toArray(String[]::new);
        sql = "UPDATE " + table.toString() + " SET "+
                String.join(", ", IntStream.range(0, columns.length).mapToObj(i -> columns[i]+" = "+valueStrings[i])
                        .toArray(String[]::new));
        return this;
    }

    public StatementBuilder delete() {
        sql = "DELETE";
        return this;
    }

    public StatementBuilder from(Tables table) {
        sql += " FROM " + table.toString();
        return this;
    }

    public <T> StatementBuilder where(String column, String operator, T value) {
        if (value instanceof String) {
            value = (T) ("'"+value+"'");
        }
        sql += " WHERE " + column + " "+operator+" " + value;
        return this;
    }

    public <T> StatementBuilder and(String column, String operator, T value) {
        if (value instanceof String) {
            value = (T) ("'"+value+"'");
        }
        sql += " AND " + column + " "+operator+" " + value;
        return this;
    }

    public <T> StatementBuilder or(String column, String operator, T value) {
        if (value instanceof String) {
            value = (T) ("'"+value+"'");
        }
        sql += " OR " + column + " "+operator+" " + value;
        return this;
    }

    public StatementBuilder groupBy(String[] columns) {
        sql += " GROUP BY " + String.join(", ", columns);
        return this;
    }

    public StatementBuilder orderBy(Object... columnsWithOrder) {
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

    public boolean execute(DatabaseConnection conn) throws SQLException {
        sql += ";";
        Statement stmt = conn.getConnection().createStatement();
        boolean res = stmt.execute(sql);
        stmt.close();
        return res;
    }

    public ResultSet executeQuery(DatabaseConnection conn) throws SQLException {
        sql += ";";
        Statement stmt = conn.getConnection().createStatement();
        ResultSet res = stmt.executeQuery(sql);
        stmt.close();
        return res;
    }

    public int executeUpdate(DatabaseConnection conn) throws SQLException {
        sql += ";";
        Statement stmt = conn.getConnection().createStatement();
        int res = stmt.executeUpdate(sql);
        stmt.close();
        return res;
    }

    public String getSql() {
        return sql;
    }
}
