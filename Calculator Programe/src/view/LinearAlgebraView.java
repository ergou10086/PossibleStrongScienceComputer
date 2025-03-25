package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 线性代数计算器视图类
 */
public class LinearAlgebraView extends JFrame {
    
    private JPanel mainPanel;
    private JComboBox<String> operationComboBox;
    private JPanel matrixInputPanel;
    private JPanel resultPanel;
    private JTextArea resultTextArea;
    private JButton calculateButton;
    private JButton clearButton;
    private JButton backButton;
    
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // 矩阵输入面板
    private JPanel determinantPanel;
    private JPanel matrixAddPanel;
    private JPanel matrixMultiplyPanel;
    private JPanel matrixTransposePanel;
    private JPanel matrixInversePanel;
    private JPanel matrixRankPanel;
    private JPanel eigenvaluesPanel;
    private JPanel adjointMatrixPanel;
    private JPanel luDecompositionPanel;
    private JPanel qrDecompositionPanel;
    
    // 矩阵维度选择
    private JSpinner rowsSpinner;
    private JSpinner colsSpinner;
    private JButton createMatrixButton;
    
    /**
     * 构造函数
     */
    public LinearAlgebraView() {
        setTitle("线性代数计算器");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initComponents();
        layoutComponents();
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 操作选择下拉框
        String[] operations = {
            "计算行列式", 
            "矩阵加法", 
            "矩阵减法",
            "矩阵乘法", 
            "矩阵转置", 
            "矩阵求逆", 
            "计算矩阵的秩", 
            "计算特征值",
            "计算伴随矩阵",
            "LU分解",
            "QR分解"
        };
        operationComboBox = new JComboBox<>(operations);
        
        // 矩阵维度选择面板
        JPanel dimensionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dimensionPanel.add(new JLabel("行数:"));
        rowsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        dimensionPanel.add(rowsSpinner);
        
        dimensionPanel.add(new JLabel("列数:"));
        colsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        dimensionPanel.add(colsSpinner);
        
        createMatrixButton = new JButton("创建矩阵");
        dimensionPanel.add(createMatrixButton);
        
        // 卡片布局面板
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // 初始化各种操作面板
        determinantPanel = new JPanel(new BorderLayout());
        matrixAddPanel = new JPanel(new BorderLayout());
        matrixMultiplyPanel = new JPanel(new BorderLayout());
        matrixTransposePanel = new JPanel(new BorderLayout());
        matrixInversePanel = new JPanel(new BorderLayout());
        matrixRankPanel = new JPanel(new BorderLayout());
        eigenvaluesPanel = new JPanel(new BorderLayout());
        adjointMatrixPanel = new JPanel(new BorderLayout());
        luDecompositionPanel = new JPanel(new BorderLayout());
        qrDecompositionPanel = new JPanel(new BorderLayout());
        
        cardPanel.add(determinantPanel, "计算行列式");
        cardPanel.add(matrixAddPanel, "矩阵加法");
        cardPanel.add(matrixAddPanel, "矩阵减法");
        cardPanel.add(matrixMultiplyPanel, "矩阵乘法");
        cardPanel.add(matrixTransposePanel, "矩阵转置");
        cardPanel.add(matrixInversePanel, "矩阵求逆");
        cardPanel.add(matrixRankPanel, "计算矩阵的秩");
        cardPanel.add(eigenvaluesPanel, "计算特征值");
        cardPanel.add(adjointMatrixPanel, "计算伴随矩阵");
        cardPanel.add(luDecompositionPanel, "LU分解");
        cardPanel.add(qrDecompositionPanel, "QR分解");
        
        // 结果面板
        resultPanel = new JPanel(new BorderLayout());
        resultTextArea = new JTextArea(10, 40);
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        resultPanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        calculateButton = new JButton("计算");
        clearButton = new JButton("清除");
        backButton = new JButton("返回主界面");
        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);
        
        // 监听操作选择变化
        operationComboBox.addActionListener(e -> {
            String selectedOperation = (String) operationComboBox.getSelectedItem();
            cardLayout.show(cardPanel, selectedOperation);
            updateDimensionConstraints(selectedOperation);
        });
    }
    
    /**
     * 根据选择的操作更新维度约束
     */
    private void updateDimensionConstraints(String operation) {
        switch (operation) {
            case "计算行列式":
            case "矩阵求逆":
            case "计算特征值":
            case "计算伴随矩阵":
            case "LU分解":
                // 要求方阵
                colsSpinner.setValue(rowsSpinner.getValue());
                colsSpinner.setEnabled(false);
                break;
            case "QR分解":
                // QR分解要求行数 >= 列数
                if ((Integer)rowsSpinner.getValue() < (Integer)colsSpinner.getValue()) {
                    rowsSpinner.setValue(colsSpinner.getValue());
                }
                colsSpinner.setEnabled(true);
                break;
            default:
                colsSpinner.setEnabled(true);
                break;
        }
    }
    
    /**
     * 布局组件
     */
    private void layoutComponents() {
        // 顶部控制面板
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel operationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        operationPanel.add(new JLabel("选择操作:"));
        operationPanel.add(operationComboBox);
        topPanel.add(operationPanel, BorderLayout.NORTH);
        topPanel.add(createDimensionPanel(), BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(resultPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    /**
     * 创建维度选择面板
     */
    private JPanel createDimensionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("行数:"));
        panel.add(rowsSpinner);
        panel.add(new JLabel("列数:"));
        panel.add(colsSpinner);
        panel.add(createMatrixButton);
        return panel;
    }
    
    /**
     * 创建矩阵输入面板
     * @param rows 行数
     * @param cols 列数
     * @return 矩阵输入面板
     */
    public JPanel createMatrixInputPanel(int rows, int cols) {
        JPanel panel = new JPanel(new GridLayout(rows, cols, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("输入矩阵"));
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JTextField field = new JTextField("0", 5);
                field.setName("matrix_" + i + "_" + j);
                panel.add(field);
            }
        }
        
        return panel;
    }
    
    /**
     * 创建两个矩阵的输入面板
     * @param rows1 第一个矩阵的行数
     * @param cols1 第一个矩阵的列数
     * @param rows2 第二个矩阵的行数
     * @param cols2 第二个矩阵的列数
     * @return 包含两个矩阵输入面板的面板
     */
    public JPanel createTwoMatricesInputPanel(int rows1, int cols1, int rows2, int cols2) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        JPanel matrix1Panel = createMatrixInputPanel(rows1, cols1);
        matrix1Panel.setBorder(BorderFactory.createTitledBorder("矩阵 A"));
        
        JPanel matrix2Panel = createMatrixInputPanel(rows2, cols2);
        matrix2Panel.setBorder(BorderFactory.createTitledBorder("矩阵 B"));
        
        panel.add(matrix1Panel);
        panel.add(matrix2Panel);
        
        return panel;
    }
    
    /**
     * 从面板中获取矩阵数据
     * @param panel 矩阵输入面板
     * @param rows 行数
     * @param cols 列数
     * @return 矩阵数据
     */
    public double[][] getMatrixFromPanel(JPanel panel, int rows, int cols) {
        double[][] matrix = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JTextField field = (JTextField) findComponentByName(panel, "matrix_" + i + "_" + j);
                if (field != null) {
                    try {
                        matrix[i][j] = Double.parseDouble(field.getText().trim());
                    } catch (NumberFormatException e) {
                        matrix[i][j] = 0;
                    }
                }
            }
        }
        
        return matrix;
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
    
    /**
     * 设置计算按钮的动作监听器
     * @param listener 动作监听器
     */
    public void setCalculateButtonListener(ActionListener listener) {
        calculateButton.addActionListener(listener);
    }
    
    /**
     * 设置清除按钮的动作监听器
     * @param listener 动作监听器
     */
    public void setClearButtonListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }
    
    /**
     * 设置返回按钮的动作监听器
     * @param listener 动作监听器
     */
    public void setBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
    
    /**
     * 设置创建矩阵按钮的动作监听器
     * @param listener 动作监听器
     */
    public void setCreateMatrixButtonListener(ActionListener listener) {
        createMatrixButton.addActionListener(listener);
    }
    
    /**
     * 获取选择的操作
     * @return 操作名称
     */
    public String getSelectedOperation() {
        return (String) operationComboBox.getSelectedItem();
    }
    
    /**
     * 获取行数
     * @return 行数
     */
    public int getRows() {
        return (Integer) rowsSpinner.getValue();
    }
    
    /**
     * 获取列数
     * @return 列数
     */
    public int getCols() {
        return (Integer) colsSpinner.getValue();
    }
    
    /**
     * 设置结果文本
     * @param text 结果文本
     */
    public void setResultText(String text) {
        resultTextArea.setText(text);
    }
    
    /**
     * 清除结果文本
     */
    public void clearResultText() {
        resultTextArea.setText("");
    }
    
    /**
     * 更新行列式面板
     */
    public void updateDeterminantPanel(int size) {
        determinantPanel.removeAll();
        JPanel matrixPanel = createMatrixInputPanel(size, size);
        determinantPanel.add(matrixPanel, BorderLayout.CENTER);
        determinantPanel.revalidate();
        determinantPanel.repaint();
    }
    
    /**
     * 更新矩阵加法面板
     */
    public void updateMatrixAddPanel(int rows, int cols) {
        matrixAddPanel.removeAll();
        JPanel twoMatricesPanel = createTwoMatricesInputPanel(rows, cols, rows, cols);
        matrixAddPanel.add(twoMatricesPanel, BorderLayout.CENTER);
        matrixAddPanel.revalidate();
        matrixAddPanel.repaint();
    }
    
    /**
     * 更新矩阵乘法面板
     */
    public void updateMatrixMultiplyPanel(int rows1, int cols1, int rows2, int cols2) {
        matrixMultiplyPanel.removeAll();
        JPanel twoMatricesPanel = createTwoMatricesInputPanel(rows1, cols1, rows2, cols2);
        matrixMultiplyPanel.add(twoMatricesPanel, BorderLayout.CENTER);
        matrixMultiplyPanel.revalidate();
        matrixMultiplyPanel.repaint();
    }
    
    /**
     * 更新矩阵转置面板
     */
    public void updateMatrixTransposePanel(int rows, int cols) {
        matrixTransposePanel.removeAll();
        JPanel matrixPanel = createMatrixInputPanel(rows, cols);
        matrixTransposePanel.add(matrixPanel, BorderLayout.CENTER);
        matrixTransposePanel.revalidate();
        matrixTransposePanel.repaint();
    }
    
    /**
     * 更新矩阵求逆面板
     */
    public void updateMatrixInversePanel(int size) {
        matrixInversePanel.removeAll();
        JPanel matrixPanel = createMatrixInputPanel(size, size);
        matrixInversePanel.add(matrixPanel, BorderLayout.CENTER);
        matrixInversePanel.revalidate();
        matrixInversePanel.repaint();
    }
    
    /**
     * 更新矩阵秩面板
     */
    public void updateMatrixRankPanel(int rows, int cols) {
        matrixRankPanel.removeAll();
        JPanel matrixPanel = createMatrixInputPanel(rows, cols);
        matrixRankPanel.add(matrixPanel, BorderLayout.CENTER);
        matrixRankPanel.revalidate();
        matrixRankPanel.repaint();
    }
    
    /**
     * 更新特征值面板
     */
    public void updateEigenvaluesPanel(int size) {
        eigenvaluesPanel.removeAll();
        JPanel matrixPanel = createMatrixInputPanel(size, size);
        eigenvaluesPanel.add(matrixPanel, BorderLayout.CENTER);
        eigenvaluesPanel.revalidate();
        eigenvaluesPanel.repaint();
    }
    
    /**
     * 更新伴随矩阵面板
     */
    public void updateAdjointMatrixPanel(int size) {
        adjointMatrixPanel.removeAll();
        JPanel matrixPanel = createMatrixInputPanel(size, size);
        adjointMatrixPanel.add(matrixPanel, BorderLayout.CENTER);
        adjointMatrixPanel.revalidate();
        adjointMatrixPanel.repaint();
    }
    
    /**
     * 更新LU分解面板
     */
    public void updateLUDecompositionPanel(int size) {
        luDecompositionPanel.removeAll();
        JPanel matrixPanel = createMatrixInputPanel(size, size);
        luDecompositionPanel.add(matrixPanel, BorderLayout.CENTER);
        luDecompositionPanel.revalidate();
        luDecompositionPanel.repaint();
    }
    
    /**
     * 更新QR分解面板
     */
    public void updateQRDecompositionPanel(int rows, int cols) {
        qrDecompositionPanel.removeAll();
        JPanel matrixPanel = createMatrixInputPanel(rows, cols);
        qrDecompositionPanel.add(matrixPanel, BorderLayout.CENTER);
        qrDecompositionPanel.revalidate();
        qrDecompositionPanel.repaint();
    }
    
    /**
     * 获取当前操作面板
     */
    public JPanel getCurrentOperationPanel() {
        String operation = getSelectedOperation();
        switch (operation) {
            case "计算行列式": return determinantPanel;
            case "矩阵加法": 
            case "矩阵减法": return matrixAddPanel;
            case "矩阵乘法": return matrixMultiplyPanel;
            case "矩阵转置": return matrixTransposePanel;
            case "矩阵求逆": return matrixInversePanel;
            case "计算矩阵的秩": return matrixRankPanel;
            case "计算特征值": return eigenvaluesPanel;
            case "计算伴随矩阵": return adjointMatrixPanel;
            case "LU分解": return luDecompositionPanel;
            case "QR分解": return qrDecompositionPanel;
            default: return null;
        }
    }
    
    /**
     * 显示错误消息
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}
