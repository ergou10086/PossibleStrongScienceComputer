package controller;

import model.CalculatorModel;
import view.CalculatorView;
import view.FunctionView;
import view.LinearAlgebraView;
import controller.FunctionController;
import controller.LinearAlgebraController;
import model.FunctionModel;
import model.LinearAlgebraModel;
import view.VectorView;

import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class CalculatorController {
    private CalculatorModel model;
    private CalculatorView view;
    private StringBuilder expression;      // 完整表达式
    private boolean startNewInput;         // 是否开始新输入
    private boolean hasCalculated;         // 是否已经计算过结果

    public CalculatorController(CalculatorModel model, CalculatorView view) {
        this.model = model;
        this.view = view;
        this.expression = new StringBuilder();
        this.startNewInput = true;
        this.hasCalculated = false;

        // 监听按钮事件
        this.view.addButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // command代表按钮的标签，判断按下的什么按钮
                String command = e.getActionCommand();
                // 处理计算
                handleButtonPress(command);
            }
        });
    }

    private void handleButtonPress(String command) {
        try {
            // 数字按钮
            if (isNumber(command)) {
                handleNumberInput(command);
            }
            // 操作符按钮
            else if (isOperator(command)) {
                handleOperatorInput(command);
            }
            // 函数按钮
            else if (isFunction(command)) {
                handleFunctionInput(command);
            }
            // 常数按钮
            else if (isConstant(command)) {
                handleConstantInput(command);
            }
            // 等号按钮
            else if (command.equals("=")) {
                calculateResult();
            }
            // 清除按钮
            else if (command.equals("AC")) {
                clearAll();
            }
            // 退格按钮
            else if (command.equals("C")) {
                clearLastChar();
            }
            // 历史按钮
            else if (command.equals("历史")) {
                view.showHistory(model.getHistory());
            }
            // 弧度/角度切换按钮
            else if (command.equals("RAD") || command.equals("DEG")) {
                view.toggleRadianMode();
            }
            // 函数计算器按钮
            else if (command.equals("函数")) {
                openFunctionView();
            }
            // 线性代数按钮
            else if (command.equals("线代")) {
                openLinearAlgebraView();
            }
            // 向量按钮
            else if (command.equals("向量")) {
                openVectorView();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "计算错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            clearAll();
        }
    }

    // 处理数字输入
    private void handleNumberInput(String number) {
        if (startNewInput) {
            expression.setLength(0);
            startNewInput = false;
        }
        
        if (hasCalculated) {
            clearAll();
            hasCalculated = false;
        }
        
        expression.append(number);
        updateDisplay();
    }

    // 处理操作符输入
    private void handleOperatorInput(String operator) {
        if (hasCalculated) {
            hasCalculated = false;
        }
        
        // 处理小数点
        if (operator.equals(".")) {
            // 检查是否已经有小数点
            if (startNewInput) {
                expression.setLength(0);
                expression.append("0.");
                startNewInput = false;
            } else {
                // 检查最后一个数字是否已经有小数点
                int lastIndex = expression.length() - 1;
                boolean hasDecimal = false;
                int i = lastIndex;
                
                // 从后向前查找，直到遇到非数字字符
                while (i >= 0) {
                    char c = expression.charAt(i);
                    if (c == '.') {
                        hasDecimal = true;
                        break;
                    } else if (!Character.isDigit(c)) {
                        break;
                    }
                    i--;
                }
                
                if (!hasDecimal) {
                    // 如果最后一个字符不是数字，先添加0
                    if (lastIndex < 0 || !Character.isDigit(expression.charAt(lastIndex))) {
                        expression.append("0");
                    }
                    expression.append(".");
                }
            }
            updateDisplay();
            return;
        }
        
        // 处理正负号
        if (operator.equals("±")) {
            if (expression.length() > 0) {
                // 找到最后一个数字的开始位置
                int lastIndex = expression.length() - 1;
                int startIndex = lastIndex;
                
                // 从后向前查找，直到遇到非数字字符
                while (startIndex >= 0) {
                    char c = expression.charAt(startIndex);
                    if (!Character.isDigit(c) && c != '.') {
                        if (startIndex == lastIndex || c != '-') {
                            startIndex++;
                        }
                        break;
                    }
                    startIndex--;
                }
                
                if (startIndex <= 0) {
                    startIndex = 0;
                }
                
                // 检查是否已经有负号
                if (startIndex > 0 && expression.charAt(startIndex - 1) == '-' &&
                    (startIndex == 1 || !Character.isDigit(expression.charAt(startIndex - 2)))) {
                    // 已有负号，删除它
                    expression.deleteCharAt(startIndex - 1);
                } else {
                    // 没有负号，添加负号
                    expression.insert(startIndex, '-');
                }
                updateDisplay();
            }
            return;
        }
        
        // 确保表达式不为空或者是括号
        if (operator.equals("(")) {
            expression.append(operator);
            startNewInput = false; // 新增
            updateDisplay();
            return;
        }
        
        // 处理右括号，确保有对应的左括号
        if (operator.equals(")")) {
            int leftCount = 0;
            int rightCount = 0;
            
            for (int i = 0; i < expression.length(); i++) {
                if (expression.charAt(i) == '(') {
                    leftCount++;
                } else if (expression.charAt(i) == ')') {
                    rightCount++;
                }
            }

            if (leftCount > rightCount) {
                expression.append(operator);
                startNewInput = false;
                updateDisplay();
            }
            return;
        }
        
        // 处理其他操作符
        if (expression.length() > 0) {
            char lastChar = expression.charAt(expression.length() - 1);
            
            // 如果最后一个字符是操作符，替换它
            if (isOperatorChar(lastChar) && lastChar != ')') {
                expression.setLength(expression.length() - 1);
            }
            
            // 确保不在表达式开头添加操作符（除了负号）
            if (expression.length() > 0 && (lastChar != '(' || operator.equals("-"))) {
                expression.append(operator);
                startNewInput = false; // 新增
                updateDisplay();
            }
        } else if (operator.equals("-")) {
            expression.append(operator);
            startNewInput = false; // 新增
            updateDisplay();
        }
    }

    // 处理函数输入
    private void handleFunctionInput(String function) {
        if (hasCalculated) {
            clearAll();
            hasCalculated = false;
        }
        
        // 对于简单输入，直接计算
        if (expression.length() > 0 && !containsOperator(expression.toString())) {
            try {
                double value = Double.parseDouble(expression.toString());
                double result = calculateFunction(value, function);
                expression.setLength(0);
                expression.append(formatResult(result));
                updateDisplay();
                return;
            } catch (NumberFormatException e) {
                // 不是简单数字，继续处理
            }
        }
        
        // 对于复杂表达式，添加函数名和左括号
        expression.append(function).append("(");
        startNewInput = false;
        updateDisplay();
    }

    // 处理常数输入
    private void handleConstantInput(String constant) {
        if (startNewInput) {
            expression.setLength(0);
            startNewInput = false;
        }
        
        if (hasCalculated) {
            clearAll();
            hasCalculated = false;
        }

        if (constant.equals("π")) {
            expression.append("π");
        } else if (constant.equals("e")) {
            expression.append("e");
        }
        startNewInput = false; // 新增
        updateDisplay();
    }

    // 计算结果
    private void calculateResult() {
        if (expression.length() > 0) {
            try {
                String expressionStr = expression.toString();
                
                // 替换常量
                expressionStr = expressionStr.replace("π", String.valueOf(Math.PI));
                expressionStr = expressionStr.replace("e", String.valueOf(Math.E));
                
                // 计算表达式
                double result = evaluateExpression(expressionStr);
                
                // 更新显示
                String calculation = expression.toString();
                view.setDisplayText(calculation, "", formatResult(result));
                
                // 添加到历史记录
                model.getHistory().add(calculation + " = " + formatResult(result));
                
                // 重置状态
                expression.setLength(0);
                expression.append(formatResult(result));
                hasCalculated = true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "表达式错误: " + e.getMessage(), "计算错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 清除所有输入
    private void clearAll() {
        expression.setLength(0);
        startNewInput = true;
        hasCalculated = false;
        view.setDisplayText("", "", "");
    }

    // 清除最后一个字符
    private void clearLastChar() {
        if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
            updateDisplay();
        }
    }

    // 更新显示
    private void updateDisplay() {
        view.setDisplayText(expression.toString(), "", "");
    }

    // 检查是否为数字
    private boolean isNumber(String command) {
        return command.matches("[0-9]");
    }

    // 检查是否为操作符
    private boolean isOperator(String command) {
        return command.matches("[+\\-×÷^√%\\.±()]");
    }
    
    // 检查字符是否为操作符
    private boolean isOperatorChar(char c) {
        return "+-×÷^√%()".indexOf(c) != -1;
    }

    // 检查是否为函数
    private boolean isFunction(String command) {
        return command.matches("(sin|cos|tan|asin|acos|atan|ln|log|!)");
    }

    // 检查是否为常数
    private boolean isConstant(String command) {
        return command.equals("π") || command.equals("e");
    }
    
    // 检查表达式是否包含操作符
    private boolean containsOperator(String expr) {
        for (char c : expr.toCharArray()) {
            if (isOperatorChar(c)) {
                return true;
            }
        }
        return false;
    }

    // 计算函数结果
    private double calculateFunction(double value, String function) {
        switch (function) {
            case "sin":
                return view.isInRadianMode() ? model.sin(value) : model.sin(Math.toRadians(value));
            case "cos":
                return view.isInRadianMode() ? model.cos(value) : model.cos(Math.toRadians(value));
            case "tan":
                return view.isInRadianMode() ? model.tan(value) : model.tan(Math.toRadians(value));
            case "asin":
                double asinResult = model.arcsin(value);
                return view.isInRadianMode() ? asinResult : Math.toDegrees(asinResult);
            case "acos":
                double acosResult = model.arccos(value);
                return view.isInRadianMode() ? acosResult : Math.toDegrees(acosResult);
            case "atan":
                double atanResult = model.arctan(value);
                return view.isInRadianMode() ? atanResult : Math.toDegrees(atanResult);
            case "ln":
                return model.ln(value);
            case "log":
                return model.log10(value);
            case "!":
                return model.factorial(value);
            default:
                throw new UnsupportedOperationException("未知函数: " + function);
        }
    }
    
    // 评估表达式
    private double evaluateExpression(String expression) {
        // 创建表达式计算器
        ExpressionEvaluator evaluator = new ExpressionEvaluator(view.isInRadianMode(), model);
        return evaluator.evaluate(expression);
    }
    
    // 格式化结果，避免显示过多小数位
    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        } else {
            return String.format("%.8g", result);
        }
    }
    
    // 表达式计算器内部类
    private class ExpressionEvaluator {
        private boolean radianMode;
        private CalculatorModel model;
        private int pos = -1;
        private int ch;
        private String expr;
        
        public ExpressionEvaluator(boolean radianMode, CalculatorModel model) {
            this.radianMode = radianMode;
            this.model = model;
        }
        
        public double evaluate(String expression) {
            this.expr = expression;
            pos = -1;
            nextChar();
            double result = parseExpression();
            if (pos < expr.length()) {
                throw new RuntimeException("意外的字符: " + (char)ch);
            }
            return result;
        }
        
        private void nextChar() {
            ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
        }
        
        private boolean eat(int charToEat) {
            while (ch == ' ') nextChar();
            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }
        
        // 解析表达式
        private double parseExpression() {
            double x = parseTerm();
            while (true) {
                if (eat('+')) x += parseTerm(); // 加法
                else if (eat('-')) x -= parseTerm(); // 减法
                else return x;
            }
        }
        
        // 解析项
        private double parseTerm() {
            double x = parseFactor();
            while (true) {
                if (eat('×')) x = model.multiply(x, parseFactor()); // 乘法
                else if (eat('÷')) x = model.divide(x, parseFactor()); // 除法
                else if (eat('%')) x = model.percent(x); // 百分比
                else return x;
            }
        }
        
        // 解析因子
        private double parseFactor() {
            if (eat('+')) return parseFactor(); // 一元加
            if (eat('-')) return -parseFactor(); // 一元减
            
            double x;
            int startPos = this.pos;
            
            // 处理括号
            if (eat('(')) {
                x = parseExpression();
                eat(')');
            }
            // 处理数字
            else if ((ch >= '0' && ch <= '9') || ch == '.') {
                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                x = Double.parseDouble(expr.substring(startPos, this.pos));
            }
            // 处理函数和常量
            else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) nextChar();
                String func = expr.substring(startPos, this.pos);
                
                if (func.equals("sin")) {
                    eat('(');
                    x = radianMode ? model.sin(parseExpression()) : model.sin(Math.toRadians(parseExpression()));
                    eat(')');
                } else if (func.equals("cos")) {
                    eat('(');
                    x = radianMode ? model.cos(parseExpression()) : model.cos(Math.toRadians(parseExpression()));
                    eat(')');
                } else if (func.equals("tan")) {
                    eat('(');
                    x = radianMode ? model.tan(parseExpression()) : model.tan(Math.toRadians(parseExpression()));
                    eat(')');
                } else if (func.equals("asin")) {
                    eat('(');
                    double result = model.arcsin(parseExpression());
                    x = radianMode ? result : Math.toDegrees(result);
                    eat(')');
                } else if (func.equals("acos")) {
                    eat('(');
                    double result = model.arccos(parseExpression());
                    x = radianMode ? result : Math.toDegrees(result);
                    eat(')');
                } else if (func.equals("atan")) {
                    eat('(');
                    double result = model.arctan(parseExpression());
                    x = radianMode ? result : Math.toDegrees(result);
                    eat(')');
                } else if (func.equals("ln")) {
                    eat('(');
                    x = model.ln(parseExpression());
                    eat(')');
                } else if (func.equals("log")) {
                    eat('(');
                    x = model.log10(parseExpression());
                    eat(')');
                } else {
                    throw new RuntimeException("未知函数: " + func);
                }
            } else {
                throw new RuntimeException("意外的字符: " + (char)ch);
            }
            
            // 处理指数运算
            if (eat('^')) x = model.power(x, parseFactor());
            
            // 处理阶乘
            if (eat('!')) x = model.factorial(x);
            
            // 处理平方根
            if (eat('√')) x = model.squareRoot(parseFactor());
            
            return x;
        }
    }

    private void openFunctionView() {
        FunctionModel functionModel = new FunctionModel();
        FunctionView functionView = new FunctionView();
        FunctionController functionController = new FunctionController(functionView, functionModel);
        functionView.setVisible(true);
    }
    
    // 打开线性代数计算器视图
    private void openLinearAlgebraView() {
        LinearAlgebraModel linearAlgebraModel = new LinearAlgebraModel();
        LinearAlgebraView linearAlgebraView = new LinearAlgebraView();
        LinearAlgebraController linearAlgebraController = new LinearAlgebraController(linearAlgebraModel, linearAlgebraView);
        linearAlgebraView.setVisible(true);
    }

    // 打开向量计算器视图
    private void openVectorView() {
        VectorView vectorView = new VectorView();
        new VectorController(vectorView);
        vectorView.setVisible(true);
    }
}
