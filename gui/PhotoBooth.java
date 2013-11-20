package gui;

import javax.swing.*;

/**
 * Author: Pippo Morfe
 * Date: 14/11/2013
 * Time: 2:31 PM
 */
public class PhotoBooth {
    public static void main(String[] args) throws InterruptedException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame();
            }
        });
    }
}
