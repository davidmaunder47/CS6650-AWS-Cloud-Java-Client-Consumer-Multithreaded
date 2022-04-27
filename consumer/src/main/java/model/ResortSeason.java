package model;

public class ResortSeason {
  private int resortId;
  private int seasonId;

  public ResortSeason(int resortId, int seasonId) {
    this.resortId = resortId;
    this.seasonId = seasonId;
  }

  public int getResortId() {
    return resortId;
  }

  public void setResortId(int resortId) {
    this.resortId = resortId;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public void setSeasonId(int seasonId) {
    this.seasonId = seasonId;
  }

  @Override
  public String toString() {
    return "{" +
            "'resortId':" + resortId +
            ", 'seasonId':" + seasonId +
            '}';
  }
}
