package model;

import java.util.*;

public class CalculatorModel {
    private List<String> history;   // 存储计算历史
    public static final double PI = Math.PI;  // 常数π
    public static final double E = Math.E;    // 常数e

    // 构造方法
    public CalculatorModel() {
        history = new ArrayList<>();
    }

    // 进行加法运算
    public double add(double a, double b) {
        double result = a + b;
        history.add(a + " + " + b + " = " + result);
        return result;
    }

    // 进行减法运算
    public double subtract(double a, double b) {
        double result = a - b;
        history.add(a + " - " + b + " = " + result);
        return result;
    }

    // 进行乘法运算
    public double multiply(double a, double b) {
        double result = a * b;
        history.add(a + " × " + b + " = " + result);
        return result;
    }

    // 进行除法运算
    public double divide(double a, double b) throws ArithmeticException {
        // 不能除0，避免
        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
        double result = a / b;
        history.add(a + " ÷ " + b + " = " + result);
        return result;
    }

    // 进行幂运算
    public double power(double base, double exponent) {
        double result = Math.pow(base, exponent);
        history.add(base + " ^ " + exponent + " = " + result);
        return result;
    }

    // 进行平方根运算
    public double squareRoot(double value) {
        if (value < 0) throw new ArithmeticException("Cannot calculate square root of negative number");
        double result = Math.sqrt(value);
        history.add("√" + value + " = " + result);
        return result;
    }

    // 进行sin运算
    public double sin(double value) {
        double result = Math.sin(value);
        history.add("sin(" + value + ") = " + result);
        return result;
    }

    // 进行cos运算
    public double cos(double value) {
        double result = Math.cos(value);
        history.add("cos(" + value + ") = " + result);
        return result;
    }

    // 进行tan运算
    public double tan(double value) {
        if (Math.cos(value) == 0) throw new ArithmeticException("Tangent is undefined at this value");
        double result = Math.tan(value);
        history.add("tan(" + value + ") = " + result);
        return result;
    }

    // 进行arcsin运算
    public double arcsin(double value) {
        if (value < -1 || value > 1) throw new ArithmeticException("Arcsin domain error: value must be between -1 and 1");
        double result = Math.asin(value);
        history.add("arcsin(" + value + ") = " + result);
        return result;
    }

    // 进行arccos运算
    public double arccos(double value) {
        if (value < -1 || value > 1) throw new ArithmeticException("Arccos domain error: value must be between -1 and 1");
        double result = Math.acos(value);
        history.add("arccos(" + value + ") = " + result);
        return result;
    }

    // 进行arctan运算
    public double arctan(double value) {
        double result = Math.atan(value);
        history.add("arctan(" + value + ") = " + result);
        return result;
    }

    // 进行自然对数运算
    public double ln(double value) {
        if (value <= 0) throw new ArithmeticException("Cannot calculate logarithm of non-positive number");
        double result = Math.log(value);
        history.add("ln(" + value + ") = " + result);
        return result;
    }

    // 进行以10为底的对数运算
    public double log10(double value) {
        if (value <= 0) throw new ArithmeticException("Cannot calculate logarithm of non-positive number");
        double result = Math.log10(value);
        history.add("log₁₀(" + value + ") = " + result);
        return result;
    }

    // 进行阶乘运算
    public double factorial(double value) {
        if (value < 0) throw new ArithmeticException("Cannot calculate factorial of negative number");
        if (value > 170) throw new ArithmeticException("Factorial too large to calculate");
        
        int n = (int)value;
        if (n != value) throw new ArithmeticException("Factorial requires an integer");
        
        double result = 1;
        for(int i = 1; i <= n; i++) {
            result *= i;
        }
        history.add(n + "! = " + result);
        return result;
    }

    // 进行百分比运算
    public double percent(double value) {
        double result = value / 100;
        history.add(value + "% = " + result);
        return result;
    }

    // 获取历史记录
    public List<String> getHistory() {
        return history;
    }
    
    // 清除历史记录
    public void clearHistory() {
        history.clear();
    }
}
