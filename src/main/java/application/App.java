package application;

import dao.ConnectionClass;
import dao.Entity;
import dao.entityField.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final int INITIAL_FILE_QUANTITY = 100;
    private static final int INITIAL_STRING_QUANTITY = 100_000;

    //Local Storage for created records
    private static final List<List<Entity>> GIGA_LIST = new ArrayList<>();
    //Connection Object
    private static final Connection CONNECTION = ConnectionClass.getInstance().getConnection();
    private static final String OUTPUT_DIRECTORY_NAME = "outputContent";
    private static final String GIGA_FILE_NAME = "gigaFile.txt";
    private static final String FILE_NAME = "output";


    public static void main(String[] args) {
        launch();
    }

    //Method that starts the application
    private static void launch() {
        Scanner in = new Scanner(System.in);
        while (true) {
            showOptions();
            System.out.print("\nEnter: ");
            int key = in.nextInt();
            switch (key) {
                case 0:
                    closeConnection();
                    in.close();
                    System.exit(1);
                case 1:
                    generateGigaList();
                    System.out.println("Strings generated\n\n");
                    break;
                case 2:
                    getInfoFromDatabase();
                    System.out.println("Done\n\n");
                    break;
                case 3:
                    truncateTable();
                    System.out.println("Cleared\n\n");
                    break;
                case 4:
                    generateFiles();
                    generateGigaFile();
                    System.out.println("Check output directory\n\n");
                    break;
                case 5:
                    addGigaListInDb();
                    break;
                case 6:
                    System.out.println("The Sum of Ints: " + getSumOfIntsFromDb() + "\n\n");
                    break;
                case 7:
                    System.out.println("The Median of Doubles: " + getMedianDouble() + "\n\n");
                    break;
                case 8:
                    in = new Scanner(System.in);
                    System.out.print("Enter the sequence to find and delete: ");
                    deleteFromGigaListAndDbIfContains(in.nextLine());
                    System.out.println("Deleted\n\n");
                    break;
                case 9:
                    printGigaList();
                    break;
            }
        }
    }

    //Method that prints all options
    private static void showOptions() {
        System.out.println("Choose the action: \n");
        System.out.println("1 \t-\t Generate (existing records will be deleted) " + INITIAL_FILE_QUANTITY + " files (Local Storage) with " +
                INITIAL_STRING_QUANTITY + " records");
        System.out.println("2 \t-\t Get Records from Database");
        System.out.println("3 \t-\t Clear Table");
        System.out.println("4 \t-\t Create (existing files will be deleted)" + INITIAL_FILE_QUANTITY + " files with " + INITIAL_STRING_QUANTITY +
                " records + General File with name: " + GIGA_FILE_NAME + "; Output Directory Name: " + OUTPUT_DIRECTORY_NAME);
        System.out.println("5 \t-\t Import all records into Database (from Local Storage)");
        System.out.println("6 \t-\t Get the Sum of Ints from Database");
        System.out.println("7 \t-\t Get the Median of Doubles from Database");
        System.out.println("8 \t-\t Delete from Database by Sequence");
        System.out.println("9 \t-\t Print all records (from Local Storage)");
        System.out.println("0 \t-\t Exit");
    }

    //Method to get Data from Database
    private static void getInfoFromDatabase() {
        String sql = "SELECT * FROM entity";
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = CONNECTION.createStatement();
            //get all records from database
            rs = statement.executeQuery(sql);
            //move all records to local storage
            for (int i = 0; i < INITIAL_FILE_QUANTITY; i++) {
                List<Entity> tmpList = new ArrayList<>();
                while (rs.next()) {
                    Entity entity = new Entity(
                            new RndDate(rs.getDate(2)),
                            new RndStringEn(rs.getString(3)),
                            new RndStringRu(rs.getString(4)),
                            new RndInt(rs.getLong(5)),
                            new RndDouble(rs.getDouble(6)));
                    tmpList.add(entity);
                }
                GIGA_LIST.add(tmpList);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            //close resources
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    //Method that calls stored procedure that returns the Median
    private static Double getMedianDouble() {
        String sql = "{CALL sp_getMedianDouble()}";
        CallableStatement statement = null;
        ResultSet rs = null;
        Double result = null;
        try {
            statement = CONNECTION.prepareCall(sql);
            rs = statement.executeQuery();
            rs.next();
            result = rs.getDouble(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            //close resources
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
        //to get the double value with 8 signs after the point
        DecimalFormat df = new DecimalFormat("#.00000000");
        return Double.valueOf(df.format(result));
    }

    //Method that calls stored procedure that returns the Sum of Integers
    private static Long getSumOfIntsFromDb() {
        String sql = "{CALL sp_getIntSum()}";
        CallableStatement statement = null;
        ResultSet rs = null;
        Long result = null;
        try {
            statement = CONNECTION.prepareCall(sql);
            rs = statement.executeQuery();
            rs.next();
            result = rs.getLong(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            //close resources
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
        return result;
    }

    //Method that inserts records from local storage into database
    private static void addGigaListInDb() {
        if (GIGA_LIST.size() == 0) {
            System.out.println("Nothing to insert\n\n");
            return;
        }
        PreparedStatement statement = null;
        try {
            statement = CONNECTION
                    .prepareStatement("INSERT INTO entity(date, string_en, string_ru, rnd_int, rnd_double) VALUES (?, ?, ?, ?, ?)");

            int progress = 0;
            for (int i = 0; i < INITIAL_FILE_QUANTITY; i++) {
                for (Entity entity : GIGA_LIST.get(i)) {
                    statement.setDate(1, entity.getDate());
                    statement.setString(2, entity.getStringEn());
                    statement.setString(3, entity.getStringRu());
                    statement.setLong(4, entity.getInt());
                    statement.setDouble(5, entity.getDouble());
                    statement.executeUpdate();
                    showProgress(++progress);
                }
            }
            System.out.println("Added into Database\n\n");
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            //close resources
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    //Method that shows progress
    private static void showProgress(int rem) {
        int strQnt = 0;
        for (int i = 0; i < INITIAL_FILE_QUANTITY; i++) {
            strQnt += GIGA_LIST.get(i).size();
        }
        DecimalFormat df = new DecimalFormat("0.00");
        double percentage = (double) rem * 100 / (double) strQnt;
        if (percentage != 100) {
            System.out.print("\rImporting records: " + rem + " of " + strQnt + "\t(" + df.format(percentage) + "%)");
        } else {
            System.out.println("\rImporting records: " + rem + " of " + strQnt + "\t(" + df.format(percentage) + "%)");
        }
    }

    //Method that clears database
    private static void truncateTable() {
        Statement statement = null;
        try {
            statement = CONNECTION.createStatement();
            String sql = "TRUNCATE TABLE entity";
            statement.executeUpdate(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            //close resources
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    //Method that finds and deletes records by sequence
    private static void deleteFromGigaListAndDbIfContains(String seq) {
        if ("||".equals(seq) || seq.length() < 2) {
            System.out.println("Wrong sequence");
            return;
        }
        if (GIGA_LIST.size() != 0) {
            for (int i = 0; i < INITIAL_FILE_QUANTITY; i++) {
                List<Entity> tmpList = GIGA_LIST.get(i);
                for (int j = 0; j < tmpList.size(); j++) {
                    String stringToCompare = tmpList.get(j).toString();
                    if (stringToCompare.contains(seq)) {
                        GIGA_LIST.get(i).remove(j);
                        j--;
                    }
                }
            }
        } else {
            System.out.println("Local Storage is empty, trying to delete from Database");
        }
        PreparedStatement statement = null;
        try {
            statement = CONNECTION.prepareStatement("DELETE FROM entity WHERE string_en LIKE ? OR string_ru LIKE ?");
            statement.setString(1, "%" + seq + "%");
            statement.setString(2, "%" + seq + "%");
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            //close resources
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    //Method that creates the File with all the records from small files
    private static void generateGigaFile() {
        File file = new File(OUTPUT_DIRECTORY_NAME + "/" + GIGA_FILE_NAME);
        BufferedWriter bf = null;
        try {
            file.createNewFile();
            bf = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < INITIAL_FILE_QUANTITY; i++) {
                List<Entity> tmpList = GIGA_LIST.get(i);
                for (int j = 0; j < tmpList.size(); j++) {
                    bf.append(tmpList.get(j).toString());
                    if (i + 1 < INITIAL_FILE_QUANTITY || j + 1 < tmpList.size()) {
                        bf.append("\n");
                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            //close resources
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    //Method that creates several files(small files)
    private static void generateFiles() {
        cleanOutputDirectory(new File(OUTPUT_DIRECTORY_NAME));
        File file = new File(OUTPUT_DIRECTORY_NAME);
        file.mkdir();
        for (int i = 0; i < INITIAL_FILE_QUANTITY; i++) {
            String fileName = FILE_NAME.concat(String.valueOf(i + 1)).concat(".txt");
            File outputFile = new File(OUTPUT_DIRECTORY_NAME + "/" + fileName);
            BufferedWriter bf = null;
            try {
                outputFile.createNewFile();
                bf = new BufferedWriter(new FileWriter(outputFile));
                List<Entity> tmpList = GIGA_LIST.get(i);
                for (int j = 0; j < tmpList.size(); j++) {
                    bf.append(tmpList.get(j).toString());
                    if (j + 1 < INITIAL_STRING_QUANTITY) {
                        bf.append("\n");
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                //close resources
                if (bf != null) {
                    try {
                        bf.close();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    //Method that generates all records and saves them in local storage
    private static void generateGigaList() {
        GIGA_LIST.clear();
        for (int i = 0; i < INITIAL_FILE_QUANTITY; i++) {
            List<Entity> tmpList = new ArrayList<>();
            for (int j = 0; j < INITIAL_STRING_QUANTITY; j++) {
                tmpList.add(new Entity());
            }
            GIGA_LIST.add(tmpList);
        }
    }

    //Method that cleans up the output directory (invokes on files creating or can be invoked by user)
    private static void cleanOutputDirectory(File fileToBeDeleted) {
        File[] allContents = fileToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                cleanOutputDirectory(file);
            }
        }
        fileToBeDeleted.delete();
    }

    //Method that prints local storage records
    private static void printGigaList() {
        if(GIGA_LIST.size()==0){
            System.out.println("Local Storage is empty\n\n");
        }else {
            for (int i = 0; i < INITIAL_FILE_QUANTITY; i++) {
                for (Entity entity : GIGA_LIST.get(i)) {
                    System.out.println(entity.toString());
                }
            }
        }
    }

    //Method that closes the connection to database
    private static void closeConnection() {
        ConnectionClass.getInstance().shutdown();
    }
}
