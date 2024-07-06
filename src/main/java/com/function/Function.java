package com.function;
// ______________________________________________________________________________________________________________________

import java.rmi.server.UID;

// import com.microsoft.azure.functions.ExecutionContext;
// import com.microsoft.azure.functions.HttpMethod;
// import com.microsoft.azure.functions.HttpRequestMessage;
// import com.microsoft.azure.functions.HttpResponseMessage;
// import com.microsoft.azure.functions.HttpStatus;
// import com.microsoft.azure.functions.annotation.AuthorizationLevel;
// import com.microsoft.azure.functions.annotation.FunctionName;
// import com.microsoft.azure.functions.annotation.HttpTrigger;

// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.ResultSet;
// import java.util.Optional;
// import java.sql.Statement;

// /**
//  * Azure Functions with HTTP Trigger.
//  */
// public class Function {
//     /**
//      * This function listens at endpoint "/api/HttpExample". Two ways to invoke it
//      * using "curl" command in bash:
//      * 1. curl -d "HTTP Body" {your host}/api/HttpExample
//      * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
//      */
//     // @FunctionName("HttpExample")
//     // public HttpResponseMessage run(
//     // @HttpTrigger(
//     // name = "req",
//     // methods = {HttpMethod.GET, HttpMethod.POST},
//     // authLevel = AuthorizationLevel.ANONYMOUS)
//     // HttpRequestMessage<Optional<String>> request,
//     // final ExecutionContext context) {
//     // context.getLogger().info("Java HTTP trigger processed a request.");

//     // // Parse query parameter
//     // final String query = request.getQueryParameters().get("name");
//     // final String name = request.getBody().orElse(query);

//     // if (name == null) {
//     // return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please
//     // pass a name on the query string or in the request body").build();
//     // } else {
//     // return request.createResponseBuilder(HttpStatus.OK).body("Hello, " +
//     // name).build();
//     // }
//     // }

//     @FunctionName("HttpExample")
//     public HttpResponseMessage run(
//             @HttpTrigger(name = "req", methods = { HttpMethod.GET,
//                     HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
//             final ExecutionContext context) {

//         context.getLogger().info("Java HTTP trigger processed a request.");

//         String jdbcUrl =
//         "jdbc:mysql://mysqlserver2309.mysql.database.azure.com:3306/azuretest";
//         String username = "mysqlusername";
//         String password = "VelVel@2024";
//         // String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s",
//         //         System.getenv("MYSQL_HOST"),
//         //         System.getenv("MYSQL_PORT"),
//         //         System.getenv("MYSQL_DATABASE"));
//         // String username = System.getenv("MYSQL_USERNAME");
//         // String password = System.getenv("MYSQL_PASSWORD");

//         try {
//             // Load the MySQL JDBC driver
//             Class.forName("com.mysql.cj.jdbc.Driver");

//             // Connect to the database
//             Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

//             // Create a statement
//             Statement statement = connection.createStatement();

//             // Execute a query
//             ResultSet resultSet = statement.executeQuery("SELECT * FROM azuretest.employees");

//             // Process the result set
//             while (resultSet.next()) {
//                 context.getLogger().info("Record: " + resultSet.getString("your_column"));
//             }

//             // Close the connection
//             connection.close();
//         } catch (Exception e) {
//             context.getLogger().severe("Database connection error: " + e.getMessage());
//             return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Database connection error")
//                     .build();
//         }

//         return request.createResponseBuilder(HttpStatus.OK).body("Database connection successful").build();
//     }
// }

// ______________________________________________________________________________________________________________________

import java.sql.*;
import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class Function {
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        String connectionString = "jdbc:mysql://mysqlserver2309.mysql.database.azure.com:3306/azuretest?user=mysqlusername&password=VelVel@2024";

        // String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s",
        // System.getenv("MYSQL_HOST"),
        // System.getenv("MYSQL_PORT"),
        // System.getenv("MYSQL_DATABASE"));
        // String username = System.getenv("MYSQL_USERNAME");
        // String password = System.getenv("MYSQL_PASSWORD");
        try (
                Connection connection = DriverManager.getConnection(connectionString);
                // DriverManager.getConnection(jdbcUrl, username, password);
                Statement statement = connection.createStatement()) {
            // Perform database operations
            // Example: Execute a query
            String sqlQuery = "SELECT * FROM employees";
            try (ResultSet resultSet = statement.executeQuery(sqlQuery)) {
                StringBuilder responseBody = new StringBuilder();
                while (resultSet.next()) {
                    responseBody.append("EmployeeID: ").append(resultSet.getString("EmployeeID")).append(", ")
                            .append("FirstName: ").append(resultSet.getString("FirstName")).append(", ")
                            .append("LastName: ").append(resultSet.getString("LastName")).append(", ")
                            .append("BirthDate: ").append(resultSet.getString("BirthDate")).append(", ")
                            .append("HireDate: ").append(resultSet.getString("HireDate")).append(", ")
                            .append("Position: ").append(resultSet.getString("Position")).append(", ")
                            .append("Salary: ").append(resultSet.getString("Salary")).append(", ")
                            .append("DepartmentID: ").append(resultSet.getString("DepartmentID")).append("\n");
                }
                return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "text/plain; charset=utf-8")
                        .body(responseBody.toString())
                        .build();
            }
        } catch (SQLException ex) {
            context.getLogger().severe("Error connecting to MySQL database: " + ex.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @FunctionName("HttpPostExample")
    public HttpResponseMessage handlePostRequest(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a POST request.");
        Optional<String> requestBody = request.getBody();

        if (!requestBody.isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Missing request body.")
                    .build();
        }
        String requestBodyContent = requestBody.get();
        System.out.println("Request Body: " + requestBodyContent);
        String connectionString = "jdbc:mysql://mysqlserver2309.mysql.database.azure.com:3306/azuretest?user=mysqlusername&password=VelVel@2024";
        try (Connection connection = DriverManager.getConnection(connectionString);
                Statement statement = connection.createStatement()) {
            int uniqueId = generateUniqueId(connection);
            String[] st = requestBodyContent.split(",");
            
            String filteString = "";
            for (String s : st) {
                System.out.println("S : " + s.trim());
                String[] strInLoop = s.trim().split(":");
                for (int i = 0; i < strInLoop.length; i++) {
                    if (i % 2 != 0) {

                        String strActualValue = strInLoop[i];
                        filteString = filteString.trim()+"'" + strActualValue.trim() + "',";

                    }
                }
            }
            int lastIndwx = filteString.lastIndexOf(",");
            String filterValue = "(" + filteString.substring(0, lastIndwx) + ");";
            System.out.println("FilterValue : " + filterValue);
            System.out.println("End...........");

            String insertQuery = "INSERT INTO Employees (EmployeeID, FirstName, LastName, BirthDate, HireDate, Position, Salary, DepartmentID) VALUES ("
                    + uniqueId + ", "
                    + filterValue;
                    // ",'Vikahs', 'Kumar', '2000-09-23', '2023-01-10', 'Software Engineer', 111000.00, 1);";
            statement.executeUpdate(insertQuery);

            return request.createResponseBuilder(HttpStatus.OK)
                    .body("POST request processed successfully.")
                    .build();
        } catch (SQLException ex) {
            context.getLogger().severe("Error connecting to MySQL database: " + ex.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static int generateUniqueId(Connection connection) throws SQLException {
        // SQL to get the maximum ID from the users table
        String sql = "SELECT COALESCE(MAX(EmployeeID), 0) FROM employees";
        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1) + 1;
            } else {
                return 1; // Start with ID 1 if table is empty
            }
        }
    }
}