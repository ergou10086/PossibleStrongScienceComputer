package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 方程求解模型类
 * 提供方程求解的各种数值方法
 */
public class EquationSolverModel {
    private FunctionModel functionModel;
    private static final double DEFAULT_TOLERANCE = 1e-10;
    private static final int MAX_ITERATIONS = 100;

    /**
     * 构造函数
     */
    public EquationSolverModel() {
        this.functionModel = new FunctionModel();
    }
    public EquationSolverModel(FunctionModel functionModel) {
        this.functionModel = functionModel;
    }

    /**
     * 二分法求解方程 f(x) = 0
     * @param expression 函数表达式
     * @param lowerBound 下界
     * @param upperBound 上界
     * @param tolerance 误差容忍度
     * @return 方程的根
     */
    public double bisectionMethod(String expression, double lowerBound, double upperBound, double tolerance) {
        double a = lowerBound;
        double b = upperBound;
        double fa = functionModel.evaluateFunction(expression, a);
        double fb = functionModel.evaluateFunction(expression, b);

        // 检查区间端点是否为根
        if (Math.abs(fa) < tolerance) return a;
        if (Math.abs(fb) < tolerance) return b;

        // 检查区间是否包含根
        if (fa * fb >= 0) {
            throw new IllegalArgumentException("区间端点函数值符号相同，可能不包含根");
        }

        int iteration = 0;
        double c = a;
        double fc;

        while ((b - a) > tolerance && iteration < MAX_ITERATIONS) {
            // 计算中点
            c = (a + b) / 2;
            fc = functionModel.evaluateFunction(expression, c);

            if (Math.abs(fc) < tolerance) {
                break; // 找到根
            }

            // 更新区间
            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }

            iteration++;
        }

        return c;
    }

    /**
     * 牛顿法求解方程 f(x) = 0
     * @param expression 函数表达式
     * @param initialGuess 初始猜测值
     * @param tolerance 误差容忍度
     * @param h 微分步长
     * @return 方程的根
     */
    public double newtonMethod(String expression, double initialGuess, double tolerance, double h) {
        double x = initialGuess;
        int iteration = 0;
        double fx = functionModel.evaluateFunction(expression, x);

        while (Math.abs(fx) > tolerance && iteration < MAX_ITERATIONS) {
            // 计算导数
            double fPrime = functionModel.differentiate(expression, x, h);

            // 检查导数是否接近零
            if (Math.abs(fPrime) < 1e-10) {
                throw new ArithmeticException("导数接近零，牛顿法可能不收敛");
            }

            // 牛顿迭代
            x = x - fx / fPrime;
            fx = functionModel.evaluateFunction(expression, x);

            iteration++;
        }

        if (iteration >= MAX_ITERATIONS) {
            throw new ArithmeticException("超过最大迭代次数，可能不收敛");
        }

        return x;
    }

    /**
     * 割线法求解方程 f(x) = 0
     * @param expression 函数表达式
     * @param x0 第一个初始猜测值
     * @param x1 第二个初始猜测值
     * @param tolerance 误差容忍度
     * @return 方程的根
     */
    public double secantMethod(String expression, double x0, double x1, double tolerance) {
        double x_prev = x0;
        double x_curr = x1;
        double f_prev = functionModel.evaluateFunction(expression, x_prev);
        double f_curr = functionModel.evaluateFunction(expression, x_curr);

        int iteration = 0;
        double x_next;

        while (Math.abs(f_curr) > tolerance && iteration < MAX_ITERATIONS) {
            // 计算割线法的下一个近似值
            double denominator = f_curr - f_prev;

            // 检查分母是否接近零
            if (Math.abs(denominator) < 1e-10) {
                throw new ArithmeticException("割线法中分母接近零，可能不收敛");
            }

            x_next = x_curr - f_curr * (x_curr - x_prev) / denominator;

            // 更新值
            x_prev = x_curr;
            f_prev = f_curr;
            x_curr = x_next;
            f_curr = functionModel.evaluateFunction(expression, x_curr);

            iteration++;
        }

        if (iteration >= MAX_ITERATIONS) {
            throw new ArithmeticException("超过最大迭代次数，可能不收敛");
        }

        return x_curr;
    }

    /**
     * 寻找方程在给定区间内的所有根
     * @param expression 函数表达式
     * @param start 区间起点
     * @param end 区间终点
     * @param subdivisions 区间细分数
     * @param tolerance 误差容忍度
     * @return 根的列表
     */
    public List<Double> findAllRoots(String expression, double start, double end, int subdivisions, double tolerance) {
        List<Double> roots = new ArrayList<>();
        double step = (end - start) / subdivisions;

        for (int i = 0; i < subdivisions; i++) {
            double a = start + i * step;
            double b = a + step;
            double fa = functionModel.evaluateFunction(expression, a);
            double fb = functionModel.evaluateFunction(expression, b);

            // 检查是否有符号变化（可能存在根）
            if (fa * fb <= 0) {
                try {
                    // 使用二分法找到这个子区间内的根
                    double root = bisectionMethod(expression, a, b, tolerance);

                    // 检查是否与已有的根重复
                    boolean isDuplicate = false;
                    for (Double existingRoot : roots) {
                        if (Math.abs(existingRoot - root) < tolerance) {
                            isDuplicate = true;
                            break;
                        }
                    }

                    if (!isDuplicate) {
                        roots.add(root);
                    }
                } catch (Exception e) {
                    // 忽略无法找到根的子区间
                }
            }
        }

        return roots;
    }

    /**
     * 寻找方程在给定区间内的所有根（简化版本）
     * @param expression 函数表达式
     * @param start 区间起点
     * @param end 区间终点
     * @param tolerance 误差容忍度
     * @return 根的列表
     */
    public List<Double> findAllRoots(String expression, double start, double end, double tolerance) {
        // 默认细分为100个子区间
        return findAllRoots(expression, start, end, 100, tolerance);
    }

    /**
     * 使用二分法寻找方程在给定区间内的所有根
     * @param expression 函数表达式
     * @param start 区间起点
     * @param end 区间终点
     * @param tolerance 误差容忍度
     * @param maxIterations 最大迭代次数
     * @return 根的列表
     */
    public List<Double> findRootsBisection(String expression, double start, double end, double tolerance, int maxIterations) {
        List<Double> roots = new ArrayList<>();
        int subdivisions = 100; // 默认细分为100个子区间
        double step = (end - start) / subdivisions;

        for (int i = 0; i < subdivisions; i++) {
            double a = start + i * step;
            double b = a + step;
            double fa = functionModel.evaluateFunction(expression, a);
            double fb = functionModel.evaluateFunction(expression, b);

            // 检查是否有符号变化（可能存在根）
            if (fa * fb <= 0) {
                try {
                    // 使用二分法找到这个子区间内的根
                    double root = bisectionMethod(expression, a, b, tolerance);

                    // 检查是否与已有的根重复
                    boolean isDuplicate = false;
                    for (Double existingRoot : roots) {
                        if (Math.abs(existingRoot - root) < tolerance) {
                            isDuplicate = true;
                            break;
                        }
                    }

                    if (!isDuplicate) {
                        roots.add(root);
                    }
                } catch (Exception e) {
                    // 忽略无法找到根的子区间
                }
            }
        }

        return roots;
    }

    /**
     * 使用牛顿法寻找方程在给定区间内的所有根
     * @param expression 函数表达式
     * @param start 区间起点
     * @param end 区间终点
     * @param tolerance 误差容忍度
     * @param maxIterations 最大迭代次数
     * @return 根的列表
     */
    public List<Double> findRootsNewton(String expression, double start, double end, double tolerance, int maxIterations) {
        List<Double> roots = new ArrayList<>();
        int subdivisions = 20; // 牛顿法收敛快，可以用较少的初始点
        double step = (end - start) / subdivisions;
        double h = 1e-6; // 数值微分步长

        for (int i = 0; i <= subdivisions; i++) {
            double initialGuess = start + i * step;

            try {
                // 使用牛顿法从不同初始点寻找根
                double root = newtonMethod(expression, initialGuess, tolerance, h);

                // 检查根是否在区间内
                if (root >= start && root <= end) {
                    // 检查是否与已有的根重复
                    boolean isDuplicate = false;
                    for (Double existingRoot : roots) {
                        if (Math.abs(existingRoot - root) < tolerance) {
                            isDuplicate = true;
                            break;
                        }
                    }

                    if (!isDuplicate) {
                        roots.add(root);
                    }
                }
            } catch (Exception e) {
                // 忽略牛顿法失败的情况
            }
        }

        return roots;
    }

    /**
     * 求解二次方程 ax² + bx + c = 0
     * @param a 二次项系数
     * @param b 一次项系数
     * @param c 常数项
     * @return 根的数组，长度为0、1或2
     */
    private double[] solveQuadraticEquationArray(double a, double b, double c) {
        // 处理特殊情况：不是二次方程
        if (Math.abs(a) < 1e-10) {
            if (Math.abs(b) < 1e-10) {
                return new double[0]; // 无解或无穷多解
            } else {
                return new double[] { -c / b }; // 一次方程
            }
        }

        // 计算判别式
        double discriminant = b * b - 4 * a * c;

        if (Math.abs(discriminant) < 1e-10) {
            // 一个重根
            return new double[] { -b / (2 * a) };
        } else if (discriminant > 0) {
            // 两个不同的实根
            double sqrtDiscriminant = Math.sqrt(discriminant);
            return new double[] {
                    (-b + sqrtDiscriminant) / (2 * a),
                    (-b - sqrtDiscriminant) / (2 * a)
            };
        } else {
            // 没有实根
            return new double[0];
        }
    }

    /**
     * 求解二次方程 ax² + bx + c = 0
     *
     * @param a 二次项系数
     * @param b 一次项系数
     * @param c 常数项
     * @return 根的列表
     */
    public List<Double> solveQuadraticEquation(double a, double b, double c) {
        List<Double> rootsList = new ArrayList<>();
        double[] roots = solveQuadraticEquationArray(a, b, c);

        for (double root : roots) {
            rootsList.add(root);
        }

        return rootsList;
    }

    /**
     * 求解一元高次方程（使用数值方法）
     * @param coefficients 系数数组，从高次到低次
     * @param start 搜索区间起点
     * @param end 搜索区间终点
     * @param tolerance 误差容忍度
     * @return 方程的根列表
     */
    public List<Double> solvePolynomialEquation(double[] coefficients, double start, double end, double tolerance) {
        // 构建多项式表达式
        StringBuilder expression = new StringBuilder();
        for (int i = 0; i < coefficients.length; i++) {
            int power = coefficients.length - i - 1;
            if (Math.abs(coefficients[i]) > 1e-10) {
                if (expression.length() > 0 && coefficients[i] > 0) {
                    expression.append("+");
                }

                if (power == 0) {
                    expression.append(coefficients[i]);
                } else if (power == 1) {
                    expression.append(coefficients[i]).append("*x");
                } else {
                    expression.append(coefficients[i]).append("*x^").append(power);
                }
            }
        }

        // 如果表达式为空，返回空列表
        if (expression.length() == 0) {
            return new ArrayList<>();
        }

        // 使用数值方法寻找根
        return findAllRoots(expression.toString(), start, end, 1000, tolerance);
    }

    public List<double[]> solveTwoQuadraticEquations(double[] coefficient, double[] coefficient1, double[] initialGuess, double tolerance, int maxIterations) {

        return List.of();
    }
}
