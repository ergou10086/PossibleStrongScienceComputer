package view;

import controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;

    public LoginView() {initView();}

    private void initView() {
        // 这个是标题
        setTitle("登录系统");
        // 这个是大小
        setSize(260, 200);
        // 这个是相对屏幕居中
        setLocationRelativeTo(null);
        // x退出程序
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 存放控件的内容
        setContentPane(createCenterPane());
    }

    private JPanel createCenterPane() {
        JPanel panel = new JPanel(new BorderLayout());
        // 处理间隙
        panel.setBorder(new EmptyBorder(15,15,15,15));
        // 登录系统
        panel.add(
                new JLabel("登录系统",JLabel.CENTER),
                BorderLayout.NORTH
        );
        // 用户名和密码框
        panel.add(createMiddlePanel(), BorderLayout.CENTER);
        // 按钮
        panel.add(createButtonPane(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createFieldPane(), BorderLayout.NORTH);
        return panel;
    }

    private JPanel createFieldPane() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 6));
        panel.add(createNamePanel());
        panel.add(createPwdPane());
        return panel;
    }

    private JPanel createNamePanel() {
        // 创建存放username的面板
        JPanel panel = new JPanel(new BorderLayout(6,0));
        panel.add(
                new JLabel("账号:"),
                BorderLayout.WEST
        );
        usernameField = new JTextField();
        panel.add(usernameField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPwdPane() {
        // 创建存放password的面板
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.add(
                new JLabel("密码:"),
                BorderLayout.WEST
        );
        passwordField = new JPasswordField();
        panel.add(passwordField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPane() {
        JPanel panel = new JPanel();
        loginButton = new JButton("登录");
        panel.add(loginButton);
        cancelButton = new JButton("取消");
        panel.add(cancelButton);
        return panel;
    }


    // getter和setter
    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getCancelButton(){
        return cancelButton;
    }

    public JButton getLoginButton() {
        return loginButton;
    }
}
