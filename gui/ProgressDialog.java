package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Pippo Morfe
 * Date: 15/11/2013
 * Time: 12:12 AM
 */
public class ProgressDialog extends JDialog {

    private JProgressBar progressBar;

    public ProgressDialog(JFrame parent) {
        super(parent, "Connecting to the Database", true);
        progressBar = new JProgressBar();
        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(250, 70));
        add(progressBar);
        setLocationRelativeTo(parent);

        pack();
    }


    public void setMaximum(int value) {
        progressBar.setMaximum(value);
    }

    public void setValue(int value) {
        progressBar.setValue(value);
    }
}
