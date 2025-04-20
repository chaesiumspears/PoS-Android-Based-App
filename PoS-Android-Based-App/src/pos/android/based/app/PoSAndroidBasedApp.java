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
        SignInForm signIn = new SignInForm();
        signIn.setVisible(true);
        signIn.pack();
        signIn.setLocationRelativeTo(null);
        signIn.setDefaultCloseOperation(SignInForm.EXIT_ON_CLOSE);
    }
    
}
