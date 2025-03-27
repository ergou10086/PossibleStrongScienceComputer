package model;

// 单位转换类

public class UnitConverterModel {

    // 长度单位转换：厘米转英寸
    public double cmToInch(double cm) {
        return cm / 2.54;
    }

    // 长度单位转换：英寸转厘米
    public double inchToCm(double inch) {
        return inch * 2.54;
    }

    // 温度单位转换：摄氏度转华氏度
    public double celsiusToFahrenheit(double celsius) {
        return celsius * 9 / 5 + 32;
    }

    // 温度单位转换：华氏度转摄氏度
    public double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }

    // 重量单位转换：千克转磅
    public double kgToLb(double kg) {
        return kg * 2.20462;
    }

    // 重量单位转换：磅转千克
    public double lbToKg(double lb) {
        return lb / 2.20462;
    }
}
