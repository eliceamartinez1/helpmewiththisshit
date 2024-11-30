import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HTTPServer {
    public static void main(String[] args) throws IOException {
        // Create an HTTP server that listens on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Set the handler for the "/data" endpoint
        server.createContext("/data", new MyHandler());
        server.setExecutor(null); // Use the default executor
        server.start();
        System.out.println("Server started on port 8080");
    }

    // Define a handler for requests to "/data"
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Set CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");

            // Check if it's a GET request
            if ("GET".equals(exchange.getRequestMethod())) {
                // Extract the query parameter from the URL (e.g., "/data?query=productName")
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("query=")) {
                    String productName = query.split("=")[1];
                    List<String> results = performSearchByProductName(productName);

                    // Format the results as JSON
                    String jsonResponse = formatResultsAsJson(results);

                    // Send the response to the client as JSON
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(jsonResponse.getBytes());
                    os.close();
                } else {
                    // Default response if no query
                    String response = "Hello, this is your Java backend!";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        }

        // Perform the database search by product name and return the reviews
  // Perform the database search by product name and return the reviews
private List<String> performSearchByProductName(String productName) {
    List<String> results = new ArrayList<>();

    // Database connection parameters
    String url = "jdbc:mysql://localhost:3306/project_1";
    String username = "root";
    String password = ""; //enter your database password here

    // SQL query to search reviews based on product name
    String sql = "SELECT r.review FROM reviews r WHERE r.brand LIKE ?";

    try (Connection conn = DriverManager.getConnection(url, username, password);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, "%" + productName + "%");  // Use product name in query with wildcard for partial matches
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            results.add(rs.getString("review"));  // Add review text to the results list
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return results;
}


        // Format results as a simple JSON array
        private String formatResultsAsJson(List<String> results) {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < results.size(); i++) {
                json.append("\"").append(results.get(i)).append("\"");
                if (i < results.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            return json.toString();
        }
    }
}
