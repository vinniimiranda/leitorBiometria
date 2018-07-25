/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class ConectaCache{
	public static void main(String[] args){
        
        String url="***";
 		String user = "****";
 		String password = "****";
 		String stQuery = "Select top 10 nome from cadastro.aluno order by id desc";

 		try {
 			Class.forName ("com.intersys.jdbc.CacheDriver");
 			Connection dbconnection = DriverManager.getConnection(url,user,password);
 			Statement stmt = dbconnection.createStatement();
 			java.sql.ResultSet rs = stmt.executeQuery(stQuery);
 			ResultSetMetaData rsmd = rs.getMetaData();

 			int colnum = rsmd.getColumnCount();
 			while (rs.next()) {
 				for (int i=1; i<=colnum; i++) {
                                    System.out.println(rs.getString(i) + "  ");
 				}
 				System.out.println();
 			}

 			dbconnection.close();
 		} catch (Exception ex) {
 			System.out.println("Caught exception: " +
 					ex.getClass().getName()
 					+ ": " + ex.getMessage());
 		}
 	}
 }

/*
 * End-of-file
 *
 */
