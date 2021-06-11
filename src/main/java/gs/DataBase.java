package gs;

import gs.service.Farm;
import gs.service.Player;
import gs.service.PlayerStatistics;
import gs.service.items.GraphicsCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DataBase {
    private final String URL = "jdbc:mysql://localhost/discordbot";
    private final String USERNAME = "root";
    private final String PASSWORD = "root";
    Connection connection;
    Statement statement;

    DataBase() {
        connect();
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            statement = connection.createStatement();

            if (!connection.isClosed()) {
                System.out.println("Соединение с базой данных установлено");
            }

        } catch (SQLException e) {
            System.err.println("Не удаЛОСЬ ХАХВХАХ ЛООООСЬ установить соединение с базой данных");
            e.printStackTrace();
        }
    }

    public synchronized Player getPlayer(Player player) {
        System.out.println("dataBase.getPlayer started");
        String name = player.getDisplayName();
        long playerID = player.getId();
        int level = player.getLevel();
        int money = player.getMoney();
        int energy = player.getEnergy();
        LinkedHashMap<String, Integer> inventory = player.inventory;
        PlayerStatistics statistics = player.statistics;
        Integer coffee = inventory.get("coffee");
        Integer energy_drink = inventory.get("energy_drink");
        Integer graphics_card_1 = inventory.get("graphics_card_1");
        Integer graphics_card_2 = inventory.get("graphics_card_2");
        Integer graphics_card_3 = inventory.get("graphics_card_3");
        int acquired_money = statistics.getAcquiredMoney();
        int cases_done = statistics.getCasesDone();
        int coffee_consumed = statistics.getCoffeeConsumed();
        int energy_drinks_consumed = statistics.getEnergyDrinksConsumed();
        try {
            //language=SQL
            ResultSet resultSet = statement.executeQuery(String.format("select energy, level, money from players where id = %d", playerID));
            if (resultSet.next()) {
                System.out.println("Nгрок уже есть в базе данных");
                player.setEnergy(resultSet.getInt("energy"));
                player.setLevel(resultSet.getInt("level"));
                player.setMoney(resultSet.getInt("money"));
                player.inventory = getInventory(playerID);
                player.statistics = getStatistics(playerID);
                player.farm.stopCalculatingProfit();
                player.farm = getFarm(player);
            } else {
                System.out.printf("Добавление игрока %s с id: %d в базу данных\n", name, playerID);
                try {
                    //language=SQL
                    statement.execute(String.format(
                            "insert into players (id, name, level, money, energy, coffee, energy_drink, graphics_card_1, graphics_card_2, graphics_card_3, acquired_money, cases_done, coffee_consumed, energy_drinks_consumed, graphics_card_1_installed, graphics_card_2_installed,graphics_card_3_installed) " +
                                    "VALUES(%d,'%s', %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, 0, 0, 0)",
                            playerID, name, level, money, energy, coffee != null ? coffee : 0, energy_drink != null ? energy_drink : 0, graphics_card_1 != null ? graphics_card_1 : 0, graphics_card_2 != null ? graphics_card_2 : 0, graphics_card_3 != null ? graphics_card_3 : 0, acquired_money, cases_done, coffee_consumed, energy_drinks_consumed));
                    System.out.println("Nгрок был добавлен в базу данных");
                } catch (SQLException e) {
                    System.err.println("Не удалось добавить игрока в базу данных");
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return player;
    }

    public synchronized void update(long playerID, String column, int value) {
        System.out.printf("Nзменение поля %s, у игрока с id: %d на %d\n", column, playerID, value);
        try {
            //language=SQL
            ResultSet resultSet = statement.executeQuery(String.format("select %s from players where id = %d", column, playerID));

            System.out.println("Значение до изменения: " + get(playerID, column));

            //language=SQL
            statement.executeUpdate(String.format("update players set %s = %d where id = %d", column, value, playerID));

            System.out.println("Значение после изменения: " + get(playerID, column));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void increment(long playerID, String column, int value) {
        System.out.printf("Увеличение поля %s, у игрока с id: %d на %d \n", column, playerID, value);
        update(playerID, column, get(playerID, column) + value);
    }

    public synchronized int get(long playerID, String column) {
        try {
            //language=SQL
            ResultSet resultSet = statement.executeQuery(String.format("select %s from players where id = %d", column, playerID));
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public LinkedHashMap<String, Integer> getInventory(long PlayerID) {
        System.out.printf("Nнвентарь игрока с id %d\n", PlayerID);
        //language=SQL
        String query = String.format("select energy_drink, coffee, graphics_card_1, graphics_card_2, graphics_card_3 from players where id = %d", PlayerID);
        return getHashmap(5, query);
    }

    public HashMap<String, Integer> getStats(long PlayerID) {
        System.out.printf("Статы игрока с id %d\n", PlayerID);
        //language=SQL
        String query = String.format("select level, money, energy from players where id = %d", PlayerID);
        return getHashmap(3, query);
    }

    public synchronized PlayerStatistics getStatistics(long PlayerID) {
        System.out.printf("Статистика игрока с id %d\n", PlayerID);
        PlayerStatistics statistics = new PlayerStatistics();
        try {
            //language=SQL
            ResultSet resultSet = statement.executeQuery(String.format("select acquired_money, cases_done, coffee_consumed, energy_drinks_consumed from players where id = %d", PlayerID));
            if (resultSet.next()) {
                int acquired_money = resultSet.getInt("acquired_money");
                int cases_done = resultSet.getInt("cases_done");
                int coffee_consumed = resultSet.getInt("coffee_consumed");
                int energy_drinks_consumed = resultSet.getInt("energy_drinks_consumed");
                statistics = new PlayerStatistics(acquired_money, cases_done, coffee_consumed, energy_drinks_consumed);
            } else {
                System.err.println("Nгрок не найден");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statistics;
    }
    public synchronized Farm getFarm(Player player){
        System.out.printf("Ферма игрока с id: %d\n",player.getId());
        Farm farm = new Farm(player);
        ArrayList<GraphicsCard> cards = new ArrayList<>();
        try{
            //language=SQL
            ResultSet resultSet = statement.executeQuery(String.format("select graphics_card_1_installed, graphics_card_2_installed,graphics_card_3_installed from players where id = %d",player.getId()));
            if(resultSet.next()){
                ResultSetMetaData cardNames = resultSet.getMetaData();
                for(int i = 1; i<=3; i++){
                    for(int j = 0; j<resultSet.getInt(i); j++){
                        cards.add(new GraphicsCard(cardNames.getColumnName(i).replace("_installed","")));
                    }
                }
            }else{
                System.err.println("Nгрок не найден");
            }
            farm.addCards(cards);
        }catch (SQLException e){
            e.printStackTrace();
        }

        return farm;
    }

    private synchronized LinkedHashMap<String, Integer> getHashmap(int n, String query) {
        LinkedHashMap<String, Integer> hashmap = new LinkedHashMap<>();
        try {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                for (int i = 1; i <= n; i++) {
                    String column = resultSet.getMetaData().getColumnLabel(i);
                    Integer value = resultSet.getInt(i);
                    System.out.print(column + ": ");
                    System.out.println(value);
                    if (value != 0) {
                        hashmap.put(column, value);
                    }
                }
            } else {
                System.err.println("Nгрок не найден");
            }
            return hashmap;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashmap;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}