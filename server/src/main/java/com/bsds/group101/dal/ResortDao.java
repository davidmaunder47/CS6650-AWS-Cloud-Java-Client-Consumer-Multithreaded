package com.bsds.group101.dal;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ResortDao {

    private static HikariDataSource hikariDataSource;

    public ResortDao() {
        hikariDataSource = HikariCPDataSource.getDataSource();
    }
    // Make sure database and table exists before you call this data query
    public ArrayList<Integer> getResortsList() {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String selectQueryStatement =
                "SELECT DISTINCT resortId FROM Consumer.ResortSeasons ORDER BY resortId ASC;";
        ArrayList<Integer> resortsList = new ArrayList<>();
        try {
            conn = hikariDataSource.getConnection();
            preparedStatement = conn.prepareStatement(selectQueryStatement);

            // execute select SQL statement
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                resortsList.add(resultSet.getInt("resortId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return resortsList;
    }

    //  get number of unique skiers at resort/season/day
    public int getNumberOfSkiersAtResort(int resortId, int seasonId, int dayId) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String selectQueryStatement =
                "SELECT COUNT(DISTINCT skierId) as numSkiers "
                        + "FROM Consumer.LiftRides "
                        + "WHERE resortId = ? AND seasonId = ?  AND dayId = ?;";
        int numUniqueSkiers = 0;
        try {
            conn = hikariDataSource.getConnection();
            preparedStatement = conn.prepareStatement(selectQueryStatement);
            preparedStatement.setInt(1, resortId);
            preparedStatement.setInt(2, seasonId);
            preparedStatement.setInt(3, dayId);

            // execute select SQL statement
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                numUniqueSkiers = resultSet.getInt("numSkiers");
            }
            return numUniqueSkiers;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return numUniqueSkiers;
    }

    //  get a list of seasons for the specified resort
    public ArrayList<Integer> getSeasonsListAtResort(int resortId) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String selectQueryStatement =
                "SELECT DISTINCT seasonId "
                        + "FROM Consumer.ResortSeasons "
                        + "WHERE resortId = ? ORDER BY seasonId ASC;";
        ArrayList<Integer> seasonsList = new ArrayList<>();
        try {
            conn = hikariDataSource.getConnection();
            preparedStatement = conn.prepareStatement(selectQueryStatement);
            preparedStatement.setInt(1, resortId);

            // execute select SQL statement
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                seasonsList.add(resultSet.getInt("seasonId"));
            }
            return seasonsList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return seasonsList;
    }
}