/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.erms.pages.home;

import com.erms.client.Employee.EmployeeClient;
import com.erms.context.AuthenticatedEmployee;
import com.erms.model.*;
import com.erms.model.enums.Role;
import com.erms.utils.ButtonColumn;
import com.erms.utils.TableHeaderAlignment;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import raven.popup.DefaultOption;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.awt.SystemColor.text;
import static java.awt.SystemColor.window;

/**
 * @author yassi
 */
public class EmployeeListPanel extends javax.swing.JPanel {

    /**
     * Creates new form EmployeeListPanel
     */
    public EmployeeListPanel() {
        this.page = 0;
        initComponents();
        init();
    }

    private void init() {
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "background:$Table.background");

        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "height:30;"
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background;"
                + "font:bold;");

        table.putClientProperty(FlatClientProperties.STYLE, ""
                + "rowHeight:30;"
                + "showHorizontalLines:true;"
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");

        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:999;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");

        lableTitle.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:bold +5;");

        btnPrevPage.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:@accentColor;" +
                "foreground:@background;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");
        btnNextPage.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:@accentColor;" +
                "foreground:@background;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        btnExport.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:@accentColor;" +
                "foreground:@background;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("static/svg/search.svg", 16, 16));
        txtSearch.putClientProperty(FlatClientProperties.STYLE, ""
                + "margin:5,20,5,20;");

        btnPrevPage.setIcon(new FlatSVGIcon("static/svg/menu_left.svg"));
        btnNextPage.setIcon(new FlatSVGIcon("static/svg/menu_right.svg"));

        table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table));
        loadTableData();

        ButtonColumn editButton = getEditColumn();
        editButton.setMnemonic(KeyEvent.VK_D);
        ButtonColumn deleteButton = getDeleteColumn();
        deleteButton.setMnemonic(KeyEvent.VK_D);

        btnNextPage.addActionListener(e -> {
            page++;
            loadTableData();
        });

        btnPrevPage.addActionListener(e -> {
            page--;
            loadTableData();
        });

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    page = 0;
                    loadFilteredTableData();
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && txtSearch.getText().isEmpty()) {
                    page = 0;
                    loadTableData();
                }
            }
        });

        btnExport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy"));
            fileChooser.setSelectedFile(new File("report-"+localDateString+".pdf"));
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                EmployeeClient employeeClient = new EmployeeClient();
                byte[] res =employeeClient.downloadReport();
                try {
                    Files.write(fileToSave.toPath(), res);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private ButtonColumn getEditColumn() {

        Action edit = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                EditForm editForm = new EditForm();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                String id = table.getModel().getValueAt(modelRow, 0).toString();
                editForm.setEmployee(id);
                DefaultOption option = new DefaultOption() {
                    @Override
                    public boolean closeWhenClickOutside() {
                        return true;
                    }
                };
                if (AuthenticatedEmployee.getInstance().getAuthenticationResponse().getRole().equals(Role.ADMIN)){
                    adminEditPopUp(editForm, id, option);
                }
                else {
                    nonAdminEditPopUp(editForm, id, option);
                }


            }
        };

        ButtonColumn buttonColumn = new ButtonColumn(table, edit, 10);
        return buttonColumn;
    }

    private void nonAdminEditPopUp(EditForm editForm, String id, DefaultOption option) {

        String actions[] = new String[]{"Cancel", "Save"};
        GlassPanePopup.showPopup(new SimplePopupBorder(editForm, "Edit Employee", actions, (pc, i) -> {
            if (i == 1) {
                EmployeeClient employeeClient = new EmployeeClient();
                try {
                    EmployeeDto requestBody = editForm.getEmployeeDto();
                    var response = employeeClient.updateEmployee(id,requestBody);
                    if (response instanceof Employee) {
                        Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.BOTTOM_RIGHT, "Employee updated successfully");
                    } else {
                        ApiError error = (ApiError) response;
                        Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT, error.getMessage());
                    }
                    loadTableData();
                    pc.closePopup();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                pc.closePopup();
            }
        }), option);
    }

    private void adminEditPopUp(EditForm editForm, String id, DefaultOption option) {

        String actions[] = new String[]{"Cancel","Edit password", "Save"};
        GlassPanePopup.showPopup(new SimplePopupBorder(editForm, "Edit Employee", actions, (pc, i) -> {
            EmployeeClient employeeClient = new EmployeeClient();
            if(i == 1){
                String password = JOptionPane.showInputDialog("Enter new password");
                if (password == null || password.isEmpty()) {
                    return;
                }
                try {
                    var response = employeeClient.registerEmployee(new RegisterRequest(id,password));
                    if (response instanceof Employee) {
                        Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.BOTTOM_RIGHT, "Password updated successfully");
                    } else {
                        ApiError error = (ApiError) response;
                        Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT, error.getMessage());
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }else if (i == 2) {
                try {
                    EmployeeDto requestBody = editForm.getEmployeeDto();
                    var response = employeeClient.updateEmployee(id,requestBody);
                    if (response instanceof Employee) {
                        Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.BOTTOM_RIGHT, "Employee updated successfully");
                    } else {
                        ApiError error = (ApiError) response;
                        Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT, error.getMessage());
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                loadTableData();
                pc.closePopup();
            } else{
                pc.closePopup();
            }

        }), option);
    }

    private ButtonColumn getDeleteColumn() {

        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                int modelRow = Integer.valueOf( e.getActionCommand() );
                String id = table.getModel().getValueAt(modelRow, 0).toString();
                String fullName = table.getModel().getValueAt(modelRow, 1).toString();
                Window window = SwingUtilities.windowForComponent(table);
                if (AuthenticatedEmployee.getInstance().getAuthenticationResponse().getRole().equals(Role.MANAGER)){
                    Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT, "You are not authorized to delete employees");
                    return;
                }
                int result = JOptionPane.showConfirmDialog(
                        window,
                        "Are you sure you want to " + fullName,
                        "Delete Row Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION)
                {
					System.out.println( "Deleting row: " + modelRow);
                    EmployeeClient employeeClient = new EmployeeClient();
                    try {
                        var response = employeeClient.deleteEmployee(id);
                        if (response instanceof String) {
                            Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.BOTTOM_RIGHT, "Employee deleted successfully");
                        } else {
                            ApiError error = (ApiError) response;
                            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT, error.getMessage());
                        }
                        loadTableData();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        };

        ButtonColumn buttonColumn = new ButtonColumn(table, delete, 11);
        return buttonColumn;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        txtSearch = new javax.swing.JTextField();
        lableTitle = new javax.swing.JLabel();
        btnNextPage = new javax.swing.JButton();
        btnPrevPage = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        panel.setPreferredSize(new java.awt.Dimension(800, 600));

        scroll.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Full name", "Email", "Job title", "Hire date", "Department", "Status", "Contact info", "address", "Role", "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scroll.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(130);
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
            table.getColumnModel().getColumn(3).setPreferredWidth(100);
            table.getColumnModel().getColumn(4).setPreferredWidth(80);
            table.getColumnModel().getColumn(5).setPreferredWidth(100);
            table.getColumnModel().getColumn(6).setPreferredWidth(50);
            table.getColumnModel().getColumn(7).setPreferredWidth(80);
            table.getColumnModel().getColumn(8).setPreferredWidth(100);
            table.getColumnModel().getColumn(9).setPreferredWidth(50);
            table.getColumnModel().getColumn(10).setResizable(false);
            table.getColumnModel().getColumn(10).setPreferredWidth(18);
            table.getColumnModel().getColumn(11).setResizable(false);
            table.getColumnModel().getColumn(11).setPreferredWidth(18);
        }

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        lableTitle.setText("Employee list");

        btnExport.setText("Download report");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        if (AuthenticatedEmployee.getInstance().getAuthenticationResponse().getRole().equals(Role.ADMIN)){
            panelLayout.setHorizontalGroup(
                    panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                            .addComponent(jSeparator1)
                            .addGroup(panelLayout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(panelLayout.createSequentialGroup()
                                                    .addComponent(lableTitle)
                                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(panelLayout.createSequentialGroup()
                                                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(btnExport)
                                                    .addGap(20, 20, 20))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnPrevPage, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(8, 8, 8)
                                    .addComponent(btnNextPage, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(20, 20, 20))
            );
            panelLayout.setVerticalGroup(
                    panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelLayout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(lableTitle)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnExport))
                                    .addGap(18, 18, 18)
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, 0)
                                    .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(btnNextPage, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnPrevPage, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(10, 10, 10))
            );
        }else {
            panelLayout.setHorizontalGroup(
                    panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                            .addComponent(jSeparator1)
                            .addGroup(panelLayout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(panelLayout.createSequentialGroup()
                                                    .addComponent(lableTitle)
                                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(panelLayout.createSequentialGroup()
                                                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(20, 412, Short.MAX_VALUE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnPrevPage, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(8, 8, 8)
                                    .addComponent(btnNextPage, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(20, 20, 20))
            );
            panelLayout.setVerticalGroup(
                    panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelLayout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(lableTitle)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, 0)
                                    .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(btnNextPage, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnPrevPage, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(10, 10, 10))
            );
        }

        add(panel);
    }// </editor-fold>//GEN-END:initComponents

    private void loadTableData() {
        EmployeeClient employeeClient = new EmployeeClient();
        try {
            var response = employeeClient.getEmployees(page);
            if (response instanceof PageWrapper<?>) {
                PageWrapper<Employee> page = (PageWrapper<Employee>) response;
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                btnNextPage.setEnabled(page.isHasNext());
                btnPrevPage.setEnabled(page.isHasPrevious());
                model.setRowCount(0);
                List<Employee> list = page.getPage();
                for (Employee e : list) {
                    model.addRow(convetToTableRow(e));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadFilteredTableData() {
        EmployeeClient employeeClient = new EmployeeClient();
        String keyword = txtSearch.getText();
        try {
            var response = employeeClient.searchEmployees(keyword,page);
            if (response instanceof PageWrapper<?>) {
                PageWrapper<Employee> page = (PageWrapper<Employee>) response;
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                btnNextPage.setEnabled(page.isHasNext());
                btnPrevPage.setEnabled(page.isHasPrevious());
                model.setRowCount(0);
                List<Employee> list = page.getPage();
                for (Employee e : list) {
                    model.addRow(convetToTableRow(e));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] convetToTableRow(Employee employee) {
        return new Object[]{
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getJobTitle(),
                employee.getHireDate(),
                employee.getDepartment(),
                employee.getEmploymentStatus(),
                employee.getContactInformation(),
                employee.getAddress(),
                employee.getRole(),
                new FlatSVGIcon("static/svg/edit.svg", 16, 16),
                new FlatSVGIcon("static/svg/delete.svg", 16, 16)
        };
    }


    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private int page;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnNextPage;
    private javax.swing.JButton btnPrevPage;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lableTitle;
    private javax.swing.JPanel panel;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
