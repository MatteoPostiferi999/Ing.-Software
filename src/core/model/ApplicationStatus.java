package core.model;
// ApplicationStatus.java
// package core.model;

/**
 * Stati possibili di un’Application di una guida per un viaggio.
 */
public enum ApplicationStatus {
    PENDING,   // appena inviata
    ACCEPTED,  // approvata dall’agenzia
    REJECTED   // rifiutata dall’agenzia
}