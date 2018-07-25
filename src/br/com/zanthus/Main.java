package br.com.zanthus;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import br.com.zanthus.util.ButtonLayout;
import br.com.zanthus.util.Logger;
import br.com.zanthus.util.TextComponentLogger;
import veridis.biometric.BiometricException;
import veridis.biometric.BiometricException.FeatureNotAvailableForFreeException;
import veridis.biometric.BiometricException.FeatureNotLicensedException;
import veridis.biometric.BiometricException.UnsupportedBiometricModalityException;
import veridis.biometric.BiometricImage;
import veridis.biometric.BiometricSDK;
import veridis.biometric.BiometricScanner;
import veridis.biometric.BiometricTemplate;
import veridis.biometric.CaptureEventListener;
import veridis.biometric.JBiometricPanel;
import veridis.biometric.samples.util.LicenseHelper;

public class Main<MyObject> extends JPanel implements
        CaptureEventListener {

    private static final long serialVersionUID = 2120072397820454600L;
    /*panel where fingerprint will be displayed*/
    private JBiometricPanel imagePanel = new JBiometricPanel();
    private static BiometricImage image = null;
    private JFrame frame;
    Logger logPanel;

    private JButton btnLoadFromFile, btnSaveAsBMP, btnSaveAsWSQ, btnCadastro,
            btnIdentify, btnVerify, btnMerge, btnStartCapture;
    private CaptureEventListener listener = this;
    // private BiometricImageIO fileChooser = new BiometricImageIO();

    private boolean isCaptureOn = false;
    String arch;

    public Main() {


        UUID id = UUID.randomUUID();
        String tempFolderName = System.getProperty("java.io.tmpdir") + "/" + id;
        copyToTempFolder(tempFolderName);

		/*
		 * You have to set the directory where the .dll (windows) or .so (linux)
		 * are found 
		 * Don't forget that they must be on the system_path, or on
		 * the java library path or in temporary path
		 */
        LicenseHelper.setLibrariesDirectory(tempFolderName);
		
		/*Asks for license key before starting
		 * you MUST install a license before using this SDK*/
        String key = "0000-0324-E49B-D796-171A"; // JOptionPane.showInputDialog("Insira a Chave da Licença");
        BiometricSDK.InstallLicense(key);


        /** Inicializa para capturar*/
        startCapture();


		/* Creates the image panel, where the fingerprints will be displayed */
        imagePanel.setPreferredSize(new Dimension(300, 300));

		/* Creates buttons */
        btnCadastro = new JButton("Cadastrar");
        btnIdentify = new JButton("Identificar");
        btnVerify = new JButton("Verificar");
        btnMerge = new JButton("Consolidar");
        //btnStartCapture = new JButton("Iniciar Captura");

		/* creates logPanel */
        JTextArea logView = new JTextArea();
        TextComponentLogger logger = new TextComponentLogger(logView);
        logView.setPreferredSize(new Dimension(100, 150));
        logPanel = new Logger(logger);
        logView.setText("");

		/* The main frame */
        frame = new JFrame("Biometria - Real Sociedade");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        try{
			UIManager.setLookAndFeel(
		            "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
        catch(Exception e){
			System.err.print("Problem in the look and feel.");
		}
        

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

       
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

		/* buttons at the left */
        JPanel bottomLeft = new JPanel();
        bottomLeft.setLayout(new BoxLayout(bottomLeft, BoxLayout.Y_AXIS));
        ButtonLayout.changeWidth(bottomLeft);

		/* Image panel */
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(imagePanel);
        ButtonLayout.changeHeight(imagePanel);
        ButtonLayout.changeBoth(imagePanel);
        ButtonLayout.changeWidth(left);
        left.add(bottomLeft);
        ButtonLayout.changeBoth(left);

		/* buttons at the right */
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(btnCadastro);
        ButtonLayout.changeHeight(btnCadastro);
        ButtonLayout.changeBoth(btnCadastro);
        right.add(btnIdentify);
        ButtonLayout.changeHeight(btnIdentify);
        ButtonLayout.changeBoth(btnIdentify);
        /*right.add(btnVerify);
        ButtonLayout.changeHeight(btnVerify);
        ButtonLayout.changeBoth(btnVerify);
        right.add(btnMerge);
        ButtonLayout.changeHeight(btnMerge);
        ButtonLayout.changeBoth(btnMerge);*/


        /*right.add(btnStartCapture);
        ButtonLayout.changeHeight(btnStartCapture);
        ButtonLayout.changeBoth(btnStartCapture);*/

		/*where log panel stays*/
        JScrollPane sp = new JScrollPane(logView);
        sp.setBorder(new TitledBorder(new EtchedBorder(), "Informações:"));
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        right.add(sp);

        left.setPreferredSize(new Dimension(100, 100));
        right.setPreferredSize(new Dimension(100, 100));

        ButtonLayout.changeBoth(right);
        contentPane.add(left);
        contentPane.add(right);

		/*sets main frame properties*/
        frame.setPreferredSize(new Dimension(600, 400));
        frame.pack();
        frame.setLocationRelativeTo(null);
        /*ImageIcon icon = new ImageIcon(this.getClass().getResource("/br/com/realsociedade/logo.jpg"));  
        frame.setIconImage(icon.getImage());*/
        frame.setVisible(true);

		
		
		/* Handles StartCapture clicking; calls function to start capture *//*
        btnStartCapture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

            }
        });*/


        // Handles btnIdentify clicking. Gets all templates from
        // database, to check if it finds one matching
        btnIdentify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
            	startCapture();
                if (image != null)
                    try {

                        String ret = dbAccess
                                .findIDTemplate(new BiometricTemplate(image));
                        //System.out.println(image);
                        if (ret != null)
                            logPanel.log("Usuário " + ret);
                        else
                            logPanel.log("Usuário n�o encontrado.");

                    } catch (FeatureNotLicensedException
                            | FeatureNotAvailableForFreeException
                            | UnsupportedBiometricModalityException
                            | SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                else
                    logPanel.log("Primeiro entre com uma digital.");
            }
        });



		/*
		 * When "Cadastro" button is clicked - Opens new window
		 */
        btnCadastro.addActionListener(new ActionListener() {

        	
            public void actionPerformed(ActionEvent event) {
            	startCapture();
                final Thread t = new Thread(new Runnable() {
                    public void run() {
                        Cadastro cad = new Cadastro();

                    }
                });
                t.start();
            }
        });






        // Handles btnMerge clicking. It is supposed to open new window to
        // capture 3 images and merges them
        btnMerge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                stopCapture();

                final Thread t = new Thread(new Runnable() {
                    public void run() {
                        Consolidacao cons = new Consolidacao();
                    }
                });
                t.start();
            }
        });



        // Handles btnMerge clicking. It is supposed to open new window to
        // capture 3 images and merges them
        btnVerify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                final Thread t = new Thread(new Runnable() {
                    public void run() {
                        Verificacao ver = new Verificacao();
                    }
                });
                t.start();
            }
        });



        // When closing, stops capturing
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                if (isCaptureOn)
                    BiometricSDK.StopSDK(listener);
            }
        });
    }

    

	/* Add listener(this) to the capture event listener to start capture */
    public void startCapture() {
        if (!isCaptureOn)
            BiometricSDK.StartSDK(this);
        isCaptureOn = true;
        
    }

    /* Remove listener(this) to the capture event listener to stop capture */
    public void stopCapture() {

        if (isCaptureOn) {
            BiometricSDK.StopSDK(this);
            System.out.println("parou captura");
        }
        isCaptureOn = false;
    }

    public static BiometricImage getImage() {
        return image;
    }

    /*copy files to temporary folder*/
    public void copyToTempFolder(String tempFolderName) {
		/*finds out if you are running on 32 or 64bits*/
        if (System.getProperty("sun.arch.data.model").equals("64")) {
            arch = "x64";
        } else arch = "x86";

        ArrayList<String> dllNames = new ArrayList<String>();

    	/*dlls to be loaded*/
        dllNames.add("pthreadVC2.dll");
        dllNames.add("VrBio.dll");
        dllNames.add("VrModuleFutronic.dll");
        dllNames.add("VrModuleDigitalPersona.dll");
        dllNames.add("VrModuleNitgen.dll");
        dllNames.add("VrModuleSuprema.dll");
        dllNames.add("libusb0.dll");
        dllNames.add("ftrScanAPI.dll");
        dllNames.add("NBioBSP.dll");
        dllNames.add("UFScanner.dll");
        dllNames.add("UFLicense.dat");

        String dllName;
		/*for each name in list*/
        for (int i = 0; i < dllNames.size(); i++) {
            dllName = dllNames.get(i);
    		/*makes new folder*/
            new File(tempFolderName).mkdirs();
            File tmpDir = new File(tempFolderName);
            File tmpFile = new File(tmpDir, dllName);

            if (!new File(tmpDir + "/" + dllName).isFile()) {
                try {
                    InputStream in = getClass().getResourceAsStream("/dlls/" + arch + "/" + dllName);
                    OutputStream out = new FileOutputStream(tmpFile);

                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();

                } catch (UnsatisfiedLinkError e) {
                    e.printStackTrace();
                    // deal with exception
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * Must be implemented in order to implement CaptureEventListener; handles
     * the capture events
     */
    public void onCaptureEvent(CaptureEventType eventType,
                               BiometricScanner reader, BiometricImage image) {
        // Prints the event, including the image size and resolution, if a image
        // is available
        if (image != null)
            logPanel.log(reader + ": " + eventType + "  => " + image);
        else
            logPanel.log(reader + ": " + eventType);

        switch (eventType) {
            // Device plugged. I want to receive images from it.
            case PLUG: {
                try {
                    reader.addCaptureEventListener(this);

                } catch (BiometricException e) {
                    logPanel.log("Cannot start device " + reader + ": " + e);
                }
                break;
            }
            // Device unplugged.
            // It might be nice to display it on your UI, but no action is required.
            case UNPLUG:
                break;

            // The biometric feature has been placed on the scanner.
            // It might be nice to display it on your UI, but no action is required.
            case PLACED:
                break;
            // The biometric feature has been removed from the scanner.
            // It might be nice to display it on your UI, but no action is required.
            case REMOVED:
                break;

            // A image frame has been received.
            // It might be nice to display it on your UI, but no action is required.
            case IMAGE_FRAME:
                imagePanel.setImage(image);
                // pack(); //Refresh layout
                break;
            // A 'final' image has been captured. THIS is the image that we must
            // handle!
            case IMAGE_CAPTURED: {

                imagePanel.setImage(image);

                this.image = image;
                
                

                break;
            }

        }

    }

    public static void main(String args[]) {
        new Main();
        
    }
}
