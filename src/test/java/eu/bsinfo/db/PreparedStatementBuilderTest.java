package eu.bsinfo.db;

import eu.bsinfo.db.enums.SQLSortingOrder;
import eu.bsinfo.db.enums.Tables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PreparedStatementBuilderTest {
    @Test
    public void testIfStringIsGettingBuiltCorrectly() {
        Assertions.assertEquals(
                "INSERT INTO "+ Tables.CUSTOMERS+" (id, firstName) VALUES (?, ?)",
                new PreparedStatementBuilder()
                        .insertInto(Tables.CUSTOMERS, new String[]{"id", "firstName"})
                        .getSql()
        );

        Assertions.assertEquals(
                "UPDATE "+Tables.CUSTOMERS+" SET firstName = ?, lastName = ? WHERE id = ?",
                new PreparedStatementBuilder()
                        .update(Tables.CUSTOMERS, new String[]{"firstName", "lastName"})
                        .where("id", "=")
                        .getSql()
        );

        Assertions.assertEquals(
                "DELETE FROM "+Tables.CUSTOMERS+" WHERE id = ?",
                new PreparedStatementBuilder()
                        .delete()
                        .from(Tables.CUSTOMERS)
                        .where("id", "=")
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT * FROM "+Tables.CUSTOMERS+" WHERE firstName = ? OR lastName = ?",
                new PreparedStatementBuilder()
                        .select(new String[]{"*"})
                        .from(Tables.CUSTOMERS)
                        .where("firstName", "=")
                        .or("lastName", "=")
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT * FROM "+Tables.CUSTOMERS+" GROUP BY age",
                new PreparedStatementBuilder()
                        .select(new String[]{"*"})
                        .from(Tables.CUSTOMERS)
                        .groupBy(new String[]{"age"})
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT * FROM "+Tables.CUSTOMERS+" GROUP BY age, name",
                new PreparedStatementBuilder()
                        .select(new String[]{"*"})
                        .from(Tables.CUSTOMERS)
                        .groupBy(new String[]{"age", "name"})
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT * FROM "+Tables.CUSTOMERS+" ORDER BY name ASC, age DESC, birthDate",
                new PreparedStatementBuilder()
                        .select(new String[]{"*"})
                        .from(Tables.CUSTOMERS)
                        .orderBy("name", SQLSortingOrder.ASC, "age", SQLSortingOrder.DESC, "birthDate")
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT * FROM "+Tables.CUSTOMERS+" ORDER BY name ASC, age, birthDate DESC",
                new PreparedStatementBuilder()
                        .select(new String[]{"*"})
                        .from(Tables.CUSTOMERS)
                        .orderBy("name", SQLSortingOrder.ASC, "age", "birthDate", SQLSortingOrder.DESC)
                        .getSql()
        );

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new PreparedStatementBuilder()
                        .orderBy(SQLSortingOrder.DESC, SQLSortingOrder.ASC)
        );

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new PreparedStatementBuilder()
                        .orderBy("name", 1, true)
        );
    }
}
