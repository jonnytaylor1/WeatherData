package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import sample.datamodel.StationInfo;
import sample.datamodel.TextFileInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;

public class allStationsController implements Initializable {
    @FXML
    private TableView<StationInfo> tableview;
    @FXML
    private TableColumn<StationInfo, String> colStation;
    @FXML
    private TableColumn colLatestYear;
    @FXML
    private TableColumn<StationInfo, Double> colHighestTmax;
    @FXML
    private TableColumn<StationInfo, Double> colLowestTmin;
    @FXML
    private TableColumn<StationInfo, Integer> colTotalAFDays;
    @FXML
    private TableColumn<StationInfo, Double> colTotalRainfall;
    @FXML
    private TableColumn<StationInfo, String> colDetailedInfo;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab allWeatherTab;
    @FXML
    private Tab detailedTab;

    public AnchorPane anchorPane;

    private ObservableList<StationInfo> observableList;
    private List<TreeMap<String, StationInfo>> stationList;

    public Button generateReportBtn;
    private List<TextFileInfo> txtFileInfoArray;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allWeatherTab.setClosable(false);
        populateTable();
    }

    //Populates the summary table of all stations

    public void populateTable() {
        colStation.setCellValueFactory(new PropertyValueFactory<>("StationName"));
        colLatestYear.setCellValueFactory(new PropertyValueFactory<>("Year"));
        colHighestTmax.setCellValueFactory(new PropertyValueFactory<>("TempMax"));
        colLowestTmin.setCellValueFactory(new PropertyValueFactory<>("TempMin"));
        colTotalAFDays.setCellValueFactory(new PropertyValueFactory<>("AfTotal"));
        colTotalRainfall.setCellValueFactory(new PropertyValueFactory<>("TotalRainfall"));

        observableList = FXCollections.observableArrayList();


        //Iterates through all of the station treemap objects and
        //retrieves the information relating to the most recent year
        //Ignores empty files

        try {
            stationList = RetrieveAndStoreData.createData()[0];
            stationList.forEach( item ->{
                if(!item.isEmpty()){
                    String firstYear = item.firstKey();
                    StationInfo stationInfo = item.get(firstYear);
                    observableList.add(stationInfo);
                }
            } );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //Creates the buttons to the right of each row
        //Each of these buttons collects the index information
        //and activates the sendStationIndex function when clicked

        //The code from line 132-163 was adapted from the reference:
        //Dan Mlayah. 2019. JavaFX Tableview with Mysql Database and Action Buttons. Available at: https://www.youtube.com/watch?v=gvko7jLPZT0 [Accessed: 29 March 2020].

        Callback<TableColumn<StationInfo, String>, TableCell<StationInfo, String>> cellFactory=(param)-> {
            final TableCell<StationInfo, String> cell = new TableCell<StationInfo, String>() {

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        final Button detailsButton = new Button("Historical Weather");
                        detailsButton.setOnAction(event -> {
                            int index = getIndex();
                            try {
                                sendStationIndex(index);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        setGraphic(detailsButton);
                        setText(null);
                    }

                }
            };
            return cell;
        };

        colDetailedInfo.setCellFactory(cellFactory);

        tableview.setItems(observableList);
    };

    //Generates the txt file and stores in the JavafX Coursework directory

    public void generateReport(ActionEvent actionEvent) throws FileNotFoundException {
        txtFileInfoArray = RetrieveAndStoreData.createData()[1];
        try {
            File file = new File("summary_report.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter printWriter = new PrintWriter(file);

            txtFileInfoArray.forEach( txtFile-> {
                String stationName = txtFile.getStationName();
                int sequenceNumber = txtFile.getSequenceNumber();
                String tMaxMonthYear = txtFile.gettMaxMonthYear();
                String tMinMonthYear = txtFile.gettMinMonthYear();
                Double averageAnnualAf = txtFile.getAverageAfDays();
                Double averageAnnualRainfall = txtFile.getAverageRainfallDays();

                printWriter.println("Sequence Number: " + sequenceNumber);
                printWriter.println("Station: " + stationName);
                printWriter.println("Highest: " + tMaxMonthYear);
                printWriter.println("Lowest: " + tMinMonthYear);
                printWriter.println("Average annual af: " + averageAnnualAf + " days");
                printWriter.println("Average annual rainfall: " + averageAnnualRainfall + "mm");
                printWriter.println();
            });
            printWriter.close();
            Alert a = new Alert(Alert.AlertType.NONE);
            a.setTitle("Report Generated");
            a.getDialogPane().getButtonTypes().add(ButtonType.OK);
            a.setContentText("The report has been generated and \n is stored in the 'JavaFX Coursework' directory");
            a.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Sends the station index and the key information (years) to the detailed table controller

    //Also adds a tab with the detailed weather information of a specific station (the station name is set as the text of the tab)


    public void sendStationIndex(int index) throws IOException {



        RetrieveAndStoreData.setIndex(index);
        Set<String> keys = stationList.get(index).keySet();
        RetrieveAndStoreData.setIndex(index);
        RetrieveAndStoreData.setKeySet(keys);
        anchorPane = new AnchorPane();
        detailedTab = new Tab();
        Object firstKey = keys.toArray()[0];
        String stationName = stationList.get(index).get(firstKey).getStationName();
        detailedTab.setText(stationName);


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("detailedTable.fxml"));
        Node node = loader.load();

        detailedTab.setContent(node);
        tabPane.getTabs().add(detailedTab);
    }
}
