package net.gangelov;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.gangelov.transporter.gui.MainDialog;

import java.io.IOException;

public class GUIMain {
    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
//        dialog.pack();
        dialog.setBounds(0, 0, 350, 250);
        dialog.setVisible(true);
    }
}
