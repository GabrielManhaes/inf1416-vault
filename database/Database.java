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

    public static void createUser(String login_name, String name, String certificate, String password, String gid, String salt) {
        String query = "INSERT INTO usuarios (login_name, name, certificate, password, gid, salt) VALUES ('" +
                        login_name + "','" + 
                        name + "','" + 
                        certificate + "','" + 
                        password + "','" + 
                        gid + "','" + 
                        salt + "')";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
    }

    public static boolean userExists(String login_name) {
        String query = "SELECT id FROM usuarios WHERE login_name = '" + login_name + "'";
        try {
            ResultSet resultSet = query(query);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return false;
    }

    public static boolean userBlocked(String login_name) {
        String query = "SELECT id FROM usuarios WHERE block > CURRENT_TIMESTAMP() AND login_name = '" + login_name + "'";
        try {
            ResultSet resultSet = query(query);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return false;
    }

    public static void blockUser(String login_name) {
        String query = "UPDATE usuarios SET block = adddate(CURRENT_TIMESTAMP(), INTERVAL 2 MINUTE) WHERE login_name = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, login_name);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
    }

    public static int getCountUsers() {
        String query = "SELECT COUNT(*) FROM usuarios";
        try {
            ResultSet resultSet = query(query);
            if (resultSet.next()) {
                Integer count = resultSet.getInt(1);
                resultSet.close();
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return -1;
    }

    public static String getSalt(String login_name) {
        String query = "SELECT salt FROM usuarios WHERE login_name = '" + login_name + "'";
        try {
            ResultSet resultSet = query(query);
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return null;
    }

    public static boolean validatePassword(String login_name, String password) {
        String query = "SELECT id FROM usuarios WHERE login_name = '" + login_name + "' AND password = '" + password + "'";
        try {
            ResultSet resultSet = query(query);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return false;
    }

    public static void setPassword(String login_name, String password) {
        String query = "UPDATE usuarios SET password = '" + password + "' WHERE login_name = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, login_name);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
    }

    public static String getCertificate(String login_name) {
        String query = "SELECT certificate FROM usuarios WHERE login_name = '" + login_name + "'";
        try {
            ResultSet resultSet = query(query);
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return null;
    }

    public static void setCertificate(String login_name, String certificate) {
        String query = "UPDATE usuarios SET certificate = '" + certificate + "' WHERE login_name = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, login_name);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
    }

    public static String getGroup(String login_name) {
        String query = "SELECT g.name FROM grupos g, usuarios u WHERE u.gid=g.id AND u.login_name = '" + login_name + "'";
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

    public static String getGroupId(String login_name) {
        String query = "SELECT g.id FROM grupos g, usuarios u WHERE u.gid=g.id AND u.login_name = '" + login_name + "'";
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

    public static String getName(String login_name) {
        String query = "SELECT name FROM usuarios WHERE login_name = '" + login_name + "'";
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

    public static void addEntry(int code, String login_name) {
        String query = "INSERT INTO registros (code, login_name) VALUES ('" + Integer.toString(code) + "','" + login_name + "')";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
    }

    public static void addEntry(int code, String login_name, String file) {
        String query = "INSERT INTO registros (code, login_name, file_name) VALUES ('" + Integer.toString(code) + "','" + login_name + "','" + file + "')";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
    }

    public static int getCountLogins(String login_name) {
        String query = "SELECT COUNT(*) FROM registros WHERE login_name = '" + login_name + "' AND code = 5001";
        try {
            ResultSet resultSet = query(query);
            if (resultSet.next()) {
                Integer count = resultSet.getInt(1);
                resultSet.close();
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return -1;

    }

    public static int getCountUserQueries(String login_name) {
        String query = "SELECT COUNT(*) FROM registros WHERE login_name = '" + login_name + "' AND code = 8003";
        try {
            ResultSet resultSet = query(query);
            if (resultSet.next()) {
                Integer count = resultSet.getInt(1);
                resultSet.close();
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to query database.");
        }
        return -1;
    }
}