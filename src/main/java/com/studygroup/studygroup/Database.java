package com.studygroup.studygroup;

import java.sql.*;

public class Database {
    static Connection con;
    static PreparedStatement statement;
    static ResultSet resultSet;

    Database() throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/StudyGroup", "root", "Benedict#28");

    }

    public static boolean checkCredentials(String username, String password) throws SQLException {
        String sql = "SELECT Username, Password FROM StudyGroup WHERE Username = ? AND Password = ?;";
        statement = con.prepareStatement(sql);
        statement.setString(1, username);
        statement.setString(2, password);

        resultSet = statement.executeQuery(sql);
        String usernameResult = " ", passwordResult = " ";
        while(resultSet.next()) {
            usernameResult = resultSet.getString(1);
            passwordResult = resultSet.getString(2);
        }

        if(username.equals(usernameResult) && password.equals(passwordResult)) {
            return true;
        }

        return false;
    }

    public static void main(String[] args) throws SQLException {
        Database db = new Database();
    }
}
