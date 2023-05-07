package Javapro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class entryGUI extends JFrame implements ActionListener , KeyListener{
    private JTextField nameTextField;
    private JButton LoginButton, SignUpButton;
    private JPasswordField userPassword;
    private String name, passWord;
    private final String userDataPath;

    private userDataExcel ude;

    public String getuserDataPath() {
        return userDataPath;
    }
    public userDataExcel getude() {
        return ude;
    }

    public entryGUI(String userDataPath) {
        // initialize the frame
        super("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(280, 125);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setResizable(false);

        // initialize the components
        nameTextField = new JTextField("Username",10);
        nameTextField.addKeyListener(this);
        nameTextField.setForeground(Color.GRAY);
        nameTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e){
                if (nameTextField.getText().equals("Username")){
                    nameTextField.setText("");
                    nameTextField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                if (nameTextField.getText().isEmpty()){
                    nameTextField.setText("Username");
                    nameTextField.setForeground(Color.GRAY);
                }
            }
        });
        add(nameTextField);

        userPassword = new JPasswordField("Password",10);
        userPassword.addKeyListener(this);
        userPassword.setForeground(Color.GRAY);
        userPassword.setEchoChar((char)0);
        userPassword.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e){
                String pw = new String(userPassword.getPassword());
                if (pw.equals("Password")) {
                    userPassword.setText("");
                    userPassword.setForeground(Color.BLACK);
                    userPassword.setEchoChar('\u2022');
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                String pw = new String(userPassword.getPassword());
                if (pw.isEmpty()){
                    userPassword.setText("Password");
                    userPassword.setForeground(Color.GRAY);
                    userPassword.setEchoChar((char)0);
                }
            }
        });
        add(userPassword);

        SignUpButton = new JButton("Signup");
        SignUpButton.setPreferredSize(new Dimension(100, 30));
        SignUpButton.addActionListener(this);
        SignUpButton.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent ke){
				if(ke.getKeyCode() == KeyEvent.VK_ENTER){
					new SignUpGUI(entryGUI.this);
				}
			}
			public void keyTyped(KeyEvent ke){}
			public void keyReleased(KeyEvent ke){}
        });
        add(SignUpButton);

        LoginButton = new JButton("Login");
        LoginButton.setPreferredSize(new Dimension(100, 30));
        LoginButton.addActionListener(this);
        LoginButton.addKeyListener(this);
        add(LoginButton);

        // initialize the data
        this.userDataPath = userDataPath;
        setVisible(true);
        try{
            ude = new userDataExcel(userDataPath);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void keyPressed(KeyEvent ke){
        if(ke.getKeyCode() == KeyEvent.VK_ENTER){
            LOGIN();
        }
    }
    public void keyTyped(KeyEvent ke){}
    public void keyReleased(KeyEvent ke){}
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == LoginButton) {
            LOGIN();
        }
        if(e.getSource() == SignUpButton) {
            new SignUpGUI(this);
        }
    }
    private void LOGIN(){
        name = nameTextField.getText();
        passWord = new String(userPassword.getPassword());
        String getPassword = "";
        if (name.equals("Username")) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            userPassword.setText("Password");
            userPassword.setForeground(Color.GRAY);
            userPassword.setEchoChar((char)0);
            return;
        }
        getPassword = ude.findUserPassword(name);
        
        if (getPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username not found", "Error", JOptionPane.ERROR_MESSAGE);
            userPassword.setText("Password");
            userPassword.setForeground(Color.GRAY);
            userPassword.setEchoChar((char)0);
            return;
        }
        if(passWord.equals(getPassword)) {
            dispose();
            MyGUI mygui = new Javapro.MyGUI(name,ude);
            mygui.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Wrong Password", "Error", JOptionPane.ERROR_MESSAGE);
            userPassword.setText("Password");
            userPassword.setForeground(Color.GRAY);
            userPassword.setEchoChar((char)0);
        }
    }
}