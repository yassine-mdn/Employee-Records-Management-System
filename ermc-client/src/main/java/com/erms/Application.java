package com.erms;

import com.erms.auth.Login;
import com.erms.context.CardPanelManager;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import raven.toast.Notifications;


import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {
    public Application() {
        init();
    }

    private void init() {
        setTitle("FlatLaf Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1200, 700));
        CardPanelManager cardPanelManager = CardPanelManager.getInstance();
        JPanel cardPanel = cardPanelManager.getCardPanel();
        cardPanel.add(new Login(), "login-page");
        cardPanel.add(new JPanel(), "test");
        add(cardPanel);
        cardPanelManager.showPanel("login-page");
        Notifications.getInstance().setJFrame(this);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> new Application().setVisible(true));
    }

}