package com.ainnotate.aidas.web.rest;

import java.sql.*;

public class Test {
    public static void main(String[] args){
        try (Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3308/ainnotateservice", "root", "")) {

            if (conn != null) {
                String query ="SELECT table_name FROM information_schema.tables WHERE  table_schema='ainnotateservice'";
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    //if(rs.getString(1).contains("_aud")) {
                        String output = "<update catalogName=\"ainnotateservice\"\n" +
                                        "      schemaName=\"ainnotateservice\"\n" +
                                        "      tableName=\""+rs.getString(1)+"\">\n" +
                                        "      <column name=\"status\" value=\"1\"/>\n" +
                                        "</update>\n";
                        System.out.println(output);
                   // }
                }
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
