package com.rh.ts.inputMySQL;

import java.sql.Connection; 
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * 数据库连接（非线程池）
 * @author leader
 *
 */
public class BeyondbConnection {
public static Connection getConnection() {


Connection con = null;
String driver = "com.mysql.jdbc.Driver";
String url = "jdbc:mysql://localhost:3306/icbc_ks";
String user = "root";
String password = "root";


try {


con = DriverManager.getConnection(url, user, password);
} catch (SQLException ex) {
Logger.getLogger(BeyondbConnection.class.getName()).log(Level.SEVERE, null, ex);
}
return con;


}
}
