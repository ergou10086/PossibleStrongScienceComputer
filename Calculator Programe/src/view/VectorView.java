package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VectorView extends JFrame {
    private JComboBox<String> operationCombo;
    private JPanel inputPanel;
    private JTextArea resultArea;
    private JButton calculateButton;
    private JTextField[] v1Fields;
    private JTextField[] v2Fields;

    public VectorView() {
        initComponents();
        setTitle("向量计算器");
        setSize(500, 400);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 操作选择
        String[] operations = {
                "点积", "叉积", "模长", "加法", "减法",
                "夹角", "正交检查", "平行检查", "投影"
        };
        operationCombo = new JComboBox<>(operations);
        operationCombo.addActionListener(e -> updateInputFields());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("选择操作:"));
        topPanel.add(operationCombo);

        // 输入面板
        inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        createVectorInputFields(3); // 默认三维输入

        // 结果区域
        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);

        // 计算按钮
        calculateButton = new JButton("计算");

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(inputPanel), BorderLayout.CENTER);
        mainPanel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void createVectorInputFields(int dimension) {
        inputPanel.removeAll();
        inputPanel.setLayout(new GridLayout(0, 2, 5, 5));

        v1Fields = new JTextField[dimension];
        v2Fields = new JTextField[dimension];

        inputPanel.add(new JLabel("向量1:"));
        inputPanel.add(new JLabel("向量2:"));

        for (int i = 0; i < dimension; i++) {
            inputPanel.add(new JLabel("分量 " + (i+1) + ":"));
            v1Fields[i] = new JTextField(5);
            inputPanel.add(v1Fields[i]);

            inputPanel.add(new JLabel("分量 " + (i+1) + ":"));
            v2Fields[i] = new JTextField(5);
            inputPanel.add(v2Fields[i]);
        }

        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void updateInputFields() {
        String operation = (String) operationCombo.getSelectedItem();
        int dimension = 3; // 默认三维
        if ("叉积".equals(operation)) {
            dimension = 3;
        } else if ("模长".equals(operation) || "正交检查".equals(operation)
                || "平行检查".equals(operation) || "投影".equals(operation)) {
            dimension = getVectorDimension(); // 根据输入确定维度
        }
        createVectorInputFields(dimension);
    }

    private int getVectorDimension() {
        try {
            return Integer.parseInt(JOptionPane.showInputDialog("请输入向量维度:"));
        } catch (NumberFormatException e) {
            return 3;
        }
    }

    public void addCalculateListener(ActionListener listener) {
        calculateButton.addActionListener(listener);
    }

    public double[] getVector1() {
        return parseVector(v1Fields);
    }

    public double[] getVector2() {
        return parseVector(v2Fields);
    }

    private double[] parseVector(JTextField[] fields) {
        double[] vector = new double[fields.length];
        for (int i = 0; i < fields.length; i++) {
            try {
                vector[i] = Double.parseDouble(fields[i].getText());
            } catch (NumberFormatException e) {
                vector[i] = 0;
            }
        }
        return vector;
    }

    public String getSelectedOperation() {
        return (String) operationCombo.getSelectedItem();
    }

    public void setResult(String result) {
        resultArea.setText(result);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}