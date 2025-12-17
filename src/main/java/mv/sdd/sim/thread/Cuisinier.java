// Cuisinier.java
package mv.sdd.sim.thread;

import mv.sdd.model.Commande;
import mv.sdd.sim.Restaurant;

public class Cuisinier implements Runnable {

    private final Restaurant restaurant;
    private volatile boolean enService = true;

    public Cuisinier(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void arreter() {
        enService = false;
    }

    @Override
    public void run() {
        while (enService) {
            Commande cmd = restaurant.retirerProchaineCommande();
            if (cmd != null) {
                restaurant.demarrerPreparationCommande(cmd);
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
