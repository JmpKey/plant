package org.example.plant.realization;

import org.example.plant.protocol.Message;

import javax.swing.*;

public class MesWin implements Message {
    private static Message instance;

    public static Message getInstance() {
        if (instance == null) {
            instance = new MesWin();
        }
        return instance;
    }

    @Override
    public void showMessage(String text) {
        JOptionPane.showMessageDialog(null, text);
    }
}
