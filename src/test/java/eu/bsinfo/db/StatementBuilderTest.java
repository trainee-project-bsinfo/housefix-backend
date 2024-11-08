package eu.bsinfo.db;

import eu.bsinfo.db.enums.SQLSortingOrder;
import eu.bsinfo.db.enums.Tables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StatementBuilderTest {
    @Test
    public void testIfStringIsGettingBuiltCorrectly() {
        Assertions.assertEquals(
                "SELECT firstName, lastName FROM "+Tables.CUSTOMERS,
                new StatementBuilder()
                        .select(new String[]{"firstName", "lastName"})
                        .from(Tables.CUSTOMERS)
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT firstName, lastName FROM "+Tables.CUSTOMERS+" WHERE id = 1",
                new StatementBuilder()
                        .select(new String[]{"firstName", "lastName"})
                        .from(Tables.CUSTOMERS)
                        .where("id", "=", 1)
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT firstName, lastName FROM "+Tables.CUSTOMERS+" WHERE id = 1 AND firstName = 'John'",
                new StatementBuilder()
                        .select(new String[]{"firstName", "lastName"})
                        .from(Tables.CUSTOMERS)
                        .where("id", "=", 1)
                        .and("firstName", "=", "John")
                        .getSql()
        );

        Assertions.assertEquals(
                "INSERT INTO "+Tables.CUSTOMERS+" (id, firstName) VALUES (1, 'John')",
                new StatementBuilder()
                        .insertInto(Tables.CUSTOMERS, new String[]{"id", "firstName"}, 1, "John")
                        .getSql()
        );

        Assertions.assertEquals(
                "UPDATE "+Tables.CUSTOMERS+" SET firstName = 'John', lastName = 'Doe', age = 35 WHERE id = 1",
                new StatementBuilder()
                        .update(Tables.CUSTOMERS, new String[]{"firstName", "lastName", "age"}, "John", "Doe", 35)
                        .where("id", "=", 1)
                        .getSql()
        );

        Assertions.assertEquals(
                "DELETE FROM "+Tables.CUSTOMERS+" WHERE id = 1",
                new StatementBuilder()
                        .delete()
                        .from(Tables.CUSTOMERS)
                        .where("id", "=", 1)
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT * FROM "+Tables.CUSTOMERS+" WHERE firstName = 'John' OR lastName = 'Doe'",
                new StatementBuilder()
                        .select(new String[]{"*"})
                        .from(Tables.CUSTOMERS)
                        .where("firstName", "=", "John")
                        .or("lastName", "=", "Doe")
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT * FROM "+Tables.CUSTOMERS+" GROUP BY age",
                new StatementBuilder()
                        .select(new String[]{"*"})
                        .from(Tables.CUSTOMERS)
                        .groupBy(new String[]{"age"})
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT * FROM "+Tables.CUSTOMERS+" GROUP BY age, name",
                new StatementBuilder()
                        .select(new String[]{"*"})
                        .from(Tables.CUSTOMERS)
                        .groupBy(new String[]{"age", "name"})
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT * FROM "+Tables.CUSTOMERS+" ORDER BY name ASC, age DESC, birthDate",
                new StatementBuilder()
                        .select(new String[]{"*"})
                        .from(Tables.CUSTOMERS)
                        .orderBy("name", SQLSortingOrder.ASC, "age", SQLSortingOrder.DESC, "birthDate")
                        .getSql()
        );

        Assertions.assertEquals(
                "SELECT * FROM "+Tables.CUSTOMERS+" ORDER BY name ASC, age, birthDate DESC",
                new StatementBuilder()
                        .select(new String[]{"*"})
                        .from(Tables.CUSTOMERS)
                        .orderBy("name", SQLSortingOrder.ASC, "age", "birthDate", SQLSortingOrder.DESC)
                        .getSql()
        );

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new StatementBuilder()
                        .orderBy(SQLSortingOrder.DESC, SQLSortingOrder.ASC)
        );

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new StatementBuilder()
                        .orderBy("name", 1, true)
        );
    }
}
