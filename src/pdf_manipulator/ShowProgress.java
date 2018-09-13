/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf_manipulator;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import static pdf_manipulator.MainFrame01.frameProgress;
import static pdf_manipulator.MainFrame01.mergeCount;

/**
 *
 * @author satadru
 */
public class ShowProgress extends JPanel
        implements PropertyChangeListener {
    
    private JProgressBar progressBar;
    //private JButton startButton;
    private Task task;
    
    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        
        @Override
        public Void doInBackground() {
            Random random = new Random();
            int progress = 0;
            //Initialize progress property.
            setProgress(0);
            while (progress < 100) {
                //Sleep for up to one second.
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException ignore) {
                }
                //Make random progress.
                progress += random.nextInt(10);
                setProgress(Math.min(progress, 100));
            }
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            setCursor(null); //turn off the wait cursor
            mergeCount = true;
            JOptionPane.showMessageDialog(frameProgress, "Processing complete","Merge Files",JOptionPane.PLAIN_MESSAGE);
            frameProgress.dispose();
        }
    }
    
    public ShowProgress() {
        super(new BorderLayout());

        //Create the demo's UI.
        //startButton = new JButton("Start");
        //startButton.setActionCommand("start");
        //startButton.addActionListener(this);
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        
        JPanel panel = new JPanel();
//        panel.add(startButton);
        panel.add(progressBar);

        //this.actionPerformed(new ActionEvent(startButton, ActionEvent.ACTION_PERFORMED, null));
        initiateTask();
        
        add(panel, BorderLayout.PAGE_START);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLocation(200, 200);
        
    }

   
    public void initiateTask() {

        //startButton.setEnabled(false);
        //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setCursor(null);
        
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
    }

    /**
     * Create the GUI and show it. As with all GUI code, this must run on the
     * event-dispatching thread.
     */
    protected static void createAndShowGUI() {
        //Create and set up the window.
        frameProgress.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new ShowProgress();
        newContentPane.setOpaque(true); //content panes must be opaque
        frameProgress.setContentPane(newContentPane);

        //Display the window.
        frameProgress.setAlwaysOnTop(true);
        frameProgress.setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
        frameProgress.setUndecorated(true);
        frameProgress.setLocationRelativeTo(null);
        frameProgress.pack();
        frameProgress.setVisible(true);
    }
    
}
