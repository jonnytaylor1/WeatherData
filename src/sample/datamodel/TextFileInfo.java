package sample.datamodel;

public class TextFileInfo {
    private int sequenceNumber;
    private String stationName;
    private String tMaxMonthYear;
    private String tMinMonthYear;
    private Double averageAfDays;
    private Double averageRainfallDays;

    public TextFileInfo(int sequenceNumber, String stationName, String tMaxMonthYear, String tMinMonthYear, Double averageAfDays, Double averageRainfallDays) {
        this.sequenceNumber = sequenceNumber;
        this.stationName = stationName;
        this.tMaxMonthYear = tMaxMonthYear;
        this.tMinMonthYear = tMinMonthYear;
        this.averageAfDays = averageAfDays;
        this.averageRainfallDays = averageRainfallDays;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getStationName() {
        return stationName;
    }

    public String gettMaxMonthYear() {
        return tMaxMonthYear;
    }

    public String gettMinMonthYear() {
        return tMinMonthYear;
    }

    public Double getAverageAfDays() {
        return averageAfDays;
    }

    public Double getAverageRainfallDays() {
        return averageRainfallDays;
    }
}
