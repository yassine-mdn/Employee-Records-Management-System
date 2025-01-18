package com.erms.pages.home;

import com.erms.client.Employee.EmployeeClient;
import com.erms.model.ApiError;
import com.erms.model.Employee;
import com.erms.model.EmployeeDto;
import com.erms.model.enums.Department;
import com.erms.model.enums.EmploymentStatus;
import com.erms.model.enums.Role;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.datetime.DatePicker;
import raven.toast.Notifications;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

public class DefaultPanel extends javax.swing.JPanel {

    DefaultPanel() {
       init();
    }


    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
        txtFullname = new JTextField();
        txtEmail = new JTextField();
        txtJobTitle = new JTextField();
        txtContactInfo = new JTextField();
        txtAddress = new JTextField();
        dpHireDate = new DatePicker();
        btnAddEmployee = new JButton("Add Employee");
        cmbDepartment = new JComboBox<>(Department.values());
        cmbEmploymentStatus = new JComboBox<>(EmploymentStatus.values());
        cmbRole = new JComboBox<>(Role.values());

        JFormattedTextField editor = new JFormattedTextField();
        dpHireDate.setEditor(editor);

        BufferedImage myPicture = null;
        try {
            myPicture = ImageIO.read(new File("src/main/resources/static/logo.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 45 55 40 55", "fill,600:280"));


        btnAddEmployee.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:@accentColor;" +
                "foreground:@background;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        txtFullname.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter full name");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter email");
        txtJobTitle.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter job title");
        txtContactInfo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter contact info");
        txtAddress.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter address");

        JLabel lbTitle = new JLabel("Add new Employee");
        JLabel description = new JLabel("Add new Employee to database");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        panel.add(lbTitle);
        panel.add(description);
        panel.add(new JLabel("Full Name"), "gapy 8");
        panel.add(txtFullname);
        panel.add(new JLabel("Email"), "gapy 8");
        panel.add(txtEmail);
        panel.add(new JLabel("Job title"), "gapy 8");
        panel.add(txtJobTitle);
        panel.add(new JLabel("Department"), "gapy 8");
        panel.add(cmbDepartment);
        panel.add(new JLabel("Hire date"), "gapy 8");
        panel.add(editor);
        panel.add(new JLabel("Employment status"), "gapy 8");
        panel.add(cmbEmploymentStatus);
        panel.add(new JLabel("Contact info"), "gapy 8");
        panel.add(txtContactInfo);
        panel.add(new JLabel("Address"), "gapy 8");
        panel.add(txtAddress);
        panel.add(new JLabel("Role"), "gapy 8");
        panel.add(cmbRole);
        panel.add(btnAddEmployee, "gapy 10");
        add(panel);



        btnAddEmployee.addActionListener(e -> {
            EmployeeDto body = EmployeeDto.builder()
                    .fullName(txtFullname.getText())
                    .jobTitle(txtJobTitle.getText())
                    .email(txtEmail.getText())
                    .department((Department) cmbDepartment.getSelectedItem())
                    .employmentStatus((EmploymentStatus) cmbEmploymentStatus.getSelectedItem())
                    .contactInformation(txtContactInfo.getText())
                    .address(txtAddress.getText())
                    .hireDate(dpHireDate.getSelectedDate())
                    .role((Role) cmbRole.getSelectedItem())
                    .build();

            System.out.println(body);
            EmployeeClient employeeClient = new EmployeeClient();
            try {
                Object response = employeeClient.addEmployee(body);
                if (response instanceof Employee) {
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.BOTTOM_RIGHT, "Employee added successfully");
                } else {
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT, ((ApiError) response).getMessage());
                }
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        });

    }

    private void cleanForm() {
        txtFullname.setText("");
        txtEmail.setText("");
        txtJobTitle.setText("");
        txtContactInfo.setText("");
        txtAddress.setText("");
    }


    private JTextField txtFullname;
    private JTextField txtEmail;
    private JTextField txtJobTitle;
    private JTextField txtContactInfo;
    private JTextField txtAddress;
    private DatePicker dpHireDate;
    private JComboBox<Department> cmbDepartment;
    private JComboBox<EmploymentStatus> cmbEmploymentStatus;
    private JComboBox<Role> cmbRole;
    private JButton btnAddEmployee;
}
