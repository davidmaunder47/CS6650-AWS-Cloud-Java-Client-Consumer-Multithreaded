package dal;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.ResortSeason;

public class ResortSeasonDao {

  private static HikariDataSource hikariDataSource;

  public ResortSeasonDao() {
    hikariDataSource = HikariCPDataSource.getDataSource();
  }

  // Make sure database and table exists before you call this data insertion
  public void createResortSeason(ResortSeason newResortSeason) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement =
            "INSERT INTO ResortSeasons (resortId, seasonId) "
                    + "VALUES (?,?)";
    try {
//      conn = dataSource.getConnection();
      conn = hikariDataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      preparedStatement.setInt(1, newResortSeason.getResortId());
      preparedStatement.setInt(2, newResortSeason.getSeasonId());

      // execute insert SQL statement
      preparedStatement.executeUpdate();

//      // log JDBC status
//      System.out.println("STORE TO DATABASE AT " + dataSource.getUrl());

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
  }
}
