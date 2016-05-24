package com.stickshooter.database;

import com.badlogic.gdx.Gdx;

import java.sql.*;

/**
 * Created by Marian on 25.04.2016.
 */
public class DatabaseClient {

    private Connection conn = null;
    private PreparedStatement stmt = null;
    private ResultSet rs = null;

    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://mysql.agh.edu.pl:3306/izet", "izet", "FH3SmbW0");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        } catch (Exception e) {
            try {
                conn.close();
                stmt.close();
                rs.close();
            } catch (SQLException ex) {

                Gdx.app.exit();

            }
        }
    }

    public boolean verifyUser(String username, String password){
        try {

            String sql = "SELECT pixshooter_username, pixshooter_password FROM pixshooter_users WHERE pixshooter_username = ? AND pixshooter_password = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();

            if(rs.next()){

                return true;

            } else {

                return false;

            }

        }catch (SQLException ex){
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());

            try {
                conn.close();
                stmt.close();
                rs.close();
            } catch (SQLException e) {

                Gdx.app.exit();

            }

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



            String sql = "INSERT INTO pixshooter_users (pixshooter_username, pixshooter_password) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();


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



            String sql = "UPDATE pixshooter_users SET pixshooter_password = ? WHERE pixshooter_username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, password);
            stmt.setString(2, username);
            stmt.executeUpdate();


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



            String sql = "DELETE FROM pixshooter_users WHERE pixshooter_username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.executeUpdate();


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
