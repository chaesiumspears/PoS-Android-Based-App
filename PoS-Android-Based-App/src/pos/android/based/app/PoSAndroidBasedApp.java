/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pos.android.based.app;

/**
 *
 * @author chari
 */
public class PoSAndroidBasedApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SignUpForm signUp = new SignUpForm();
        signUp.setVisible(true);
        signUp.pack();
        signUp.setLocationRelativeTo(null);
        signUp.setDefaultCloseOperation(SignUpForm.EXIT_ON_CLOSE);
    }
    
}
