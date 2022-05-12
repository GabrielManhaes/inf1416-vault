package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class Database {
    static String db_url = "jdbc:mysql://localhost:3306/t4?useTimezone=true&serverTimezone=UTC";
    static String db_user = "root";
    static String db_password = "root";
    
    private static Database database;
    private static Connection connection;

    private Database() {

    }

    public static Database getInstance(){
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public static void connect() {
        System.out.println("Loading driver...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded!");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }
        try {
            connection = DriverManager.getConnection(db_url, db_user, db_password);
            System.out.println("\nConnected to the database.\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static ResultSet query(String query)
	{
		try {
		    Statement statement = connection.createStatement();
		    ResultSet resultSet = statement.executeQuery(query);
		    return resultSet;
		
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to query database.");
		}
		
		return null;
	}

    public static boolean userExists(String email) {
        String query = "SELECT id FROM usuarios WHERE login_name = '" + email + "'";
        try {
            ResultSet resultSet = query(query);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return false;
    }

    public static boolean userBlocked(String email) {
        String query = "SELECT id FROM usuarios WHERE block > CURRENT_TIMESTAMP() AND login_name = '" + email + "'";
        try {
            ResultSet resultSet = query(query);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return false;
    }

    public static void blockUser(String email) {
        String query = "UPDATE usuario SET block = adddate(CURRENT_TIMESTAMP(), INTERVAL 2 MINUTE) WHERE login_name = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
    }

    public static String getGroup(String email) {
        String query = "SELECT g.name FROM grupos g, usuarios u WHERE u.gid=g.id AND u.login_name = '" + email + "'";
        try {
            ResultSet resultSet = query(query);
            resultSet.next();
            String group = resultSet.getString(1);
            resultSet.close();
            return group;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return "";
    }

    public static String getName(String email) {
        String query = "SELECT name FROM usuarios WHERE login_name = '" + email + "'";
        try {
            ResultSet resultSet = query(query);
            resultSet.next();
            String name = resultSet.getString(1);
            resultSet.close();
            return name;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return "";
    }

    public static void addEntry(int code) {
        String query = "INSERT INTO registros (code) VALUES ('" + Integer.toString(code) + "')";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
    }

    public static void addEntry(int code, String email) {
        System.out.println(code);
        System.out.println(email);
        String query = "INSERT INTO registros (code, login_name) VALUES ('" + Integer.toString(code) + "','" + email + "')";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
    }

    public static void addEntry(int code, String email, String file) {
        String query = "INSERT INTO registros (code, login_name, file_name) VALUES ('" + Integer.toString(code) + "','" + email + "','" + file + "')";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
    }
}