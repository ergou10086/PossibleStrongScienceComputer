package controller;

import model.VectorModel;
import view.VectorView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VectorController implements ActionListener {
    private VectorView view;
    private VectorModel model;

    public VectorController(VectorView view) {
        this.view = view;
        this.model = new VectorModel();
        view.addCalculateListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String operation = view.getSelectedOperation();
            double[] v1 = view.getVector1();
            double[] v2 = view.getVector2();

            switch (operation) {
                case "点积":
                    handleDotProduct(v1, v2);
                    break;
                case "叉积":
                    handleCrossProduct(v1, v2);
                    break;
                case "模长":
                    handleMagnitude(v1);
                    break;
                case "加法":
                    handleAddition(v1, v2);
                    break;
                case "减法":
                    handleSubtraction(v1, v2);
                    break;
                case "夹角":
                    handleAngle(v1, v2);
                    break;
                case "正交检查":
                    handleOrthogonal(v1, v2);
                    break;
                case "平行检查":
                    handleParallel(v1, v2);
                    break;
                case "投影":
                    handleProjection(v1, v2);
                    break;
            }
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
    }

    private void handleDotProduct(double[] v1, double[] v2) {
        double result = model.dotProduct(v1, v2);
        view.setResult(String.format("点积结果: %.4f", result));
    }

    private void handleCrossProduct(double[] v1, double[] v2) {
        double[] result = model.crossProduct(v1, v2);
        view.setResult("叉积结果: " + model.formatVector(result));
    }

    private void handleMagnitude(double[] v) {
        double result = model.magnitude(v);
        view.setResult(String.format("模长: %.4f", result));
    }

    private void handleAddition(double[] v1, double[] v2) {
        double[] result = model.add(v1, v2);
        view.setResult("加法结果: " + model.formatVector(result));
    }

    private void handleSubtraction(double[] v1, double[] v2) {
        double[] result = model.subtract(v1, v2);
        view.setResult("减法结果: " + model.formatVector(result));
    }

    private void handleAngle(double[] v1, double[] v2) {
        double angle = model.angleBetween(v1, v2);
        view.setResult(String.format("夹角: %.4f 弧度 (约 %.2f 度)",
                angle, Math.toDegrees(angle)));
    }

    private void handleOrthogonal(double[] v1, double[] v2) {
        boolean isOrtho = model.isOrthogonal(v1, v2);
        view.setResult("正交: " + isOrtho);
    }

    private void handleParallel(double[] v1, double[] v2) {
        boolean isParallel = model.isParallel(v1, v2);
        view.setResult("平行: " + isParallel);
    }

    private void handleProjection(double[] v, double[] onto) {
        try {
            double[] projection = model.projection(v, onto);
            view.setResult("投影向量: " + model.formatVector(projection));
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }
}