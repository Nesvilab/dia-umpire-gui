/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dia.umpire.gui;

import javax.swing.JFrame;

/**
 *
 * @author dattam
 */
public class DIAUmpireGUI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UmpireUnargetedDbSearchFrame frame = new UmpireUnargetedDbSearchFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
}
