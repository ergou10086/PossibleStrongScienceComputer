package controller;

import model.UnitConverterModel;
import view.UnitConverterView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnitConverterController {
    private final UnitConverterView view;
    private final UnitConverterModel model;

    public UnitConverterController(UnitConverterView view, UnitConverterModel model) {
        this.view = view;
        this.model = model;
        initListener();
    }

    private void initListener() {
        view.addConvertListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double input = view.getInputValue();
                if (Double.isNaN(input)) return;

                int type = view.getConversionType();
                double result = switch (type) {
                    case 0 -> model.cmToInch(input);
                    case 1 -> model.inchToCm(input);
                    case 2 -> model.celsiusToFahrenheit(input);
                    case 3 -> model.fahrenheitToCelsius(input);
                    case 4 -> model.kgToLb(input);
                    case 5 -> model.lbToKg(input);
                    default -> Double.NaN;
                };

                if (!Double.isNaN(result)) {
                    view.setResult(result);
                }
            }
        });

        view.addCloseListener(e -> view.dispose());
    }

    // 显示转换界面
    public void showConverter() {
        view.setVisible(true);
    }
}