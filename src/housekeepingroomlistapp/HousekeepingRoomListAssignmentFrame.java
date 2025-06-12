package housekeepingroomlistapp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javax.swing.event.TableModelEvent;
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
 * HousekeepingRoomListAssignmentFrame class inherits JFrame components creates
 * GUI components Requires ListIntegratorLibrary
 *
 * @author Hardikkumar Bhakta
 */
public class HousekeepingRoomListAssignmentFrame extends JFrame {

    private ListIntegrator listIntegrator;
    private RoomListOperator roomListOperator;

    private JPanel northPanel;
    private JPanel eastPanel;
    private JPanel westPanel;
    private JPanel optionalLabelPanel;
    private JPanel stayOverWagePanel;
    private JPanel checkedOutWagePanel;
    private JPanel southPanel;

    private DefaultTableModel tableModel;
    private JTable table;
    private final String[] columnNames = {"Room Number", "Type", "Status"};
    private Object[][] data;

    private ArrayList<JCheckBox> checkboxes;
    private JCheckBox selectAllCheckBox;
    private JCheckBox optionalFeatureCheckBox;

    private JButton markAsStayOverButton;
    private JButton markAsCheckOutButton;
    private JButton saveButton;
    private JButton removeButton;

    private ActionListener checkBoxListener;

    private JLabel employeeLabel;
    private JLabel dateLabel;
    private JLabel stayOverCleanWageLabel;
    private JLabel checkedOutCleanWageLabel;

    private JTextField employeeNameField;
    private JFormattedTextField dateField;
    private JFormattedTextField checkedOutCleaningWageField;
    private JFormattedTextField stayOverCleaningWageField;

    public HousekeepingRoomListAssignmentFrame() throws HeadlessException {
        listIntegrator = new ListIntegrator();
        roomListOperator = new RoomListOperator();
        readFromConfigurationFileToList();

        setTitle("Housekeeping Room List Assigner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);

        Font font = new Font("Arial", Font.BOLD, 14);
        westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        checkboxes = new ArrayList<>();

        selectAllCheckBox = new JCheckBox("Select All");
        westPanel.add(selectAllCheckBox);

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

        JScrollPane scrollPaneWest = new JScrollPane(westPanel);
        add(scrollPaneWest, BorderLayout.WEST);

        markAsCheckOutButton = new JButton("Mark as CHECKED OUT");
        markAsStayOverButton = new JButton("Mark as STAY OVER");

        markAsCheckOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disableRemoveButton();
                disableBothCOAndSOButton();
                completeRoomAssignmentTask(RoomStatus.CHECKED_OUT);
                updateAllList();
                enableBothCOAndSOButton();
                enableRemoveButton();
            }
        });

        markAsStayOverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disableRemoveButton();
                disableBothCOAndSOButton();
                completeRoomAssignmentTask(RoomStatus.STAY_OVER);
                updateAllList();
                enableBothCOAndSOButton();
                enableRemoveButton();
            }
        });
        westPanel.add(markAsCheckOutButton);
        westPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        westPanel.add(markAsStayOverButton);
        westPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
        employeeLabel = new JLabel("Employee: ");
        employeeNameField = new JTextField();
        employeeNameField.setColumns(20);
        employeeNameField.setFont(font);
        dateLabel = new JLabel("Date: ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateField = new JFormattedTextField(simpleDateFormat.format(new Date()));
        dateField.setColumns(20);
        dateField.setFont(font);
        northPanel.add(employeeLabel);
        northPanel.add(employeeNameField);
        northPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        northPanel.add(dateLabel);
        northPanel.add(dateField);

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

        northPanel.add(saveButton);
        add(northPanel, BorderLayout.NORTH);

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

            @Override
            public void removeRow(int row) {
                super.removeRow(row);
            }

            @Override
            public void rowsRemoved(TableModelEvent event) {
                super.rowsRemoved(event);
            }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scrollPaneCenter = new JScrollPane(table);
        add(scrollPaneCenter, BorderLayout.CENTER);

        eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedRowsFromDataTableModel();
                if (tableModel.getRowCount() <= 0) {
                    disableRemoveButton();
                }
                updateAllList();
            }
        });
        disableRemoveButton();
        eastPanel.add(removeButton);
        add(eastPanel, BorderLayout.EAST);

        southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        optionalLabelPanel = new JPanel();
        optionalLabelPanel.setLayout(new BoxLayout(optionalLabelPanel, BoxLayout.X_AXIS));
        optionalFeatureCheckBox = new JCheckBox("Input rate per room cleaned (optional): ");
        optionalFeatureCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = optionalFeatureCheckBox.isSelected();
                if (isSelected) {
                    enableCOAndSORateField();
                } else {
                    disableCOAndSORateField();
                }
            }
        });
        optionalLabelPanel.add(optionalFeatureCheckBox);
        optionalLabelPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        southPanel.add(optionalLabelPanel, BorderLayout.CENTER);

        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        checkedOutWagePanel = new JPanel();
        checkedOutWagePanel.setLayout(new BoxLayout(checkedOutWagePanel, BoxLayout.X_AXIS));
        Dimension rateFieldDimension = new Dimension(100, 25);
        checkedOutCleanWageLabel = new JLabel("C/O Rate: ");
        checkedOutWagePanel.add(checkedOutCleanWageLabel);
        checkedOutCleaningWageField = new JFormattedTextField(numberInstance);
        checkedOutCleaningWageField.setFont(font);
        checkedOutCleaningWageField.setPreferredSize(rateFieldDimension);
        checkedOutCleaningWageField.setMaximumSize(checkedOutCleaningWageField.getPreferredSize());
        checkedOutWagePanel.add(checkedOutCleaningWageField, BorderLayout.CENTER);
        southPanel.add(checkedOutWagePanel, BorderLayout.WEST);

        stayOverWagePanel = new JPanel();
        stayOverWagePanel.setLayout(new BoxLayout(stayOverWagePanel, BoxLayout.X_AXIS));
        stayOverCleanWageLabel = new JLabel("S/O Rate: ");
        stayOverWagePanel.add(stayOverCleanWageLabel);
        stayOverCleaningWageField = new JFormattedTextField(numberInstance);
        stayOverCleaningWageField.setFont(font);
        stayOverCleaningWageField.setPreferredSize(rateFieldDimension);
        stayOverCleaningWageField.setMaximumSize(stayOverCleaningWageField.getPreferredSize());
        stayOverWagePanel.add(stayOverCleaningWageField, BorderLayout.CENTER);
        southPanel.add(stayOverWagePanel, BorderLayout.EAST);
        disableCOAndSORateField();
        add(southPanel, BorderLayout.SOUTH);

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
     *
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
            westPanel.add(checkBox);
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
     * Assign room status calls addRowToTable() method
     *
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
     * Adds new row to data table calls revalidate() and repaint() methods
     *
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

    /**
     * Updates all list calls to completeAddingToList()
     */
    private void updateAllList() {
        roomListOperator.clearAllList();
        completeAddingToList();
    }

    /**
     * Adds room to list from tableModel using roomListOperator calls add method
     * of RoomListOperator class
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
     * Gets employeeNameFiled, dateField, updated content, and creates
     * housekeepingreport Gets content from housekeeping report Writes content
     * to text file
     */
    private void completeWritingToTextFile() {
        StringBuilder sb = new StringBuilder();
        Employee employee = new Employee(employeeNameField.getText());
        String date = dateField.getText();
        String formattedList = roomListOperator.getUpdatedContent();
        
        HouseKeepingReportCreator houseKeepingReportCreator = new HouseKeepingReportCreator(employee.toString(), date, formattedList);
        
        String coCleaningWage = checkedOutCleaningWageField.getText();
        String soCleaningWage = stayOverCleaningWageField.getText();
        String optionalContent = "";
        
        if (!coCleaningWage.isEmpty() && !soCleaningWage.isEmpty()) {
            roomListOperator.setWagePerCheckedOutRoomCleaned(Double.valueOf(coCleaningWage));
            roomListOperator.setWagePerStayOverRoomCleaned(Double.valueOf(soCleaningWage));
            optionalContent = roomListOperator.getOptionalContent(date);
            houseKeepingReportCreator.appendOptionalContent(optionalContent);
        }
        
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
     * Display success dialog message with location of file where data is saved
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
     *
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

    private void disableRemoveButton() {
        removeButton.setEnabled(false);
    }

    private void enableRemoveButton() {
        removeButton.setEnabled(true);
    }

    /**
     * Removes selected rows from the data table
     */
    private void removeSelectedRowsFromDataTableModel() {
        int[] indicesOfAllSelectedRows = table.getSelectedRows();
        List<Room> temp = new ArrayList<>();
        for (int i = 0; i < indicesOfAllSelectedRows.length; i++) {
            Integer number = (Integer) tableModel.getValueAt(indicesOfAllSelectedRows[i], 0);
            String roomType = (String) tableModel.getValueAt(indicesOfAllSelectedRows[i], 1);
            RoomStatus status = (RoomStatus) tableModel.getValueAt(indicesOfAllSelectedRows[i], 2);
            temp.add(new Room(number, roomType, status));
        }

        outer:
        for (int k = 0; k < temp.size(); k++) {
            Room room = temp.get(k);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Integer number = (Integer) tableModel.getValueAt(i, 0);
                String roomType = (String) tableModel.getValueAt(i, 1);
                RoomStatus status = (RoomStatus) tableModel.getValueAt(i, 2);
                if (room.getNumber() == number && room.getType().equals(roomType) && room.getStatus() == status) {
                    tableModel.removeRow(i);
                    continue outer;
                }
            }
        }
        temp.clear();
    }

    private void disableCOAndSORateField() {
        checkedOutCleaningWageField.setText("");
        stayOverCleaningWageField.setText("");
        checkedOutCleaningWageField.setEditable(false);
        stayOverCleaningWageField.setEditable(false);
        
    }

    private void enableCOAndSORateField() {
        checkedOutCleaningWageField.setEditable(true);
        stayOverCleaningWageField.setEditable(true);
    }

    
}
