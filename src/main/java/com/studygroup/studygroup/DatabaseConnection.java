package com.studygroup.studygroup;

import java.sql.*;

public class DatabaseConnection {
    Connection con;
    PreparedStatement statement;
    ResultSet resultSet;
    String sqlCommand;

    DatabaseConnection() throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/studygroup", "root", "07282005");
    }

    public boolean checkTable(String tableName) {
        return false;
    };

    public void createTable(String tableName) {

    };
}



