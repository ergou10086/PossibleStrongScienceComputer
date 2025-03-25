package controller;

import java.util.*;

import view.LoginView;
import view.CalculatorView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LoginView loginView;
    private CalculatorView calculatorView;

    //构造函数
    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        calculatorView = new CalculatorView();

        // 为按钮设置监听器
        this.loginView.getLoginButton().addActionListener(new LoginListener());
        this.loginView.getCancelButton().addActionListener(new CanelListener());
    }

    // 登录逻辑实现
    private void login(String username, String password) {
        // 检查用户输入是否为空
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "用户名和密码不能为空!");
            return;
        }

        if(username.equals("admin") && password.equals("password")) {
            JOptionPane.showMessageDialog(loginView, "Login successful!");
            // 显示计算器主页面视图
            calculatorView.setVisible(true);
            // 关闭登陆页面，避免重开
            loginView.setVisible(false);
        } else {   // 异常
            JOptionPane.showMessageDialog(loginView, "Invalid username or password!");
        }
    }

    // 登录按钮的监听器
    public class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginView.getUsernameField().getText();
            String password = loginView.getPasswordField().getText();

            login(username, password);
        }
    }

    // 取消按钮的监听器
    public class CanelListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loginView.setVisible(false);
            calculatorView.setVisible(false);
            System.exit(0);
        }
    }

}
