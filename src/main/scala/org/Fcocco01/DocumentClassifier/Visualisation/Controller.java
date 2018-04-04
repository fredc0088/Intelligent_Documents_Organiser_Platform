package org.Fcocco01.DocumentClassifier.Visualisation;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ListView inclusionList;
    @FXML
    private ListView exclusionList;
    final ToggleGroup group = new ToggleGroup();
    @FXML
    RadioButton customOption;
    @FXML
    RadioButton defaultOption;
    @FXML
    TextField regexOption;
    @FXML
    ChoiceBox clusteringType;
    @FXML
    ChoiceBox weightingList;
    @FXML
    ChoiceBox strategyList;
    @FXML
    CheckBox independentVector;

    protected ListProperty<String> inclusionListProperty = new SimpleListProperty<>();
    protected ListProperty<String> exclusionListProperty = new SimpleListProperty<>();
    protected File customFile;

    @FXML
    public void loadDirectories(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String id = btn.getId();
        if (id.equals("directoryFileChooser")) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory =
                    directoryChooser.showDialog(null);
            if (selectedDirectory != null) {
                System.out.println("event = [" + event + "], selectedFile = [" + selectedDirectory.getAbsolutePath() + "]");
                List<String> inclusionDirItems = new ArrayList<>();
                if (inclusionListProperty.getValue() != null && !inclusionListProperty.getValue().isEmpty()) {
                    inclusionDirItems.addAll(inclusionListProperty.getValue());
                }
                inclusionDirItems.add(selectedDirectory.getAbsolutePath());
                inclusionListProperty.set(FXCollections.observableArrayList(inclusionDirItems));
            } else {
                System.out.println("event = [" + event + "], selectedFile = [No Directory selected]");
            }
        } else if (id.equals("exclusionFileChooser")) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                System.out.println("event = [" + event + "], selectedFile = [" + selectedFile + "]");
                List<String> exclusionItems = new ArrayList<>();
                if (exclusionListProperty.getValue() != null && !exclusionListProperty.getValue().isEmpty()) {
                    exclusionItems.addAll(exclusionListProperty.getValue());
                }
                exclusionItems.add(selectedFile.getAbsolutePath());
                exclusionListProperty.set(FXCollections.observableArrayList(exclusionItems));
            } else {
                System.out.println("event = [" + event + "], selectedFile = [ File selection cancelled. ]");
            }
        }
    }

    @FXML
    public void radioOptionChange(ActionEvent event) {
        System.out.println("event = [" + event + "]");
        RadioButton btn = (RadioButton) event.getSource();
        String id = btn.getId();
        if (id.equals("customOption")) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            customFile = fileChooser.showOpenDialog(null);
            if (customFile == null) {
                defaultOption.setSelected(true);
            }
        }
        else if (id.equals("defaultOption")) {
            if (customFile != null) {
                customFile=null;
            }
        }
    }

    public void independentVectorOnChange(ActionEvent event) {
        System.out.println("event = [" + event + "]");

    }

    public void onStart(ActionEvent event) {
        inclusionList.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("location = [" + location + "], resources = [" + resources + "]");
        inclusionList.itemsProperty().bind(inclusionListProperty);
        exclusionList.itemsProperty().bind(exclusionListProperty);
        inclusionList.setCellFactory(param -> new XCell());
        exclusionList.setCellFactory(param -> new XCell());

        customOption.setToggleGroup(group);
        customOption.setUserData("custom");
        defaultOption.setToggleGroup(group);
        defaultOption.setUserData("default");
        defaultOption.setSelected(true);

        regexOption.setDisable(false);

        ObservableList<String> clusteringTypeList = FXCollections.observableArrayList(
                "Flat", "Hierarchical");
        clusteringType.getItems().addAll(clusteringTypeList);
        clusteringType.setValue("Hierarchical");
        clusteringType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                System.out.println(clusteringType.getItems().get((Integer) number2));
            }
        });

        /*weightingList.getItems().add("Bag-Of-Words");
        weightingList.getItems().add("TfIdf");
        weightingList.getItems().add("WdIdf");
        weightingList.getItems().add("Wdf");
        weightingList.getItems().add("Tf");
        weightingList.getItems().add("Log Normalisation");*/
        ObservableList<String> weightingTypeList = FXCollections.observableArrayList(
                "Bag-Of-Words", "TfIdf", "WdIdf", "Wdf", "Tf", "Log Normalisation");
        weightingList.getItems().addAll(weightingTypeList);
        weightingList.setValue("Tf");
        weightingList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                System.out.println(weightingList.getItems().get((Integer) number2));
            }
        });

        /*strategyList.getItems().add("Cosine Sim");
        strategyList.getItems().add("Euclidean Dist");
        strategyList.getItems().add("Manhattan Dist");*/
        ObservableList<String> strategyTypeList = FXCollections.observableArrayList(
                "Cosine Sim", "Euclidean Dist", "Manhattan Dist");
        strategyList.getItems().addAll(strategyTypeList);
        strategyList.setValue("Cosine Sim");
        strategyList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                System.out.println(strategyList.getItems().get((Integer) number2));
            }
        });

        independentVector.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println("observable = [" + observable + "], oldValue = [" + oldValue + "], newValue = [" + newValue + "]");
                //independentVector.setSelected(!newValue);
                if(newValue) {
                    weightingList.getItems().remove("TfIdf");
                    weightingList.getItems().remove("WdIdf");
                } else {
                    weightingList.getItems().addAll("TfIdf", "WdIdf");
                }
            }
        });

    }
}
