

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    private static String JDBC_CONNECTION_URL =
            "C:\\Users\\Ramya\\mysample.db";


    public static void main(String[] args) {
        try {
            Connection conn = null;
            try {
                // db parameters
                String url = "jdbc:sqlite:C:/Users/Ramya/mysample.db";

                // create a connection to the database
                conn = DriverManager.getConnection(url);

                System.out.println("Connection to SQLite has been established.");


            } catch (SQLException e) {
                System.out.println(e.getMessage());

            }

            CSVLoader loader = new CSVLoader(conn);

            loader.loadCSV("C:\\Users\\Ramya\\Desktop\\empl.csv", "Customer", true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}