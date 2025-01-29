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

    public void runTableCheck() throws SQLException {
        createTableCredentials();
        createTableChatGroups();
        createTableGroupMemberships();
        createTableMessages();
    }

    public void createTableCredentials() throws SQLException {
        sqlCommand = "CREATE TABLE IF NOT EXISTS credentials (" +
                "    UserID INT AUTO_INCREMENT PRIMARY KEY,  " +
                "    FirstName VARCHAR(50) NOT NULL,          " +
                "    LastName VARCHAR(50) NOT NULL,           " +
                "    Username VARCHAR(50) NOT NULL UNIQUE,    " +
                "    EmailAddress VARCHAR(100) NOT NULL UNIQUE, " +
                "    Password VARCHAR(255) NOT NULL, " +
                "    Gender VARCHAR(10), " +
                "    Role VARCHAR(20))";

        statement = con.prepareStatement(sqlCommand);
        statement.executeUpdate();
        System.out.println("Success1");
    };

    public void createTableChatGroups() throws SQLException {
        sqlCommand = "CREATE TABLE IF NOT EXISTS ChatGroups(" +
                "    ChatGroupID VARCHAR(5) PRIMARY KEY, " +
                "    ChatGroupName VARCHAR(50)," +
                "    CreatorRole VARCHAR(5)," +
                "    CourseCategory VARCHAR(255)," +
                "    ChatTitle VARCHAR(50)," +
                "    ChatDescription VARCHAR(350)," +
                "    UserID INT, " +
                "    IP VARCHAR(15)," +
                "    PortNumber INT," +
                "    FOREIGN KEY (UserID) REFERENCES credentials(UserID) " +
                ");";

        statement = con.prepareStatement(sqlCommand);
        statement.executeUpdate();
        System.out.println("Success2");
    };

    public void createTableGroupMemberships() throws SQLException {
        sqlCommand = "CREATE TABLE IF NOT EXISTS GroupMemberships (" +
                "    UserID INT," +
                "    ChatGroupID VARCHAR(5)," +
                "    FOREIGN KEY (UserID) REFERENCES credentials(UserID), " +
                "    FOREIGN KEY (ChatGroupID) REFERENCES ChatGroups(ChatGroupID) " +
                ");";

        statement = con.prepareStatement(sqlCommand);
        statement.executeUpdate();
        System.out.println("Success3");
    };

    public void createTableMessages() throws SQLException {
        sqlCommand = "CREATE TABLE IF NOT EXISTS Messages (" +
                "    MessageID INT AUTO_INCREMENT PRIMARY KEY, " +
                "    ChatGroupID VARCHAR(5) NOT NULL, " +
                "    UserID INT NOT NULL, " +
                "    MessageText TEXT NOT NULL, " +
                "    Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "    FOREIGN KEY (ChatGroupID) REFERENCES ChatGroups(ChatGroupID) ON DELETE CASCADE," +
                "    FOREIGN KEY (UserID) REFERENCES credentials(UserID) ON DELETE CASCADE" +
                ");";

        statement = con.prepareStatement(sqlCommand);
        statement.executeUpdate();
        System.out.println("Success4");
    };
}



