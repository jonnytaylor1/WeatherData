package sample.datamodel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class StationInfo {

    private SimpleStringProperty stationName;
    private SimpleStringProperty year;
    private SimpleDoubleProperty tempMax;
    private SimpleDoubleProperty tempMin;
    private SimpleIntegerProperty afTotal;
    private SimpleDoubleProperty totalRainfall;



    public StationInfo(String stationName, String year, Double tempMax, Double tempMin, Integer afTotal, Double totalRainfall) {
        this.stationName = new SimpleStringProperty(stationName);
        this.year = new SimpleStringProperty(year);
        this.tempMax = new SimpleDoubleProperty(tempMax);
        this.tempMin = new SimpleDoubleProperty(tempMin);
        this.afTotal = new SimpleIntegerProperty(afTotal);
        this.totalRainfall = new SimpleDoubleProperty(totalRainfall);
    }

    public String getStationName() {
        return stationName.get();
    }

    public double getTempMin() {
        return tempMin.get();
    }

    public double getTempMax() {
        return tempMax.get();
    }


    public int getAfTotal() {
        return afTotal.get();
    }


    public double getTotalRainfall() {
        return totalRainfall.get();
    }

    //Despite it saying that getYear() is not used it is infact needed
    //to populate the table using cell value factory

    public String getYear() {
        return year.get();
    }

}