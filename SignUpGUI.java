package Javapro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignUpGUI extends JFrame implements ActionListener, KeyListener {
    private JLabel nameLabel, passwordLabel, confirmPasswordLabel;
    private JTextField nameTextField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton SignUpButton;
    private entryGUI entry;
    private String name, password, confirmPassword;

    public SignUpGUI(entryGUI entry) {
        // initialize the frame
        super("Signup");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                entry.setVisible(true);
                dispose();
            }
        });
        setSize(280, 190);
        setLocationRelativeTo(entry);
        setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        setResizable(false);

        // initialize the components
        nameLabel = new JLabel(String.format("%17s","Username:"));
        add(nameLabel);
        nameTextField = new JTextField(10);
        nameTextField.addKeyListener(this);
        add(nameTextField);

        passwordLabel = new JLabel(String.format("%17s","Password:"));
        add(passwordLabel);
        passwordField = new JPasswordField(10);
        passwordField.addKeyListener(this);
        add(passwordField);

        confirmPasswordLabel = new JLabel(String.format("%17s","Confirm Password:"));
        add(confirmPasswordLabel);
        confirmPasswordField = new JPasswordField(10);
        confirmPasswordField.addKeyListener(this);
        add(confirmPasswordField);

        SignUpButton = new JButton("Signup");
        SignUpButton.setPreferredSize(new Dimension(100, 30));
        SignUpButton.addActionListener(this);
        SignUpButton.addKeyListener(this);
        add(SignUpButton);

        // initialize the data
        this.entry = entry;
        entry.setVisible(false);
        setVisible(true);
    }
    public void keyTyped(KeyEvent e){}
    public void keyReleased(KeyEvent e){}
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            SignUp();
        }
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == SignUpButton){
            SignUp();
        }
    }
    private void SignUp(){
        name = nameTextField.getText();
        password = new String(passwordField.getPassword());
        confirmPassword = new String(confirmPasswordField.getPassword());
        if(name.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Username cannot be empty","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(name.equals("Username")) {
            JOptionPane.showMessageDialog(this, "Can't use this name as your Username", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(password.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Password cannot be empty","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Confirm password cannot be empty","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(!password.equals(confirmPassword)){
            JOptionPane.showMessageDialog(this,"Password are not match","Error",JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.setText("");
            return;
        }
        if(entry.getude().hasUserName(name)){
            JOptionPane.showMessageDialog(this,"Username was used","Error",JOptionPane.ERROR_MESSAGE);
            nameTextField.setText("");
            return;
        }
        try{
            entry.getude().write(name,password);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        entry.setVisible(true);
        dispose();
    }
}
