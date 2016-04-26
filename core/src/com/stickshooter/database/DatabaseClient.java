package com.stickshooter.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Marian on 25.04.2016.
 */
public class DatabaseClient {

        private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;

        public void connect() {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();

                conn = DriverManager.getConnection("jdbc:mysql://mysql.agh.edu.pl:3306/izet", "izet", "FH3SmbW0");


            } catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    public boolean verifyUser(String username, String password){
        try {

            stmt = conn.createStatement();

            String sql = "SELECT pixshooter_username, pixshooter_password FROM pixshooter_users WHERE pixshooter_username = '" + username + "' AND pixshooter_password = '" + password + "'";
            rs = stmt.executeQuery(sql);

            if(rs.next()){

                return true;

            } else {

                return false;

            }

        }catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());

        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }

        return false;

    }

    public boolean createUser(String username, String password) {

        try {

            stmt = conn.createStatement();

            String sql = "INSERT INTO pixshooter_users (pixshooter_username, pixshooter_password) VALUES ('" + username + "', '" + password +"')";
            stmt.executeUpdate(sql);


        }catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;

        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }

        return true;

    }

    public boolean changePassword(String username, String password) {

        try {

            stmt = conn.createStatement();

            String sql = "UPDATE pixshooter_users SET pixshooter_password = '"+ password +"' WHERE pixshooter_username = '" + username +"'";
            stmt.executeUpdate(sql);


        }catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;

        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }

        return true;

    }

    public boolean deleteUser(String username) {

        try {

            stmt = conn.createStatement();

            String sql = "DELETE FROM pixshooter_users WHERE pixshooter_username = '" + username + "'";
            stmt.executeUpdate(sql);


        }catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;

        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }

        return true;

    }

}
