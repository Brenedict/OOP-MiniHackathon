package com.studygroup.studygroup;

import java.sql.*;

public class Database {
    static Connection con;
    static PreparedStatement statement;
    static ResultSet resultSet;

    Database() throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/StudyGroup", "root", "Benedict#28");

    }

    public boolean checkCredentials(String email, String password)  {
        try {
            String sql = "SELECT Email, Password FROM Credentials WHERE Email = ? AND Password = ?;";
            statement = con.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, password);

            resultSet = statement.executeQuery();
            String usernameResult = " ", passwordResult = " ";
            while (resultSet.next()) {
                usernameResult = resultSet.getString(1);
                passwordResult = resultSet.getString(2);
            }

            if (email.equals(usernameResult) && password.equals(passwordResult)) {
                return true;
            }

            return false;
        }
        catch(SQLException e) {
            System.out.println("Error");
            return false;
        }

    }

//    public static void main(String[] args) throws SQLException {
//        Database db = new Database();
//    }
}
