package dal;

import com.zaxxer.hikari.HikariDataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import model.LiftRide;

import javax.sound.midi.SysexMessage;

public class LiftRideDao {
  private static BasicDataSource dataSource;
  private static HikariDataSource hikariDataSource;

  private AtomicInteger counter;


  public LiftRideDao() {
    //dataSource = DBCPDataSource.getDataSource();

    hikariDataSource = HikariCPDataSource.getDataSource();

  }

  public int getNextUniqueIndex() {
    return counter.getAndIncrement();
  }

  // Make sure database and table exists before you call this data insertion
  public void createLiftRide(LiftRide newLiftRide) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
   //String insertQueryStatement =
            //"INSERT INTO LiftRides (skierId, resortId, seasonId, dayId, time, waitTime, liftID) "
            // + "VALUES (?,?,?,?,?,?,?)";
    /**String insertQueryStatement =
            "INSERT INTO LiftRides (skierId, resortId, seasonId, dayId, time, waitTime,  liftID) "
                    + "VALUES (?,?,?,?,?,?,?)"; **/
    String insertQueryStatement =
            "INSERT INTO LiftRides (skierId, resortId, seasonId, dayId, time, waitTime,  liftride) "
                    + "VALUES (?,?,?,?,?,?,?)";

    try {
//      conn = dataSource.getConnection();
      conn = hikariDataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      preparedStatement.setInt(1, newLiftRide.getSkierId());
      preparedStatement.setInt(2, newLiftRide.getResortId());
      preparedStatement.setInt(3, newLiftRide.getSeasonId());
      preparedStatement.setInt(4, newLiftRide.getDayId());
      preparedStatement.setInt(5, newLiftRide.getTime());
      preparedStatement.setInt(6, newLiftRide.getWaitTime());
      preparedStatement.setInt(7, newLiftRide.getLiftID());

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
