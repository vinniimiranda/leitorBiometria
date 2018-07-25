package br.com.zanthus;
//resta fazer: tela de identificação e a de salvartemplate, ok!? 


import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import br.com.zanthus.util.ButtonLayout;
import veridis.biometric.BiometricException;
import veridis.biometric.BiometricImage;
import veridis.biometric.BiometricMerge;
import veridis.biometric.BiometricModality;
import veridis.biometric.BiometricSDK;
import veridis.biometric.BiometricScanner;
import veridis.biometric.CaptureEventListener;
import veridis.biometric.JBiometricPanel;
import veridis.biometric.samples.util.LicenseHelper;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
 * ButtonDemo.java requires the following files:
 *   images/right.gif
 *   images/middle.gif
 *   images/left.gif
 */
public class Consolidacao extends JPanel implements CaptureEventListener {

	private static final long serialVersionUID = 304186961045424542L;
	private JBiometricPanel imagePanel = new JBiometricPanel();
	private BiometricImage image = null;
	private BiometricMerge merge = null;

	private JButton btnOK, btnCancel;
	private CaptureEventListener listener = this;
	private int capturedImages = 0;
	private JFrame frame;
	private JTextField textField;

	public Consolidacao() {
		/*creates panel to display the fingerprints*/
		imagePanel.setPreferredSize(new Dimension(200, 200));

		/*creates buttons*/
		btnCancel = new JButton("Cancelar");
		btnCancel.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnCancel.setHorizontalTextPosition(AbstractButton.LEADING);
		btnCancel.setMnemonic(KeyEvent.VK_M);

		btnOK = new JButton("Capture 3 imagens para continuar...");
		btnOK.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnOK.setHorizontalTextPosition(AbstractButton.LEADING);
		btnOK.setMnemonic(KeyEvent.VK_M);

		/*creates main frame*/
		frame = new JFrame("Veridis - Java Consolidacao");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/*gets container where components will be organized*/
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		/*creates pane and adds all components*/
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.add(imagePanel);
		ButtonLayout.changeWidth(imagePanel);
		pane.add(btnCancel);
		ButtonLayout.changeBoth(btnCancel);
		ButtonLayout.changeBoth(pane);
		pane.add(btnOK);
		ButtonLayout.changeBoth(btnOK);
		ButtonLayout.changeBoth(pane);
		textField = new JTextField();
		textField.setBorder(new TitledBorder(new EtchedBorder(),
				"Identificador:"));
		pane.add(textField);

		/*adds pane to main frame*/
		contentPane.add(pane);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);


	

		/*
		 * You have to set the directory where the .dll (windows) or .so (linux)
		 * are found Don't forget that they must be on the system_path, or on
		 * the java library path
		 */
		LicenseHelper.setLibrariesDirectory("C:\\testeDlls");

		/* Add listener to start capture */
		BiometricSDK.StartSDK(this);

		// When closing, stops capturing
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				BiometricSDK.StopSDK(listener);
			}
		});
		
		/* Cancel event: sets everything as null */
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				merge = null;
				capturedImages = 0;
				image = null;
				imagePanel.setImage(image);
			}
		});
		/* Finishes register and erases data */
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Cadastrar();
				merge = null;
				capturedImages = 0;
				image = null;
				imagePanel.setImage(image);
			}
		});
	}
	

	/*
	 * This function registers the merge from 3 fingerprints with the given
	 * identifier
	 */
	public void Cadastrar() {
		int input = Integer.parseInt(textField.getText().toString());

		if (merge != null && input != 0)
			try {				
				JOptionPane.showMessageDialog(null, "Realizando o cadastro.");
				/* adds to database */
				if (dbAccess.AddTemplate(merge.getTemplate(), input)) {
					JOptionPane.showMessageDialog(null, "Usuario " + input
							+ " cadastrado");
				} else {
					JOptionPane.showMessageDialog(null, "Usuario " + input
							+ " não pode ser cadastrado");
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Usuario " + input
						+ " não pode ser cadastrado");
			}

	}
	/*treats capture events received*/
	public void onCaptureEvent(CaptureEventType eventType,
			BiometricScanner reader, BiometricImage image) {

		switch (eventType) {
		// Device plugged. I want to receive images from it.
		case PLUG: {
			try {
				reader.addCaptureEventListener(this);

			} catch (BiometricException e) {
				// log("Cannot start device " + reader + ": " + e);
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

			break;
		// A 'final' image has been captured. THIS is the image that we must
		// handle!
		case IMAGE_CAPTURED: {
			capturedImages++;

			imagePanel.setImage(image);
			/*if merge == null, creates new merge; must be done before adding images to it*/
			if (merge == null)
				merge = new BiometricMerge(BiometricModality.FINGERPRINT);

			/* Adds image to current merge */
			merge.add(image);


			break;
		}
		}
	}

	public static void main(String args[]) {
		new Consolidacao();
	}
}
