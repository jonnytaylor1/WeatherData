package sample;


import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import sample.datamodel.StationInfo;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class GraphsController implements Initializable {

    public LineChart<String, Number> lineChartTemp;
    public LineChart lineChartAf;
    public LineChart lineChartRainfall;
    private int index = RetrieveAndStoreData.getIndex();
    private Set<String> keys = RetrieveAndStoreData.getKeySet();
    private TreeMap<String, StationInfo> stationInfoTreeMap;
    private XYChart.Series tMaxSeries;
    private XYChart.Series tMinSeries;
    private XYChart.Series afSeries;
    private XYChart.Series rainfallSeries;
    private List<TreeMap<String, StationInfo>> stationList;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateGraphs();
    }

    private void populateGraphs() {
        int index = RetrieveAndStoreData.getIndex();
        try {
            stationList = RetrieveAndStoreData.createData()[0];
            stationInfoTreeMap = stationList.get(index);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String yearLabel = "Year";
        String tempLabel  = "Temp (" + "\u00B0" + "C)";
        String afDaysLabel = "Number of Airfrost Days";
        String rainfallLabel = "Total Rainfall (mm)";
        setLabels(lineChartTemp, yearLabel, tempLabel);
        setLabels(lineChartAf, yearLabel, afDaysLabel);
        setLabels(lineChartRainfall, yearLabel, rainfallLabel);
        lineChartTemp.setTitle("Highest and Lowest Annual Temperature");
        lineChartAf.setTitle("Number of Rainfrost Days Per Year");
        lineChartRainfall.setTitle("Total Rainfall Per Year");



        lineChartTemp.setAnimated(false);
        tMaxSeries = new XYChart.Series<String, Number>();
        tMaxSeries.setName("Max Temp");
        tMinSeries = new XYChart.Series<String, Number>();
        tMinSeries.setName("Min Temp");
        afSeries = new XYChart.Series<String, Number>();
        afSeries.setName("Air Frost Days");
        rainfallSeries = new XYChart.Series<String, Number>();
        rainfallSeries.setName("Total Rainfall");
        lineChartTemp.setLegendSide(Side.RIGHT);
        lineChartAf.setLegendSide(Side.RIGHT);
        lineChartRainfall.setLegendSide(Side.RIGHT);




        for (int i=keys.size()-1; i>=0; i--){
            tMaxSeries.getData().add(new XYChart.Data<>(keys.toArray()[i], stationInfoTreeMap.get(keys.toArray()[i]).getTempMax()));
            tMinSeries.getData().add(new XYChart.Data<>(keys.toArray()[i], stationInfoTreeMap.get(keys.toArray()[i]).getTempMin()));
            afSeries.getData().add(new XYChart.Data<>(keys.toArray()[i], stationInfoTreeMap.get(keys.toArray()[i]).getAfTotal()));
            rainfallSeries.getData().add(new XYChart.Data<>(keys.toArray()[i], stationInfoTreeMap.get(keys.toArray()[i]).getTotalRainfall()));
        }
        lineChartTemp.getData().addAll(tMaxSeries, tMinSeries);


        lineChartAf.getData().add(afSeries);
        lineChartRainfall.getData().add(rainfallSeries);
    }

    public void setLabels(LineChart linechart, String xLabel, String yLabel){
        linechart.getXAxis().setLabel(xLabel);
        linechart.getYAxis().setLabel(yLabel);
    }
}


