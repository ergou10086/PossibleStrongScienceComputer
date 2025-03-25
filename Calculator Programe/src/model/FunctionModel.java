package model;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 函数模型类
 * 处理函数计算、微分、积分等操作
 */
public class FunctionModel {
    private Map<String, String> savedFunctions;
    
    public FunctionModel() {
        this.savedFunctions = new HashMap<>();
    }
    
    /**
     * 保存用户定义的函数
     * @param name 函数名称
     * @param expression 函数表达式
     */
    public void saveFunction(String name, String expression) {
        savedFunctions.put(name, expression);
    }
    
    /**
     * 获取保存的函数
     * @param name 函数名称
     * @return 函数表达式
     */
    public String getFunction(String name) {
        return savedFunctions.get(name);
    }
    
    /**
     * 获取所有保存的函数
     * @return 函数映射表
     */
    public Map<String, String> getAllFunctions() {
        return new HashMap<>(savedFunctions);
    }
    
    /**
     * 计算函数在指定点的值
     * @param expression 函数表达式
     * @param x x值
     * @return 函数值
     */
    public double evaluateFunction(String expression, double x) {
        // 替换表达式中的x为具体值
        String replacedExpr = expression.replace("x", "(" + x + ")");
        
        // 替换常量
        replacedExpr = replacedExpr.replace("π", String.valueOf(Math.PI));
        replacedExpr = replacedExpr.replace("e", String.valueOf(Math.E));
        
        // 使用现有的表达式计算器计算结果
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        return evaluator.evaluate(replacedExpr);
    }
    
    /**
     * 数值积分 - 使用辛普森法则
     * @param expression 函数表达式
     * @param lowerBound 下限
     * @param upperBound 上限
     * @param intervals 区间数量（越大越精确）
     * @return 积分结果
     */
    public double integrate(String expression, double lowerBound, double upperBound, int intervals) {
        if (intervals % 2 != 0) {
            intervals++; // 确保区间数量为偶数
        }
        
        double h = (upperBound - lowerBound) / intervals;
        double sum = evaluateFunction(expression, lowerBound) + evaluateFunction(expression, upperBound);
        
        // 偶数项
        for (int i = 2; i < intervals; i += 2) {
            double x = lowerBound + i * h;
            sum += 2 * evaluateFunction(expression, x);
        }
        
        // 奇数项
        for (int i = 1; i < intervals; i += 2) {
            double x = lowerBound + i * h;
            sum += 4 * evaluateFunction(expression, x);
        }
        
        return sum * h / 3;
    }
    
    /**
     * 数值微分 - 中心差分法
     * @param expression 函数表达式
     * @param x 计算点
     * @param h 步长
     * @return 导数值
     */
    public double differentiate(String expression, double x, double h) {
        return (evaluateFunction(expression, x + h) - evaluateFunction(expression, x - h)) / (2 * h);
    }
    
    /**
     * 计算n阶导数 - 递归方法
     * @param expression 函数表达式
     * @param x 计算点
     * @param order 导数阶数
     * @param h 步长
     * @return n阶导数值
     */
    public double differentiateNthOrder(String expression, double x, int order, double h) {
        if (order < 0) {
            throw new IllegalArgumentException("导数阶数不能为负数");
        }
        
        if (order == 0) {
            return evaluateFunction(expression, x);
        }
        
        if (order == 1) {
            return differentiate(expression, x, h);
        }
        
        // 递归计算n阶导数
        return (differentiateNthOrder(expression, x + h, order - 1, h) - 
                differentiateNthOrder(expression, x - h, order - 1, h)) / (2 * h);
    }
    
    /**
     * 获取函数在指定区间的数据点
     * @param expression 函数表达式
     * @param start 起始x值
     * @param end 结束x值
     * @param points 点的数量
     * @return x和y值的数组
     */
    public double[][] getFunctionPoints(String expression, double start, double end, int points) {
        double[][] result = new double[2][points];
        double step = (end - start) / (points - 1);
        
        for (int i = 0; i < points; i++) {
            double x = start + i * step;
            result[0][i] = x;
            try {
                result[1][i] = evaluateFunction(expression, x);
            } catch (Exception e) {
                result[1][i] = Double.NaN; // 处理无法计算的点
            }
        }
        
        return result;
    }
    
    /**
     * 获取导数在指定区间的数据点
     * @param expression 函数表达式
     * @param start 起始x值
     * @param end 结束x值
     * @param points 点的数量
     * @return x和y'值的数组
     */
    public double[][] getDerivativePoints(String expression, double start, double end, int points) {
        double[][] result = new double[2][points];
        double step = (end - start) / (points - 1);
        double h = 0.0001; // 微分步长
        
        for (int i = 0; i < points; i++) {
            double x = start + i * step;
            result[0][i] = x;
            try {
                result[1][i] = differentiate(expression, x, h);
            } catch (Exception e) {
                result[1][i] = Double.NaN; // 处理无法计算的点
            }
        }
        
        return result;
    }
    
    /**
     * 获取n阶导数在指定区间的数据点
     * @param expression 函数表达式
     * @param start 起始x值
     * @param end 结束x值
     * @param points 点的数量
     * @param order 导数阶数
     * @param h 微分步长
     * @return x和y'值的数组
     */
    public double[][] getNthDerivativePoints(String expression, double start, double end, int points, int order, double h){
        double[][] result = new double[2][points];
        double step = (end - start) / (points - 1);
        
        for (int i = 0; i < points; i++) {
            double x = start + i * step;
            result[0][i] = x;
            try {
                result[1][i] = differentiateNthOrder(expression, x, order, h);
            } catch (Exception e) {
                result[1][i] = Double.NaN; // 处理无法计算的点
            }
        }
        
        return result;
    }

    public Map<String, String> getSavedFunctions() {
        return new HashMap<>(savedFunctions);
    }

    /**
     * 计算导数的符号表达式（使用符号微分）
     * 支持基本函数的导数计算，包括多项式、三角函数、指数和对数函数
     * @param expression 原函数表达式
     * @return 导数表达式
     */
    public String getDerivativeExpression(String expression) {
        // 使用简单的符号微分规则
        // 这里只实现了基本的多项式、三角函数和指数函数的导数
        
        // 替换常量
        expression = expression.replace("π", String.valueOf(Math.PI));
        expression = expression.replace("e", String.valueOf(Math.E));
        
        // 简单情况处理
        if (expression.equals("x")) {
            return "1";
        } else if (!expression.contains("x")) {
            return "0"; // 常数的导数为0
        }
        
        // 尝试解析表达式并计算导数
        try {
            // 多项式处理
            if (isPolynomial(expression)) {
                return differentiatePolynomial(expression);
            }
            
            // 三角函数处理
            if (expression.contains("sin") || expression.contains("cos") || 
                expression.contains("tan")) {
                return differentiateTrigonometric(expression);
            }
            
            // 指数和对数函数处理
            if (expression.contains("exp") || expression.contains("log")) {
                return differentiateExponentialLog(expression);
            }
            
            // 复合函数暂不支持精确符号微分，返回近似表示
            return "d/dx(" + expression + ")";
        } catch (Exception e) {
            return "d/dx(" + expression + ")"; // 无法计算时返回形式导数表示
        }
    }
    
    /**
     * 计算n阶导数的符号表达式
     * @param expression 原函数表达式
     * @param order 导数阶数
     * @return n阶导数表达式
     */
    public String getNthDerivativeExpression(String expression, int order) {
        if (order < 0) {
            throw new IllegalArgumentException("导数阶数不能为负数");
        }
        
        if (order == 0) {
            return expression;
        }
        
        String result = expression;
        for (int i = 0; i < order; i++) {
            result = getDerivativeExpression(result);
            
            // 如果无法继续计算符号导数，则使用形式表示
            if (result.startsWith("d/dx")) {
                return "d^" + order + "/dx^" + order + "(" + expression + ")";
            }
        }
        
        return result;
    }
    
    /**
     * 判断表达式是否为多项式
     * @param expression 表达式
     * @return 是否为多项式
     */
    private boolean isPolynomial(String expression) {
        // 简单判断是否为多项式
        return expression.matches(".*[0-9x\\+\\-\\*\\^\\s]+.*") && 
               !expression.matches(".*[a-wyz].*") && // 不包含除x以外的变量
               !expression.contains("sin") && 
               !expression.contains("cos") && 
               !expression.contains("tan") && 
               !expression.contains("log") && 
               !expression.contains("exp");
    }
    
    /**
     * 计算多项式的导数表达式
     * @param expression 多项式表达式
     * @return 导数表达式
     */
    private String differentiatePolynomial(String expression) {
        // 简化处理，针对形如 ax^n 的项
        // 实际应用中应该使用更复杂的表达式解析
        
        // 处理简单的幂函数
        if (expression.matches(".*x\\^([0-9]+).*")) {
            // 提取幂次
            String[] parts = expression.split("\\^");
            if (parts.length > 1) {
                try {
                    String coeffStr = parts[0].replace("x", "").trim();
                    double coeff = coeffStr.isEmpty() || coeffStr.equals("+") ? 1 : 
                                  coeffStr.equals("-") ? -1 : Double.parseDouble(coeffStr);
                    int power = Integer.parseInt(parts[1].trim());
                    
                    if (power == 0) {
                        return "0"; // x^0 的导数为0
                    } else if (power == 1) {
                        return String.valueOf(coeff); // x^1 的导数为系数
                    } else {
                        return (coeff * power) + "*x^" + (power - 1);
                    }
                } catch (NumberFormatException e) {
                    // 解析失败，返回形式导数
                    return "d/dx(" + expression + ")";
                }
            }
        }
        
        // 处理简单的线性项
        if (expression.matches(".*[0-9]*x.*") && !expression.contains("^")) {
            String coeff = expression.replace("x", "").trim();
            if (coeff.isEmpty() || coeff.equals("+")) {
                return "1";
            } else if (coeff.equals("-")) {
                return "-1";
            } else {
                try {
                    return coeff;
                } catch (NumberFormatException e) {
                    return "d/dx(" + expression + ")";
                }
            }
        }
        
        // 对于更复杂的多项式，返回形式导数
        return "d/dx(" + expression + ")";
    }
    
    /**
     * 计算三角函数的导数表达式
     * @param expression 三角函数表达式
     * @return 导数表达式
     */
    private String differentiateTrigonometric(String expression) {
        // 简单处理sin(x), cos(x), tan(x)的导数
        if (expression.equals("sin(x)")) {
            return "cos(x)";
        } else if (expression.equals("cos(x)")) {
            return "-sin(x)";
        } else if (expression.equals("tan(x)")) {
            return "1/cos(x)^2";
        }
        
        // 对于更复杂的三角函数，返回形式导数
        return "d/dx(" + expression + ")";
    }
    
    /**
     * 计算指数和对数函数的导数表达式
     * @param expression 指数或对数函数表达式
     * @return 导数表达式
     */
    private String differentiateExponentialLog(String expression) {
        // 简单处理exp(x)和log(x)的导数
        if (expression.equals("exp(x)")) {
            return "exp(x)";
        } else if (expression.equals("log(x)")) {
            return "1/x";
        }
        
        // 对于更复杂的指数和对数函数，返回形式导数
        return "d/dx(" + expression + ")";
    }
    
    /**
     * 求解一元一次方程 ax + b = 0
     */
    public double solveLinearEquation(double a, double b) {
        if (Math.abs(a) < 1e-10) {
            throw new ArithmeticException("不是一元一次方程");
        }
        return -b / a;
    }
    
    /**
     * 求解二元一次方程组
     */
    public double[] solveTwoLinearEquations(double a1, double b1, double c1, 
                                          double a2, double b2, double c2) {
        double det = a1 * b2 - a2 * b1;
        if (Math.abs(det) < 1e-10) {
            return null; // 无解或无穷多解
        }
        
        double x = (c1 * b2 - c2 * b1) / det;
        double y = (a1 * c2 - a2 * c1) / det;
        
        return new double[] {x, y};
    }
    
    /**
     * 求解三元一次方程组
     */
    public double[] solveThreeLinearEquations(double[][] coefficients) {
        // 使用克莱默法则求解
        double[][] matrix = new double[3][3];
        double[] constants = new double[3];
        
        for (int i = 0; i < 3; i++) {
            System.arraycopy(coefficients[i], 0, matrix[i], 0, 3);
            constants[i] = coefficients[i][3];
        }
        
        double det = determinant(matrix);
        if (Math.abs(det) < 1e-10) {
            return null; // 无解或无穷多解
        }
        
        double[] solution = new double[3];
        for (int i = 0; i < 3; i++) {
            double[][] temp = new double[3][3];
            for (int j = 0; j < 3; j++) {
                System.arraycopy(matrix[j], 0, temp[j], 0, 3);
                temp[j][i] = constants[j];
            }
            solution[i] = determinant(temp) / det;
        }
        
        return solution;
    }
    
    /**
     * 计算3x3矩阵的行列式
     */
    private double determinant(double[][] matrix) {
        return matrix[0][0] * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1])
             - matrix[0][1] * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0])
             + matrix[0][2] * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]);
    }
    
    /**
     * 求解二元二次方程组（使用数值方法）
     * a1*x^2 + b1*x*y + c1*y^2 + d1*x + e1*y + f1 = 0
     * a2*x^2 + b2*x*y + c2*y^2 + d2*x + e2*y + f2 = 0
     * 
     * @param coeffs1 第一个方程系数 [a1,b1,c1,d1,e1,f1]
     * @param coeffs2 第二个方程系数 [a2,b2,c2,d2,e2,f2]
     * @param initialGuess 初始猜测值 [x0,y0]
     * @param tolerance 误差容忍度
     * @param maxIterations 最大迭代次数
     * @return 解的列表，每个解是一个 [x,y] 数组
     */
    public List<double[]> solveTwoQuadraticEquations(double[] coeffs1, double[] coeffs2, 
                                                    double[] initialGuess, double tolerance, int maxIterations) {
        List<double[]> solutions = new ArrayList<>();
        
        // 使用牛顿法求解非线性方程组
        double[] solution = newtonMethodForSystem(coeffs1, coeffs2, initialGuess, tolerance, maxIterations);
        if (solution != null) {
            solutions.add(solution);
        }
        
        // 尝试不同的初始值以找到所有解
        // 这里简单地尝试几个不同的初始点
        double[][] initialGuesses = {
            {-1.0, -1.0}, {-1.0, 1.0}, {1.0, -1.0}, {1.0, 1.0},
            {0.0, -1.0}, {0.0, 1.0}, {-1.0, 0.0}, {1.0, 0.0}
        };
        
        for (double[] guess : initialGuesses) {
            if (!Arrays.equals(guess, initialGuess)) { // 避免重复计算初始猜测
                solution = newtonMethodForSystem(coeffs1, coeffs2, guess, tolerance, maxIterations);
                if (solution != null) {
                    // 检查是否是新解
                    boolean isNewSolution = true;
                    for (double[] existingSolution : solutions) {
                        if (Math.abs(existingSolution[0] - solution[0]) < tolerance && 
                            Math.abs(existingSolution[1] - solution[1]) < tolerance) {
                            isNewSolution = false;
                            break;
                        }
                    }
                    
                    if (isNewSolution) {
                        solutions.add(solution);
                    }
                }
            }
        }
        
        return solutions;
    }
    
    /**
     * 使用牛顿法求解二元二次方程组
     * @param coeffs1 第一个方程系数
     * @param coeffs2 第二个方程系数
     * @param initialGuess 初始猜测值
     * @param tolerance 误差容忍度
     * @param maxIterations 最大迭代次数
     * @return 解 [x,y] 或者 null（如果不收敛）
     */
    private double[] newtonMethodForSystem(double[] coeffs1, double[] coeffs2, double[] initialGuess, 
                                          double tolerance, int maxIterations) {
        double x = initialGuess[0];
        double y = initialGuess[1];
        
        for (int i = 0; i < maxIterations; i++) {
            // 计算函数值
            double f1 = evaluateQuadratic(coeffs1, x, y);
            double f2 = evaluateQuadratic(coeffs2, x, y);
            
            // 检查是否收敛
            if (Math.abs(f1) < tolerance && Math.abs(f2) < tolerance) {
                return new double[] {x, y};
            }
            
            // 计算雅可比矩阵
            double df1dx = 2 * coeffs1[0] * x + coeffs1[1] * y + coeffs1[3];
            double df1dy = coeffs1[1] * x + 2 * coeffs1[2] * y + coeffs1[4];
            double df2dx = 2 * coeffs2[0] * x + coeffs2[1] * y + coeffs2[3];
            double df2dy = coeffs2[1] * x + 2 * coeffs2[2] * y + coeffs2[4];
            
            // 计算行列式
            double det = df1dx * df2dy - df1dy * df2dx;
            
            // 检查行列式是否接近零
            if (Math.abs(det) < 1e-10) {
                return null; // 雅可比矩阵奇异，可能不收敛
            }
            
            // 计算下一个迭代值
            double dx = (df2dy * f1 - df1dy * f2) / det;
            double dy = (df1dx * f2 - df2dx * f1) / det;
            
            x = x - dx;
            y = y - dy;
        }
        
        // 超过最大迭代次数，检查最终结果是否足够接近解
        double f1 = evaluateQuadratic(coeffs1, x, y);
        double f2 = evaluateQuadratic(coeffs2, x, y);
        
        if (Math.abs(f1) < tolerance && Math.abs(f2) < tolerance) {
            return new double[] {x, y};
        }
        
        return null; // 不收敛
    }
    
    /**
     * 计算二次方程的值
     * @param coeffs 方程系数 [a,b,c,d,e,f]
     * @param x x值
     * @param y y值
     * @return 方程值
     */
    private double evaluateQuadratic(double[] coeffs, double x, double y) {
        return coeffs[0] * x * x + coeffs[1] * x * y + coeffs[2] * y * y + 
               coeffs[3] * x + coeffs[4] * y + coeffs[5];
    }

    /**
     * 表达式计算器内部类 - 用于计算包含变量的表达式
     */
    private class ExpressionEvaluator {
        private int pos = -1;
        private int ch;
        private String expr;
        
        public ExpressionEvaluator() {
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
                if (eat('×') || eat('*')) x *= parseFactor(); // 乘法
                else if (eat('÷') || eat('/')) x /= parseFactor(); // 除法
                else if (eat('%')) x %= parseFactor(); // 取余
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
                    x = Math.sin(parseExpression());
                    eat(')');
                } else if (func.equals("cos")) {
                    eat('(');
                    x = Math.cos(parseExpression());
                    eat(')');
                } else if (func.equals("tan")) {
                    eat('(');
                    x = Math.tan(parseExpression());
                    eat(')');
                } else if (func.equals("asin")) {
                    eat('(');
                    x = Math.asin(parseExpression());
                    eat(')');
                } else if (func.equals("acos")) {
                    eat('(');
                    x = Math.acos(parseExpression());
                    eat(')');
                } else if (func.equals("atan")) {
                    eat('(');
                    x = Math.atan(parseExpression());
                    eat(')');
                } else if (func.equals("ln")) {
                    eat('(');
                    x = Math.log(parseExpression());
                    eat(')');
                } else if (func.equals("log")) {
                    eat('(');
                    x = Math.log10(parseExpression());
                    eat(')');
                } else {
                    throw new RuntimeException("未知函数: " + func);
                }
            } else {
                throw new RuntimeException("意外的字符: " + (char)ch);
            }
            
            // 处理指数运算
            if (eat('^')) x = Math.pow(x, parseFactor());
            
            // 处理阶乘
            if (eat('!')) {
                double temp = x;
                x = 1;
                while (temp > 0) {
                    x *= temp--;
                }
            }
            
            // 处理平方根
            if (eat('√')) x = Math.sqrt(parseFactor());
            
            return x;
        }
    }
}
