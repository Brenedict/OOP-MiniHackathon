package com.studygroup.studygroup;

import java.sql.*;

public class DatabaseConnection {
    Connection con;
    PreparedStatement statement;
    ResultSet resultSet;
    String sqlCommand;

    DatabaseConnection() throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/StudyGroup", "root", "Benedict#28");
    }

    public boolean checkTable(String tableName) {
        return false;
    };

    public void createTable(String tableName) {

    };
}



