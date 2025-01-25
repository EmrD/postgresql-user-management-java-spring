package com.example.demo.Controllers;
import com.example.demo.Columns;
import com.example.demo.DbProps;
import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1")
class PostgresqlControllers {
    public Connection connectDatabase() {try {return DriverManager.getConnection(DbProps.connectionString , DbProps.username, DbProps.password);} catch (Exception e) {return null;}}

    @PostMapping("/addUser")
    public String addUser(String name, int age, String email) { //Example Schema: name, age, email
        try {
            Connection connection = connectDatabase();
            Statement statement = connection.createStatement();
            String query = "INSERT INTO testtable (" + Columns.name.name()  + ", "+  Columns.age.name() + ", " +  Columns.email.name() + ")" + "VALUES ('" + name + "', '" + age + "', '" + email + "')";
            if (!name.matches("[a-zA-Z]+")) {
                throw new IllegalArgumentException("Invalid name format");
            }
            else if (age < 0) {
                throw new IllegalArgumentException("Invalid age format");
            }
            else if (!email.matches("^(.+)@(.+)$")) {
                throw new IllegalArgumentException("Invalid email format");
            }
            else{
                statement.executeUpdate(query);
                return "User added successfully.";
            }
        } catch (Exception e) {
            return "Error while adding user.";
        }
    }

    @PostMapping("/getUser")
    public String getUser(String name){
        try {
            Connection connection = connectDatabase();
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM testtable WHERE name = '" + name + "'";
            if (!name.matches("[a-zA-Z]+")) {
                throw new IllegalArgumentException("Invalid name format");
            }
            else {
                ResultSet resultSet = statement.executeQuery(query);
                resultSet.next();
                String email = resultSet.getString("email");
                int age = resultSet.getInt("age");
                return "User found " + name + " age: " + age + " email: " + email;
            }
        } catch (Exception e) {
            return "Error while getting user.";
        }
    }

    @PostMapping("/deleteUser")
    public String deleteUser(String name){
        try {
            Connection connection = connectDatabase();
            Statement statement = connection.createStatement();
            String query = "DELETE FROM testtable WHERE name = '" + name + "'";
            if (name.matches("[a-zA-Z]+")) {
                statement.executeUpdate(query);
                return "User deleted successfully.";
            }
            else{
                return "Invalid name format";
            }
        } catch (Exception e) {
            return "Error while deleting user.";
        }
    }

    public String formatDate(String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date.substring(0, 19).replace(" ", "T"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    @GetMapping("/getAllUsers")
    public String getAllUsers(){
        try {
            Connection connection = connectDatabase();
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM testtable";
            ResultSet resultSet = statement.executeQuery(query);
            String result = "";
            while (resultSet.next()) {
                String name = resultSet.getString(Columns.name.name());
                String email = resultSet.getString(Columns.email.name());
                String createdAt = formatDate(resultSet.getString(Columns.xata_createdat.name()));
                int age = resultSet.getInt(Columns.age.name());
                result += "User " + name + " age: " + age + " email: " + email + " created at: "+ createdAt + "\n";
            }
            return result;
        } catch (Exception e) {
            return "Error while getting users.";
        }
    }
}