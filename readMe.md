## Software Requirements
- Windows 10 / 11
- Java Development Kit (JDK)  23
- JavaFX SDK (already included in JDK 23)
- MySQL Database Server (8.0+)
- MySQL Connector for Java (Provided in the repository)
- Maven (IntelliJ IDEA Provided)

## Hardware Requirements
- Minimum 4GB RAM (8GB recommended)
- Minimum 1GB disk space for database and application files

## Installation & Setup
- Download Java Development Kit and JavaFX SDK (ver. 23)
- Clone the repository
  
	_git clone https://github.com/Brenedict/OOP-MiniHackathon.git_


### Connect the MYSQLConnector
- File > Project Structure > Modules > Dependencies > Add Jar or Directories > Locate the mysqlconnector under
  
_OOP-MiniHackathon\lib\mysql-connector-j-9.0.0.jar_

### Database preparations
- Install MySQL ver 8.0 & Configure Database
- Create a database
  _CREATE DATABASE studygroup;_

- Use the database you created
  _USE studygroup_
  
- Update DatabaseConnection.java constructor with your MySQL credentials
  
  _con = DriverManager.getConnection(<copy_JDBC_session_link>, <mysql-username, <mysql-password>);_

- Run the application
- Open project in Intellij IDEA
- Properly set up JavaFX libraries
- Run Main.java

## User Guide
- Login & Registration
- New users can sign up by clicking the “Sign up” link
- Use your student number and password to log in
- Joining & Creating Study Groups
- Create a study group by clicking the “Create” button and provide
- Group Chat
- Click the group where you want to enter
- Send and receive messages via multicast networking

### Create Multiple Instances of the program
- Main menu > run > edit configurations > add new configuration > application > set main class > modify options > allow multiple instances

