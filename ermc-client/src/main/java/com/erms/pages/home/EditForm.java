package com.erms.pages.home;

import com.erms.Application;
import com.erms.client.Employee.EmployeeClient;
import com.erms.context.AuthenticatedEmployee;
import com.erms.model.*;
import com.erms.model.enums.Department;
import com.erms.model.enums.EmploymentStatus;
import com.erms.model.enums.Role;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.datetime.DatePicker;
import raven.toast.Notifications;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class EditForm extends JPanel {

    public EditForm() {
        init();
    }

    private void init() {
        setLayout(new MigLayout());
        txtFullname = new JTextField();
        txtEmail = new JTextField();
        txtJobTitle = new JTextField();
        txtContactInfo = new JTextField();
        txtAddress = new JTextField();
        dpHireDate = new DatePicker();
        cmbDepartment = new JComboBox<>(Department.values());
        cmbEmploymentStatus = new JComboBox<>(EmploymentStatus.values());
        cmbRole = new JComboBox<>(Role.values());

        JFormattedTextField editor = new JFormattedTextField();
        dpHireDate.setEditor(editor);

        AuthenticationResponse authEmployee = AuthenticatedEmployee.getInstance().getAuthenticationResponse();
        if (authEmployee == null) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT,"Session Expired Please Login Again");
            Application.logout();
        }


        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 35 45 30 45", "fill,600:280"));


        txtFullname.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter full name");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter email");
        txtJobTitle.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter job title");
        txtContactInfo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter contact info");
        txtAddress.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter address");

        panel.add(new JLabel("Full Name"), "gapy 8");
        panel.add(txtFullname);
        panel.add(new JLabel("Email"), "gapy 8");
        panel.add(txtEmail);
        panel.add(new JLabel("Job title"), "gapy 8");
        panel.add(txtJobTitle);
        if (!authEmployee.getRole().equals(Role.MANAGER)){
            panel.add(new JLabel("Department"), "gapy 8");
            panel.add(cmbDepartment);
            panel.add(new JLabel("Hire date"), "gapy 8");
            panel.add(editor);
            panel.add(new JLabel("Employment status"), "gapy 8");
            panel.add(cmbEmploymentStatus);
        }
        panel.add(new JLabel("Contact info"), "gapy 8");
        panel.add(txtContactInfo);
        panel.add(new JLabel("Address"), "gapy 8");
        panel.add(txtAddress);
        if (authEmployee.getRole().equals(Role.ADMIN)) {
            panel.add(new JLabel("Role"), "gapy 8");
            panel.add(cmbRole);
        }
        add(panel);

    }

    public void setEmployee(String id) {
        EmployeeClient employeeClient = new EmployeeClient();
        try {
            var response = employeeClient.getEmployeeById(id);
            if (response instanceof Employee) {
                Employee employee = (Employee) response;
                txtFullname.setText(employee.getFullName());
                txtEmail.setText(employee.getEmail());
                txtJobTitle.setText(employee.getJobTitle());
                txtContactInfo.setText(employee.getContactInformation());
                txtAddress.setText(employee.getAddress());
                dpHireDate.setSelectedDate(employee.getHireDate());
                cmbDepartment.setSelectedItem(employee.getDepartment());
                cmbEmploymentStatus.setSelectedItem(employee.getEmploymentStatus());
                cmbRole.setSelectedItem(employee.getRole());
            } else {
                ApiError error = (ApiError) response;
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT, error.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public EmployeeDto getEmployeeDto() {
        return EmployeeDto.builder()
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
}
