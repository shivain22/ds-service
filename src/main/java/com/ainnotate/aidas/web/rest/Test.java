package com.ainnotate.aidas.web.rest;

import liquibase.pro.packaged.A;

import java.sql.*;
import java.util.ArrayList;

public class Test {
    public static void main(String[] args){
        try (Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3308/ainnotateservice", "root", "")) {

            if (conn != null) {
                String query ="select id,id,-1,value from property order by id";
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    System.out.println(rs.getInt(1)+";"+rs.getInt(2)+";"+rs.getInt(3)+";"+rs.getString(4));
                }
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
