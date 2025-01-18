package com.erms.context;

import javax.swing.*;
import java.awt.*;

public class CardPanelManager {
    private static CardPanelManager instance;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private CardPanelManager() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
    }

    public static CardPanelManager getInstance() {
        if (instance == null) {
            instance = new CardPanelManager();
        }
        return instance;
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }
}
