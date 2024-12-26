package com.depot;

import com.depot.gui.controller.DepotController;
import com.depot.gui.model.DepotModel;
import com.depot.gui.view.DepotView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DepotModel model = new DepotModel();
            DepotView view = new DepotView();
            DepotController controller = new DepotController(model, view);
            view.setVisible(true);
        });
    }
} 