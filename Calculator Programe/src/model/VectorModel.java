package model;

/**
 * 向量计算模型类
 */
public class VectorModel {
    
    /**
     * 计算两个向量的点积（内积）
     * @param v1 向量1
     * @param v2 向量2
     * @return 点积结果
     * @throws IllegalArgumentException 如果向量维度不匹配
     */
    public double dotProduct(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double result = 0;
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }
        
        return result;
    }
    
    /**
     * 计算两个三维向量的叉积（外积）
     * @param v1 向量1
     * @param v2 向量2
     * @return 叉积结果向量
     * @throws IllegalArgumentException 如果向量不是三维向量
     */
    public double[] crossProduct(double[] v1, double[] v2) {
        if (v1.length != 3 || v2.length != 3) {
            throw new IllegalArgumentException("叉积只适用于三维向量");
        }
        
        double[] result = new double[3];
        result[0] = v1[1] * v2[2] - v1[2] * v2[1];
        result[1] = v1[2] * v2[0] - v1[0] * v2[2];
        result[2] = v1[0] * v2[1] - v1[1] * v2[0];
        
        return result;
    }
    
    /**
     * 计算向量的模长
     * @param v 向量
     * @return 模长
     */
    public double magnitude(double[] v) {
        double sumOfSquares = 0;
        for (double component : v) {
            sumOfSquares += component * component;
        }
        
        return Math.sqrt(sumOfSquares);
    }
    
    /**
     * 归一化向量（单位化）
     * @param v 向量
     * @return 归一化后的向量
     * @throws IllegalArgumentException 如果向量是零向量
     */
    public double[] normalize(double[] v) {
        double mag = magnitude(v);
        
        if (mag < 1e-10) {
            throw new IllegalArgumentException("零向量无法归一化");
        }
        
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = v[i] / mag;
        }
        
        return result;
    }
    
    /**
     * 计算两个向量的夹角（弧度）
     * @param v1 向量1
     * @param v2 向量2
     * @return 夹角（弧度）
     * @throws IllegalArgumentException 如果向量维度不匹配或者有零向量
     */
    public double angleBetween(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double dotProduct = dotProduct(v1, v2);
        double mag1 = magnitude(v1);
        double mag2 = magnitude(v2);
        
        if (mag1 < 1e-10 || mag2 < 1e-10) {
            throw new IllegalArgumentException("零向量无法计算夹角");
        }
        
        // 使用点积公式：a·b = |a|·|b|·cos(θ)
        double cosTheta = dotProduct / (mag1 * mag2);
        
        // 处理浮点数精度问题
        if (cosTheta > 1.0) {
            cosTheta = 1.0;
        } else if (cosTheta < -1.0) {
            cosTheta = -1.0;
        }
        
        return Math.acos(cosTheta);
    }
    
    /**
     * 检查两个向量是否正交（垂直）
     * @param v1 向量1
     * @param v2 向量2
     * @return 是否正交
     * @throws IllegalArgumentException 如果向量维度不匹配
     */
    public boolean isOrthogonal(double[] v1, double[] v2) {
        return Math.abs(dotProduct(v1, v2)) < 1e-10;
    }
    
    /**
     * 检查两个向量是否平行
     * @param v1 向量1
     * @param v2 向量2
     * @return 是否平行
     * @throws IllegalArgumentException 如果向量维度不匹配
     */
    public boolean isParallel(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        // 检查是否为零向量
        boolean v1IsZero = true;
        boolean v2IsZero = true;
        
        for (int i = 0; i < v1.length; i++) {
            if (Math.abs(v1[i]) > 1e-10) {
                v1IsZero = false;
            }
            if (Math.abs(v2[i]) > 1e-10) {
                v2IsZero = false;
            }
        }
        
        if (v1IsZero || v2IsZero) {
            return true; // 零向量与任何向量平行
        }
        
        // 检查是否所有分量比例相同
        double ratio = 0;
        int firstNonZeroIndex = -1;
        
        for (int i = 0; i < v1.length; i++) {
            if (Math.abs(v1[i]) > 1e-10) {
                firstNonZeroIndex = i;
                ratio = v2[i] / v1[i];
                break;
            }
        }
        
        if (firstNonZeroIndex == -1) {
            return true; // 如果v1是零向量
        }
        
        for (int i = firstNonZeroIndex + 1; i < v1.length; i++) {
            if (Math.abs(v1[i]) > 1e-10) {
                double currentRatio = v2[i] / v1[i];
                if (Math.abs(currentRatio - ratio) > 1e-10) {
                    return false;
                }
            } else if (Math.abs(v2[i]) > 1e-10) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 向量加法
     * @param v1 向量1
     * @param v2 向量2
     * @return 和向量
     * @throws IllegalArgumentException 如果向量维度不匹配
     */
    public double[] add(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double[] result = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            result[i] = v1[i] + v2[i];
        }
        
        return result;
    }
    
    /**
     * 向量减法
     * @param v1 向量1
     * @param v2 向量2
     * @return 差向量
     * @throws IllegalArgumentException 如果向量维度不匹配
     */
    public double[] subtract(double[] v1, double[] v2) {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double[] result = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            result[i] = v1[i] - v2[i];
        }
        
        return result;
    }
    
    /**
     * 向量数乘
     * @param scalar 标量
     * @param v 向量
     * @return 数乘结果向量
     */
    public double[] scalarMultiply(double scalar, double[] v) {
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = scalar * v[i];
        }
        
        return result;
    }
    
    /**
     * 计算向量的投影
     * @param v 被投影的向量
     * @param onto 投影到的向量
     * @return 投影向量
     * @throws IllegalArgumentException 如果向量维度不匹配或onto是零向量
     */
    public double[] projection(double[] v, double[] onto) {
        if (v.length != onto.length) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double ontoMagSquared = 0;
        for (double component : onto) {
            ontoMagSquared += component * component;
        }
        
        if (ontoMagSquared < 1e-10) {
            throw new IllegalArgumentException("不能投影到零向量上");
        }
        
        double dotProduct = dotProduct(v, onto);
        double scalar = dotProduct / ontoMagSquared;
        
        return scalarMultiply(scalar, onto);
    }
    
    /**
     * 格式化向量为字符串
     * @param v 向量
     * @return 格式化后的字符串
     */
    public String formatVector(double[] v) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < v.length; i++) {
            sb.append(String.format("%.4f", v[i]));
            if (i < v.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
