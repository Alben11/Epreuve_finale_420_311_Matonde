package mv.sdd.model;

import mv.sdd.utils.Constantes;

import java.util.ArrayList;
import java.util.List;

public class Commande {
    private int id;
    private static int nbCmd = 0;
    private final Client client;
    private EtatCommande etat = EtatCommande.EN_ATTENTE;
    private int tempsRestant; // en minutes simulées
    // TODO : ajouter l'attribut plats et son getter avec le bon type et le choix de la SdD adéquat
    // private final <Votre structure de choix adéquat> plats
    private final List<MenuPlat> plats = new ArrayList<>();

    // TODO : Ajout du ou des constructeur(s) nécessaires ou compléter au besoin
    public Commande(Client client, MenuPlat plat) {
        id = ++nbCmd;
        this.client = client;
        // À compléter
        ajouterPlat(plat);
    }
    // dans Commande.java
    public Commande(Client client) {
        id = ++nbCmd;
        this.client = client;
    }


    public List<MenuPlat> getPlats() {
        return plats;
    }

    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public int getTempsRestant() {
        return tempsRestant;
    }

    public void setEtat(EtatCommande etat) {
        this.etat = etat;
    }

    // TODO : Ajoutez la méthode ajouterPlat
    public void ajouterPlat(MenuPlat codePlat) {
        plats.add(codePlat);
    }

    // TODO : Ajoutez la méthode demarrerPreparation
    public void demarrerPreparation() {
        this.tempsRestant = calculerTempsPreparationTotal();
        this.etat = EtatCommande.EN_PREPARATION;
    }

    // TODO : Ajoutez la méthode decrementerTempsRestant
    public void decrementerTempsRestant() {
        if (etat == EtatCommande.EN_PREPARATION && tempsRestant > 0) {
            tempsRestant--;
        }
    }

    // TODO : Ajoutez la méthode estTermineeParTemps
    public boolean estTermineeParTemps() {
        return etat == EtatCommande.EN_PREPARATION && tempsRestant <= 0;
    }

    // TODO : Ajoutez la méthode calculerTempsPreparationTotal
    public int calculerTempsPreparationTotal() {
        int total = 0;
        for (MenuPlat codePlat : plats) {
            Plat plat = (Plat) Constantes.MENU.get(codePlat);
            if (plat != null) {
                total += plat.getTempsPreparation();
            }
        }
        return total;
    }

    // TODO : Ajoutez la méthode calculerMontant
    public double calculerMontant() {
        double total = 0.0;
        for (MenuPlat codePlat : plats) {
            Plat plat = (Plat) Constantes.MENU.get(codePlat);
            if (plat != null) {
                total += plat.getPrix();
            }
        }
        return total;
    }
}
