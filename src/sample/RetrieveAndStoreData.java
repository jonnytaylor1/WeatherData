package sample;

import sample.datamodel.StationInfo;
import sample.datamodel.TextFileInfo;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class RetrieveAndStoreData {


    private static int index;
    private static Set keySet;

    public static Set getKeySet() {
        return keySet;
    }

    public static void setKeySet(Set keySet) {
        RetrieveAndStoreData.keySet = keySet;
    }

    public static int getIndex() {
        return index;
    }

    public static void setIndex(int index) {
        RetrieveAndStoreData.index = index;
    }


    // Iterates through the files and returns a list array
    //One of the lists is the stationList (containing a treemap for each station
    // which has all of the weather info, key is the year, value is a StationInfo object)
    //The second list contains a list of TextFileInfo objects (one for each station)
    //This contains all of the information needed for the txt file

    public static List[] createData() throws FileNotFoundException {
        List<TreeMap<String, StationInfo>> stationList = new ArrayList<>();
        List<TextFileInfo> txtFileInfoArray = new ArrayList();
        String filePrefix = "CMT205CWDATA/";
        File fileDirectory = new File(filePrefix);
        List[] filesList = listFilesInDirectory(fileDirectory);
        List<String> fileNames = filesList[0];
        List<String> stationNames = filesList[1];
        int sequenceNumber = 1;
        for (int i=0; i<fileNames.size(); i++){
            TreeMap<String, StationInfo> sortedMap = fromCSVtoModel(filePrefix + fileNames.get(i), stationNames.get(i));
            TextFileInfo textFileInfo = csvToTxtFile(filePrefix + fileNames.get(i), sequenceNumber,stationNames.get(i));
            if(!sortedMap.isEmpty()){
                stationList.add(sortedMap);
                txtFileInfoArray.add(textFileInfo);
                sequenceNumber++;
            }
        }
        List[] dataLists = {stationList, txtFileInfoArray};
        return dataLists;
    }

    //This removes the extension of the file. This was needed
    //to separate the station name

    public static String removeExtension(String fileName){
        String[] splitFile = fileName.split("\\.");
        String fileNameNoExtension = splitFile[0];
        return fileNameNoExtension;
    }

    //Returns a list of the files (including the extension)
    //Returns a list of the station names

    public static List[] listFilesInDirectory(File directory) {
        List<String> filesList = new LinkedList<>();
        List<String> stationNames = new LinkedList<>();
        Integer i =0;

        for ( File fileEntry : directory.listFiles()) {
            String fileName = fileEntry.getName();
            if(!fileName.contains(".DS_Store")){
                stationNames.add(removeExtension(fileName));
                filesList.add(fileName);
            }
        }
        Collections.sort(filesList);
        Collections.sort(stationNames);
        List[] listArray = {filesList, stationNames};

        return listArray;
    }


    //Reads the file with the specified filepath and returns a treemap with the year as a key and the stationInfo model as a value

    public static TreeMap<String, StationInfo> fromCSVtoModel(String filePath, String stationName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        Scanner lineScanner = null;
        int index = 0;
        int monthCounter = 1;
        List<Double> tMaxList = new ArrayList<>();
        List<Double> tMinList = new ArrayList<>();
        Double highestTmax;
        Double lowestTmin;
        Integer afDays = 0;
        Double totalRainfall = 0.0;
        TreeMap<String, StationInfo> sortedMap = new TreeMap<>(Collections.reverseOrder());


        String currentYear = "";
        while (scanner.hasNextLine()) {
            lineScanner = new Scanner(scanner.nextLine());
            lineScanner.useDelimiter(",");

            while (lineScanner.hasNext()) {
                String data = lineScanner.next();

                if (monthCounter == 1 && index == 0){
                    currentYear = data;
                }
                if (index == 2)
                    tMaxList.add(Double.parseDouble(data));
                else if (index == 3)
                    tMinList.add(Double.parseDouble(data));
                else if (index == 4)
                    afDays += Integer.parseInt(data);
                else if (index == 5){
                    totalRainfall += Double.parseDouble(data);
                }
                else {

                }
                index++;
            }
            index = 0;
            if(monthCounter == 12 ){
                highestTmax = Collections.max(tMaxList);
                lowestTmin = Collections.min(tMinList);
                tMaxList = new ArrayList<>();
                tMinList = new ArrayList<>();
                BigDecimal bd = new BigDecimal(totalRainfall).setScale(2, RoundingMode.HALF_UP);
                totalRainfall = bd.doubleValue();
                StationInfo weatherInfo = new StationInfo(stationName, currentYear, highestTmax, lowestTmin, afDays, totalRainfall);
                sortedMap.put(currentYear, weatherInfo);
                monthCounter = 1;
            }
            else{
                monthCounter++;
            }
        }
        scanner.close();
        return sortedMap;
    }

    //Collects information from the CSV file and returns a textFileInfo object with
    //the relavent information needed for the txt file

    public static TextFileInfo csvToTxtFile(String filePath, int sequenceNumber, String stationName) throws FileNotFoundException {
        //Info Needed
        String reportStationName = stationName;
        int monthCounter = 1;
        int yearCounter = 1;
        int index = 0;
        Scanner scanner = new Scanner(new File(filePath));
        Scanner lineScanner;

        String currentYear = "";
        String tempMaxYear = "";
        String tempMaxMonth = "";
        String tempMinYear = "";
        String tempMinMonth = "";
        Double tempMax = null;
        Double tempMin = null;
        Double afDaysRunningTotal = 0.0;
        Double totalYearlyRainfall = 0.0;
        Double totalRainfallRunningTotal = 0.0;
        String tMaxMonthYear;
        String tMinMonthYear;
        Double afDaysYearly = 0.0;

        while (scanner.hasNextLine()) {
            lineScanner = new Scanner(scanner.nextLine());
            lineScanner.useDelimiter(",");

            while (lineScanner.hasNext()) {
                String data = lineScanner.next();
                if (monthCounter == 1 && index == 0) {
                    currentYear = data;
                } else if (index == 2 && (tempMax == null || Double.parseDouble(data) > tempMax)) {
                    tempMax = Double.parseDouble(data);
                    tempMaxYear = currentYear;
                    tempMaxMonth = numberToMonth(monthCounter);
                } else if (index == 3 && (tempMin == null || Double.parseDouble(data) < tempMin)) {
                    tempMin = Double.parseDouble(data);
                    tempMinYear = currentYear;
                    tempMinMonth = numberToMonth(monthCounter);
                } else if (index == 4) {
                    afDaysYearly += Double.parseDouble(data);
                } else if (index == 5) {
                    totalYearlyRainfall += Double.parseDouble(data);
                }
                index++;
            }
            index = 0;
            if (monthCounter == 12 && scanner.hasNextLine()) {
                afDaysRunningTotal += afDaysYearly;
                afDaysYearly = 0.0;
                totalRainfallRunningTotal += totalYearlyRainfall;
                totalYearlyRainfall = 0.0;
                yearCounter ++;
                monthCounter = 1;
            }
            else if(monthCounter == 12){
                afDaysRunningTotal += afDaysYearly;
                totalRainfallRunningTotal += totalYearlyRainfall;
            }
            else {
                monthCounter++;
            }
        }
        Double averageAfDays = afDaysRunningTotal / yearCounter;
        BigDecimal bd = new BigDecimal(averageAfDays).setScale(2, RoundingMode.HALF_UP);
        averageAfDays = bd.doubleValue();
        Double averageRainfall = totalRainfallRunningTotal / yearCounter;
        BigDecimal bd2 = new BigDecimal(averageRainfall).setScale(2, RoundingMode.HALF_UP);
        averageRainfall = bd2.doubleValue();
        tMaxMonthYear = tempMaxMonth + " " + tempMaxYear;
        tMinMonthYear = tempMinMonth + " " + tempMinYear;

        TextFileInfo textFileInfo = new TextFileInfo(sequenceNumber, stationName, tMaxMonthYear, tMinMonthYear, averageAfDays, averageRainfall);
        return textFileInfo;
    }




    public static String numberToMonth(int number) {
        String monthString = "";
        switch (number) {
            case 1:
                monthString = "January";
                break;
            case 2:
                monthString = "February";
                break;
            case 3:
                monthString = "March";
                break;
            case 4:
                monthString = "April";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "June";
                break;
            case 7:
                monthString = "July";
                break;
            case 8:
                monthString = "August";
                break;
            case 9:
                monthString = "September";
                break;
            case 10:
                monthString = "October";
                break;
            case 11:
                monthString = "November";
                break;
            case 12:
                monthString = "December";
                break;
        }
        return monthString;
    }
}