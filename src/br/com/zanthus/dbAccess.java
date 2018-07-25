package br.com.zanthus;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import br.com.zanthus.model.Aluno;
import veridis.biometric.BiometricIdentification;
import veridis.biometric.BiometricTemplate;
import veridis.biometric.samples.applet.Base64;



public class dbAccess {
	
	protected static Connection con = null;
	protected static Statement stm = null;
	protected static int minimumThreshold = 40;
	
	/**
	 * Funcao para adicionar o template(imagem) no BD.
	 *  esperado um ID para cadastro do usuario e a imagem recebida do leitor biometrico.
	 * Este ID vem do ComboBox dos alunos cadastrados na tabela.
	 */
	
	public static boolean AddTemplate(BiometricTemplate template, int id) throws SQLException{
		try{
			if(con == null)
				connectToDB();
			if(stm == null)
				stm = con.createStatement();
			System.out.println(id);
			
			/**
			 * Query que analisa se o aluno ja possui cadastro biometrico na tabela
			 */
			String queryAnalisaId = "SELECT Aluno from cadastro.biometria where Aluno = "+id;
			ResultSet rs = stm.executeQuery(queryAnalisaId); 
			
			//caso possua ele atualiza a digital
			if (rs.next()) {
				Statement stm1 = con.createStatement();
				String query = "UPDATE CADASTRO.Biometria Set Digital = '"+Base64.encodeBytes(template.getData())+"' where aluno = "+id;
				boolean result = stm1.execute(query);
				if (!result) return true;
				return false;
				
			//caso nao possua ele cria um novo registro na tabela com o ID do aluno selecionado e a digital Capturada pelo leitor
			}else {
				Statement stm1 = con.createStatement();
				String query = "INSERT INTO CADASTRO.Biometria (Digital, Aluno)  VALUES ('"+Base64.encodeBytes(template.getData())+"', "+id+")";
				
				boolean result = stm1.execute(query);
				if (!result) return true;
				return false;
			}
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}

	
	}

	/***
	 * FUNCAO PARA BUSCAR O TEMPLATE NO BANCO DE DADOS CONFORME ID INFORMADO
	 */
	
	public static BiometricTemplate getTemplate(BiometricTemplate template, String id) throws SQLException{

		if(con == null)
			connectToDB();
		if(stm == null)
			stm = con.createStatement();
		
		
		String query = "SELECT Digital, aluno->nome FROM cadastro.biometria where Aluno = \""+id+"\"";
		

		ResultSet res = stm.executeQuery(query); 
		if(res.next()){		
			try{
				
				BiometricTemplate tempBD = new BiometricTemplate((Base64.decode(res.getString("Digital"))));
	
				return tempBD;
			}
			catch(Exception e ){
				System.out.println(e.getLocalizedMessage());
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Function to verify if the given template matches with the template corresponding to the given id
	 */
	public static int verificaIdentificador(BiometricTemplate template, String id) throws SQLException{

		if(con == null)
			connectToDB();
		if(stm == null)
			stm = con.createStatement();
		

		String query = "SELECT Digital FROM cadastro.biometria where aluno = \""+id+"\"";
		

		ResultSet res = stm.executeQuery(query); 
		if(res.next()){		
			try{
				
				BiometricTemplate tempBD = new BiometricTemplate((Base64.decode(res.getString("Digital"))));
	
				if (tempBD.match(template) > minimumThreshold)
					return 1;
			}
			catch(Exception e ){
				System.out.println(e.getLocalizedMessage());
				return -1;
			}
		}
		return 0;
	}
	
	/***
	 * Funcao para buscar a lista de alunos ativos no sistema
	 */
	public ArrayList<Aluno> buscaAlunosAtivos() throws SQLException{

		if(con == null)
			connectToDB();
		if(stm == null)
			stm = con.createStatement();
		

		String query = "select %ID as id, nome from CADASTRO.Aluno where %Internal(Situacao) = 1 order by nome";
		ArrayList<Aluno> alunos = new ArrayList<>();

		ResultSet res = stm.executeQuery(query); 
		while(res.next()){
			
			try{
				Aluno aluno = new Aluno();
				aluno.setId(res.getInt("id"));
				aluno.setNome(res.getString("nome"));
				
				alunos.add(aluno);
				
			}
			catch(Exception e ){
				System.out.println(e.getLocalizedMessage());
			}
		}
		return alunos;
	}
	
	/**
	 * DE ACORDO COM ID FORNECIDO, PROCURA NO BANCO UMA DIGITAL QUE COMBINE
	 */
	public static String  findIDTemplate(BiometricTemplate template) throws SQLException{
		
		
		if(con == null)
			connectToDB();
		if(stm == null)
			stm = con.createStatement();
		
		String query = "SELECT Aluno, Aluno->Nome, Digital FROM Cadastro.Biometria";
		ResultSet res = stm.executeQuery(query); 


		/*prepares identification context to perform match faster*/
		BiometricIdentification temp = new BiometricIdentification(template);
		
		while(res.next()){	
				try{
					/**
					 * RECEBE O TEMPLATE E DECODIFICA
					 */
					BiometricTemplate tempBD = new BiometricTemplate((Base64.decode(res.getString("Digital"))));
					
					/**
					 *VALIDA SE O SCORE DA COMBINADO e MAIOR QUE 40
					 */
					if( temp.match(tempBD) > 40)
						//String queryValidaPlano = "SELECT   ";
						return res.getString("Nome");

				}
				catch(Exception e ){
					System.out.println(e.getLocalizedMessage());
					return null;
				}
			}
		
		return null;
		
	}
	
	public static void connectToDB(){
		
		/**
		 * CONEXaO COM BANCO DE DADOS PR�PRIO DO JAVA USANDO BIBLIOTECA SQLITE
		 */
		/*try{ 
		 Class.forName("org.sqlite.JDBC");
		
		 con = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("java.io.tmpdir") + "banco.db");
		 stm = con.createStatement();
		 stm.executeUpdate("DROP TABLE IF EXISTS cadastro");
		 stm.executeUpdate("CREATE TABLE cadastro (" +
				 "id varchar(100) PRIMARY KEY NOT NULL," +
				 "template varchar(200) NOT NULL);");
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}*/
		
		/**
		 * CONEXaO COM O BANCO DE DADOS DO CACHe
		 */
        String url="jdbc:Cache://20.0.0.7:1972/jiujitsu"; //SERVIDOR E NAMESPACE
 		String user = "flavio.miranda";
 		String password = "aco0910";
 		
		try {
 			Class.forName ("com.intersys.jdbc.CacheDriver");
 			con = DriverManager.getConnection(url,user,password);
 			stm = con.createStatement();
 			
 		} catch (Exception ex) {
 			System.out.println("Caught exception: " +
 					ex.getClass().getName()
 					+ ": " + ex.getMessage());
 		}
 	
	
	}

}