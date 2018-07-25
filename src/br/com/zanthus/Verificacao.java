package br.com.zanthus;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import br.com.zanthus.util.ButtonLayout;
import veridis.biometric.BiometricImage;
import veridis.biometric.BiometricTemplate;
import veridis.biometric.BiometricException.FeatureNotAvailableForFreeException;
import veridis.biometric.BiometricException.FeatureNotLicensedException;
import veridis.biometric.BiometricException.UnsupportedBiometricModalityException;


public class Verificacao {
	
	/*minimum value to consider a match*/
	protected static int minimumThreshold = 40;
	
	private JTextField textField;
	private JButton btnOK, btnCancel;
	
	Verificacao(){
		/*creates buttons*/
		btnOK = new JButton("OK");
		btnCancel = new JButton("Cancelar");
		
		/*creates textfield*/
		textField = new JTextField("", 15);
		JPanel buttonsPane = new JPanel();
		buttonsPane.setLayout(new BoxLayout(buttonsPane, BoxLayout.X_AXIS));
			
		/*creates main frame*/
		JFrame frame = new JFrame("Veridis - Cadastro");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		/*gets container where components will be organized*/
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		/*adds buttons*/
		buttonsPane.add(btnOK);
		buttonsPane.add(btnCancel);
		
		/*adds border to textfield*/
		textField.setBorder(new TitledBorder(new EtchedBorder(),
				"Identificador:"));	
		ButtonLayout.changeBoth(textField);
		
		/*adds id field and buttons to main frame*/
		contentPane.add(textField);
		contentPane.add(buttonsPane);
		
		frame.pack();
		frame.setVisible(true);

		
		/* Cancel event: sets everything as null */
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		/* Finishes register and erases data */
		btnOK.addActionListener(new ActionListener() {
				/*gets template from db corresponding to identifier and returns the 
				 * match between it and the last image captured*/
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String input = textField.getText().toString();
					try {
						/*receives image from main application*/
						BiometricImage image = Main.getImage();
						/*creates template from it*/
						BiometricTemplate template = new BiometricTemplate(image);
						/*gets template from database that corresponds to the given id*/
						BiometricTemplate tempDB = dbAccess.getTemplate(
								template , input);
						
						if (tempDB == null)
							JOptionPane.showMessageDialog(null, "Busca nao retornou nenhum template.");
						/*checks the match score between the 2 templates*/
						else if (tempDB.match(template) > minimumThreshold) {
							JOptionPane.showMessageDialog(null, "Usuario " + input + " Identificado");
							frame.dispose();
						}
						else
							JOptionPane.showMessageDialog(null, "Usuario n�o identificado.");

					} catch (FeatureNotLicensedException
							| FeatureNotAvailableForFreeException
							| UnsupportedBiometricModalityException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		});
	}
	public static void main(String args[]) {
		new Verificacao();
	}
}
