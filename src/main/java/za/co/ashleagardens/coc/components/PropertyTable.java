package za.co.ashleagardens.coc.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Deon
 */
public class PropertyTable extends JXTable {

    private final Map<String, String> propertyMap;
    private final PropertyTableModel tableModel;

    public PropertyTable(Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
        this.tableModel = new PropertyTableModel();
        this.setModel(tableModel);

        initTable();
    }

    /**
     * Helper method to init table properties.
     */
    private void initTable() {
        this.addMouseListener(new JTableButtonMouseListener(this));
        this.getColumn("").setCellRenderer(new JTableButtonRenderer());
        this.getColumnModel().getColumn(2).setMaxWidth(10);
        this.getColumnModel().getColumn(2).setMinWidth(10);

        this.setSize(200, propertyMap.size() * 25);

        Color color = UIManager.getColor("Table.gridColor");
        MatteBorder border = new MatteBorder(1, 1, 0, 1, color);

        this.setBorder(BorderFactory.createLineBorder(color, 1));
        this.getTableHeader().setBorder(border);

        this.setVisible(true);
    }

    /**
     * A decorator method that delegates the call to the backing table model's
     * helper method to return the data array as a map.
     *
     * @return a map representing the property table.
     */
    public Map<String, String> getUpdatedPropertyMap() {
        return tableModel.getDataAsPropertyMap();
    }

    private void initDirChooser(final int rowCountForChooser) {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setCurrentDirectory(new java.io.File((String) tableModel.getValueAt(rowCountForChooser, 1)));
        dirChooser.setDialogTitle("Select a folder");
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setAcceptAllFileFilterUsed(false);

        int result = dirChooser.showOpenDialog(PropertyTable.this);

        if (result == JFileChooser.APPROVE_OPTION) {
            if (dirChooser.getSelectedFile() != null) {
                setValueAt(dirChooser.getSelectedFile().getAbsolutePath(), rowCountForChooser, 1);
            }
        }
    }

    /**
     * Backing model for the <tt>PropertyTable</tt>.
     */
    private class PropertyTableModel extends AbstractTableModel {

        private final String[] columnNames = {"Property Name", "Property Value", ""};
        private Object[][] data;

        public PropertyTableModel() {
            data = new Object[propertyMap.size()][3];
            setTableDataFromPropertiesMap();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 1 && row >= 0;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }

        private void setTableDataFromPropertiesMap() {
            if (!propertyMap.isEmpty()) {
                int rowCount = 0;
                for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
                    data[rowCount][0] = entry.getKey();
                    data[rowCount][1] = entry.getValue();
                    JButton invokeDirChooserBtn = new JButton("...");

                    final int rowCountForChooser = rowCount;
                    invokeDirChooserBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            initDirChooser(rowCountForChooser);
                        }
                    });
                    data[rowCount++][2] = invokeDirChooserBtn;
                }
            } else {
                //TODO: Re-evaluate whether this is necessary
                data = new Object[1][2];
                data[0][0] = "No properties in selected property file.";
                data[0][1] = "";
                data[0][2] = "";
            }
        }

        public Map<String, String> getDataAsPropertyMap() {
            Map<String, String> updatedPropertyMap = new HashMap<>();

            for (Object[] row : data) {
                updatedPropertyMap.put((String) row[0], (String) row[1]);
            }

            return updatedPropertyMap;
        }
    }

    /**
     * Private helper class to render buttons in the cell of the table.
     */
    private class JTableButtonRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = (JButton) value;

            button.setToolTipText("Select a directory");
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(UIManager.getColor("Button.background"));
            }
            return button;
        }
    }

    /**
     * Private helper class used for mouse events on the table.
     */
    private class JTableButtonMouseListener extends MouseAdapter {

        private final JTable table;

        public JTableButtonMouseListener(JTable table) {
            this.table = table;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Point clickPoint = e.getPoint();

            int row = table.rowAtPoint(clickPoint);
            int column = table.columnAtPoint(clickPoint);

            if (column == 2 && row >= 0) {
                Object value = table.getValueAt(row, column);
                if (value instanceof JButton) {
                    ((JButton) value).doClick();
                }
            }

            if (tableModel.isCellEditable(row, column)) {
                table.editCellAt(row, column);
            }
        }
    }
}
