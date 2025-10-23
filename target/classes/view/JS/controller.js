// controller.js - Contrôleur principal du frontend (pattern State)

/**
 * États possibles de l'application
 */
const AppState = {
    INITIAL: 'initial',
    CARTE_CHARGEE: 'carte_chargee',
    LIVRAISON_CHARGEE: 'livraison_chargee',
    LIVRAISON_CALCULEE: 'livraison_calculee'
};

/**
 * Contrôleur de l'application - Gère l'état et les transitions
 */
class ApplicationController {
    constructor() {
        this.currentState = AppState.INITIAL;
        this.carteChargee = false;
        this.livraisonChargee = false;
        this.livraisonCalculee = false;
        this.listeners = [];
        
        console.log('🎮 [Controller] Initialisation en état:', this.currentState);
    }

    /**
     * Ajoute un listener pour les changements d'état
     * @param {Function} callback - Fonction appelée lors d'un changement d'état
     */
    addStateListener(callback) {
        this.listeners.push(callback);
    }

    /**
     * Notifie tous les listeners du changement d'état
     */
    notifyStateChange() {
        const stateInfo = this.getStateInfo();
        console.log('📢 [Controller] Notification changement d\'état:', stateInfo);
        this.listeners.forEach(listener => listener(stateInfo));
    }

    /**
     * Change l'état courant
     * @param {string} newState - Nouvel état
     */
    setState(newState) {
        const oldState = this.currentState;
        this.currentState = newState;
        console.log(`🔄 [Controller] Transition: ${oldState} → ${newState}`);
        this.notifyStateChange();
    }

    /**
     * Obtient les informations sur l'état actuel
     * @returns {Object} Informations sur l'état
     */
    getStateInfo() {
        return {
            state: this.currentState,
            carteChargee: this.carteChargee,
            livraisonChargee: this.livraisonChargee,
            livraisonCalculee: this.livraisonCalculee,
            canLoadCarte: true, // Toujours possible
            canLoadLivraison: this.carteChargee,
            canCalculate: this.carteChargee && this.livraisonChargee && !this.livraisonCalculee,
            canModify: this.livraisonCalculee
        };
    }

    /**
     * Vérifie si une action est permise dans l'état actuel
     * @param {string} action - Action à vérifier
     * @returns {boolean} True si l'action est permise
     */
    canPerformAction(action) {
        const info = this.getStateInfo();
        switch(action) {
            case 'loadCarte':
                return info.canLoadCarte;
            case 'loadLivraison':
                return info.canLoadLivraison;
            case 'calculate':
                return info.canCalculate;
            case 'modify':
                return info.canModify;
            default:
                return false;
        }
    }

    /**
     * Marque la carte comme chargée
     */
    onCarteLoaded() {
        console.log('✅ [Controller] Carte chargée');
        this.carteChargee = true;
        this.livraisonChargee = false;
        this.livraisonCalculee = false;
        this.setState(AppState.CARTE_CHARGEE);
    }

    /**
     * Marque la livraison comme chargée
     */
    onLivraisonLoaded() {
        if (!this.carteChargee) {
            console.error('❌ [Controller] Impossible de charger une livraison sans carte!');
            throw new Error('Veuillez d\'abord charger un plan de distribution');
        }
        console.log('✅ [Controller] Livraison chargée');
        this.livraisonChargee = true;
        this.livraisonCalculee = false;
        this.setState(AppState.LIVRAISON_CHARGEE);
    }

    /**
     * Marque la livraison comme calculée
     */
    onLivraisonCalculated() {
        if (!this.carteChargee || !this.livraisonChargee) {
            console.error('❌ [Controller] Impossible de calculer sans carte et livraison!');
            throw new Error('Veuillez d\'abord charger un plan et une livraison');
        }
        console.log('✅ [Controller] Livraison calculée');
        this.livraisonCalculee = true;
        this.setState(AppState.LIVRAISON_CALCULEE);
    }

    /**
     * Réinitialise l'état (retour au début)
     */
    reset() {
        console.log('🔄 [Controller] Réinitialisation');
        this.carteChargee = false;
        this.livraisonChargee = false;
        this.livraisonCalculee = false;
        this.setState(AppState.INITIAL);
    }

    /**
     * Affiche l'état actuel dans la console (debug)
     */
    debugState() {
        console.log('🐛 [Controller] État actuel:', this.getStateInfo());
    }
}

// Instance globale du contrôleur
const appController = new ApplicationController();

// Exporter pour utilisation dans d'autres fichiers
if (typeof window !== 'undefined') {
    window.appController = appController;
    window.AppState = AppState;
}