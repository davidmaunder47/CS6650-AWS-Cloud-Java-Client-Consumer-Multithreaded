package dal;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sound.midi.SysexMessage;

public class HikariCPDataSource {
  // https://github.com/brettwooldridge/HikariCP
  private static HikariDataSource dataSource;

  // NEVER store sensitive information below in plain text!
  //private static final String HOST_NAME = "database.cpedm66g7koc.us-west-2.rds.amazonaws.com";
  //MYSQL

  /**
  private static final String HOST_NAME = "jdbc:mysql://localhost:3306";
  private static final String PORT = "3306";
  private static final String DATABASE = "Consumer";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "Mondo47!";
   **/

  //This is to configure for PostGres
  //private static final String HOST_NAME = "lab10.cu2pbwz3y5gk.us-west-2.rds.amazonaws.com";
  private static final String PORT = "5403";
  private static final String DATABASE = "finalproject";
  private static final String USERNAME = "postgres";
  private static final String PASSWORD = "Mondo47!";



  static {
    // https://github.com/brettwooldridge/HikariCP

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    HikariConfig config = new HikariConfig();

    //Use this String for MySQL
    //String url = String.format( HOST_NAME, PORT, DATABASE);
    //Use this String URL for Postgres
    String url = "jdbc:postgresql://localhost:5432/finalproject";
    // The MySQL DataSource is known to be broken with respect to network timeout support. Use jdbcUrl configuration instead.
    config.setJdbcUrl(url);
    config.setUsername(USERNAME);
    config.setPassword(PASSWORD);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");



    dataSource = new HikariDataSource(config);

  }

  public static HikariDataSource getDataSource() {
    return dataSource;
  }

}
