package gs;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DataBase {
    private final String URL = "jdbc:mysql://localhost/discordbot";
    private final String USERNAME = "root";
    private final String PASSWORD = "root";
    Connection connection;
    Statement statement;

    DataBase(){
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

    public void createNewPlayer(long playerID, String name) {
        System.out.printf("Добавление игрока %s с id: %d в базу данных\n",name,playerID);
        try {
            //language=SQL
            statement.execute(String.format("insert into players (id, name, level, money, energy, coffee, energy_drink, graphics_card_1, graphics_card_2, graphics_card_3, acquired_money, cases_done, coffee_consumed, energy_drinks_consumed) VALUES(%d,'%s',0,0,0,0,0,0,0,0,0,0,0,0)", playerID,name));
            System.out.println("Nгрок был добавлен в базу данных");
        } catch (SQLException e) {
            System.err.println("Не удалось добавить игрока в базу данных");
            e.printStackTrace();
        }
    }
    public void update(long playerID, String column, int value){
        System.out.printf("Nзменение поля %s, у игрока с id: %d на %d\n",column,playerID,value);
        try {
            //language=SQL
            ResultSet resultSet = statement.executeQuery(String.format("select %s from players where id = %d", column, playerID));

            System.out.println("Значение до изменения: " +get(playerID, column));

            //language=SQL
            statement.executeUpdate(String.format("update players set %s = %d where id = %d",column, value, playerID));

            System.out.println("Значение после изменения: " +get(playerID, column));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int get(long playerID, String column){
        try {
            //language=SQL
            ResultSet resultSet = statement.executeQuery(String.format("select %s from players where id = %d", column, playerID));
            if(resultSet.next()){
                 return resultSet.getInt(1);
            }else{
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public HashMap<String, Integer> getInventory(long PlayerID){
        System.out.printf("Nнвентарь игрока с id %d\n", PlayerID);
        //language=SQL
        String query = String.format("select energy_drink, coffee, graphics_card_1, graphics_card_2, graphics_card_3 from players where id = %d",PlayerID);
        return getHashmap(5, query);
    }
    public HashMap<String, Integer> getStats(long PlayerID){
        System.out.printf("Статы игрока с id %d\n", PlayerID);
        //language=SQL
        String query = String.format("select level, money, energy from players where id = %d",PlayerID);
        return getHashmap(3, query);
    }
    public HashMap<String, Integer> getStatistics(long PlayerID){
        System.out.printf("Статистика игрока с id %d\n", PlayerID);
        //language=SQL
        String query = String.format("select acquired_money, cases_done, coffee_consumed, energy_drinks_consumed from players where id = %d",PlayerID);
        return getHashmap(4,query);
    }

    private LinkedHashMap<String,Integer> getHashmap(int n, String query){
        LinkedHashMap<String,Integer> hashmap = new LinkedHashMap<>();
        try {
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                for(int i = 1; i<=n; i++){
                    String column = resultSet.getMetaData().getColumnLabel(i);
                    Integer value =resultSet.getInt(i);
                    System.out.print(column+": ");
                    System.out.println(value);
                    hashmap.put(column, value);
                }
            }else{
                System.err.println("Nгрок не найден");
            }
            return hashmap;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return hashmap;
    }
    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}