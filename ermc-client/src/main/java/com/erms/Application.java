package com.erms;

import com.erms.context.AuthenticatedEmployee;
import com.erms.pages.auth.Login;
import com.erms.context.CardPanelManager;
import com.erms.pages.home.Home;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import raven.popup.GlassPanePopup;
import raven.toast.Notifications;


import javax.swing.*;
import java.awt.*;

public class Application extends JFrame {

    private static Application app;
    private final Home homePage;
    private final Login loginPage;

    public Application() {
        initComponents();
        setSize(new Dimension(1300, 800));
        setLocationRelativeTo(null);
        homePage = new Home();
        loginPage = new Login();
        setContentPane(loginPage);
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        Notifications.getInstance().setJFrame(this);
        GlassPanePopup.install(this);
    }


    public static void showHomePage(Component component) {
        component.applyComponentOrientation(app.getComponentOrientation());
        app.homePage.showForm(component);
    }

    public static void login() {
        FlatAnimatedLafChange.showSnapshot();
        app.setContentPane(app.homePage);
        app.homePage.applyComponentOrientation(app.getComponentOrientation());
        setSelectedMenu(0, 0);
        app.homePage.hideMenu();
        SwingUtilities.updateComponentTreeUI(app.loginPage);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void logout() {
        FlatAnimatedLafChange.showSnapshot();
        AuthenticatedEmployee.getInstance().setAuthenticationResponse(null);
        app.setContentPane(app.loginPage);
        app.loginPage.applyComponentOrientation(app.getComponentOrientation());
        SwingUtilities.updateComponentTreeUI(app.loginPage);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void setSelectedMenu(int index, int subIndex) {
        app.homePage.setSelectedMenu(index, subIndex);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 719, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 521, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> {
            app = new Application();
            app.setVisible(true);
        });
    }

}