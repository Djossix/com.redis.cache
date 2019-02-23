package com.redis.chache;

import redis.clients.jedis.Jedis;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class App {
	
	public static Jedis jedis = new Jedis("192.168.1.121");
	public static Connection con;
	public static Statement stmt = null;
	
    public static void main( String[] args )
    {
    	
        System.out.println("Server: " + jedis.ping());
        Date start = new Date();
        
        int count = 0;
        int runs = 0;
        
        while(runs < 4) {
        	try {
        		runs += 1;
            	con = DriverManager.getConnection("jdbc:mariadb://192.168.1.121:3306/user?user=root&password=123456");
            	
            	if(count < 1) {
            		createDatabase();
            		createTableMariaDB();
            		addValueInDB();
            		count = 1;
            	}
            	
            	if(keyExistence("name")) {
            		System.out.println("The value of the key: " + jedis.get("name"));
            	} else {
            		String value = getNameValueMariaDB();
            		jedis.set("name", value);
            		jedis.expire("name", 30);
            		
            		System.out.println("The value of the key: " + value);
            	}
            	
            	con.close();
            	jedis.close();
            	
            	Date stop = new Date();
            	long diffInSec = TimeUnit.MILLISECONDS.toSeconds(stop.getTime() - start.getTime());
            	System.out.println("Time difference from start to stop: " + diffInSec);

            } catch (SQLException e) {
            	System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    public void addKey(String name) {
    	jedis.set("name", name);
    }
    
    public static boolean keyExistence(String key) {
    	String status = jedis.get(key);
		return status != null;
    }
    
    public Set<String> getKeys() {
    	Set<String> key = jedis.keys("*");
		return key;
    }
    
    public static void createDatabase() throws SQLException {
    	stmt = con.createStatement();
    	String query = "CREATE DATABASE IF NOT EXISTS user";
    	stmt.executeUpdate(query);
    }
    
    public static void createTableMariaDB() throws SQLException {
    	stmt = con.createStatement();
    	String query = "CREATE TABLE name (value varchar(200) primary key";
    	stmt.executeUpdate(query);
    }
    
    public static void addValueInDB() throws SQLException {
    	stmt = con.createStatement();
    	String query = "INSERT INTO name(value) VALUES('jessica')";
    	stmt.executeUpdate(query);
    }
    
    public static String getNameValueMariaDB() throws SQLException {
    	stmt = con.createStatement();
    	String query = "SELECT * FROM name";
    	String value = "";
    	stmt.executeQuery("do sleep(30)");
    	
    	ResultSet rs = stmt.executeQuery(query);
    	if(rs.next()) {
    		value = rs.getString(1);
    	}
    	
		return value;
    }
}
