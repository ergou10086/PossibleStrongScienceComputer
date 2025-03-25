package controller;

import model.FunctionModel;
import model.EquationSolverModel;
import view.FunctionView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

/**
 * 函数控制器类
 * 处理函数绘图和计算的业务逻辑
 */
public class FunctionController implements ActionListener {
    private FunctionView view;
    private FunctionModel functionModel;
    private EquationSolverModel equationSolverModel;

    /**
     * 构造函数
     * @param view 函数视图
     * @param functionModel 函数模型
     */
    public FunctionController(FunctionView view, FunctionModel functionModel) {
        this.view = view;
        this.functionModel = functionModel;
        this.equationSolverModel = new EquationSolverModel(functionModel);

        // 注册按钮事件监听器
        view.addActionListener(this);

        // 更新已保存函数列表
        updateSavedFunctionsList();
    }

    /**
     * 处理绘制函数按钮的点击事件
     */
    public void handlePlotButtonClick() {
        try {
            // 获取函数表达式
            String expression = view.getFunctionExpression();
            if (expression == null || expression.trim().isEmpty()) {
                view.showErrorMessage("请先输入函数表达式");
                return;
            }

            // 获取绘图范围
            double xMin = view.getXMin();
            double xMax = view.getXMax();
            int points = 1000;

            // 绘制函数
            double[][] functionPoints = functionModel.getFunctionPoints(expression, xMin, xMax, points);
            view.setFunctionPoints(functionPoints);
            view.setShowDerivative(false);
            view.setShowEquationRoots(false);
            view.setOriginalExpression(expression);
            view.clearMultipleDerivatives();
            view.repaintPlot();
            view.setStatusText("函数已绘制: " + expression);
        } catch (Exception e) {
            view.showErrorMessage("绘制函数时出错: " + e.getMessage());
        }
    }

    /**
     * 处理绘制导数按钮的点击事件
     */
    public void handleDerivativeButtonClick() {
        try {
            // 获取函数表达式
            String expression = view.getFunctionExpression();
            if (expression == null || expression.trim().isEmpty()) {
                view.showErrorMessage("请先输入函数表达式");
                return;
            }

            // 显示导数对话框
            Map<String, Object> config = view.showDerivativeDialog();
            if (config == null) {
                return; // 用户取消操作
            }

            // 获取绘图范围
            double xMin = view.getXMin();
            double xMax = view.getXMax();
            int points = 1000;
            double h = 0.0001; // 微分步长

            String type = (String) config.get("type");
            if ("single".equals(type)) {
                // 绘制单阶导数
                int order = (Integer) config.get("order");
                
                // 计算导数表达式
                String derivativeExpression;
                if (order == 1) {
                    derivativeExpression = functionModel.getDerivativeExpression(expression);
                } else {
                    derivativeExpression = functionModel.getNthDerivativeExpression(expression, order);
                }
                
                // 计算导数点
                double[][] derivativePoints;
                if (order == 1) {
                    derivativePoints = functionModel.getDerivativePoints(expression, xMin, xMax, points);
                } else {
                    derivativePoints = functionModel.getNthDerivativePoints(expression, xMin, xMax, points, order, h);
                }
                
                // 更新视图
                view.setDerivativePoints(derivativePoints);
                view.setDerivativeExpression(derivativeExpression);
                view.setDerivativeOrder(order);
                view.setPlotDerivative(true);
                view.setPlotMultipleDerivatives(false);
                view.clearMultipleDerivatives();
                view.repaintPlot();
                view.setStatusText("已绘制 " + order + " 阶导数");
            } else if ("multiple".equals(type)) {
                // 绘制多阶导数
                @SuppressWarnings("unchecked")
                List<Integer> orders = (List<Integer>) config.get("orders");
                
                // 清除旧的导数数据
                view.clearMultipleDerivatives();
                
                // 为每个阶数计算导数
                for (int order : orders) {
                    // 计算导数表达式
                    String derivativeExpression;
                    if (order == 1) {
                        derivativeExpression = functionModel.getDerivativeExpression(expression);
                    } else {
                        derivativeExpression = functionModel.getNthDerivativeExpression(expression, order);
                    }
                    
                    // 计算导数点
                    double[][] derivativePoints;
                    if (order == 1) {
                        derivativePoints = functionModel.getDerivativePoints(expression, xMin, xMax, points);
                    } else {
                        derivativePoints = functionModel.getNthDerivativePoints(expression, xMin, xMax, points, order, h);
                    }
                    
                    // 添加到多重导数列表
                    view.addDerivative(derivativePoints, derivativeExpression, order);
                }
                
                // 更新视图
                view.setSelectedDerivativeOrders(orders);
                view.setPlotDerivative(false);
                view.setPlotMultipleDerivatives(true);
                view.repaintPlot();
                view.setStatusText("已绘制多阶导数: " + orders);
            }
        } catch (Exception e) {
            view.showErrorMessage("计算导数时出错: " + e.getMessage());
        }
    }

    /**
     * 处理解方程按钮的点击事件
     */
    public void handleSolveEquationButtonClick() {
        try {
            // 显示方程求解对话框
            Map<String, Object> config = view.showUnifiedEquationSolverDialog();
            if (config == null) {
                return; // 用户取消操作
            }

            String type = (String) config.get("type");
            StringBuilder resultText = new StringBuilder("方程求解结果:\n");

            if ("general".equals(type)) {
                // 一般方程 f(x) = 0
                String expression = (String) config.get("expression");
                double start = (Double) config.get("start");
                double end = (Double) config.get("end");

                // 设置函数表达式
                view.setFunctionExpression(expression);

                // 求解方程
                double root = equationSolverModel.bisectionMethod(expression, start, end, 1e-10);
                resultText.append(String.format("方程 %s = 0 的解为: x = %.6f", expression, root));
            } else if ("quadratic".equals(type)) {
                // 二次方程 ax² + bx + c = 0
                double a = (Double) config.get("a");
                double b = (Double) config.get("b");
                double c = (Double) config.get("c");

                // 求解二次方程
                List<Double> roots = equationSolverModel.solveQuadraticEquation(a, b, c);

                if (roots.size() == 2) {
                    resultText.append(String.format("方程 %.2fx² + %.2fx + %.2f = 0 的解为:\n", a, b, c));
                    resultText.append(String.format("x₁ = %.6f\nx₂ = %.6f", roots.get(0), roots.get(1)));
                } else if (roots.size() == 1) {
                    resultText.append(String.format("方程 %.2fx² + %.2fx + %.2f = 0 的解为:\n", a, b, c));
                    resultText.append(String.format("x = %.6f (重根)", roots.get(0)));
                } else {
                    resultText.append(String.format("方程 %.2fx² + %.2fx + %.2f = 0 没有实数解", a, b, c));
                }
            } else if ("linear".equals(type)) {
                // 一元一次方程 ax + b = 0
                double a = (Double) config.get("a");
                double b = (Double) config.get("b");

                // 求解一元一次方程
                double root = functionModel.solveLinearEquation(a, b);
                resultText.append(String.format("方程 %.2fx + %.2f = 0 的解为:\nx = %.6f", a, b, root));
            } else if ("twoLinear".equals(type)) {
                // 二元一次方程组
                double[] coefficients = (double[]) config.get("coefficients");
                double a1 = coefficients[0];
                double b1 = coefficients[1];
                double c1 = coefficients[2];
                double a2 = coefficients[3];
                double b2 = coefficients[4];
                double c2 = coefficients[5];

                // 求解二元一次方程组
                double[] solution = functionModel.solveTwoLinearEquations(a1, b1, c1, a2, b2, c2);

                if (solution != null) {
                    resultText.append(String.format("方程组:\n%.2fx + %.2fy = %.2f\n%.2fx + %.2fy = %.2f\n的解为:\n",
                            a1, b1, c1, a2, b2, c2));
                    resultText.append(String.format("x = %.6f\ny = %.6f", solution[0], solution[1]));
                } else {
                    resultText.append("方程组无解或有无穷多解");
                }
            } else if ("threeLinear".equals(type)) {
                // 三元一次方程组
                double[][] coefficients = (double[][]) config.get("coefficients");

                // 求解三元一次方程组
                double[] solution = functionModel.solveThreeLinearEquations(coefficients);

                if (solution != null) {
                    resultText.append("方程组:\n");
                    for (int i = 0; i < 3; i++) {
                        resultText.append(String.format("%.2fx + %.2fy + %.2fz = %.2f\n",
                                coefficients[i][0], coefficients[i][1],
                                coefficients[i][2], coefficients[i][3]));
                    }
                    resultText.append("的解为:\n");
                    resultText.append(String.format("x = %.6f\ny = %.6f\nz = %.6f",
                            solution[0], solution[1], solution[2]));
                } else {
                    resultText.append("方程组无解或有无穷多解");
                }
            } else if ("twoQuadratic".equals(type)) {
                // 二元二次方程组
                double[][] coefficients = (double[][]) config.get("coefficients");

                // 求解二元二次方程组
                double[] initialGuess = {0.0, 0.0};
                double tolerance = 1e-10;
                int maxIterations = 100;

                List<double[]> solutions = equationSolverModel.solveTwoQuadraticEquations(
                        coefficients[0], coefficients[1], initialGuess, tolerance, maxIterations);

                if (solutions != null && !solutions.isEmpty()) {
                    resultText.append("方程组:\n");
                    for (int i = 0; i < 2; i++) {
                        resultText.append(String.format("%.2fx² + %.2fxy + %.2fy² + %.2fx + %.2fy + %.2f = 0\n",
                                coefficients[i][0], coefficients[i][1],
                                coefficients[i][2], coefficients[i][3],
                                coefficients[i][4], coefficients[i][5]));
                    }
                    resultText.append("的解为:\n");

                    for (int i = 0; i < solutions.size(); i++) {
                        double[] sol = solutions.get(i);
                        resultText.append(String.format("解 %d: x = %.6f, y = %.6f\n", i + 1, sol[0], sol[1]));
                    }
                } else {
                    resultText.append("未找到方程组的解");
                }
            }

            // 显示结果
            view.showInfoMessage(resultText.toString());

        } catch (Exception e) {
            view.showErrorMessage("解方程时出错: " + e.getMessage());
        }
    }

    /**
     * 处理绘制积分按钮的点击事件
     */
    public void handleIntegralButtonClick() {
        try {
            // 获取函数表达式
            String expression = view.getFunctionExpression();
            if (expression == null || expression.trim().isEmpty()) {
                view.showErrorMessage("请先输入函数表达式");
                return;
            }

            // 获取积分范围
            double[] bounds = view.showIntegralDialog();
            if (bounds == null) {
                return;
            }
            double lowerBound = bounds[0];
            double upperBound = bounds[1];

            // 计算定积分
            double result = functionModel.integrate(expression, lowerBound, upperBound, 50);
            view.setIntegralResult(result, lowerBound, upperBound);
            view.repaintPlot();
            view.setStatusText(String.format("积分结果: %.6f", result));
        } catch (Exception e) {
            view.showErrorMessage("计算积分时出错: " + e.getMessage());
        }
    }

    /**
     * 处理清除按钮的点击事件
     */
    public void handleClearButtonClick() {
        view.clearPlot();
        view.setStatusText("已清除绘图区域");
    }

    /**
     * 处理保存按钮的点击事件
     */
    public void handleSaveButtonClick() {
        // 获取函数表达式
        String expression = view.getFunctionExpression();
        if (expression == null || expression.trim().isEmpty()) {
            view.showErrorMessage("请先输入函数表达式");
            return;
        }

        // 获取函数名称
        String name = view.showSaveFunctionDialog();
        if (name != null && !name.isEmpty()) {
            functionModel.saveFunction(name, expression);
            updateSavedFunctionsList();
            view.setStatusText("函数已保存: " + name);
        }
    }

    /**
     * 处理加载按钮的点击事件
     */
    public void handleLoadButtonClick() {
        // 获取函数名称
        String name = view.getSelectedFunctionName();

        if (name != null) {
            String expression = functionModel.getFunction(name);
            view.setFunctionExpression(expression);
            view.setStatusText("函数已加载: " + name);
        }
    }

    /**
     * 处理计算函数值按钮的点击事件
     */
    public void handleEvaluateButtonClick() {
        try {
            // 获取函数表达式
            String expression = view.getFunctionExpression();
            if (expression == null || expression.trim().isEmpty()) {
                view.showErrorMessage("请先输入函数表达式");
                return;
            }

            // 显示计算函数值对话框
            String xValueStr = view.showInputDialog("请输入x的值:", "计算函数值");
            if (xValueStr == null || xValueStr.trim().isEmpty()) {
                return; // 用户取消操作
            }

            try {
                double xValue = Double.parseDouble(xValueStr);
                double result = functionModel.evaluateFunction(expression, xValue);
                
                // 显示计算结果
                String resultMessage = String.format("函数: %s\nx = %.6f 时的值为: %.6f", expression, xValue, result);
                view.showInfoMessage(resultMessage);
                view.setStatusText(String.format("f(%.2f) = %.6f", xValue, result));
            } catch (NumberFormatException e) {
                view.showErrorMessage("请输入有效的数值");
            }
        } catch (Exception e) {
            view.showErrorMessage("计算函数值时出错: " + e.getMessage());
        }
    }

    /**
     * 更新已保存函数列表
     */
    private void updateSavedFunctionsList() {
        Map<String, String> functions = functionModel.getSavedFunctions();
        view.updateSavedFunctions(functions);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        try {
            switch (command) {
                case "plot":
                    handlePlotButtonClick();
                    break;
                case "derivative":
                    handleDerivativeButtonClick();
                    break;
                case "evaluate":
                    handleEvaluateButtonClick();
                    break;
                case "solve":
                    handleSolveEquationButtonClick();
                    break;
                case "integral":
                    handleIntegralButtonClick();
                    break;
                case "save":
                    handleSaveButtonClick();
                    break;
                case "load":
                    handleLoadButtonClick();
                    break;
                case "clear":
                    view.clearPlot();
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            view.showErrorMessage("操作失败: " + ex.getMessage());
        }
    }
}
