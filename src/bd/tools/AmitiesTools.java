package bd.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import bd.Database;
import exceptions.IndexInvalideException;

public class AmitiesTools {
	
	
	/**
	 * Retourne true si id_ami1 a deja ajoute id_ami2 en ami, false sinon.
	 * @param id_ami1 : L'ID de l'ami ajoutant
	 * @param id_ami2 : L'ID de l'ami ajoute
	 * @return Un booleen a true si id_ami1 suit deja id_ami2, false sinon
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static boolean suitDeja(String id_ami1, String id_ami2) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// Connection a la base de donnees
        Connection connection = Database.getMySQLConnection();
        
        // Creation et execution de la requete
        String requete = "SELECT * FROM Amities WHERE (id_ami1=? AND id_ami2=?);";
        PreparedStatement statement = connection.prepareStatement(requete);
        statement.setInt(1, Integer.parseInt(id_ami1));
        statement.setInt(2, Integer.parseInt(id_ami2));
        statement.executeQuery();
        
        // Recuperation des donnees
        ResultSet resultSet = statement.getResultSet();
        boolean retour = resultSet.next();
        
        // Liberation des ressources
        resultSet.close();
        statement.close();
        connection.close();
        
        return retour;
	}
	
	
	/**
	 * Ajoute id_ami2 dans la liste d'amis de id_ami1
	 * @param id_ami1 : L'ami ajoutant
	 * @param id_ami2 : L'aime ajoute
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static void ajouterAmi(String id_ami1, String id_ami2) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// Connection a la base de donnees
        Connection connection = Database.getMySQLConnection();
        
        // Creation et execution de la requete
        String requete = "INSERT INTO Amities Values (?, ?, null);";
        PreparedStatement statement = connection.prepareStatement(requete);
        statement.setInt(1, Integer.parseInt(id_ami1));
        statement.setInt(2, Integer.parseInt(id_ami2));
        statement.executeUpdate();
        
        // Liberation des ressources
        statement.close();
        connection.close();
	}
	
	
	public static void supprimerAmitie(String id_ami1, String id_ami2) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// Connection a la base de donnees
        Connection connection = Database.getMySQLConnection();
        
        // Creation et execution de la requete
        String requete = "DELETE FROM Amities WHERE id_ami1=? AND id_ami2=?;";
        PreparedStatement statement = connection.prepareStatement(requete);
        statement.setInt(1, Integer.parseInt(id_ami1));
        statement.setInt(2, Integer.parseInt(id_ami2));
        statement.executeUpdate();
        
        // Liberation des ressources
        statement.close();
        connection.close();
	}
	
	
	public static void supprimerAmitiesConcernant(String id_ami) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// Connection a la base de donnees
        Connection connection = Database.getMySQLConnection();
        
        // Creation et execution de la requete
        String requete = "DELETE FROM Amities WHERE id_ami1=? OR id_ami2=?;";
        PreparedStatement statement = connection.prepareStatement(requete);
        statement.setInt(1, Integer.parseInt(id_ami));
        statement.setInt(2, Integer.parseInt(id_ami));
        statement.executeUpdate();
        
        // Liberation des ressources
        statement.close();
        connection.close();
	}

	
	/**
	 * Retourne nombre_demandes d'amis de l'utilisateur d'ID id_utilisateur en partant de index_debut.
	 * @param id_utilisateur : L'ID de l'utilisateur dont on liste les amis
	 * @param index_debut : Sa valeur initiale est zero. Indique a partir de quel index on souhaite lister les amis.
	 * @param nombre_demandes : Indique le nombre d'amis desires.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IndexInvalideException
	 */
	public static JSONObject listerAmis(String id_utilisateur, String index_debut, String nombre_demandes) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IndexInvalideException {
    	if (Integer.parseInt(index_debut) < 0) {
    		throw new IndexInvalideException("L'index ne peut pas etre negatif.");
    	}
		
		// Connection a la base de donnees
        Connection connection = Database.getMySQLConnection();
        
        // Creation et execution de la requete
        String requete = "SELECT id_ami2 FROM Amities WHERE id_ami1=? LIMIT ? OFFSET ?;";
        PreparedStatement statement = connection.prepareStatement(requete);
        statement.setInt(1, Integer.parseInt(id_utilisateur));
        statement.setInt(2, Integer.parseInt(nombre_demandes));
        statement.setInt(3, Integer.parseInt(index_debut));
        ResultSet res = statement.executeQuery();
        
        // creation d'un JSONObject dans lequel on met les amis
        JSONObject liste = new JSONObject();
        liste.put("Amis", new JSONArray());	// Liste vide au cas ou l'utilisateur n'a pas d'ami
        while (res.next()){
        	String id_ami=res.getString("id_ami2");
        	String pseudo = UtilisateursTools.getPseudoUtilisateur(id_ami);
        	if (pseudo == null) {
        		continue;
        	}
        	JSONObject ami = new JSONObject();
        	ami.accumulate("id", id_ami);
        	ami.accumulate("pseudo", pseudo);
        	liste.accumulate("Amis", ami);
        }
        
        // Liberation des ressources
        statement.close();
        connection.close();

        return liste;
	}

	
	/**
	 * Retourne tous les id des utilisateurs suivis par id_utilisateur
	 * @param id_utilisateur
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IndexInvalideException
	 */
	public static JSONObject listerTousLesAmis(String id_utilisateur) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// Connection a la base de donnees
        Connection connection = Database.getMySQLConnection();
        
        // Creation et execution de la requete
        String requete = "SELECT id_ami2 FROM Amities WHERE id_ami1=?;";
        PreparedStatement statement = connection.prepareStatement(requete);
        statement.setInt(1, Integer.parseInt(id_utilisateur));
        ResultSet res = statement.executeQuery();
        
        // creation d'un JSONObject dans lequel on met les amis
        JSONObject liste = new JSONObject();
        liste.put("Amis", new JSONArray());	// Liste vide au cas ou l'utilisateur n'a pas d'ami
        while (res.next()){
        	liste.accumulate("Amis", res.getString("id_ami2"));
        }
        
        // Liberation des ressources
        statement.close();
        connection.close();
        
        return liste;
	}
	
	
	/**
	 * Vide entierement le contenu de la table 'Amities' dans
	 * notre BDD MySQL.
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static void nettoieTableSessions() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		// Connection a la base de donnees
        Connection connection = Database.getMySQLConnection();
        
        // Creation et execution de la requete
        String requete = "TRUNCATE TABLE Amities;";
        PreparedStatement statement = connection.prepareStatement(requete);
        statement.executeQuery();
        
        // Liberation des ressources
        statement.close();
        connection.close();
	}
	
	
	/**
	 * Creer la table 'Amities' dans notre
	 * BDD MySQL.
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static void creerTableAmities() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		// Connection a la base de donnees
        Connection connection = Database.getMySQLConnection();
        
        // Creation et execution de la requete
        String requete = String.format("%s%s%s%s%s%s",
        		"CREATE TABLE Amities(",
        			"id_ami1 Integer,",
        			"id_ami2 Integer,",
        			"timestamp Timestamp,",
        			"PRIMARY KEY (id_ami1, id_ami2)",
        		");");

        PreparedStatement statement = connection.prepareStatement(requete);
        statement.executeQuery();
        
        // Liberation des ressources
        statement.close();
        connection.close();
	}
	
	

	 /**		
	  * Retourne tous les id des utilisateurs suivis par id_utilisateur dans une ArrayList		
	  * @param id_utilisateur		
	  * @throws SQLException 		
	  * @throws ClassNotFoundException 		
      * @throws IllegalAccessException 		
	  * @throws InstantiationException 		
	  */		
	 public static ArrayList<String> getAmisEnArrayList(String id_utilisateur) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {		
	     // Connection a la base de donnees		
	     Connection connection = Database.getMySQLConnection();		
	        		
	     // Creation et execution de la requete		
	     String requete = "SELECT id_ami2 FROM Amities WHERE id_ami1=?;";		
	     PreparedStatement statement = connection.prepareStatement(requete);		
	     statement.setInt(1, Integer.parseInt(id_utilisateur));		
	     ResultSet res = statement.executeQuery();		
	      		
	     // creation d'un JSONObject dans lequel on met les amis		
	     ArrayList<String> liste = new ArrayList<String>();		
	     while (res.next()){		
		 	liste.add(res.getString("id_ami2"));		
	     }		
	   		
	     // Liberation des ressources		
	     statement.close();		
	     connection.close();		
	       		
	 	 return liste;		
	 }
}
