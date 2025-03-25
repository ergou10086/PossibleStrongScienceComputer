import controller.LoginController;
import model.CalculatorModel;
import view.CalculatorView;
import view.LoginView;
import controller.CalculatorController;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // 设置UI风格为系统风格
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 如果需要登陆，请解除注释
        /*
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginView);
        loginView.setVisible(true);
        */
        CalculatorView calculatorView = new CalculatorView();
        CalculatorModel calculatorModel = new CalculatorModel();
        CalculatorController calculatorController = new CalculatorController(calculatorModel, calculatorView);
        calculatorView.setVisible(true);
    }
}
