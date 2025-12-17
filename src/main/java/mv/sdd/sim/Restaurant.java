package mv.sdd.sim;

import mv.sdd.io.Action;
import mv.sdd.io.ActionType;
import mv.sdd.model.*;
import mv.sdd.sim.thread.Cuisinier;
import mv.sdd.utils.Constantes;
import mv.sdd.utils.Formatter;
import mv.sdd.utils.Logger;
import java.util.*;

public class Restaurant {
    private final Logger logger;
    // TODO : Ajouter les attributs nécessaires ainsi que les getters et les setters
    private final Map<Integer, Client> clients = new HashMap<>();
    private final Queue<Commande> fileCommandes = new ArrayDeque<>();
    private final List<Commande> commandesEnPreparation = new ArrayList<>();

    // TODO : Ajouter le(s) constructeur(s)
    private Horloge horloge;
    private Stats stats;

    private Cuisinier cuisinier;
    private Thread threadCuisinier;

    private boolean serviceDemarre = false;
    private int dureeMaxService;

    public Restaurant(Logger logger) {
        this.logger = logger;
    }

    // TODO : implémenter les méthodes suivantes
    // Méthode appelée depuis App pour chaque action
    public void executerAction(Action action){
        ActionType type = action.getType();
        switch (type) {
            case DEMARRER_SERVICE -> demarrerService(
                    action.getParam1(),   // dureeMax
                    action.getParam2()    // nbCuisiniers
            );
            case AJOUTER_CLIENT -> ajouterClient(
                    action.getParam1(),   // id
                    action.getParam3(),   // nom
                    action.getParam2()    // patience
            );
            case PASSER_COMMANDE -> passerCommande(
                    action.getParam1(),   // idClient
                    MenuPlat.valueOf(action.getParam3()) // codePlat
            );
            case AVANCER_TEMPS -> avancerTemps(action.getParam1());
            case AFFICHER_ETAT -> afficherEtat();
            case AFFICHER_STATS -> afficherStatistiques();
            case QUITTER -> arreterService();
        }
    }

    public void demarrerService(int dureeMax, int nbCuisiniers) {

        this.dureeMaxService = dureeMax;
        this.horloge = new Horloge();
        this.stats = new Stats(horloge);
        serviceDemarre = true;

        logger.logLine(String.format(Constantes.DEMARRER_SERVICE, dureeMax, nbCuisiniers));
        cuisinier = new Cuisinier(this);
        threadCuisinier = new Thread(cuisinier, "Cuisinier-1");
        threadCuisinier.start();
    }

    public void avancerTemps(int minutes) {
        for (int i = 0; i < minutes; i++) {
            tick();
        }
        logger.logLine(Constantes.AVANCER_TEMPS + minutes);
    }


    public void arreterService(){
        // Votre code ici.
        serviceDemarre = false;
        if (cuisinier != null) {
            cuisinier.arreter();
        }
        if (threadCuisinier != null) {
            try {
                threadCuisinier.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // TODO : Déclarer et implémenter les méthodes suivantes
    // tick()
    private void tick() {
        if (!serviceDemarre) return;
        horloge.avancerTempsSimule(1);
        synchronized (this){notifyAll();}
        avancerCommandesEnPreparation();
        diminuerPatienceClients();

    }

    private void avancerCommandesEnPreparation() {
        synchronized (this) {
            Iterator<Commande> it = commandesEnPreparation.iterator();
            while (it.hasNext()) {
                Commande cmd = it.next();
                cmd.decrementerTempsRestant();
                if (cmd.estTermineeParTemps()) {
                    marquerCommandeTerminee(cmd);
                    it.remove();
                }
            }
        }
    }


    private void diminuerPatienceClients() {
        for (Client client : clients.values()) {
            if (client.getEtat() == EtatClient.EN_ATTENTE) {
                client.diminuerPatience(1);
                if (client.getEtat() == EtatClient.PARTI_FACHE) {

                    stats.incrementerNbFaches();
                    Commande commande = client.getCommande();
                    if (commande == null) {
                    } else {
                        switch (commande.getEtat()) {
                            case EN_ATTENTE:
                            case EN_PREPARATION:
                            case PRETE:
                                commande.setEtat(EtatCommande.PERDUE);
                                break;
                        }
                    }
                    logger.logLine(Formatter.eventClientFache(horloge.getTempsSimule(), client));
                }
            }
        }
    }


    public void afficherEtat() {
        int nbClientsPresents = 0;
        int nbServis = 0;
        int nbFaches = 0;

        for (Client client : clients.values()) {
            if (client.getEtat() != EtatClient.PARTI_FACHE) {
                nbClientsPresents++;
            }
            if (client.getEtat() == EtatClient.SERVI) {
                nbServis++;
            } else if (client.getEtat() == EtatClient.PARTI_FACHE) {
                nbFaches++;
            }
        }

        int tailleFile = fileCommandes.size();
        int nbEnPrep;
        synchronized (this) { nbEnPrep = commandesEnPreparation.size(); }


        logger.logLine(String.format(
                Constantes.RESUME_ETAT,
                horloge.getTempsSimule(),
                nbClientsPresents,
                nbServis,
                nbFaches,
                tailleFile,
                nbEnPrep
        ));

        // Une ligne par client (tu pourras remplacer par Formatter si fourni)
        for (Client client : clients.values()) {
            String emojiEtat = switch (client.getEtat()) {
                case EN_ATTENTE -> Constantes.EMO_CLIENT_ATTENTE;
                case SERVI -> Constantes.EMO_CLIENT_SERVI;
                case PARTI_FACHE -> Constantes.EMO_CLIENT_FACHE;
            };

            StringBuilder platsStr = new StringBuilder();
            if (client.getCommande() != null) {
                for (MenuPlat plat : client.getCommande().getPlats()) {
                    switch (plat) {
                        case PIZZA -> platsStr.append(" ").append(Constantes.EMO_PIZZA);
                        case BURGER -> platsStr.append(" ").append(Constantes.EMO_BURGER);
                        case FRITES -> platsStr.append(" ").append(Constantes.EMO_FRITES);
                    }
                }
            }

            logger.logLine(String.format(
                    "#%d %s %s (pat=%d,%s )",
                    client.getId(),
                    client.getNom(),
                    emojiEtat,
                    client.getPatience(),
                    platsStr.toString()
            ));
        }
    }


    public void afficherStatistiques() {
        logger.logLine(Constantes.HEADER_AFFICHER_STATS);
        logger.logLine(stats.toString());
    }

    // Client ajouterClient(int id, String nom, int patienceInitiale)
    public Client ajouterClient(int id, String nom, int patienceInitiale) {
        Client client = creerClient(id, nom, patienceInitiale);
        clients.put(id, client);
        stats.incrementerTotalClients();

        logger.logLine(String.format(
                Constantes.EVENT_ARRIVEE_CLIENT,
                horloge.getTempsSimule(),
                client.getId(),
                client.getNom(),
                client.getPatience()
        ));

        return client;
    }

    public Commande passerCommande(int idClient, MenuPlat codePlat) {
        Client client = clients.get(idClient);
        if (client == null) {
            return null;
        }

        Commande commande = client.getCommande();
        if (commande == null) {
            commande = creerCommandePourClient(client);
            commande.ajouterPlat(codePlat);
            client.setCommande(commande);
            synchronized (this) {
                fileCommandes.offer(commande);
                notifyAll(); // réveiller le cuisinier s’il attend
            }

        } else {
            commande.ajouterPlat(codePlat);
        }

        stats.incrementerVentesParPlat(codePlat);

        logger.logLine(String.format(
                Constantes.EVENT_CMD_CREE,
                horloge.getTempsSimule(),
                commande.getId(),
                client.getNom(),
                codePlat
        ));

        return commande;
    }

    public synchronized Commande retirerProchaineCommande() {
        return fileCommandes.poll();
    }

    public void demarrerPreparationCommande(Commande commande) {
        synchronized (this) {
            commande.demarrerPreparation();
            commandesEnPreparation.add(commande);
            notifyAll();
        }

        logger.logLine(String.format(
                Constantes.EVENT_CMD_DEBUT,
                horloge.getTempsSimule(),
                commande.getId(),
                commande.getTempsRestant()
        ));
    }


    public void marquerCommandeTerminee(Commande commande) {
        commande.setEtat(EtatCommande.PRETE);
        Client client = commande.getClient();
        client.setEtat(EtatClient.SERVI);
        stats.incrementerNbServis();
        stats.incrementerChiffreAffaires(commande.calculerMontant());
        logger.logLine(String.format(
                Constantes.EVENT_CMD_TERMINEE,
                horloge.getTempsSimule(),
                commande.getId(),
                client.getNom()
        ));
    }


    // ===================== Méthodes de création =====================

    public Client creerClient(int id, String nom, int patienceInitiale) {
        return new Client(id, nom, patienceInitiale);
    }

    public Commande creerCommandePourClient(Client client) {
        return new Commande(client);
    }
}