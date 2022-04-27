package com.bsds.group101.dal;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LiftRideDao {
    private static HikariDataSource hikariDataSource;

    public LiftRideDao() {
        hikariDataSource = HikariCPDataSource.getDataSource();
    }

    // Make sure database and table exists before you call this data insertion

    // get the total vertical for the skier for the specified ski day
    public int getDayVerticalOfSkiersForDay(int resortId, int seasonId, int dayId, int skierId) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String selectQueryStatement =
                "SELECT SUM(liftId * 10) AS dayVertical FROM Consumer.LiftRides "
                        + "WHERE resortId = ? AND seasonId = ? AND dayId = ? AND skierId = ?;";

        int dayVertical = 0;
        try {
            conn = hikariDataSource.getConnection();
            preparedStatement = conn.prepareStatement(selectQueryStatement);
            preparedStatement.setInt(1, resortId);
            preparedStatement.setInt(2, seasonId);
            preparedStatement.setInt(3, dayId);
            preparedStatement.setInt(4, skierId);

            // execute select SQL statement
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                dayVertical = resultSet.getInt("dayVertical");
            }
            return dayVertical;

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
        return dayVertical;
    }

    // get the total vertical for the skier the specified resort. If no season is specified, return
    // all seasons
    public Map<Integer, Integer> getSeasonsListAtSkier(int skierId) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String selectQueryStatement =
                "SELECT seasonId, SUM(liftId * 10) AS totalVert FROM Consumer.LiftRides WHERE skierId = ? GROUP BY seasonId ORDER BY seasonId ASC;";
        // instantiate a map for {"seasonID": "totalVert"}
        Map<Integer, Integer> seasonsToVerticalMap = new ConcurrentHashMap<>();
        try {
            conn = hikariDataSource.getConnection();
            preparedStatement = conn.prepareStatement(selectQueryStatement);
            preparedStatement.setInt(1, skierId);
            // execute select SQL statement
            ResultSet resultSet = preparedStatement.executeQuery();
            // iterate through the ResultSet
            while (resultSet.next()) {
                seasonsToVerticalMap.put(resultSet.getInt("seasonId"), resultSet.getInt("totalVert"));
            }
            return seasonsToVerticalMap;
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
        return seasonsToVerticalMap;
    }
}
