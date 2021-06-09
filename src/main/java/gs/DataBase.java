package gs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    private final static String URL = "jdbc:mysql://localhost/discordbot";
    private final static String USERNAME = "root";
    private final static String PASSWORD = "root";

    public static void connect() {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {

            if (!connection.isClosed()) {
                System.out.println("Соединение с базой данных установлено");
            }
            statement.execute("insert into players (id, name, level, money, energy, coffee, energy_drink, graphics_card_1, graphics_card_2, graphics_card_3, acquired_money, cases_done, coffee_consumed, energy_drinks_consumed) VALUES(2,'Slava',3,3,54,35,6,7,8,9,10,11,12,13)");

        } catch (SQLException e) {
            System.err.println("Не удаЛОСЬ ХАХВХАХ ЛООООСЬ установить соединение с базой данных");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        connect();
    }
}
