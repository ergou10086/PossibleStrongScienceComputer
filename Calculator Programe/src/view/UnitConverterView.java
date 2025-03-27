package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UnitConverterView extends JDialog {
    private final JTextField inputField = new JTextField(10);
    private final JComboBox<String> unitSelector = new JComboBox<>();
    private final JTextField resultField = new JTextField(10);
    private final JButton convertButton;
    private final JButton closeButton;

    public UnitConverterView(JFrame parent) {
        super(parent, "单位转换", true);
        convertButton = new JButton("立即转换");
        closeButton = new JButton("关闭");
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 输入标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("输入值："), gbc);

        // 输入框
        gbc.gridx = 1;
        inputField.setFont(new Font("Consolas", Font.PLAIN, 14));
        mainPanel.add(inputField, gbc);

        // 单位选择标签
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("转换类型："), gbc);

        // 单位选择下拉框
        gbc.gridx = 1;
        unitSelector.setModel(new DefaultComboBoxModel<>(new String[]{
                "厘米 → 英寸", "英寸 → 厘米",
                "摄氏度 → 华氏度", "华氏度 → 摄氏度",
                "千克 → 磅", "磅 → 千克"
        }));
        unitSelector.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(unitSelector, gbc);

        // 转换按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        convertButton.setFont(new Font("Arial", Font.BOLD, 14));
        convertButton.setBackground(new Color(200, 255, 200));
        mainPanel.add(convertButton, gbc);

        // 结果显示
        gbc.gridy = 3;
        mainPanel.add(new JLabel("转换结果："), gbc);
        gbc.gridx = 1;
        resultField.setEditable(false);
        resultField.setFont(new Font("Consolas", Font.PLAIN, 14));
        resultField.setBackground(Color.WHITE);
        mainPanel.add(resultField, gbc);

        // 关闭按钮
        gbc.gridy = 4;
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(closeButton, gbc);

        // 整体设置
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 添加按钮样式
        convertButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 设置默认按钮
        getRootPane().setDefaultButton(convertButton);
    }

    // 获取用户输入值
    public double getInputValue() {
        try {
            return Double.parseDouble(inputField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
            return Double.NaN;
        }
    }

    // 获取选择的转换类型索引
    public int getConversionType() {
        return unitSelector.getSelectedIndex();
    }

    // 显示结果
    public void setResult(double value) {
        resultField.setText(String.format("%.4f", value));
    }

    // 添加按钮监听器
    public void addConvertListener(ActionListener listener) {
        convertButton.addActionListener(listener);
        unitSelector.addActionListener(listener);
        inputField.addActionListener(listener);
    }

    // 添加关闭监听器
    public void addCloseListener(ActionListener listener) {
        closeButton.addActionListener(listener);
    }
}