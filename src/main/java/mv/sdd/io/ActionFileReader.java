package mv.sdd.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Lecture du fichier d'actions
public class ActionFileReader {
    public static List<Action> readActions(String filePath) throws IOException {
        List<Action> actions = new ArrayList<>();

        // TODO : Ajouter le code qui permet de lire et parser un fichier d'actions
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] tokens = line.split(";");
                String nomAction = tokens[0].trim();
                ActionType type = ActionType.valueOf(nomAction);

                int p1 = 0;
                int p2 = 0;
                String p3 = null;

                switch (type) {
                    case DEMARRER_SERVICE -> {
                        // DEMARRER_SERVICE;duree;nbCuisiniers
                        p1 = Integer.parseInt(tokens[1].trim()); // duree
                        p2 = Integer.parseInt(tokens[2].trim()); // nbCuisiniers
                    }
                    case AJOUTER_CLIENT -> {
                        // AJOUTER_CLIENT;id;nom;patience
                        p1 = Integer.parseInt(tokens[1].trim()); // id
                        p3 = tokens[2].trim();                   // nom
                        p2 = Integer.parseInt(tokens[3].trim()); // patience
                    }
                    case PASSER_COMMANDE -> {
                        // PASSER_COMMANDE;idClient;codePlat
                        p1 = Integer.parseInt(tokens[1].trim()); // idClient
                        p3 = tokens[2].trim();                   // codePlat
                    }
                    case AVANCER_TEMPS -> {
                        // AVANCER_TEMPS;minutes
                        p1 = Integer.parseInt(tokens[1].trim()); // minutes
                    }
                    case AFFICHER_ETAT, AFFICHER_STATS, QUITTER -> {
                        // pas de paramètres
                    }
                }

                actions.add(new Action(type, p1, p2, p3));
            }
        }

        return actions;
    }
}
