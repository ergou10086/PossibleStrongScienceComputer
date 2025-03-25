package model;

/**
 * 线性代数计算模型类
 * 提供矩阵运算相关功能
 */
public class LinearAlgebraModel {
    
    /**
     * 计算矩阵的行列式
     * @param matrix 输入矩阵
     * @return 行列式的值
     * @throws IllegalArgumentException 如果矩阵不是方阵
     */
    public double calculateDeterminant(double[][] matrix) {
        int n = matrix.length;
        
        // 检查是否为方阵
        for (int i = 0; i < n; i++) {
            if (matrix[i].length != n) {
                throw new IllegalArgumentException("计算行列式要求矩阵必须是方阵");
            }
        }
        
        // 对于1x1矩阵，行列式就是唯一的元素
        if (n == 1) {
            return matrix[0][0];
        }
        
        // 对于2x2矩阵，使用公式: ad - bc
        if (n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }
        
        // 对于更大的矩阵，使用递归的方式计算
        double det = 0;
        for (int j = 0; j < n; j++) {
            det += Math.pow(-1, j) * matrix[0][j] * calculateDeterminant(getSubMatrix(matrix, 0, j));
        }
        
        return det;
    }
    
    /**
     * 获取去掉指定行和列的子矩阵
     * @param matrix 原矩阵
     * @param row 要去掉的行
     * @param col 要去掉的列
     * @return 子矩阵
     */
    private double[][] getSubMatrix(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] subMatrix = new double[n-1][n-1];
        
        int r = 0;
        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            
            int c = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                
                subMatrix[r][c] = matrix[i][j];
                c++;
            }
            r++;
        }
        
        return subMatrix;
    }
    
    /**
     * 矩阵加法
     * @param matrix1 第一个矩阵
     * @param matrix2 第二个矩阵
     * @return 相加后的矩阵
     * @throws IllegalArgumentException 如果两个矩阵的维度不匹配
     */
    public double[][] addMatrices(double[][] matrix1, double[][] matrix2) {
        int rows1 = matrix1.length;
        int cols1 = matrix1[0].length;
        int rows2 = matrix2.length;
        int cols2 = matrix2[0].length;
        
        // 检查维度是否匹配
        if (rows1 != rows2 || cols1 != cols2) {
            throw new IllegalArgumentException("矩阵加法要求两个矩阵的维度必须相同");
        }
        
        double[][] result = new double[rows1][cols1];
        
        for (int i = 0; i < rows1; i++) {
            for (int j = 0; j < cols1; j++) {
                result[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }
        
        return result;
    }
    
    /**
     * 矩阵减法
     * @param matrix1 第一个矩阵
     * @param matrix2 第二个矩阵
     * @return 相减后的矩阵
     * @throws IllegalArgumentException 如果两个矩阵的维度不匹配
     */
    public double[][] subtractMatrices(double[][] matrix1, double[][] matrix2) {
        int rows1 = matrix1.length;
        int cols1 = matrix1[0].length;
        int rows2 = matrix2.length;
        int cols2 = matrix2[0].length;
        
        // 检查维度是否匹配
        if (rows1 != rows2 || cols1 != cols2) {
            throw new IllegalArgumentException("矩阵减法要求两个矩阵的维度必须相同");
        }
        
        double[][] result = new double[rows1][cols1];
        
        for (int i = 0; i < rows1; i++) {
            for (int j = 0; j < cols1; j++) {
                result[i][j] = matrix1[i][j] - matrix2[i][j];
            }
        }
        
        return result;
    }
    
    /**
     * 矩阵乘法
     * @param matrix1 第一个矩阵
     * @param matrix2 第二个矩阵
     * @return 相乘后的矩阵
     * @throws IllegalArgumentException 如果第一个矩阵的列数不等于第二个矩阵的行数
     */
    public double[][] multiplyMatrices(double[][] matrix1, double[][] matrix2) {
        int rows1 = matrix1.length;
        int cols1 = matrix1[0].length;
        int rows2 = matrix2.length;
        int cols2 = matrix2[0].length;
        
        // 检查维度是否匹配
        if (cols1 != rows2) {
            throw new IllegalArgumentException("矩阵乘法要求第一个矩阵的列数等于第二个矩阵的行数");
        }
        
        double[][] result = new double[rows1][cols2];
        
        for (int i = 0; i < rows1; i++) {
            for (int j = 0; j < cols2; j++) {
                for (int k = 0; k < cols1; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        
        return result;
    }
    
    /**
     * 矩阵转置
     * @param matrix 输入矩阵
     * @return 转置后的矩阵
     */
    public double[][] transposeMatrix(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        
        double[][] result = new double[cols][rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        
        return result;
    }
    
    /**
     * 计算矩阵的逆
     * @param matrix 输入矩阵
     * @return 逆矩阵
     * @throws IllegalArgumentException 如果矩阵不是方阵或者不可逆
     */
    public double[][] invertMatrix(double[][] matrix) {
        int n = matrix.length;
        
        // 检查是否为方阵
        for (int i = 0; i < n; i++) {
            if (matrix[i].length != n) {
                throw new IllegalArgumentException("计算逆矩阵要求矩阵必须是方阵");
            }
        }
        
        // 计算行列式
        double det = calculateDeterminant(matrix);
        
        // 检查是否可逆
        if (Math.abs(det) < 1e-10) {
            throw new IllegalArgumentException("矩阵不可逆（行列式为零）");
        }
        
        // 对于2x2矩阵，使用公式
        if (n == 2) {
            double[][] result = new double[2][2];
            result[0][0] = matrix[1][1] / det;
            result[0][1] = -matrix[0][1] / det;
            result[1][0] = -matrix[1][0] / det;
            result[1][1] = matrix[0][0] / det;
            return result;
        }
        
        // 对于更大的矩阵，使用伴随矩阵法
        double[][] adjoint = new double[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // 计算余子式
                double[][] subMatrix = getSubMatrix(matrix, i, j);
                double cofactor = Math.pow(-1, i + j) * calculateDeterminant(subMatrix);
                adjoint[j][i] = cofactor; // 注意这里是转置的
            }
        }
        
        // 计算逆矩阵
        double[][] inverse = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse[i][j] = adjoint[i][j] / det;
            }
        }
        
        return inverse;
    }
    
    /**
     * 计算矩阵的秩
     * @param matrix 输入矩阵
     * @return 矩阵的秩
     */
    public int calculateRank(double[][] matrix) {
        // 创建矩阵的副本，以便不修改原始矩阵
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] copy = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, cols);
        }
        
        // 使用高斯消元法计算秩
        return gaussianElimination(copy);
    }
    
    /**
     * 使用高斯消元法将矩阵转换为行阶梯形式，并返回秩
     * @param matrix 输入矩阵
     * @return 矩阵的秩
     */
    private int gaussianElimination(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int rank = 0;
        
        for (int r = 0; r < rows; r++) {
            // 找到当前列中绝对值最大的元素
            int pivotRow = r;
            for (int i = r + 1; i < rows; i++) {
                if (Math.abs(matrix[i][r]) > Math.abs(matrix[pivotRow][r])) {
                    pivotRow = i;
                }
            }
            
            // 如果主元为零，则移动到下一列
            if (Math.abs(matrix[pivotRow][r]) < 1e-10) {
                continue;
            }
            
            // 交换行
            if (pivotRow != r) {
                double[] temp = matrix[r];
                matrix[r] = matrix[pivotRow];
                matrix[pivotRow] = temp;
            }
            
            // 将主元归一化
            double pivot = matrix[r][r];
            for (int j = r; j < cols; j++) {
                matrix[r][j] /= pivot;
            }
            
            // 消元
            for (int i = 0; i < rows; i++) {
                if (i != r) {
                    double factor = matrix[i][r];
                    for (int j = r; j < cols; j++) {
                        matrix[i][j] -= factor * matrix[r][j];
                    }
                }
            }
            
            rank++;
        }
        
        return rank;
    }
    
    /**
     * 计算特征值（仅支持2x2和3x3矩阵）
     * @param matrix 输入矩阵
     * @return 特征值数组
     * @throws IllegalArgumentException 如果矩阵不是2x2或3x3方阵
     */
    public double[] calculateEigenvalues(double[][] matrix) {
        int n = matrix.length;
        
        // 检查是否为方阵
        for (int i = 0; i < n; i++) {
            if (matrix[i].length != n) {
                throw new IllegalArgumentException("计算特征值要求矩阵必须是方阵");
            }
        }
        
        // 仅支持2x2和3x3矩阵
        if (n == 2) {
            return calculateEigenvalues2x2(matrix);
        } else if (n == 3) {
            return calculateEigenvalues3x3(matrix);
        } else {
            throw new IllegalArgumentException("目前只支持2x2和3x3矩阵的特征值计算");
        }
    }
    
    /**
     * 计算2x2矩阵的特征值
     * @param matrix 2x2矩阵
     * @return 特征值数组
     */
    private double[] calculateEigenvalues2x2(double[][] matrix) {
        double a = matrix[0][0];
        double b = matrix[0][1];
        double c = matrix[1][0];
        double d = matrix[1][1];
        
        // 特征方程: λ² - (a+d)λ + (ad-bc) = 0
        double trace = a + d;
        double det = a * d - b * c;
        
        // 使用二次公式
        double discriminant = trace * trace - 4 * det;
        
        if (discriminant >= 0) {
            // 实特征值
            double lambda1 = (trace + Math.sqrt(discriminant)) / 2;
            double lambda2 = (trace - Math.sqrt(discriminant)) / 2;
            return new double[] {lambda1, lambda2};
        } else {
            // 复特征值 (返回实部和虚部)
            double realPart = trace / 2;
            double imagPart = Math.sqrt(-discriminant) / 2;
            // 由于Java不直接支持复数，我们返回实部和虚部
            // [实部1, 虚部1, 实部2, 虚部2]
            return new double[] {realPart, imagPart, realPart, -imagPart};
        }
    }
    
    /**
     * 计算3x3矩阵的特征值（使用特征多项式）
     * @param matrix 3x3矩阵
     * @return 特征值数组
     */
    private double[] calculateEigenvalues3x3(double[][] matrix) {
        double a = matrix[0][0];
        double b = matrix[0][1];
        double c = matrix[0][2];
        double d = matrix[1][0];
        double e = matrix[1][1];
        double f = matrix[1][2];
        double g = matrix[2][0];
        double h = matrix[2][1];
        double i = matrix[2][2];
        
        // 特征多项式: -λ³ + trace·λ² - M·λ + det = 0
        // 其中trace是矩阵的迹，M是所有2x2主子式行列式的和，det是矩阵的行列式
        double trace = a + e + i;
        double m = a * e + a * i + e * i - b * d - c * g - f * h;
        double det = a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g);
        
        // 将特征多项式转换为标准形式: x³ + px² + qx + r = 0
        double p = -trace;
        double q = m;
        double r = -det;
        
        // 使用Cardano公式求解三次方程
        // 首先将方程转换为形式 y³ + py + q = 0，其中 y = x + p/3
        double p1 = (3 * q - p * p) / 3;
        double q1 = (2 * p * p * p - 9 * p * q + 27 * r) / 27;
        
        // 计算判别式
        double delta = q1 * q1 / 4 + p1 * p1 * p1 / 27;
        
        double[] eigenvalues = new double[3];
        
        if (delta > 0) {
            // 一个实根和两个共轭复根
            double u = Math.cbrt(-q1 / 2 + Math.sqrt(delta));
            double v = Math.cbrt(-q1 / 2 - Math.sqrt(delta));
            
            eigenvalues[0] = u + v - p / 3;
            double realPart = -(u + v) / 2 - p / 3;
            double imagPart = (u - v) * Math.sqrt(3) / 2;
            
            eigenvalues[1] = realPart;
            eigenvalues[2] = imagPart;
        } else if (delta == 0) {
            // 所有根都是实数，且至少有两个相等
            double u = Math.cbrt(-q1 / 2);
            
            eigenvalues[0] = 2 * u - p / 3;
            eigenvalues[1] = -u - p / 3;
            eigenvalues[2] = eigenvalues[1]; // 重根
        } else {
            // 三个不同的实根
            double angle = Math.acos((-q1 / 2) / Math.sqrt(-p1 * p1 * p1 / 27));
            double r1 = 2 * Math.sqrt(-p1 / 3);
            
            eigenvalues[0] = r1 * Math.cos(angle / 3) - p / 3;
            eigenvalues[1] = r1 * Math.cos((angle + 2 * Math.PI) / 3) - p / 3;
            eigenvalues[2] = r1 * Math.cos((angle + 4 * Math.PI) / 3) - p / 3;
        }
        
        return eigenvalues;
    }
    
    /**
     * 格式化矩阵为字符串
     * @param matrix 矩阵
     * @return 格式化后的字符串
     */
    public String formatMatrix(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        int rows = matrix.length;
        int cols = matrix[0].length;
        
        for (int i = 0; i < rows; i++) {
            sb.append("[ ");
            for (int j = 0; j < cols; j++) {
                sb.append(String.format("%.4f", matrix[i][j]));
                if (j < cols - 1) {
                    sb.append("\t");
                }
            }
            sb.append(" ]");
            if (i < rows - 1) {
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }

    /**
     * 计算矩阵的伴随矩阵（代数余子式矩阵的转置）
     * @param matrix 输入矩阵
     * @return 伴随矩阵
     * @throws IllegalArgumentException 如果矩阵不是方阵
     */
    public double[][] calculateAdjointMatrix(double[][] matrix) {
        int n = matrix.length;
        
        // 检查是否为方阵
        for (int i = 0; i < n; i++) {
            if (matrix[i].length != n) {
                throw new IllegalArgumentException("计算伴随矩阵要求矩阵必须是方阵");
            }
        }
        
        // 对于1x1矩阵，伴随矩阵是1
        if (n == 1) {
            return new double[][]{{1.0}};
        }
        
        // 计算代数余子式矩阵
        double[][] cofactorMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // 代数余子式 = (-1)^(i+j) * 余子式的行列式
                cofactorMatrix[i][j] = Math.pow(-1, i + j) * calculateDeterminant(getSubMatrix(matrix, i, j));
            }
        }
        
        // 伴随矩阵是代数余子式矩阵的转置
        return transposeMatrix(cofactorMatrix);
    }
    
    /**
     * 计算矩阵的LU分解
     * 将矩阵A分解为下三角矩阵L和上三角矩阵U，使得A = LU
     * @param matrix 输入矩阵
     * @return 包含L和U矩阵的数组，result[0]是L，result[1]是U
     * @throws IllegalArgumentException 如果矩阵不是方阵或不可LU分解
     */
    public double[][][] calculateLUDecomposition(double[][] matrix) {
        int n = matrix.length;
        
        // 检查是否为方阵
        for (int i = 0; i < n; i++) {
            if (matrix[i].length != n) {
                throw new IllegalArgumentException("LU分解要求矩阵必须是方阵");
            }
        }
        
        // 创建L和U矩阵
        double[][] L = new double[n][n];
        double[][] U = new double[n][n];
        
        // 初始化L的对角线为1
        for (int i = 0; i < n; i++) {
            L[i][i] = 1.0;
        }
        
        // 复制matrix到U作为初始值
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, U[i], 0, n);
        }
        
        // 执行高斯消元
        for (int k = 0; k < n - 1; k++) {
            // 检查主元是否为0
            if (Math.abs(U[k][k]) < 1e-10) {
                throw new IllegalArgumentException("矩阵不能进行LU分解，需要行交换（需要PLU分解）");
            }
            
            for (int i = k + 1; i < n; i++) {
                L[i][k] = U[i][k] / U[k][k];
                
                for (int j = k; j < n; j++) {
                    U[i][j] -= L[i][k] * U[k][j];
                }
            }
        }
        
        return new double[][][]{L, U};
    }
    
    /**
     * 计算矩阵的QR分解
     * 将矩阵A分解为正交矩阵Q和上三角矩阵R，使得A = QR
     * 使用Gram-Schmidt正交化方法
     * @param matrix 输入矩阵
     * @return 包含Q和R矩阵的数组，result[0]是Q，result[1]是R
     */
    public double[][][] calculateQRDecomposition(double[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        
        // 检查矩阵维度
        for (int i = 1; i < m; i++) {
            if (matrix[i].length != n) {
                throw new IllegalArgumentException("矩阵列数不一致");
            }
        }
        
        // 创建Q和R矩阵
        double[][] Q = new double[m][n];
        double[][] R = new double[n][n];
        
        // 提取矩阵的列向量
        double[][] columns = new double[n][m];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                columns[j][i] = matrix[i][j];
            }
        }
        
        // 执行Gram-Schmidt正交化
        double[][] orthogonalVectors = new double[n][m];
        
        for (int j = 0; j < n; j++) {
            // 复制当前列向量
            double[] currentColumn = columns[j].clone();
            
            // 减去之前向量的投影
            for (int k = 0; k < j; k++) {
                double projection = 0.0;
                for (int i = 0; i < m; i++) {
                    projection += columns[j][i] * orthogonalVectors[k][i];
                }
                
                for (int i = 0; i < m; i++) {
                    currentColumn[i] -= projection * orthogonalVectors[k][i];
                }
            }
            
            // 归一化
            double norm = 0.0;
            for (int i = 0; i < m; i++) {
                norm += currentColumn[i] * currentColumn[i];
            }
            norm = Math.sqrt(norm);
            
            if (norm < 1e-10) {
                throw new IllegalArgumentException("矩阵列向量线性相关，无法进行QR分解");
            }
            
            for (int i = 0; i < m; i++) {
                orthogonalVectors[j][i] = currentColumn[i] / norm;
            }
        }
        
        // 构建Q矩阵（正交矩阵）
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                Q[i][j] = orthogonalVectors[j][i];
            }
        }
        
        // 构建R矩阵（上三角矩阵）
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                for (int k = 0; k < m; k++) {
                    R[i][j] += Q[k][i] * matrix[k][j];
                }
            }
        }
        
        return new double[][][]{Q, R};
    }
}
