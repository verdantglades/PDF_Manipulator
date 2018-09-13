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
import java.io.File;
import java.io.IOException;
import static java.lang.Math.random;
import java.util.Date;
import java.util.Random;
import java.util.Vector;
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
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFMergerUtility;
import static pdf_manipulator.MainFrame01.frameProgress;
import static pdf_manipulator.MainFrame01.mergeCount;
import static pdf_manipulator.MainFrame01.model;

/**
 *
 * @author satadru
 */
public class ProcessProgress extends JPanel
        implements PropertyChangeListener {

    private JProgressBar progressBar;
    //private JButton startButton;
    private Task task;

    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */

        @Override
        public Void doInBackground() throws IOException, COSVisitorException {

            //For testing time intervals only.
                       Random random = new Random();
            //
            
            Vector data = model.getDataVector();

            //String escapedFilepath = filepath.replace("\\","\\\\");
            //output.createNewFile();
            String FinalMergedName = (new Date()).getTime() + "_merged.pdf";
            String path1 = System.getProperty("user.dir") + File.separator + FinalMergedName;
            File output = new File(path1);

            //Create temporary file name.
            String tempFileName = (new Date()).getTime() + "_temp";
            String path2 = System.getProperty("java.io.tmpdir") + File.separator + tempFileName;
            File temp = new File(path2);
            //temp.createNewFile();

            PDDocument desPDDoc = null;
            PDDocument doc = null;
            PDFMergerUtility pdfMerger = new PDFMergerUtility();
            boolean hasCloneFirstDoc = false;

            //ProgressBar initialization.
            
            //Check for percentage.
            
            int fileProcCount = 0;
            float progressPerCent = 0;
            int totalFileCount = data.size();

            for (Object d1 : data) {

                String temp1 = d1.toString();
                temp1 = temp1.substring(1, temp1.length() - 1);
                String escapedFilepath = temp1.replace("\\", "\\\\");
                //System.out.println(escapedFilepath);
                doc = null;
                File file = new File(escapedFilepath);
                try {
                    if (hasCloneFirstDoc) {
                        doc = PDDocument.load(file, new org.apache.pdfbox.io.RandomAccessFile(temp, "rw"));
                        pdfMerger.appendDocument(desPDDoc, doc);
                    } else {
                        desPDDoc = PDDocument.load(file, new org.apache.pdfbox.io.RandomAccessFile(temp, "rw"));
                        hasCloneFirstDoc = true;
                    }
                } catch (Exception e) {
                    //
                } finally {
                    if (doc != null) {
                        doc.close();
                        file = null;
                        temp.deleteOnExit();
                    }
                }

                //Delayed wait for testing purposes.
                try {
                    Thread.sleep(random.nextInt(200));
                } catch (InterruptedException ignore) {
                }

                //ProgressBar calculation.
                fileProcCount++;
                System.out.println("Current file number "+fileProcCount);
                System.out.println("Total file count "+totalFileCount);
                
                //float proportionCorrect = ((float) correct) / ((float) questions);
                progressPerCent = ((float) fileProcCount / (float) totalFileCount) * 100;
//                System.out.println("Progress = "+progressPerCent);

                setProgress(Math.min((int) progressPerCent, 100));

            }

            //Close the final open file handle.
            desPDDoc.save(output);

            //Close all open file handles.
            try {
                doc.close();
            } catch (Exception e) {
                //
            } finally {
                desPDDoc.close();
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
            JOptionPane.showMessageDialog(frameProgress, "Processing complete", "Merge Files", JOptionPane.PLAIN_MESSAGE);
            frameProgress.dispose();
        }
    }

    public ProcessProgress() {
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
        JComponent newContentPane = new ProcessProgress();
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
