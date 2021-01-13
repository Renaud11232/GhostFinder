package be.renaud11232.ghostfinder.controller;

import be.renaud11232.ghostfinder.ghost.Evidence;
import be.renaud11232.ghostfinder.ghost.Ghost;
import be.renaud11232.ghostfinder.ghost.GhostIdentifier;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import jfxtras.styles.jmetro.MDL2IconFont;

import java.net.URL;
import java.util.*;

public class MainWindowController implements Initializable {

    private final GhostIdentifier ghostIdentifier;
    private final Map<Evidence, Label> labels;
    private final Map<Evidence, ToggleButton> foundButtons;
    private final Map<Evidence, ToggleButton> rejectedButtons;
    private final List<Label> ghostLabels;
    private double LABELS_Y_OFFSET;

    @FXML
    private AnchorPane container;

    private static final double BLOCK_SIZE = 50;
    private static final double OFFSET = 10;
    private static final double TEXT_OFFSET = 12.5;

    public MainWindowController() {
        ghostIdentifier = new GhostIdentifier();
        labels = new HashMap<>();
        foundButtons = new HashMap<>();
        rejectedButtons = new HashMap<>();
        ghostLabels = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        double layoutY = OFFSET;
        for(Evidence evidence : Evidence.values()) {
            ToggleGroup toggleGroup = new ToggleGroup();
            ToggleButton found = createButton(layoutY, toggleGroup, new MDL2IconFont("\uE73E"),"found");
            found.setLayoutX(OFFSET);
            ToggleButton rejected = createButton(layoutY, toggleGroup, new MDL2IconFont("\uE711"),"rejected");
            rejected.setLayoutX(BLOCK_SIZE + OFFSET * 2);
            Label label = new Label(evidence.toString());
            label.setLayoutX(3 * OFFSET + 2 * BLOCK_SIZE);
            label.setLayoutY(layoutY + TEXT_OFFSET);
            label.setFont(new Font(BLOCK_SIZE / 3));
            labels.put(evidence, label);
            foundButtons.put(evidence, found);
            rejectedButtons.put(evidence, rejected);
            container.getChildren().addAll(found, rejected, label);
            layoutY += BLOCK_SIZE + OFFSET;
            toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if(oldValue == found) {
                    ghostIdentifier.removeFoundEvidence(evidence);
                } else if(oldValue == rejected) {
                    ghostIdentifier.removeRejectedEvidence(evidence);
                }
                if(newValue == found) {
                    ghostIdentifier.addFoundEvidence(evidence);
                } else if (newValue == rejected) {
                    ghostIdentifier.addRejectedEvidence(evidence);
                }
                labels.values().forEach(l -> l.setTextFill(Color.GREY));
                foundButtons.values().forEach(b -> b.setDisable(true));
                rejectedButtons.values().forEach(b -> b.setDisable(true));
                ghostIdentifier.getPossibleEvidences().forEach(e -> {
                    labels.get(e).setTextFill(Color.WHITE);
                    rejectedButtons.get(e).setDisable(false);
                    foundButtons.get(e).setDisable(false);
                });
                displayGhosts();
            });
        }
        LABELS_Y_OFFSET = layoutY + 20;
        displayGhosts();
    }

    private ToggleButton createButton(double layoutY, ToggleGroup toggleGroup, Node graphic, String cssClass) {
        ToggleButton button = new ToggleButton();
        button.setLayoutY(layoutY);
        button.setToggleGroup(toggleGroup);
        button.setMinWidth(BLOCK_SIZE);
        button.setMaxWidth(BLOCK_SIZE);
        button.setPrefWidth(BLOCK_SIZE);
        button.setMinHeight(BLOCK_SIZE);
        button.setMaxHeight(BLOCK_SIZE);
        button.setPrefHeight(BLOCK_SIZE);
        button.getStyleClass().add(cssClass);
        graphic.setStyle("-fx-font-size: " + BLOCK_SIZE / 2);
        button.setGraphic(graphic);
        return button;
    }

    private void displayGhosts() {
        container.getChildren().removeAll(ghostLabels);
        ghostLabels.clear();
        Collection<Ghost> possibleGhosts = ghostIdentifier.getPossibleGhosts();
        if(possibleGhosts.size() == 0) {
            createGhostLabel("No existing ghost found", OFFSET,  LABELS_Y_OFFSET);
        } else if(possibleGhosts.size() == 1) {
            Ghost ghost = possibleGhosts.stream().findFirst().get();
            createGhostLabel("The ghost is " + ghostAsString(ghost), OFFSET,  LABELS_Y_OFFSET);
        } else {
            double layoutY = LABELS_Y_OFFSET;
            createGhostLabel("The ghost can be :", OFFSET, LABELS_Y_OFFSET);
            for(Ghost ghost : possibleGhosts) {
                layoutY += OFFSET + BLOCK_SIZE / 4;
                createGhostLabel("\u2022 " + ghostAsString(ghost), OFFSET + BLOCK_SIZE / 4, layoutY);
            }
        }
    }

    private String ghostAsString(Ghost ghost) {
        String det = "a";
        if(Arrays.asList(new String[]{"A", "E", "I", "O", "U"}).contains(ghost.toString().substring(0, 1))) {
            det = "an";
        }
        return det + " " + ghost;
    }

    private void createGhostLabel(String text, double layoutX, double layoutY) {
        Label ghostLabel = new Label();
        ghostLabel.setText(text);
        ghostLabel.setLayoutX(layoutX);
        ghostLabel.setLayoutY(layoutY);
        ghostLabel.setFont(new Font(BLOCK_SIZE / 4));
        container.getChildren().add(ghostLabel);
        ghostLabels.add(ghostLabel);
    }

}
