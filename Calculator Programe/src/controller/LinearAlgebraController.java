package controller;

import model.LinearAlgebraModel;
import view.LinearAlgebraView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 线性代数计算器控制器类
 */
public class LinearAlgebraController implements ActionListener {
    
    private LinearAlgebraModel model;
    private LinearAlgebraView view;
    
    /**
     * 构造函数
     * @param model 线性代数模型
     * @param view 线性代数视图
     */
    public LinearAlgebraController(LinearAlgebraModel model, LinearAlgebraView view) {
        this.model = model;
        this.view = view;
        
        // 注册监听器
        view.setCalculateButtonListener(this);
        view.setClearButtonListener(e -> view.clearResultText());
        view.setBackButtonListener(e -> view.dispose());
        view.setCreateMatrixButtonListener(e -> createMatrixInputPanels());
    }
    
    /**
     * 创建矩阵输入面板
     */
    private void createMatrixInputPanels() {
        String operation = view.getSelectedOperation();
        int rows = view.getRows();
        int cols = view.getCols();
        
        switch (operation) {
            case "计算行列式":
                // 行列式要求方阵
                view.updateDeterminantPanel(rows);
                break;
            case "矩阵加法":
            case "矩阵减法":
                // 矩阵加减法要求两个矩阵维度相同
                view.updateMatrixAddPanel(rows, cols);
                break;
            case "矩阵乘法":
                // 矩阵乘法要求第一个矩阵的列数等于第二个矩阵的行数
                int rows2 = cols; // 第二个矩阵的行数必须等于第一个矩阵的列数
                int cols2 = rows; // 可以自定义第二个矩阵的列数，这里简单设为与第一个矩阵的行数相同
                view.updateMatrixMultiplyPanel(rows, cols, rows2, cols2);
                break;
            case "矩阵转置":
                view.updateMatrixTransposePanel(rows, cols);
                break;
            case "矩阵求逆":
                // 求逆要求方阵
                view.updateMatrixInversePanel(rows);
                break;
            case "计算矩阵的秩":
                view.updateMatrixRankPanel(rows, cols);
                break;
            case "计算特征值":
                // 特征值要求方阵
                view.updateEigenvaluesPanel(rows);
                break;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String operation = view.getSelectedOperation();
            JPanel currentPanel = view.getCurrentOperationPanel();
            
            if (currentPanel == null) {
                view.showErrorMessage("请先创建矩阵");
                return;
            }
            
            switch (operation) {
                case "计算行列式":
                    calculateDeterminant(currentPanel);
                    break;
                case "矩阵加法":
                    performMatrixAddition(currentPanel);
                    break;
                case "矩阵减法":
                    performMatrixSubtraction(currentPanel);
                    break;
                case "矩阵乘法":
                    performMatrixMultiplication(currentPanel);
                    break;
                case "矩阵转置":
                    performMatrixTransposition(currentPanel);
                    break;
                case "矩阵求逆":
                    calculateMatrixInverse(currentPanel);
                    break;
                case "计算矩阵的秩":
                    calculateMatrixRank(currentPanel);
                    break;
                case "计算特征值":
                    calculateEigenvalues(currentPanel);
                    break;
            }
        } catch (Exception ex) {
            view.showErrorMessage("计算出错: " + ex.getMessage());
        }
    }
    
    /**
     * 计算行列式
     */
    private void calculateDeterminant(JPanel panel) {
        int size = view.getRows(); // 行列式是方阵，所以行数等于列数
        double[][] matrix = view.getMatrixFromPanel(panel, size, size);
        
        double det = model.calculateDeterminant(matrix);
        
        StringBuilder result = new StringBuilder();
        result.append("矩阵:\n");
        result.append(model.formatMatrix(matrix));
        result.append("\n\n行列式值: ").append(String.format("%.4f", det));
        
        view.setResultText(result.toString());
    }
    
    /**
     * 执行矩阵加法
     */
    private void performMatrixAddition(JPanel panel) {
        int rows = view.getRows();
        int cols = view.getCols();
        
        // 获取两个矩阵
        Component[] components = panel.getComponents();
        if (components.length < 2) {
            view.showErrorMessage("请先创建矩阵");
            return;
        }
        
        JPanel matrix1Panel = (JPanel) components[0];
        JPanel matrix2Panel = (JPanel) components[1];
        
        double[][] matrix1 = view.getMatrixFromPanel(matrix1Panel, rows, cols);
        double[][] matrix2 = view.getMatrixFromPanel(matrix2Panel, rows, cols);
        
        // 执行加法
        double[][] result = model.addMatrices(matrix1, matrix2);
        
        // 显示结果
        StringBuilder sb = new StringBuilder();
        sb.append("矩阵 A:\n");
        sb.append(model.formatMatrix(matrix1));
        sb.append("\n\n矩阵 B:\n");
        sb.append(model.formatMatrix(matrix2));
        sb.append("\n\n矩阵 A + B:\n");
        sb.append(model.formatMatrix(result));
        
        view.setResultText(sb.toString());
    }
    
    /**
     * 执行矩阵减法
     */
    private void performMatrixSubtraction(JPanel panel) {
        int rows = view.getRows();
        int cols = view.getCols();
        
        // 获取两个矩阵
        Component[] components = panel.getComponents();
        if (components.length < 2) {
            view.showErrorMessage("请先创建矩阵");
            return;
        }
        
        JPanel matrix1Panel = (JPanel) components[0];
        JPanel matrix2Panel = (JPanel) components[1];
        
        double[][] matrix1 = view.getMatrixFromPanel(matrix1Panel, rows, cols);
        double[][] matrix2 = view.getMatrixFromPanel(matrix2Panel, rows, cols);
        
        // 执行减法
        double[][] result = model.subtractMatrices(matrix1, matrix2);
        
        // 显示结果
        StringBuilder sb = new StringBuilder();
        sb.append("矩阵 A:\n");
        sb.append(model.formatMatrix(matrix1));
        sb.append("\n\n矩阵 B:\n");
        sb.append(model.formatMatrix(matrix2));
        sb.append("\n\n矩阵 A - B:\n");
        sb.append(model.formatMatrix(result));
        
        view.setResultText(sb.toString());
    }
    
    /**
     * 执行矩阵乘法
     */
    private void performMatrixMultiplication(JPanel panel) {
        // 获取两个矩阵
        Component[] components = panel.getComponents();
        if (components.length < 2) {
            view.showErrorMessage("请先创建矩阵");
            return;
        }
        
        JPanel matrix1Panel = (JPanel) components[0];
        JPanel matrix2Panel = (JPanel) components[1];
        
        int rows1 = view.getRows();
        int cols1 = view.getCols();
        int rows2 = cols1; // 第二个矩阵的行数必须等于第一个矩阵的列数
        int cols2 = rows1; // 这里简单设为与第一个矩阵的行数相同
        
        double[][] matrix1 = view.getMatrixFromPanel(matrix1Panel, rows1, cols1);
        double[][] matrix2 = view.getMatrixFromPanel(matrix2Panel, rows2, cols2);
        
        // 执行乘法
        double[][] result = model.multiplyMatrices(matrix1, matrix2);
        
        // 显示结果
        StringBuilder sb = new StringBuilder();
        sb.append("矩阵 A (").append(rows1).append("×").append(cols1).append("):\n");
        sb.append(model.formatMatrix(matrix1));
        sb.append("\n\n矩阵 B (").append(rows2).append("×").append(cols2).append("):\n");
        sb.append(model.formatMatrix(matrix2));
        sb.append("\n\n矩阵 A × B (").append(rows1).append("×").append(cols2).append("):\n");
        sb.append(model.formatMatrix(result));
        
        view.setResultText(sb.toString());
    }
    
    /**
     * 执行矩阵转置
     */
    private void performMatrixTransposition(JPanel panel) {
        int rows = view.getRows();
        int cols = view.getCols();
        
        double[][] matrix = view.getMatrixFromPanel(panel, rows, cols);
        
        // 执行转置
        double[][] result = model.transposeMatrix(matrix);
        
        // 显示结果
        StringBuilder sb = new StringBuilder();
        sb.append("原矩阵 (").append(rows).append("×").append(cols).append("):\n");
        sb.append(model.formatMatrix(matrix));
        sb.append("\n\n转置矩阵 (").append(cols).append("×").append(rows).append("):\n");
        sb.append(model.formatMatrix(result));
        
        view.setResultText(sb.toString());
    }
    
    /**
     * 计算矩阵的逆
     */
    private void calculateMatrixInverse(JPanel panel) {
        int size = view.getRows(); // 逆矩阵要求方阵
        
        double[][] matrix = view.getMatrixFromPanel(panel, size, size);
        
        try {
            // 计算逆矩阵
            double[][] inverse = model.invertMatrix(matrix);
            
            // 显示结果
            StringBuilder sb = new StringBuilder();
            sb.append("原矩阵:\n");
            sb.append(model.formatMatrix(matrix));
            sb.append("\n\n逆矩阵:\n");
            sb.append(model.formatMatrix(inverse));
            
            // 验证 A * A^-1 = I
            double[][] product = model.multiplyMatrices(matrix, inverse);
            sb.append("\n\n验证 A × A^-1:\n");
            sb.append(model.formatMatrix(product));
            
            view.setResultText(sb.toString());
        } catch (IllegalArgumentException e) {
            view.showErrorMessage(e.getMessage());
        }
    }
    
    /**
     * 计算矩阵的秩
     */
    private void calculateMatrixRank(JPanel panel) {
        int rows = view.getRows();
        int cols = view.getCols();
        
        double[][] matrix = view.getMatrixFromPanel(panel, rows, cols);
        
        // 计算秩
        int rank = model.calculateRank(matrix);
        
        // 显示结果
        StringBuilder sb = new StringBuilder();
        sb.append("矩阵 (").append(rows).append("×").append(cols).append("):\n");
        sb.append(model.formatMatrix(matrix));
        sb.append("\n\n矩阵的秩: ").append(rank);
        
        view.setResultText(sb.toString());
    }
    
    /**
     * 计算特征值
     */
    private void calculateEigenvalues(JPanel panel) {
        int size = view.getRows(); // 特征值要求方阵
        
        if (size > 3) {
            view.showErrorMessage("当前只支持2×2和3×3矩阵的特征值计算");
            return;
        }
        
        double[][] matrix = view.getMatrixFromPanel(panel, size, size);
        
        try {
            // 计算特征值
            double[] eigenvalues = model.calculateEigenvalues(matrix);
            
            // 显示结果
            StringBuilder sb = new StringBuilder();
            sb.append("矩阵:\n");
            sb.append(model.formatMatrix(matrix));
            sb.append("\n\n特征值:\n");
            
            if (size == 2) {
                if (eigenvalues.length == 2) {
                    // 实特征值
                    sb.append("λ₁ = ").append(String.format("%.4f", eigenvalues[0])).append("\n");
                    sb.append("λ₂ = ").append(String.format("%.4f", eigenvalues[1]));
                } else {
                    // 复特征值
                    sb.append("λ₁ = ").append(String.format("%.4f", eigenvalues[0]))
                      .append(" + ").append(String.format("%.4f", eigenvalues[1])).append("i\n");
                    sb.append("λ₂ = ").append(String.format("%.4f", eigenvalues[2]))
                      .append(" + ").append(String.format("%.4f", eigenvalues[3])).append("i");
                }
            } else if (size == 3) {
                for (int i = 0; i < eigenvalues.length; i++) {
                    sb.append("λ").append(i + 1).append(" = ")
                      .append(String.format("%.4f", eigenvalues[i])).append("\n");
                }
            }
            
            view.setResultText(sb.toString());
        } catch (IllegalArgumentException e) {
            view.showErrorMessage(e.getMessage());
        }
    }
}
