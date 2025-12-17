# **Rush au Restaurant – Projet Final 420-311** ✅ **COMPLET**

**Simulateur Java d'un restaurant pendant le rush du déjeuner.**  
**Évaluation** : Projet fonctionnel, testé avec les scénarios fournis.

---

## **Détails du Projet**

- **Nom de l'étudiant** : Alben Matonde
- **DA** : 2288532
- **Cours** : 420-311 – Structures de données
- **Professeur** : Sara Boumehraz
- **Date de soumission** : 16 décembre 2025

---

## **Technologies et organisation du code**

- **Langage** : Java 21
- **Gestion de projet** : Maven
- **IDE utilisé** : IntelliJ IDEA

---

## **Fonctionnalités implémentées**

- Lecture séquentielle des actions à partir d’un fichier texte (arrivées, commandes, avance du temps, affichage d’état, statistiques, etc.).
- **Gestion des clients avec suivi de la patience** :
  - La patience diminue au fil du temps pendant l’attente.
  - Un client devient fâché lorsque sa patience tombe à 0.
  - Sinon, il devient servi lorsque sa commande est prête.
- **Gestion des commandes** :
  - Association d’une commande à un client.
  - File d’attente des commandes à préparer.
  - États d’une commande (en attente, en préparation, prête, perdue).
- **Thread Cuisinier** qui :
  - Récupère la prochaine commande dans la file.
  - Démarre la préparation.
  - Décrémente le temps restant.
  - Marque la commande comme terminée et notifie le restaurant.
- **Calcul des statistiques finales** :
  - Nombre total de clients.
  - Nombre de servis et de fâchés.
  - Chiffre d’affaires.
  - Nombre de plats vendus par type.
- **Production d’un log structuré** :
  - Résumé de l’état à chaque **AFFICHER_ETAT**.
  - Détail des clients (état, patience, plats).
  - Événements significatifs (arrivées, début/fin de commande, clients fâchés).
  - Statistiques complètes à **AFFICHER_STATS**.

---

## **Description du Projet**

Ce programme simule le fonctionnement d'un restaurant pendant le rush du midi :

- **Clients** : Ils arrivent avec une patience limitée.
- **Commandes** : Les clients peuvent commander plusieurs plats : PIZZA🍕, BURGER🍔, FRITES🍟.
- **Cuisinier** : Un thread concurrent gère la préparation des commandes dans l'ordre.
- **Simulation du temps** : La méthode `tick()` fait avancer le temps, réduisant à la fois la patience des clients et le temps de préparation des plats.
- **État des clients** : Certains clients sont servis avec satisfaction, d'autres quittent le restaurant fâchés.
- **Statistiques** : Le programme suit le chiffre d'affaires, le nombre de clients servis ou fâchés, et les plats vendus.
- **Sortie des données** : Les logs sont générés conformément au format demandé dans un fichier de sortie.

---

## **Organisation du Code**

L'architecture du projet est structurée comme suit :

mv.sdd/
├── App.java # Point d'entrée de l'application
├── io/ # Gestion des actions depuis les fichiers
│ ├── ActionFileReader.java
│ ├── ActionParser.java
│ └── ActionType.java
├── model/ # Modèles des entités du restaurant
│ ├── Client.java
│ ├── Commande.java
│ ├── Stats.java (EnumMap plats)
│ ├── Horloge.java
│ └── ...
├── sim/ # Logique de la simulation du restaurant
│ └── Restaurant.java # Gestion des états et synchronisation
└── sim.thread/ # Gestion de la concurrence avec le cuisinier
└── Cuisinier.java # Thread Runnable pour le cuisinier
└── utils/ # Outils divers pour les logs, formatage, constantes
├── Logger.java
├── Formatter.java # Formatage des logs pour les clients
└── Constantes.java


---

## **Instructions d'Exécution**

1. **Compilation**  
   Pour compiler le projet, utilise la commande suivante :

   ```bash
   mvn clean package
   Cela génère un fichier .jar dans le répertoire https://github.com/Alben11/Epreuve_finale_420_311_Matonde.git

2. Lancer l'application
Pour exécuter l'application avec Maven :
mvn exec:java -Dexec.mainClass="mv.sdd.App" -Dexec.args="data/scenario_1.txt data/sortie_1_essaie.txt"

3. Consulter le fichier de sortie (ex. data/sortie_1_essaie.txt) pour voir le déroulement complet du service (états intermédiaires et statistiques).

## **Travail réalisé par rapport au squelette**

À partir du squelette fourni, les éléments suivants ont été complétés ou ajoutés :

- Implémentation de la logique complète de gestion du temps via `tick()` dans **Restaurant**.
- Implémentation de la diminution de patience et du changement d’état (en attente → servi ou parti fâché).
- Implémentation de la file de commandes et des méthodes de support :
  - Création de commandes,
  - Ajout de plats,
  - Passage en préparation,
  - Marquage des commandes prêtes ou perdues.
- Implémentation du thread **Cuisinier** avec synchronisation (`synchronized`, `wait`, `notifyAll`) pour :
  - Attendre lorsqu’il n’y a aucune commande,
  - Se réveiller quand une nouvelle commande arrive.
- Calcul et mise à jour des statistiques à chaque événement pertinent.
- Utilisation systématique du **Logger** et du **Formatter** pour produire un log conforme au format attendu.

## **Scénarios de test**

Le dossier **`data/`** contient :

- Des scénarios fournis (par exemple **`scenario_1.txt`**, **`scenario_2.txt`**) permettant de valider la conformité avec les sorties d’exemple.
- Éventuellement des scénarios de test personnels utilisés pour vérifier des cas particuliers (clients très impatients, plusieurs commandes en parallèle, etc.).

Chaque ligne de scénario correspond à une action (ex. **DEMARRER_SERVICE**, **AJOUTER_CLIENT**, **PASSER_COMMANDE**, **AVANCER_TEMPS**, **AFFICHER_ETAT**, **AFFICHER_STATS**).

---

## **Limites et améliorations possibles**

- La durée de préparation des plats est actuellement fixée par les constantes de l’énoncé ; elle pourrait être rendue configurable par fichier.
- D’autres stratégies de gestion de la file (priorités, tri par attente, etc.) pourraient être ajoutées.
- Une interface graphique minimale ou une visualisation en temps réel pourrait enrichir la simulation.


