package Javapro;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

public class editGUI extends JFrame implements ActionListener{
    private JTable table;
    private JPanel panel, detailPanel, radioButtonPanel, ButtonPanel;
    private JButton deleteButton, editButton;
    private JRadioButton earnButton, payButton;
    private ButtonGroup radioGroup;
    private JScrollPane scrollPane;

    private JTextField timeTextField, nameTextField, amountTextField, remarkTextField;

    private int row=-1, editTypeIndex;
    private String editTypeName;
    private Object[][] Data;
    private Object[] rowData = {"", "", "", "", "", "",};
    private String[] columnName = {"時間", "名稱", "收入", "支出", "結算", "備註"};

    private trackSpendingExcel tse;

    public editGUI(String userName, MyGUI mainGUI, trackSpendingExcel tse, String editTypeName, int editTypeIndex) {
        // initialize the frame
        super(userName + "-" + editTypeName);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                mainGUI.setVisible(true);
                dispose();
            }
        });
        setSize(640, 480);
        setLocationRelativeTo(mainGUI);
        setResizable(false);
        setVisible(true);

        // initialize the Data
        this.tse = tse;
        this.editTypeIndex = editTypeIndex;
        this.editTypeName = editTypeName.substring(0,editTypeName.indexOf(" "));

        // initialize the components
        refreshTable();

        panel = new JPanel(new GridLayout(2,1));
        detailPanel = new JPanel(new GridLayout(1,5));

        JPanel time, name, amount, remark;
        time = new JPanel(new GridLayout(2,1));
        JLabel timeLabel = new JLabel("時間:");
        timeTextField = new JTextField(10);
        time.add(timeLabel);
        time.add(timeTextField);

        name = new JPanel(new GridLayout(2,1));
        JLabel nameLabel = new JLabel("名稱:");
        nameTextField = new JTextField(10);
        name.add(nameLabel);
        name.add(nameTextField);

        amount = new JPanel(new GridLayout(2,1));
        JLabel amountLabel = new JLabel("金額:");
        amountTextField = new JTextField(10);
        amount.add(amountLabel);
        amount.add(amountTextField);

        radioButtonPanel = new JPanel(new GridLayout(2,1));
        earnButton = new JRadioButton("收入");
        payButton = new JRadioButton("支出");
        radioGroup = new ButtonGroup();
        radioGroup.add(earnButton);
        radioGroup.add(payButton);
        radioButtonPanel.add(earnButton);
        radioButtonPanel.add(payButton);

        remark = new JPanel(new GridLayout(2,1));
        JLabel remarkLabel = new JLabel("備註:");
        remarkTextField = new JTextField(10);
        remark.add(remarkLabel);
        remark.add(remarkTextField);

        detailPanel.add(time);
        detailPanel.add(name);
        detailPanel.add(amount);
        detailPanel.add(radioButtonPanel);
        detailPanel.add(remark);

        ButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteButton = new JButton("刪除");
        deleteButton.addActionListener(this);
        editButton = new JButton("編輯");
        editButton.addActionListener(this);
        ButtonPanel.add(deleteButton);
        ButtonPanel.add(editButton);

        panel.add(detailPanel);
        panel.add(ButtonPanel);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.PAGE_END);
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == deleteButton){
            try{
                if(row == -1) throw new Exception();
                tse.removeRow(editTypeName, editTypeIndex, row+1);
            } catch (Exception ee){
                JOptionPane.showMessageDialog(this, "尚未選擇資料", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
            remove(scrollPane);
            refreshTable();
            add(scrollPane, BorderLayout.CENTER);
            timeTextField.setText("");
            nameTextField.setText("");
            amountTextField.setText("");
            remarkTextField.setText("");

            SwingUtilities.updateComponentTreeUI(this);
            row = -1;
            return;
        }
        if(e.getSource() == editButton){
            String timeString = timeTextField.getText();
            String nameString = nameTextField.getText();
            String amountString = amountTextField.getText();
            String remarkString = remarkTextField.getText();
            if(row == -1) {
                JOptionPane.showMessageDialog(this, "尚未選擇資料", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (nameString.isEmpty()) {
                    throw new IllegalArgumentException();
                }
                long amount = Long.parseLong(amountString);
                if (amount <= 0 || amount > Long.MAX_VALUE) {
                    throw new Exception();
                }
                Object[] newRow = {timeString, nameString, amount, earnButton.isSelected(), remarkString};
                tse.editRow(editTypeName, editTypeIndex, row+1, newRow);
                remove(scrollPane);
                refreshTable();
                add(scrollPane, BorderLayout.CENTER);
                SwingUtilities.updateComponentTreeUI(this);
                return;
            } catch (NumberFormatException ex) {
                if(amountString.isEmpty()){
                    JOptionPane.showMessageDialog(this, "金額不得為空", "錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(this, "不合規定的金額", "錯誤", JOptionPane.ERROR_MESSAGE);
                amountTextField.setText("");
                return;
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "名稱不得為空", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception ex) {
                if(amountString.charAt(0)=='-'){
                    JOptionPane.showMessageDialog(this, "金額不得小於0", "錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(this, "金額超出儲存大小", "錯誤", JOptionPane.ERROR_MESSAGE);
                amountTextField.setText("");
                return;
            }
        }
    }
    private void refreshTable(){
        table = new JTable(Data = tse.getData(editTypeIndex), columnName);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setModel(new DefaultTableModel(Data, columnName){
            @Override
            public boolean isCellEditable(int row, int col){
                return false;
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                if (table.getSelectedRow() > -1) {
                    row = table.getSelectedRow();
                    // Get the data for the selected row and display it in the GUI's bottom area
                    rowData = new Object[columnName.length];
                    for (int column = 0; column < columnName.length; column++) {
                        rowData[column] = table.getValueAt(row, column);
                    }
                    timeTextField.setText((String)rowData[0]);
                    nameTextField.setText((String)rowData[1]);
                    amountTextField.setText(!String.valueOf(rowData[2]).equals("")?String.valueOf(rowData[2]):String.valueOf(rowData[3]));
                    earnButton.setSelected(!String.valueOf(rowData[2]).equals(""));
                    payButton.setSelected(!String.valueOf(rowData[3]).equals(""));
                    remarkTextField.setText((String)rowData[5]);
                }
            }
        });
        scrollPane = new JScrollPane(table);
    }
}
