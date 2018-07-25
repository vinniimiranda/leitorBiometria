package br.com.zanthus;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import br.com.zanthus.model.Aluno;
import br.com.zanthus.util.ButtonLayout;
import veridis.biometric.BiometricImage;
import veridis.biometric.BiometricTemplate;



public class Cadastro {
	
	JLabel textField;
	JButton btnOK, btnCancel;
	JComboBox cbxAluno;
	
	ArrayList<Aluno> alunos;
	
	dbAccess db = new dbAccess();
	
	Cadastro(){
		/*creates buttons*/
		btnOK = new JButton("OK");
		btnCancel = new JButton("Cancelar");
		/*creates textField to recieve the identifier*/
		textField = new JLabel("Vincule o aluno a digital");
		/*creates panel to organize buttons*/
		JPanel buttonsPane = new JPanel();
		buttonsPane.setLayout(new BoxLayout(buttonsPane, BoxLayout.X_AXIS));
		
		//Cria combobox
		cbxAluno = new JComboBox<>();
		loadCombo();
		
		/*creates main frame*/
		final JFrame frame = new JFrame("Biometria - Cadastro");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		/*gets container where components will be organized*/
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		/*add buttons to pane*/
		buttonsPane.add(btnOK);
		buttonsPane.add(btnCancel);
		/*configures border of textfield*/
		/*textField.setBorder(new TitledBorder(new EtchedBorder(),
				"Identificador:"));*/	
		ButtonLayout.changeBoth(textField);
		/*adds text field*/
		contentPane.add(textField);
		contentPane.add(cbxAluno);
		contentPane.add(buttonsPane);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		
		/* Cancel event: closes the window */
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//stopCapture();
				frame.dispose();
				
			}
		});
		/* Finishes register and erases data */
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				int idAluno = ((Aluno)cbxAluno.getSelectedItem()).getId();
				String nomeAluno =((Aluno)cbxAluno.getSelectedItem()).getNome();
				BiometricImage image = Main.getImage();
				
				if(image != null){
					String input = textField.getText().toString();
					/* adds to database */
					try{
						JOptionPane.showMessageDialog(null, "Realizando o cadastro.");
						try{
							new BiometricTemplate(image);
						}catch(Exception e){
							System.out.println(e.getMessage());
							
						}
						if (dbAccess.AddTemplate(new BiometricTemplate(image), idAluno)) {
							JOptionPane.showMessageDialog(null, "Digital de " + nomeAluno
									+ "  cadastrada com sucesso");
							frame.dispose();
							
						} else {
							JOptionPane.showMessageDialog(null, "Usuario " + input
									+ " não pode ser cadastrado");
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Usuario " + input
								+ " não pode ser cadastrado");
					}
				}
				else JOptionPane.showMessageDialog(null, "Nenhuma imagem disponível para cadastro");
			}
		});
	}
	
	private void loadCombo() {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		
		try {
			alunos = db.buscaAlunosAtivos();
			for (int i = 0; i < alunos.size(); i++) {
				//cbxAluno.addItem(alunos.get(i).getId() +" - "+ alunos.get(i).getNome());
				model.addElement( new Aluno(alunos.get(i).getId(), alunos.get(i).getNome()));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cbxAluno.setModel(model);
	}
	
	public static void main(String args[]) {
		new Cadastro();
	}
}
