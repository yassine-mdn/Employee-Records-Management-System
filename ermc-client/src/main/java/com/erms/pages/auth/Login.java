package com.erms.pages.auth;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.erms.Application;
import com.erms.client.auth.AuthClient;
import com.erms.context.AuthenticatedEmployee;
import com.erms.context.CardPanelManager;
import com.erms.manager.FormsManager;
import com.erms.model.ApiError;
import com.erms.model.AuthenticationRequest;
import com.erms.model.AuthenticationResponse;
import com.erms.model.enums.Role;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Login extends JPanel {

    public Login() {
        init();
    }



    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cmdLogin = new JButton("Login");

        BufferedImage myPicture = null;
        try {
            myPicture = ImageIO.read(new File("src/main/resources/static/logo.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JLabel picLabel = new JLabel(new ImageIcon(myPicture.getScaledInstance(222, 125, Image.SCALE_SMOOTH)));

        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 45 55 40 55", "fill,250:280"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter email");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        JLabel lbTitle = new JLabel("Welcome back!");
        JLabel description = new JLabel("Please sign in to access your account");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        panel.add(picLabel);
        panel.add(lbTitle);
        panel.add(description);
        panel.add(new JLabel("Email"), "gapy 8");
        panel.add(txtUsername);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(txtPassword);
        panel.add(cmdLogin, "gapy 10");
        add(panel);



        cmdLogin.addActionListener(e -> {
            String username = txtUsername.getText();
            char[] password = txtPassword.getPassword();
            AuthClient authClient = new AuthClient();
            // Replace with actual login logic
            try {
                var response = authClient.login(new AuthenticationRequest(username,new String(password)));
                if (response instanceof AuthenticationResponse) {
                    AuthenticatedEmployee.getInstance().setAuthenticationResponse((AuthenticationResponse) response);
                    var role = AuthenticatedEmployee.getInstance().getAuthenticationResponse().getRole();
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.BOTTOM_LEFT,"Login successful");
                    if (role.equals(Role.ADMIN)) {
                        System.out.println("Admin logged in");
                        getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(23,180,252));
                        getRootPane().putClientProperty("JRootPane.titleBarForeground", Color.white);
                        // TODO : navigate to admin panel
                    } else if (role.equals(Role.HR)) {
                        // TODO:
                    } else {
                        // dfghjk
                    }
                    Application.login();
                } else {
                    Notifications.getInstance().show(Notifications.Type.ERROR,Notifications.Location.BOTTOM_RIGHT,"Login unsuccessful");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }



    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton cmdLogin;

}
