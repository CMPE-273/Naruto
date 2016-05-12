package PushNotificationServer;

import java.sql.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;

//import org.json.JSONObject;
//import java.io.InputStream;
//import java.io.OutputStream;

public class dbConnect {

    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    
    public static final String API_KEY = "AIzaSyAqb-kk1JMgQhfNWhpdZZ9izhW4TbL-ABU";
    
    private static final String url = "jdbc:mysql://localhost:3306/cmpe273";
    
    private static final String user = "root";
 
    private static final String password = "narutoteam123";

    public dbConnect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
    	    stmt = con.createStatement();
            } catch (Exception ex) {
            System.out.println("Error: " + ex);
            }
        }
    
    public int getNumberOfStudents() {
        int numberOfStudents = 0;
        try {
            String query;
            query = "select count(*) as numberOfStudents from app";
            rs = stmt.executeQuery(query);
            System.out.println("Record from Database - Table : app");
            if (rs.next()) {
            	numberOfStudents = rs.getInt("numberOfStudents");
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return numberOfStudents;
    }
    
    public void sendNotifications() throws SQLException {
    	String query;
        query = "select * from app";
        ResultSet rs;
		rs = stmt.executeQuery(query);
        while (rs.next()) {
        	
    	String line;
        StringBuffer jsonString = new StringBuffer();

    try {

        URL url = new URL("https://android.googleapis.com/gcm/send");

        //escape the double quotes in json string
        String instance_id = rs.getString("instance_id");
        if(!(instance_id.length()>0)){
        	instance_id ="/topics/global";
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String payload="{\"to\":\""+instance_id+"\"}";

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "key=" + API_KEY);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        writer.write(payload);
        writer.close();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line = br.readLine()) != null) {
                jsonString.append(line);
        }
        br.close();
        System.out.println(jsonString);
        connection.disconnect();
    } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
    }
    }
    }
}