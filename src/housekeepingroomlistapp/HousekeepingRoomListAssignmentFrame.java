package housekeepingroomlistapp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import listintegratorlibrary.Employee;
import listintegratorlibrary.HouseKeepingReportCreator;
import listintegratorlibrary.ListIntegrator;
import listintegratorlibrary.ListReader;
import listintegratorlibrary.ListWriter;
import listintegratorlibrary.Room;
import listintegratorlibrary.Room.RoomStatus;
import listintegratorlibrary.RoomListOperator;

/**
 * HousekeepingRoomListAssignmentFrame class
 * inherits JFrame components
 * creates GUI components
 * Requires ListIntegratorLibrary
 * 
 * @author Hardikkumar Bhakta
 */
public class HousekeepingRoomListAssignmentFrame extends JFrame {
    
    private JPanel contentPane, panel;
    private JLabel roomNumberLabel, roomStatusLabel;
    private DefaultTableModel tableModel; 
    private JTable table;
    private JTextArea displayArea;
    private ArrayList<JCheckBox> checkboxes;
    private JCheckBox selectAllCheckBox;
    private ListIntegrator listIntegrator;
    private JPanel panel2;
    private JButton markAsStayOverButton;
    private JButton markAsCheckOutButton;
    private JButton saveButton;
    private JPanel panel3;
    private ActionListener checkBoxListener;
    private RoomListOperator roomListOperator;
    private final String[] columnNames = {"Room Number", "Type", "Status"};
    private Object[][] data;
    private JLabel employeeLabel;
    private JTextField employeeNameField;
    private JLabel dateLabel;
    private JFormattedTextField dateField;
    
    public HousekeepingRoomListAssignmentFrame() throws HeadlessException {
        listIntegrator = new ListIntegrator();
        roomListOperator = new RoomListOperator();
        readFromConfigurationFileToList();
        
        setTitle("Housekeeper Room List Assigner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700,700);
        setLocationRelativeTo(null);
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        checkboxes = new ArrayList<>();
        
        selectAllCheckBox = new JCheckBox("Select All");
        panel.add(selectAllCheckBox);
        
        checkBoxListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if (source.isSelected()) {
                    enableBothCOAndSOButton();
                }
            }
        };
        createCheckBoxes();
        selectAllCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = selectAllCheckBox.isSelected();
                for (JCheckBox checkbox : checkboxes) {
                    checkbox.setSelected(selected);
                }
                if (selected) {
                    enableBothCOAndSOButton();
                } else {
                    disableBothCOAndSOButton();
                }
            }
        });
        
        JScrollPane scrollPaneWest = new JScrollPane(panel);
        add(scrollPaneWest, BorderLayout.WEST);
        
        markAsCheckOutButton = new JButton("Mark as CHECKED OUT");
        markAsStayOverButton = new JButton("Mark as STAY OVER");
        
        markAsCheckOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disableBothCOAndSOButton();
                completeRoomAssignmentTask(RoomStatus.CHECKED_OUT);
                updateAllList();
                enableBothCOAndSOButton();
            }
        });
        
        markAsStayOverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disableBothCOAndSOButton();
                completeRoomAssignmentTask(RoomStatus.STAY_OVER);
                updateAllList();
                enableBothCOAndSOButton();
            }
        });
        panel.add(markAsCheckOutButton);
        panel.add(Box.createRigidArea(new Dimension(5,0)));
        panel.add(markAsStayOverButton);
        panel.add(Box.createRigidArea(new Dimension(5,0)));
        
        panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        
        employeeLabel = new JLabel("Employee: ");
        employeeNameField = new JTextField();
        employeeNameField.setColumns(20);
        
        dateLabel = new JLabel("Date: ");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        dateField = new JFormattedTextField(new Date());
        dateField.setColumns(20);
        
        panel2.add(employeeLabel);
        panel2.add(employeeNameField);
        panel2.add(Box.createRigidArea(new Dimension(10,0)));
        panel2.add(dateLabel);
        panel2.add(dateField);
        
        saveButton = new JButton("SAVE");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                completeAddingToListAndWritingToTextFile();
            }

            private void completeAddingToListAndWritingToTextFile() {
                disableSaveButton();
                disableCheckBoxes();
                completeWritingToTextFile();
                enableCheckBoxes();
                enableSaveButton();
            }            

            

            
        });
        disableBothCOAndSOButton();
        
        panel2.add(saveButton);
        add(panel2, BorderLayout.NORTH);
        
        data = createObjectsForTableRows();
        
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                super.setValueAt(aValue, row, column);
            }
            
            
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scrollPaneCenter = new JScrollPane(table);
        add(scrollPaneCenter, BorderLayout.CENTER);

        setVisible(true);
        revalidate();
        repaint();
        
    }

    public HousekeepingRoomListAssignmentFrame(GraphicsConfiguration gc) {
        super(gc);
    }

    public HousekeepingRoomListAssignmentFrame(String title) throws HeadlessException {
        super(title);
    }

    public HousekeepingRoomListAssignmentFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
    }

    /**
     * Reads data from configuration file and adds them to list
     */
    private void readFromConfigurationFileToList() {
        ListReader lr = new ListReader();
        if (lr.configurationFileExists() && lr.configurationFileIsReadable()) {
            String content = lr.readFromConfigurationTextFile();
            if (content != null || !content.equals("")) {
                String list[] = content.split("\n");
                for (int i = 0; i < list.length; i++) {
                    String[] line = list[i].split(" ");
                    Integer roomNumber = null;
                    String roomType = "";
                    if (!line[0].equals("")) {
                        roomNumber = Integer.valueOf(line[0]);
                    }
                    if (line.length > 1) {
                        roomType = line[1];
                    }

                    if (roomNumber != null && !roomType.equals("")) {
                        Room room = new Room(roomNumber, roomType);
                        listIntegrator.buildSortedList(room);
                    }
                }
            }
        }
        
    }
    
    /**
     * Creates row data to be displayed in dataTable
     * @return Object[][] data
     */
    private Object[][] createObjectsForTableRows() {
        int columns = 3;
        int rows = roomListOperator.getCombinedList().size();
        Object[][] data = new Object[rows][columns];
        for (int i = 0; i < rows; i++) {
            Room room = roomListOperator.getCombinedList().get(i);
            data[i][0] = String.valueOf(room.getNumber());
            data[i][1] = room.getType();
            data[i][2] = room.getStatus().toString();
        }
        return data;
    }

    /**
     * Creates check boxes for each room
     */
    private void createCheckBoxes() {
        for (Room room : listIntegrator.getIntegratedUniuqeList()) {
            JCheckBox checkBox = new JCheckBox(room.toString());
            checkBox.addActionListener(checkBoxListener);
            checkboxes.add(checkBox);
            panel.add(checkBox);
        }
    }

    /**
     * disables markAsCheckOutButton & markAsStayOverButton
     */
    private void disableBothCOAndSOButton() {
        markAsCheckOutButton.setEnabled(false);
        markAsStayOverButton.setEnabled(false);
    }
    
    /**
     * enables markAsCheckOutButton & markAsStayOverButton
     */
    private void enableBothCOAndSOButton() {
        markAsCheckOutButton.setEnabled(true);
        markAsStayOverButton.setEnabled(true);
    }
    
    /**
     * Assign room status 
     * calls addRowToTable() method
     * @param roomStatus 
     */
    private void completeRoomAssignmentTask(RoomStatus roomStatus) {
        for (JCheckBox jcb : checkboxes) {
            if (jcb.isSelected() == true) {
                Object content = jcb.getSelectedObjects()[0];
                String trimmedObject = content.toString().trim();
                String[] list = trimmedObject.split("\n");
                for (int i = 0; i < list.length; i++) {
                    String[] line = list[i].split(" ");
                    Integer roomNumber = null;
                    if (!line[0].equals("")) {
                        roomNumber = Integer.valueOf(line[0]);
                    }
                    String roomType = "";
                    if (line.length > 1) {
                        roomType = line[1];
                    }
                    if (roomNumber != null && !roomType.equals("")) {
                        Room room = new Room(roomNumber, roomType, roomStatus);
                        addRowToTable(room);
                    }
                }
            }
        }
    }

    /**
     * Adds new row to data table
     * calls revalidate() and repaint() methods
     * @param room 
     */
    private void addRowToTable(Room room) {
        Object[] rowData = {room.getNumber(), room.getType(), room.getStatus()};
        boolean found = false;
        int size = tableModel.getRowCount();
        for (int i = 0; i < table.getRowCount(); i++) {
            Integer number = (Integer) tableModel.getValueAt(i, 0);
            String roomType = (String) tableModel.getValueAt(i, 1);
            RoomStatus status = (RoomStatus) tableModel.getValueAt(i, 2);
            if (number == room.getNumber()) {
                if (status == room.getStatus()) {
                    // do nothing
                } else {
                    tableModel.setValueAt(room.getStatus(), i, 2);
                }
                found = true;
                break;
            }
        }
        if (!found) { 
            tableModel.addRow(rowData);
            
        }
        revalidate();
        repaint();
    }
    
    private void updateAllList() {
        roomListOperator.clearAllList();
        completeAddingToList();
    }
    /**
     * Adds room to list from tableModel using roomListOperator
     */
    private void completeAddingToList() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Integer number = (Integer) tableModel.getValueAt(i, 0);
            String roomType = (String) tableModel.getValueAt(i, 1);
            RoomStatus status = (RoomStatus) tableModel.getValueAt(i, 2);
            roomListOperator.add(new Room(number, roomType, status));
        }
    }
    
    /**
     * Gets employeeNameFiled, dateField, updated content, and creates housekeepingreport
     * Gets content from housekeeping report
     * Writes content to text file
     */
    private void completeWritingToTextFile() {
        StringBuilder sb = new StringBuilder();
        Employee employee = new Employee(employeeNameField.getText());
        String date = dateField.getText();
        String formattedList = roomListOperator.getUpdatedContent();
        HouseKeepingReportCreator houseKeepingReportCreator = new HouseKeepingReportCreator(employee.toString(), date, formattedList);
        ListWriter lw = new ListWriter();
        String content = houseKeepingReportCreator.getReport();
        try {
            lw.writeToHouseKeepingListTextFile(content);
            openSuccessDialog(lw);
        } catch (Exception e) {
            openErrorDialog(e);
        }
    }

    /**
     * Display success dialog message with 
     * location of file where data is saved
     */
    private void openSuccessDialog(ListWriter lw) {
        String savedLocation = "Housekeeping list saved successfully at File Path: \n" + lw.getsHousekeepingReportTextFilePath();
        JTextArea jta = new JTextArea(savedLocation);
        jta.setFont(new Font("Arial", Font.BOLD, 14));
        jta.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(jta), "Save Confirmation", JOptionPane.INFORMATION_MESSAGE);        
    }

    /**
     * Display error dialog message
     * @param e 
     */
    private void openErrorDialog(Exception e) {
        JOptionPane.showMessageDialog(this, "Error occurred while creating housekeeping report.", "Error", JOptionPane.ERROR_MESSAGE);        
    }
    
    private void disableCheckBoxes() {
        for (JCheckBox jcb : checkboxes) {
            jcb.setEnabled(false);
        }
    }

    private void disableSaveButton() {
        saveButton.setEnabled(false);
    }

    private void enableSaveButton() {
        saveButton.setEnabled(true);
    }

    private void enableCheckBoxes() {
        for (JCheckBox jcb : checkboxes) {
            jcb.setEnabled(true);
        }
    }
    
}
