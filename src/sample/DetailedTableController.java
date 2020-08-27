package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.datamodel.StationInfo;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

public class DetailedTableController implements Initializable {

    private int index = RetrieveAndStoreData.getIndex();
    private Set keys = RetrieveAndStoreData.getKeySet();
    @FXML private TableView<StationInfo> tableView;
    public TableColumn<StationInfo, String> colYear;
    public TableColumn<StationInfo, Double> colHighestTMax;
    public TableColumn<StationInfo, Double> colLowestTMin;
    public TableColumn<StationInfo, Integer> colTotalAfDays;
    public TableColumn<StationInfo, Double> colTotalRainfall;
    public ObservableList<StationInfo> detailedObservableList;
    private TreeMap<String, StationInfo> stationInfoMap;
    private List<TreeMap<String, StationInfo>> stationList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateDetailedTable();
    }

    public void populateDetailedTable(){
        colYear.setCellValueFactory(new PropertyValueFactory<>("Year"));
        colHighestTMax.setCellValueFactory(new PropertyValueFactory<>("TempMax"));
        colLowestTMin.setCellValueFactory(new PropertyValueFactory<>("TempMin"));
        colTotalAfDays.setCellValueFactory(new PropertyValueFactory<>("AfTotal"));
        colTotalRainfall.setCellValueFactory(new PropertyValueFactory<>("TotalRainfall"));
        detailedObservableList = FXCollections.observableArrayList();
        try {
            stationList = RetrieveAndStoreData.createData()[0];
            stationInfoMap = stationList.get(index);
            keys.forEach( key ->{
                StationInfo stationInfo = stationInfoMap.get(key);
                detailedObservableList.add(stationInfo);
            } );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        tableView.setItems(detailedObservableList);
    }
}
