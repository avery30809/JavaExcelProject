package Javapro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MyGUI extends JFrame implements ActionListener {
    private JPanel typePanel, namePanel, amountPanel, RadioButtonPanel, ButtonPanel, classPanel, remarkPanel;
    private JLabel typeLabel, nameLabel, amountLabel, statusLabel, remarkLabel;
    private JTextField nameTextField, amountTextField, remarkTextField;
    private JButton saveButton, settingButton, editButton;
    private JComboBox<String> typeComboBox, classComboBox;
    private JRadioButton earnButton, PayButton;
    private ButtonGroup radioGroup;
    private final String userName;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final userDataExcel ude;
    private trackSpendingExcel tse;

    public MyGUI(String userName, userDataExcel ude) {
        // initialize the frame
        super(userName+"-EasyExcel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(340, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 1));
        setResizable(false);

        // initialize the components
        typePanel = new JPanel(new BorderLayout());
        typeLabel = new JLabel(String.format("%10s          ","類別:"));
        typeComboBox = new JComboBox<String>();
        typePanel.add(typeLabel, BorderLayout.LINE_START);
        typePanel.add(typeComboBox);
        add(typePanel);

        namePanel = new JPanel(new BorderLayout());
        nameLabel = new JLabel(String.format("%10s          ","名稱:"));
        nameTextField = new JTextField();
        namePanel.add(nameLabel, BorderLayout.LINE_START);
        namePanel.add(nameTextField);
        add(namePanel);

        amountPanel = new JPanel(new BorderLayout());
        amountLabel = new JLabel(String.format("%10s          ","金額:"));
        amountTextField = new JTextField();
        amountPanel.add(amountLabel, BorderLayout.LINE_START);
        amountPanel.add(amountTextField, BorderLayout.CENTER);

        RadioButtonPanel = new JPanel(new GridLayout(2,1));
        earnButton = new JRadioButton("收入");
        earnButton.setSelected(true);
        PayButton = new JRadioButton("支出");
        radioGroup = new ButtonGroup();
        radioGroup.add(earnButton);
        radioGroup.add(PayButton);
        RadioButtonPanel.add(earnButton);
        RadioButtonPanel.add(PayButton);
        amountPanel.add(RadioButtonPanel, BorderLayout.LINE_END);
        add(amountPanel);

        remarkPanel = new JPanel(new BorderLayout());
        remarkLabel = new JLabel(String.format("%10s          ","備註:"));
        remarkTextField = new JTextField();
        remarkPanel.add(remarkLabel, BorderLayout.LINE_START);
        remarkPanel.add(remarkTextField);
        add(remarkPanel);

        ButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        settingButton = new JButton("設定匯出位置");
        settingButton.addActionListener(this);
        saveButton = new JButton("儲存");
        saveButton.addActionListener(this);
        ButtonPanel.add(settingButton);
        ButtonPanel.add(saveButton);
        add(ButtonPanel);

        classPanel = new JPanel(new BorderLayout());
        classComboBox = new JComboBox<String>();
        classComboBox.addActionListener(this);
        editButton = new JButton("編輯");
        editButton.addActionListener(this);
        classPanel.add(classComboBox);
        classPanel.add(editButton,BorderLayout.LINE_END);
        add(classPanel);

        statusLabel = new JLabel("狀態: 就緒");
        add(statusLabel);

        // initialize the data
        classComboBox.addItem("選擇類別");
        for(int i=0; i<AllTypes.size; ++i){
            classComboBox.addItem(AllTypes.types[i]);
            typeComboBox.addItem(AllTypes.types[i]);
        }
        this.userName = userName;
        this.ude = ude;
        try{
            tse = new trackSpendingExcel(userName, ude.getExcelPath(userName));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            int type = typeComboBox.getSelectedIndex();
            String name = nameTextField.getText();
            String amountString = amountTextField.getText();
            String date = "";
            String remark = remarkTextField.getText();

            try {
                if (name.isEmpty()) {
                    throw new IllegalArgumentException();
                }
                long amount = Long.parseLong(amountString);
                if (amount <= 0 || amount > Long.MAX_VALUE) {
                    throw new Exception();
                }
                date = dateFormat.format(Calendar.getInstance().getTime());
                try{
                    tse.write(type, date, name, amount, earnButton.isSelected(), remark);
                } catch (Exception ee) {
                    ee.printStackTrace();
                    return;
                }
                nameTextField.setText("");
                amountTextField.setText("");
                remarkTextField.setText("");
                return;
            } catch (NumberFormatException ex) {
                statusLabel.setText("狀態: 不合規定的金額");
                if(amountString.isEmpty()){
                    JOptionPane.showMessageDialog(this, "金額不得為空", "錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(this, "不合規定的金額", "錯誤", JOptionPane.ERROR_MESSAGE);
                amountTextField.setText("");
                return;
            } catch (IllegalArgumentException ex) {
                statusLabel.setText("狀態: 不合規定的名稱");
                JOptionPane.showMessageDialog(this, "名稱不得為空", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception ex) {
                statusLabel.setText("狀態: 不合規定的金額");
                if(amountString.charAt(0)=='-'){
                    JOptionPane.showMessageDialog(this, "金額不得小於0", "錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(this, "金額超出儲存大小", "錯誤", JOptionPane.ERROR_MESSAGE);
                amountTextField.setText("");
                return;
            }
        }
        if (e.getSource() == settingButton) {
            String newPath = "";
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int res = fc.showOpenDialog(this);
            if(res == JFileChooser.APPROVE_OPTION){
                newPath = fc.getSelectedFile().getAbsolutePath();
            }
            if(newPath.equals("")) return;
            try{
                ude.editExcelPath(userName, newPath);
                tse.changeExcelPath(newPath);
            } catch (Exception ee){
                ee.printStackTrace();
            }
            return;
        }
        if (e.getSource() == classComboBox) {
            String name = (String) classComboBox.getSelectedItem();
            if (!name.equals("選擇類別")) {
                statusLabel.setText("狀態: 已選擇 " + name);
                return;
            }
            statusLabel.setText("狀態: 尚未選擇類別");
            return;
        }
        if (e.getSource() == editButton) {
            try{
                int t = classComboBox.getSelectedIndex();
                if (t == 0) throw new IllegalArgumentException();
                setVisible(false);
                new editGUI(userName, this, tse, (String)classComboBox.getSelectedItem(), t);
            } catch (IllegalArgumentException ee) {
                JOptionPane.showMessageDialog(this, "尚未選擇類別", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }
}