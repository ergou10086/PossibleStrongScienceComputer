package view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

/**
 * 函数图像绘制界面
 * 提供函数绘图、积分、微分等功能的可视化界面
 */
public class FunctionView extends JFrame {
    private JPanel plotPanel;
    private JTextField functionField;
    private JTextField xMinField;
    private JTextField xMaxField;
    private JTextField yMinField;
    private JTextField yMaxField;
    private JButton plotButton;
    private JButton plotDerivativeButton;
    private JButton evaluatePointButton;     // 新增：计算函数值按钮
    private JButton solveEquationButton;     // 新增：解方程按钮
    private JButton integrateButton;
    private JButton saveButton;
    private JButton loadButton;
    private JComboBox<String> savedFunctionsCombo;
    private JLabel statusLabel;

    private double[][] functionPoints;
    private double[][] derivativePoints;
    private double integralResult;
    private double integralLowerBound;
    private double integralUpperBound;
    private boolean showIntegral = false;
    private boolean showDerivative = false;
    private int derivativeOrder = 1; // 导数阶数
    private List<Double> equationRoots = new ArrayList<>(); // 方程的根
    private boolean showEquationRoots = false; // 是否显示方程的根

    // 新增字段
    private String derivativeExpression = ""; // 导数表达式
    private String originalExpression = ""; // 原始函数表达式
    private Point mousePosition = null; // 鼠标位置
    private List<double[][]> multipleDerivatives = new ArrayList<>(); // 存储多个导数曲线的点
    private List<String> derivativeExpressions = new ArrayList<>(); // 存储多个导数表达式
    private List<Integer> derivativeOrders = new ArrayList<>(); // 存储导数阶数

    public FunctionView() {
        initFunctionView();
    }

    private void initFunctionView() {
        setTitle("函数计算与绘图");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建函数输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("函数设置"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 函数输入
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("函数 f(x) ="), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        functionField = new JTextField("sin(x)");
        inputPanel.add(functionField, gbc);

        // X轴范围
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        inputPanel.add(new JLabel("X 范围:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        xMinField = new JTextField("-10");
        inputPanel.add(xMinField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("到"), gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        xMaxField = new JTextField("10");
        inputPanel.add(xMaxField, gbc);

        // Y轴范围
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Y 范围:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        yMinField = new JTextField("-5");
        inputPanel.add(yMinField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("到"), gbc);

        gbc.gridx = 3;
        gbc.gridy = 2;
        yMaxField = new JTextField("5");
        inputPanel.add(yMaxField, gbc);

        // 保存的函数下拉框
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("已保存函数:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        savedFunctionsCombo = new JComboBox<>();
        inputPanel.add(savedFunctionsCombo, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        plotButton = new JButton("绘制函数");
        plotDerivativeButton = new JButton("绘制导数");
        evaluatePointButton = new JButton("计算函数值");
        solveEquationButton = new JButton("解方程");
        integrateButton = new JButton("计算积分");
        saveButton = new JButton("保存函数");
        loadButton = new JButton("加载函数");

        buttonPanel.add(plotButton);
        buttonPanel.add(plotDerivativeButton);
        buttonPanel.add(evaluatePointButton);
        buttonPanel.add(solveEquationButton);
        buttonPanel.add(integrateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        inputPanel.add(buttonPanel, gbc);

        // 状态标签
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        inputPanel.add(statusLabel, gbc);

        // 创建绘图面板
        plotPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawFunction(g);
            }
        };
        plotPanel.setBackground(Color.WHITE);
        plotPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // 将面板添加到主面板
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(plotPanel), BorderLayout.CENTER);

        // 将主面板添加到框架
        add(mainPanel);
    }

    /**
     * 绘制函数图像
     */
    private void drawFunction(Graphics g) {
        if (functionPoints == null || functionPoints[0].length == 0) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制坐标轴
        drawAxes(g2d);

        // 设置函数曲线颜色和粗细
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2.0f));

        // 绘制函数曲线
        for (int i = 0; i < functionPoints[0].length - 1; i++) {
            double x1 = functionPoints[0][i];
            double y1 = functionPoints[1][i];
            double x2 = functionPoints[0][i + 1];
            double y2 = functionPoints[1][i + 1];

            if (Double.isNaN(y1) || Double.isNaN(y2) ||
                    Double.isInfinite(y1) || Double.isInfinite(y2)) {
                continue;
            }

            int screenX1 = mapXToScreen(x1);
            int screenY1 = mapYToScreen(y1);
            int screenX2 = mapXToScreen(x2);
            int screenY2 = mapYToScreen(y2);

            g2d.drawLine(screenX1, screenY1, screenX2, screenY2);
        }

        // 如果需要显示导数
        if (showDerivative && derivativePoints != null && derivativePoints[0].length > 0) {
            // 设置导数曲线颜色和粗细
            if (derivativeOrder == 1) {
                g2d.setColor(Color.RED);
            } else {
                g2d.setColor(new Color(128, 0, 128)); // 紫色表示高阶导数
            }
            g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 5}, 0));

            // 绘制导数曲线
            for (int i = 0; i < derivativePoints[0].length - 1; i++) {
                double x1 = derivativePoints[0][i];
                double y1 = derivativePoints[1][i];
                double x2 = derivativePoints[0][i + 1];
                double y2 = derivativePoints[1][i + 1];

                if (Double.isNaN(y1) || Double.isNaN(y2) ||
                        Double.isInfinite(y1) || Double.isInfinite(y2)) {
                    continue;
                }

                int screenX1 = mapXToScreen(x1);
                int screenY1 = mapYToScreen(y1);
                int screenX2 = mapXToScreen(x2);
                int screenY2 = mapYToScreen(y2);

                g2d.drawLine(screenX1, screenY1, screenX2, screenY2);
            }
        }
        
        // 绘制多个导数曲线
        if (multipleDerivatives != null && !multipleDerivatives.isEmpty()) {
            // 为不同阶数的导数使用不同的颜色
            Color[] derivativeColors = {
                Color.RED,           // 1阶导数 - 红色
                new Color(128, 0, 128), // 2阶导数 - 紫色
                Color.GREEN.darker(),   // 3阶导数 - 深绿色
                Color.ORANGE,        // 4阶导数 - 橙色
                Color.CYAN.darker()     // 5阶导数 - 深青色
            };
            
            // 绘制每条导数曲线
            for (int d = 0; d < multipleDerivatives.size(); d++) {
                double[][] points = multipleDerivatives.get(d);
                if (points != null && points[0].length > 0) {
                    // 设置颜色和线型
                    int colorIndex = Math.min(d, derivativeColors.length - 1);
                    g2d.setColor(derivativeColors[colorIndex]);
                    g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, 
                                                 new float[]{5, 5}, d * 2)); // 不同的虚线偏移
                    
                    // 绘制曲线
                    for (int i = 0; i < points[0].length - 1; i++) {
                        double x1 = points[0][i];
                        double y1 = points[1][i];
                        double x2 = points[0][i + 1];
                        double y2 = points[1][i + 1];
                        
                        if (Double.isNaN(y1) || Double.isNaN(y2) ||
                                Double.isInfinite(y1) || Double.isInfinite(y2)) {
                            continue;
                        }
                        
                        int screenX1 = mapXToScreen(x1);
                        int screenY1 = mapYToScreen(y1);
                        int screenX2 = mapXToScreen(x2);
                        int screenY2 = mapYToScreen(y2);
                        
                        g2d.drawLine(screenX1, screenY1, screenX2, screenY2);
                    }
                    
                    // 绘制图例
                    int order = derivativeOrders.get(d);
                    String label = order + "阶导数";
                    g2d.drawString(label, 20, 60 + d * 20);
                }
            }
        }

        // 如果需要显示积分区域
        if (showIntegral) {
            // 设置积分区域颜色和透明度
            g2d.setColor(new Color(0, 200, 0, 50));

            // 绘制积分区域
            for (int i = 0; i < functionPoints[0].length - 1; i++) {
                double x1 = functionPoints[0][i];
                double y1 = functionPoints[1][i];
                double x2 = functionPoints[0][i + 1];
                double y2 = functionPoints[1][i + 1];

                // 只绘制在积分区间内的部分
                if (x1 >= integralLowerBound && x1 <= integralUpperBound) {
                    if (Double.isNaN(y1) || Double.isNaN(y2) ||
                            Double.isInfinite(y1) || Double.isInfinite(y2)) {
                        continue;
                    }

                    int screenX1 = mapXToScreen(x1);
                    int screenY1 = mapYToScreen(y1);
                    int screenX2 = mapXToScreen(x2);
                    int screenY2 = mapYToScreen(y2);
                    int screenY0 = mapYToScreen(0);

                    // 创建积分区域多边形
                    int[] xPoints = {screenX1, screenX2, screenX2, screenX1};
                    int[] yPoints = {screenY1, screenY2, screenY0, screenY0};

                    g2d.fillPolygon(xPoints, yPoints, 4);
                }
            }

            // 绘制积分结果
            g2d.setColor(Color.BLACK);
            DecimalFormat df = new DecimalFormat("#.####");
            String integralText = "∫(" + integralLowerBound + "," + integralUpperBound + ") = " + df.format(integralResult);
            g2d.drawString(integralText, 10, 20);
        }

        // 如果需要显示方程根
        if (showEquationRoots && equationRoots != null && !equationRoots.isEmpty()) {
            // 设置根的标记颜色和大小
            g2d.setColor(Color.RED);
            int rootSize = 8;

            // 绘制每个根的位置
            for (Double root : equationRoots) {
                if (root >= getXMin() && root <= getXMax()) {
                    int screenX = mapXToScreen(root);
                    int screenY = mapYToScreen(0); // 根在x轴上

                    // 绘制根的标记 (X形状)
                    g2d.drawLine(screenX - rootSize, screenY - rootSize, screenX + rootSize, screenY + rootSize);
                    g2d.drawLine(screenX - rootSize, screenY + rootSize, screenX + rootSize, screenY - rootSize);

                    // 显示根的值
                    DecimalFormat df = new DecimalFormat("#.####");
                    g2d.drawString(df.format(root), screenX + rootSize, screenY - rootSize);
                }
            }

            // 显示根的数量
            g2d.setColor(Color.BLACK);
            g2d.drawString("方程根数量: " + equationRoots.size(), 10, 40);
        }
    }

    /**
     * 绘制坐标轴
     */
    private void drawAxes(Graphics2D g2d) {
        int width = plotPanel.getWidth();
        int height = plotPanel.getHeight();

        // 获取坐标范围
        double xMin = getXMin();
        double xMax = getXMax();
        double yMin = getYMin();
        double yMax = getYMax();

        // 设置坐标轴颜色和粗细
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));

        // 绘制X轴（如果在可视范围内）
        if (yMin <= 0 && yMax >= 0) {
            int yZero = mapYToScreen(0);
            g2d.drawLine(0, yZero, width, yZero);

            // 绘制X轴刻度
            int numTicks = 10;
            double tickStep = (xMax - xMin) / numTicks;
            DecimalFormat df = new DecimalFormat("#.##");

            for (int i = 0; i <= numTicks; i++) {
                double x = xMin + i * tickStep;
                int xPos = mapXToScreen(x);

                // 绘制刻度线
                g2d.drawLine(xPos, yZero - 5, xPos, yZero + 5);

                // 绘制刻度值
                String tickLabel = df.format(x);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(tickLabel);
                g2d.drawString(tickLabel, xPos - labelWidth / 2, yZero + 20);
            }
        }

        // 绘制Y轴（如果在可视范围内）
        if (xMin <= 0 && xMax >= 0) {
            int xZero = mapXToScreen(0);
            g2d.drawLine(xZero, 0, xZero, height);

            // 绘制Y轴刻度
            int numTicks = 10;
            double tickStep = (yMax - yMin) / numTicks;
            DecimalFormat df = new DecimalFormat("#.##");

            for (int i = 0; i <= numTicks; i++) {
                double y = yMin + i * tickStep;
                int yPos = mapYToScreen(y);

                // 绘制刻度线
                g2d.drawLine(xZero - 5, yPos, xZero + 5, yPos);

                // 绘制刻度值
                String tickLabel = df.format(y);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(tickLabel);
                g2d.drawString(tickLabel, xZero - labelWidth - 5, yPos + 5);
            }
        }
    }

    /**
     * 隐藏导数曲线
     */
    public void hideDerivative() {
        this.showDerivative = false;
        plotPanel.repaint();
    }

    /**
     * 隐藏积分区域
     */
    public void hideIntegral() {
        this.showIntegral = false;
        plotPanel.repaint();
    }

    /**
     * 设置状态消息
     */
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    /**
     * 更新保存的函数列表
     */
    public void updateSavedFunctions(Map<String, String> functions) {
        savedFunctionsCombo.removeAllItems();
        for (String name : functions.keySet()) {
            savedFunctionsCombo.addItem(name);
        }
    }

    /**
     * 获取选中的保存函数名称
     */
    public String getSelectedFunctionName() {
        return (String) savedFunctionsCombo.getSelectedItem();
    }

    /**
     * 为按钮添加监听器
     */
    public void addActionListener(ActionListener listener) {
        plotButton.setActionCommand("plot");
        plotDerivativeButton.setActionCommand("derivative");
        evaluatePointButton.setActionCommand("evaluate");
        solveEquationButton.setActionCommand("solve");
        integrateButton.setActionCommand("integral");
        saveButton.setActionCommand("save");
        loadButton.setActionCommand("load");

        plotButton.addActionListener(listener);
        plotDerivativeButton.addActionListener(listener);
        evaluatePointButton.addActionListener(listener);
        solveEquationButton.addActionListener(listener);
        integrateButton.addActionListener(listener);
        saveButton.addActionListener(listener);
        loadButton.addActionListener(listener);
    }

    /**
     * 显示积分对话框
     * @return 积分区间 [下界, 上界]，如果取消返回null
     */
    public double[] showIntegralDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("下界:"));
        JTextField lowerField = new JTextField("0");
        panel.add(lowerField);
        panel.add(new JLabel("上界:"));
        JTextField upperField = new JTextField("1");
        panel.add(upperField);

        int result = JOptionPane.showConfirmDialog(this, panel, "计算积分",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double lower = Double.parseDouble(lowerField.getText());
                double upper = Double.parseDouble(upperField.getText());
                return new double[] {lower, upper};
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "请输入有效的数值", "输入错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        return null;
    }

    /**
     * 显示导数对话框
     */
    public Map<String, Object> showDerivativeDialog() {
        Map<String, Object> config = new HashMap<>();
        
        // 创建对话框面板
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        // 选择导数类型
        ButtonGroup typeGroup = new ButtonGroup();
        JRadioButton singleButton = new JRadioButton("单阶导数", true);
        JRadioButton multipleButton = new JRadioButton("多阶导数");
        typeGroup.add(singleButton);
        typeGroup.add(multipleButton);
        
        panel.add(singleButton);
        panel.add(multipleButton);
        
        // 阶数输入面板
        JPanel orderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        orderPanel.add(new JLabel("导数阶数:"));
        JTextField orderField = new JTextField(5);
        orderPanel.add(orderField);
        panel.add(orderPanel);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "导数设置",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
        if (result == JOptionPane.OK_OPTION) {
            try {
                if (singleButton.isSelected()) {
                    config.put("type", "single");
                    config.put("order", Integer.parseInt(orderField.getText().trim()));
                } else {
                    config.put("type", "multiple");
                    List<Integer> orders = new ArrayList<>();
                    String[] orderStrings = orderField.getText().split(",");
                    for (String s : orderStrings) {
                        orders.add(Integer.parseInt(s.trim()));
                    }
                    config.put("orders", orders);
                }
                return config;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "请输入有效的阶数", "输入错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    /**
     * 显示统一的方程求解对话框
     */
    public Map<String, Object> showUnifiedEquationSolverDialog() {
        Map<String, Object> config = new HashMap<>();
        
        // 创建对话框面板
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        
        // 选择方程类型
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] types = {"一般方程", "二次方程", "一元一次方程", "二元一次方程组", "三元一次方程组", "二元二次方程组"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typePanel.add(new JLabel("方程类型:"));
        typePanel.add(typeCombo);
        mainPanel.add(typePanel, BorderLayout.NORTH);
        
        // 根据不同类型显示不同的输入字段
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        
        // 一般方程面板
        JPanel generalPanel = createGeneralEquationPanel();
        cardPanel.add(generalPanel, "一般方程");
        
        // 二次方程面板
        JPanel quadraticPanel = createQuadraticEquationPanel();
        cardPanel.add(quadraticPanel, "二次方程");
        
        // 一元一次方程面板
        JPanel linearPanel = createLinearEquationPanel();
        cardPanel.add(linearPanel, "一元一次方程");
        
        // 二元一次方程组面板
        JPanel twoLinearPanel = createTwoLinearEquationsPanel();
        cardPanel.add(twoLinearPanel, "二元一次方程组");
        
        // 三元一次方程组面板
        JPanel threeLinearPanel = createThreeLinearEquationsPanel();
        cardPanel.add(threeLinearPanel, "三元一次方程组");
        
        // 二元二次方程组面板
        JPanel twoQuadraticPanel = createTwoQuadraticEquationsPanel();
        cardPanel.add(twoQuadraticPanel, "二元二次方程组");
        
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        
        // 监听类型变化，切换面板
        typeCombo.addActionListener(e -> {
            cardLayout.show(cardPanel, (String) typeCombo.getSelectedItem());
        });
        
        // 显示初始面板
        cardLayout.show(cardPanel, types[0]);
        
        int result = JOptionPane.showConfirmDialog(this, mainPanel, "方程求解",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
        if (result == JOptionPane.OK_OPTION) {
            try {
                // 根据选择的类型返回相应的配置
                String type = (String) typeCombo.getSelectedItem();
                
                // 转换类型名称为代码中使用的标识符
                String typeKey;
                switch (type) {
                    case "一般方程": typeKey = "general"; break;
                    case "二次方程": typeKey = "quadratic"; break;
                    case "一元一次方程": typeKey = "linear"; break;
                    case "二元一次方程组": typeKey = "twoLinear"; break;
                    case "三元一次方程组": typeKey = "threeLinear"; break;
                    case "二元二次方程组": typeKey = "twoQuadratic"; break;
                    default: typeKey = "general";
                }
                config.put("type", typeKey);
                
                // 根据不同类型获取输入值
                if ("一般方程".equals(type)) {
                    JTextField expressionField = (JTextField) findComponentByName(generalPanel, "expressionField");
                    JTextField startField = (JTextField) findComponentByName(generalPanel, "startField");
                    JTextField endField = (JTextField) findComponentByName(generalPanel, "endField");
                    
                    config.put("expression", expressionField.getText());
                    config.put("start", Double.parseDouble(startField.getText()));
                    config.put("end", Double.parseDouble(endField.getText()));
                } else if ("二次方程".equals(type)) {
                    JTextField aField = (JTextField) findComponentByName(quadraticPanel, "aField");
                    JTextField bField = (JTextField) findComponentByName(quadraticPanel, "bField");
                    JTextField cField = (JTextField) findComponentByName(quadraticPanel, "cField");
                    
                    config.put("a", Double.parseDouble(aField.getText()));
                    config.put("b", Double.parseDouble(bField.getText()));
                    config.put("c", Double.parseDouble(cField.getText()));
                } else if ("一元一次方程".equals(type)) {
                    JTextField aField = (JTextField) findComponentByName(linearPanel, "aField");
                    JTextField bField = (JTextField) findComponentByName(linearPanel, "bField");
                    
                    config.put("a", Double.parseDouble(aField.getText()));
                    config.put("b", Double.parseDouble(bField.getText()));
                } else if ("二元一次方程组".equals(type)) {
                    JTextField a1Field = (JTextField) findComponentByName(twoLinearPanel, "a1Field");
                    JTextField b1Field = (JTextField) findComponentByName(twoLinearPanel, "b1Field");
                    JTextField c1Field = (JTextField) findComponentByName(twoLinearPanel, "c1Field");
                    JTextField a2Field = (JTextField) findComponentByName(twoLinearPanel, "a2Field");
                    JTextField b2Field = (JTextField) findComponentByName(twoLinearPanel, "b2Field");
                    JTextField c2Field = (JTextField) findComponentByName(twoLinearPanel, "c2Field");
                    
                    double[] coefficients = new double[6];
                    coefficients[0] = Double.parseDouble(a1Field.getText());
                    coefficients[1] = Double.parseDouble(b1Field.getText());
                    coefficients[2] = Double.parseDouble(c1Field.getText());
                    coefficients[3] = Double.parseDouble(a2Field.getText());
                    coefficients[4] = Double.parseDouble(b2Field.getText());
                    coefficients[5] = Double.parseDouble(c2Field.getText());
                    
                    config.put("coefficients", coefficients);
                } else if ("三元一次方程组".equals(type)) {
                    double[][] coefficients = new double[3][4];
                    
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 4; j++) {
                            JTextField field = (JTextField) findComponentByName(threeLinearPanel, 
                                    "coef" + i + j + "Field");
                            coefficients[i][j] = Double.parseDouble(field.getText());
                        }
                    }
                    
                    config.put("coefficients", coefficients);
                } else if ("二元二次方程组".equals(type)) {
                    double[][] coefficients = new double[2][6];
                    
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 6; j++) {
                            JTextField field = (JTextField) findComponentByName(twoQuadraticPanel, 
                                    "coef" + i + j + "Field");
                            coefficients[i][j] = Double.parseDouble(field.getText());
                        }
                    }
                    
                    config.put("coefficients", coefficients);
                }
                
                return config;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "请输入有效的数值", "输入错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "发生错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return null;
    }
    
    /**
     * 根据名称查找组件
     */
    private Component findComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return component;
            }
            
            if (component instanceof Container) {
                Component found = findComponentByName((Container) component, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    // 添加这些方法
    public void setPlotDerivative(boolean b) {
        this.showDerivative = b;
    }

    public void setDerivativeExpression(String expression) {
        this.derivativeExpression = expression;
    }

    public void setPlotMultipleDerivatives(boolean b) {
        // 实现多重导数绘制逻辑
        // 清除单个导数的显示
        if (b) {
            this.showDerivative = false;
        }
    }
    
    public void setSelectedDerivativeOrders(List<Integer> orders) {
        this.derivativeOrders = orders;
    }

    /*
    public void clearMultipleDerivatives() {
        this.multipleDerivatives.clear();
        this.derivativeExpressions.clear();
        this.derivativeOrders.clear();
    }
     */

    public void setOriginalExpression(String expression) {
        this.originalExpression = expression;
    }

    private int mapXToScreen(double x) {
        double xMin = getXMin();
        double xMax = getXMax();
        int width = plotPanel.getWidth();
        return (int) ((x - xMin) / (xMax - xMin) * width);
    }

    private int mapYToScreen(double y) {
        double yMin = getYMin();
        double yMax = getYMax();
        int height = plotPanel.getHeight();
        return height - (int) ((y - yMin) / (yMax - yMin) * height);
    }

    /**
     * 设置函数表达式
     * @param expression 函数表达式
     */
    public void setFunctionExpression(String expression) {
        functionField.setText(expression);
    }

    /**
     * 获取函数表达式
     * @return 函数表达式
     */
    public String getFunctionExpression() {
        return functionField.getText();
    }

    /**
     * 设置X轴最小值
     * @param xMin X轴最小值
     */
    public void setXMin(double xMin) {
        xMinField.setText(String.valueOf(xMin));
    }

    /**
     * 获取X轴最小值
     * @return X轴最小值
     */
    public double getXMin() {
        try {
            return Double.parseDouble(xMinField.getText());
        } catch (NumberFormatException e) {
            return -10.0; // 默认值
        }
    }

    /**
     * 设置X轴最大值
     * @param xMax X轴最大值
     */
    public void setXMax(double xMax) {
        xMaxField.setText(String.valueOf(xMax));
    }

    /**
     * 获取X轴最大值
     * @return X轴最大值
     */
    public double getXMax() {
        try {
            return Double.parseDouble(xMaxField.getText());
        } catch (NumberFormatException e) {
            return 10.0; // 默认值
        }
    }

    /**
     * 设置Y轴最小值
     * @param yMin Y轴最小值
     */
    public void setYMin(double yMin) {
        yMinField.setText(String.valueOf(yMin));
    }

    /**
     * 获取Y轴最小值
     * @return Y轴最小值
     */
    public double getYMin() {
        try {
            return Double.parseDouble(yMinField.getText());
        } catch (NumberFormatException e) {
            return -5.0; // 默认值
        }
    }

    /**
     * 设置Y轴最大值
     * @param yMax Y轴最大值
     */
    public void setYMax(double yMax) {
        yMaxField.setText(String.valueOf(yMax));
    }

    /**
     * 获取Y轴最大值
     * @return Y轴最大值
     */
    public double getYMax() {
        try {
            return Double.parseDouble(yMaxField.getText());
        } catch (NumberFormatException e) {
            return 5.0; // 默认值
        }
    }

    /**
     * 设置函数点数据
     * @param points 函数点数据
     */
    public void setFunctionPoints(double[][] points) {
        this.functionPoints = points;
    }


    /**
     * 设置导数点数据
     * @param points 导数点数据
     */
    /*
    public void setDerivativePoints(double[][] points) {
        this.derivativePoints = points;
    }
    */

    /**
     * 设置是否显示导数
     * @param show 是否显示
     */
    public void setShowDerivative(boolean show) {
        this.showDerivative = show;
    }

    /**
     * 设置导数阶数
     * @param order 导数阶数
     */
    public void setDerivativeOrder(int order) {
        this.derivativeOrder = order;
    }

    /**
     * 设置方程根
     * @param roots 方程根列表
     */
    public void setEquationRoots(List<Double> roots) {
        this.equationRoots = roots;
    }

    /**
     * 设置是否显示方程根
     * @param show 是否显示
     */
    public void setShowEquationRoots(boolean show) {
        this.showEquationRoots = show;
    }

    /**
     * 设置积分结果和区间
     * @param result 积分结果
     * @param lowerBound 下界
     * @param upperBound 上界
     */
    public void setIntegralResult(double result, double lowerBound, double upperBound) {
        this.integralResult = result;
        this.integralLowerBound = lowerBound;
        this.integralUpperBound = upperBound;
        this.showIntegral = true;
    }

    /**
     * 设置状态文本
     * @param text 状态文本
     */
    /*
    public void setStatusText(String text) {
        statusLabel.setText(text);
    }
     */

    /**
     * 重绘函数图像
     */
    /*
    public void repaintPlot() {
        plotPanel.repaint();
    }
     */

    /**
     * 显示错误消息
     */
    /*
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
     */

    /**
     * 显示信息消息
     */
    /*
    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "信息", JOptionPane.INFORMATION_MESSAGE);
    }
     */

    /**
     * 清除绘图
     */
    public void clearPlot() {
        functionPoints = null;
        derivativePoints = null;
        showIntegral = false;
        showDerivative = false;
        showEquationRoots = false;
        equationRoots.clear();
        multipleDerivatives.clear();
        derivativeExpressions.clear();
        derivativeOrders.clear();
        repaintPlot();
    }

    /**
     * 创建一般方程输入面板
     */
    private JPanel createGeneralEquationPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        panel.add(new JLabel("方程表达式 f(x) = 0:"));
        JTextField expressionField = new JTextField();
        expressionField.setName("expressionField");
        panel.add(expressionField);
        
        panel.add(new JLabel("搜索区间起点:"));
        JTextField startField = new JTextField("-10");
        startField.setName("startField");
        panel.add(startField);
        
        panel.add(new JLabel("搜索区间终点:"));
        JTextField endField = new JTextField("10");
        endField.setName("endField");
        panel.add(endField);
        
        return panel;
    }

    /**
     * 创建二次方程输入面板
     */
    private JPanel createQuadraticEquationPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        panel.add(new JLabel("二次项系数 a:"));
        JTextField aField = new JTextField("1");
        aField.setName("aField");
        panel.add(aField);
        
        panel.add(new JLabel("一次项系数 b:"));
        JTextField bField = new JTextField("0");
        bField.setName("bField");
        panel.add(bField);
        
        panel.add(new JLabel("常数项 c:"));
        JTextField cField = new JTextField("0");
        cField.setName("cField");
        panel.add(cField);
        
        return panel;
    }

    /**
     * 显示保存函数对话框
     * @return 函数名称，如果取消返回null
     */
    public String showSaveFunctionDialog() {
        return JOptionPane.showInputDialog(this, 
            "请输入函数名称:", 
            "保存函数", 
            JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * 显示输入对话框
     * @param message 提示消息
     * @param title 对话框标题
     * @return 用户输入的文本，如果取消则返回null
     */
    public String showInputDialog(String message, String title) {
        return JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * 显示信息对话框
     * @param message 信息内容
     */
    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "信息", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示错误对话框
     * @param message 错误信息
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 添加导数曲线
     * @param points 导数点
     * @param expression 导数表达式
     * @param order 导数阶数
     */
    public void addDerivative(double[][] points, String expression, int order) {
        multipleDerivatives.add(points);
        derivativeExpressions.add(expression);
        derivativeOrders.add(order);
    }
    
    /**
     * 设置导数点
     * @param points 导数点
     */
    public void setDerivativePoints(double[][] points) {
        this.derivativePoints = points;
    }
    
    /**
     * 重绘图表
     */
    public void repaintPlot() {
        plotPanel.repaint();
    }
    
    /**
     * 设置状态文本
     * @param text 状态文本
     */
    public void setStatusText(String text) {
        statusLabel.setText(text);
    }
    
    /**
     * 清除多个导数曲线
     */
    public void clearMultipleDerivatives() {
        this.multipleDerivatives.clear();
        this.derivativeExpressions.clear();
        this.derivativeOrders.clear();
    }

    /**
     * 创建一元一次方程输入面板
     */
    private JPanel createLinearEquationPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        
        panel.add(new JLabel("系数 a (ax + b = 0):"));
        JTextField aField = new JTextField("1");
        aField.setName("aField");
        panel.add(aField);
        
        panel.add(new JLabel("常数项 b:"));
        JTextField bField = new JTextField("0");
        bField.setName("bField");
        panel.add(bField);
        
        return panel;
    }
    
    /**
     * 创建二元一次方程组输入面板
     */
    private JPanel createTwoLinearEquationsPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        
        // 第一个方程 a1x + b1y = c1
        panel.add(new JLabel("a₁ (a₁x + b₁y = c₁):"));
        JTextField a1Field = new JTextField("1");
        a1Field.setName("a1Field");
        panel.add(a1Field);
        
        panel.add(new JLabel("b₁:"));
        JTextField b1Field = new JTextField("1");
        b1Field.setName("b1Field");
        panel.add(b1Field);
        
        panel.add(new JLabel("c₁:"));
        JTextField c1Field = new JTextField("0");
        c1Field.setName("c1Field");
        panel.add(c1Field);
        
        // 第二个方程 a2x + b2y = c2
        panel.add(new JLabel("a₂ (a₂x + b₂y = c₂):"));
        JTextField a2Field = new JTextField("1");
        a2Field.setName("a2Field");
        panel.add(a2Field);
        
        panel.add(new JLabel("b₂:"));
        JTextField b2Field = new JTextField("1");
        b2Field.setName("b2Field");
        panel.add(b2Field);
        
        panel.add(new JLabel("c₂:"));
        JTextField c2Field = new JTextField("0");
        c2Field.setName("c2Field");
        panel.add(c2Field);
        
        return panel;
    }
    
    /**
     * 创建三元一次方程组输入面板
     */
    private JPanel createThreeLinearEquationsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // 第一个方程 a₁x + b₁y + c₁z = d₁
        panel.add(new JLabel("方程 1: a₁x + b₁y + c₁z = d₁"));
        panel.add(new JLabel(""));
        
        panel.add(new JLabel("a₁:"));
        JTextField coef00Field = new JTextField("1");
        coef00Field.setName("coef00Field");
        panel.add(coef00Field);
        
        panel.add(new JLabel("b₁:"));
        JTextField coef01Field = new JTextField("0");
        coef01Field.setName("coef01Field");
        panel.add(coef01Field);
        
        panel.add(new JLabel("c₁:"));
        JTextField coef02Field = new JTextField("0");
        coef02Field.setName("coef02Field");
        panel.add(coef02Field);
        
        panel.add(new JLabel("d₁:"));
        JTextField coef03Field = new JTextField("0");
        coef03Field.setName("coef03Field");
        panel.add(coef03Field);
        
        // 第二个方程 a₂x + b₂y + c₂z = d₂
        panel.add(new JLabel("方程 2: a₂x + b₂y + c₂z = d₂"));
        panel.add(new JLabel(""));
        
        panel.add(new JLabel("a₂:"));
        JTextField coef10Field = new JTextField("0");
        coef10Field.setName("coef10Field");
        panel.add(coef10Field);
        
        panel.add(new JLabel("b₂:"));
        JTextField coef11Field = new JTextField("1");
        coef11Field.setName("coef11Field");
        panel.add(coef11Field);
        
        panel.add(new JLabel("c₂:"));
        JTextField coef12Field = new JTextField("0");
        coef12Field.setName("coef12Field");
        panel.add(coef12Field);
        
        panel.add(new JLabel("d₂:"));
        JTextField coef13Field = new JTextField("0");
        coef13Field.setName("coef13Field");
        panel.add(coef13Field);
        
        // 第三个方程 a₃x + b₃y + c₃z = d₃
        panel.add(new JLabel("方程 3: a₃x + b₃y + c₃z = d₃"));
        panel.add(new JLabel(""));
        
        panel.add(new JLabel("a₃:"));
        JTextField coef20Field = new JTextField("0");
        coef20Field.setName("coef20Field");
        panel.add(coef20Field);
        
        panel.add(new JLabel("b₃:"));
        JTextField coef21Field = new JTextField("0");
        coef21Field.setName("coef21Field");
        panel.add(coef21Field);
        
        panel.add(new JLabel("c₃:"));
        JTextField coef22Field = new JTextField("1");
        coef22Field.setName("coef22Field");
        panel.add(coef22Field);
        
        panel.add(new JLabel("d₃:"));
        JTextField coef23Field = new JTextField("0");
        coef23Field.setName("coef23Field");
        panel.add(coef23Field);
        
        return panel;
    }
    
    /**
     * 创建二元二次方程组输入面板
     */
    private JPanel createTwoQuadraticEquationsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // 第一个方程 a₁x² + b₁xy + c₁y² + d₁x + e₁y + f₁ = 0
        panel.add(new JLabel("方程 1: a₁x² + b₁xy + c₁y² + d₁x + e₁y + f₁ = 0"));
        panel.add(new JLabel(""));
        
        panel.add(new JLabel("a₁ (x²系数):"));
        JTextField coef00Field = new JTextField("1");
        coef00Field.setName("coef00Field");
        panel.add(coef00Field);
        
        panel.add(new JLabel("b₁ (xy系数):"));
        JTextField coef01Field = new JTextField("0");
        coef01Field.setName("coef01Field");
        panel.add(coef01Field);
        
        panel.add(new JLabel("c₁ (y²系数):"));
        JTextField coef02Field = new JTextField("0");
        coef02Field.setName("coef02Field");
        panel.add(coef02Field);
        
        panel.add(new JLabel("d₁ (x系数):"));
        JTextField coef03Field = new JTextField("0");
        coef03Field.setName("coef03Field");
        panel.add(coef03Field);
        
        panel.add(new JLabel("e₁ (y系数):"));
        JTextField coef04Field = new JTextField("0");
        coef04Field.setName("coef04Field");
        panel.add(coef04Field);
        
        panel.add(new JLabel("f₁ (常数项):"));
        JTextField coef05Field = new JTextField("0");
        coef05Field.setName("coef05Field");
        panel.add(coef05Field);
        
        // 第二个方程 a₂x² + b₂xy + c₂y² + d₂x + e₂y + f₂ = 0
        panel.add(new JLabel("方程 2: a₂x² + b₂xy + c₂y² + d₂x + e₂y + f₂ = 0"));
        panel.add(new JLabel(""));
        
        panel.add(new JLabel("a₂ (x²系数):"));
        JTextField coef10Field = new JTextField("0");
        coef10Field.setName("coef10Field");
        panel.add(coef10Field);
        
        panel.add(new JLabel("b₂ (xy系数):"));
        JTextField coef11Field = new JTextField("0");
        coef11Field.setName("coef11Field");
        panel.add(coef11Field);
        
        panel.add(new JLabel("c₂ (y²系数):"));
        JTextField coef12Field = new JTextField("1");
        coef12Field.setName("coef12Field");
        panel.add(coef12Field);
        
        panel.add(new JLabel("d₂ (x系数):"));
        JTextField coef13Field = new JTextField("0");
        coef13Field.setName("coef13Field");
        panel.add(coef13Field);
        
        panel.add(new JLabel("e₂ (y系数):"));
        JTextField coef14Field = new JTextField("0");
        coef14Field.setName("coef14Field");
        panel.add(coef14Field);
        
        panel.add(new JLabel("f₂ (常数项):"));
        JTextField coef15Field = new JTextField("0");
        coef15Field.setName("coef15Field");
        panel.add(coef15Field);
        
        return panel;
    }
}
