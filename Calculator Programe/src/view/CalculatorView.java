package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

// 这个类实现了计算器的主页面
public class CalculatorView extends JFrame {
    private JTextArea display;  // 显示屏，使用 JTextArea 来显示多行文本
    private JButton[] numberButtons;  // 数字按钮
    private JButton[] operatorButtons;  // 运算符按钮组
    private JButton[] functionButtons;  // 函数按钮组
    private JButton[] constantButtons;  // 常数按钮组
    private JButton equalsButton;     // 等于号按钮
    private JButton clearButton;     // 清除按钮
    private JButton clearCharButton;  // 清除字符按钮
    private JButton historyButton;   // 历史计算按钮
    private JButton radDegButton;    // 弧度/角度切换按钮
    private JButton functionButton;  // 函数计算按钮
    private JButton linearAlgebraButton; // 线性代数按钮
    private JButton vectorButton;
    private JButton unitConvertButton;    // 单位转换按钮
    private boolean inRadianMode = true; // 默认使用弧度模式

    public CalculatorView() { initCalcView(); }

    private void initCalcView() {
        setTitle("科学计算器");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 设置整体UI风格
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("查看(V)");
        JMenu editMenu = new JMenu("编辑(E)");
        JMenu helpMenu = new JMenu("帮助(H)");
        
        // 添加菜单项
        JMenuItem historyMenuItem = new JMenuItem("历史记录");
        JMenuItem clearHistoryMenuItem = new JMenuItem("清除历史");
        JMenuItem exitMenuItem = new JMenuItem("退出");
        JMenuItem aboutMenuItem = new JMenuItem("关于");
        
        viewMenu.add(historyMenuItem);
        editMenu.add(clearHistoryMenuItem);
        viewMenu.add(exitMenuItem);
        helpMenu.add(aboutMenuItem);
        
        menuBar.add(viewMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);

        // 创建计算文本显示区域
        display = new JTextArea(4, 20); // 4行文本区域
        display.setEditable(false);
        display.setFont(new Font("Consolas", Font.PLAIN, 24));
        display.setLineWrap(true);
        display.setWrapStyleWord(true);
        display.setBackground(new Color(240, 240, 240));
        display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JScrollPane scrollPane = new JScrollPane(display); // 添加滚动条
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // 数字按钮实例化
        numberButtons = new JButton[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = createStyledButton(String.valueOf(i), new Color(250, 250, 250));
        }

        // 运算符按钮实例化
        String[] operators = {"+", "-", "×", "÷", "^", "√", "(", ")", "%", ".", "±"};
        operatorButtons = new JButton[operators.length];
        for (int i = 0; i < operators.length; i++) {
            operatorButtons[i] = createStyledButton(operators[i], new Color(230, 230, 250));
        }
        
        // 函数按钮实例化
        String[] functions = {"sin", "cos", "tan", "asin", "acos", "atan", "ln", "log", "!"};
        functionButtons = new JButton[functions.length];
        for (int i = 0; i < functions.length; i++) {
            functionButtons[i] = createStyledButton(functions[i], new Color(220, 240, 220));
        }
        
        // 常数按钮实例化
        String[] constants = {"π", "e"};
        constantButtons = new JButton[constants.length];
        for (int i = 0; i < constants.length; i++) {
            constantButtons[i] = createStyledButton(constants[i], new Color(255, 240, 220));
        }
        
        // 功能按钮实例化
        equalsButton = createStyledButton("=", new Color(255, 200, 100));
        clearButton = createStyledButton("AC", new Color(255, 200, 200));
        historyButton = createStyledButton("历史", new Color(200, 230, 255));
        clearCharButton = createStyledButton("C", new Color(255, 200, 200));
        radDegButton = createStyledButton("RAD", new Color(200, 255, 200));
        functionButton = createStyledButton("函数", new Color(200, 200, 255));
        linearAlgebraButton = createStyledButton("线代", new Color(230, 200, 255));
        vectorButton = createStyledButton("向量", new Color(220, 255, 220));
        unitConvertButton = createStyledButton("单位转换", new Color(200, 255, 255));


        // 主面板布局
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        
        // 左侧面板 - 数字和基本运算符
        JPanel leftPanel = new JPanel(new GridLayout(6, 4, 5, 5));
        
        // 右侧面板 - 科学计算功能
        JPanel rightPanel = new JPanel(new GridLayout(6, 3, 5, 5));
        
        // 添加左侧按钮
        leftPanel.add(clearButton);         // AC
        leftPanel.add(clearCharButton);     // C
        leftPanel.add(operatorButtons[8]);  // %
        leftPanel.add(operatorButtons[3]);  // ÷
        
        for (int i = 7; i <= 9; i++) {
            leftPanel.add(numberButtons[i]);  // 7 8 9
        }
        leftPanel.add(operatorButtons[2]);  // ×
        
        for (int i = 4; i <= 6; i++) {
            leftPanel.add(numberButtons[i]);  // 4 5 6
        }
        leftPanel.add(operatorButtons[1]);  // -
        
        for (int i = 1; i <= 3; i++) {
            leftPanel.add(numberButtons[i]);  // 1 2 3
        }
        leftPanel.add(operatorButtons[0]);  // +
        
        leftPanel.add(operatorButtons[10]); // ±
        leftPanel.add(numberButtons[0]);    // 0
        leftPanel.add(operatorButtons[9]);  // .
        leftPanel.add(equalsButton);        // =
        
        // 添加右侧按钮
        rightPanel.add(radDegButton);        // RAD/DEG
        rightPanel.add(constantButtons[0]);  // π
        rightPanel.add(constantButtons[1]);  // e
        
        rightPanel.add(functionButtons[0]);  // sin
        rightPanel.add(functionButtons[1]);  // cos
        rightPanel.add(functionButtons[2]);  // tan
        
        rightPanel.add(functionButtons[3]);  // asin
        rightPanel.add(functionButtons[4]);  // acos
        rightPanel.add(functionButtons[5]);  // atan
        
        rightPanel.add(operatorButtons[4]);  // ^
        rightPanel.add(operatorButtons[5]);  // √
        rightPanel.add(functionButtons[8]);  // !
        
        rightPanel.add(functionButtons[6]);  // ln
        rightPanel.add(functionButtons[7]);  // log
        rightPanel.add(historyButton);       // 历史
        
        rightPanel.add(operatorButtons[6]);  // (
        rightPanel.add(operatorButtons[7]);  // )
        rightPanel.add(functionButton);      // 函数
        
        // 添加额外的面板用于放置线性代数和向量按钮等
        JPanel extraPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        extraPanel.add(vectorButton);
        extraPanel.add(linearAlgebraButton);
        extraPanel.add(unitConvertButton);
        
        // 将左右面板添加到按钮面板
        buttonPanel.add(leftPanel);
        buttonPanel.add(rightPanel);
        
        // 将按钮面板和额外面板添加到主面板
        mainPanel.add(scrollPane, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(extraPanel, BorderLayout.SOUTH);
        
        // 将主面板添加到框架
        add(mainPanel);
    }
    
    // 创建统一风格的按钮
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return button;
    }

    public void setDisplayText(String input, String operation, String result) {
        StringBuilder displayText = new StringBuilder();
        
        if (!input.isEmpty()) {
            displayText.append(input);
        }
        
        if (!operation.isEmpty()) {
            displayText.append(" ").append(operation).append(" ");
        }
        
        if (!result.isEmpty()) {
            displayText.append("\n= ").append(result);
        }
        
        display.setText(displayText.toString());
    }
    
    public void appendToDisplay(String text) {
        display.append(text);
    }
    
    public void clearDisplay() {
        display.setText("");
    }
    
    public boolean isInRadianMode() {
        return inRadianMode;
    }
    
    public void toggleRadianMode() {
        inRadianMode = !inRadianMode;
        radDegButton.setText(inRadianMode ? "RAD" : "DEG");
    }

    // 为这些按钮都添加监听器
    public void addButtonListener(ActionListener listener) {
        for (JButton button : numberButtons) {
            button.addActionListener(listener);
        }
        for (JButton button : operatorButtons) {
            button.addActionListener(listener);
        }
        for (JButton button : functionButtons) {
            button.addActionListener(listener);
        }
        for (JButton button : constantButtons) {
            button.addActionListener(listener);
        }
        equalsButton.addActionListener(listener);
        clearButton.addActionListener(listener);
        historyButton.addActionListener(listener);
        clearCharButton.addActionListener(listener);
        radDegButton.addActionListener(listener);
        functionButton.addActionListener(listener);
        linearAlgebraButton.addActionListener(listener);
        vectorButton.addActionListener(listener);
        unitConvertButton.addActionListener(listener);
    }

    // 显示历史计算信息
    public void showHistory(List<String> history) {
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "暂无计算历史", "历史记录", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog historyDialog = new JDialog(this, "计算历史", true);
        historyDialog.setSize(400, 500);
        historyDialog.setLocationRelativeTo(this);
        
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        
        StringBuilder historyText = new StringBuilder();
        for (String entry : history) {
            historyText.append(entry).append("\n");
        }
        historyArea.setText(historyText.toString());
        
        JScrollPane scrollPane = new JScrollPane(historyArea);
        
        JButton clearButton = new JButton("清除历史");
        JButton closeButton = new JButton("关闭");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        buttonPanel.add(closeButton);
        
        historyDialog.setLayout(new BorderLayout());
        historyDialog.add(scrollPane, BorderLayout.CENTER);
        historyDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        closeButton.addActionListener(e -> historyDialog.dispose());
        
        historyDialog.setVisible(true);
        return;
    }

    // 获取线性代数按钮
    public JButton getLinearAlgebraButton() {
        return linearAlgebraButton;
    }
}
