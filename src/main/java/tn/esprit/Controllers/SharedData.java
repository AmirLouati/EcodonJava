package tn.esprit.Controllers;

import tn.esprit.entities.Association;
import tn.esprit.entities.Formation;

public class SharedData {
    public static int formationid;

    public static int getSelectedFormationId() {
        return selectedFormationId;
    }

    public static void setSelectedFormationId(int selectedFormationId) {
        SharedData.selectedFormationId = selectedFormationId;
    }

    private static int selectedFormationId;

    public static Association currentAssociation;


    public static Association getCurrentAssociation() {
        return currentAssociation;
    }

    public static void setCurrentAssociation(Association currentAssociation) {
        SharedData.currentAssociation = currentAssociation;
    }

    public static int getFormationid() {
        return formationid;
    }

    public static void setFormationid(int formationid) {
        SharedData.formationid = formationid;
    }
}
