package services.authentification;

import java.sql.SQLException;

import org.json.JSONObject;

import bd.UtilisateursTools;
import services.CodesErreur;
import services.ErrorJSON;

public class Login {
	public static JSONObject login(String login, String motDePasse) {
		if (! verificationParametres(login, motDePasse)) {
			return ErrorJSON.serviceRefused("L'un des parametres est null", CodesErreur.ERREUR_ARGUMENTS);
		}
		
		try {
			// On vérifie que l'utilisateur existe
			boolean isUser = UtilisateursTools.verificationExistenceLogin(login);
			if (! isUser) {
				return ErrorJSON.serviceRefused("L'utilisateur n'existe pas", CodesErreur.ERREUR_UTILISATEUR_INEXISTANT);
			}
			
			// On vérifie que le couple (login, mot de passe) fonctionne
			boolean passwordOk = UtilisateursTools.checkPassword(login, motDePasse);
			if (! passwordOk) {
				return ErrorJSON.serviceRefused("Erreur de mot de passe", CodesErreur.ERREUR_MDP);
			}
			
			// On génère un identifiant aléatoire
			String identifiant = UtilisateursTools.getIdUser(login);
			
			// On génère une réponse
			JSONObject retour = new JSONObject();
			String key = UtilisateursTools.insertSession(identifiant, false);
			retour.put("key", key);
			return retour;

		} catch (SQLException e) {
			return ErrorJSON.serviceRefused("Erreur de SQL", CodesErreur.ERREUR_SQL);			
		} catch (Exception e) {
			return ErrorJSON.serviceRefused("Erreur Inconnue", CodesErreur.ERREUR_INCONNUE);				
		} 
	}
	
	private static boolean verificationParametres(String login, String motDePasse) {
		return (login != null&& motDePasse != null);
	}
}
