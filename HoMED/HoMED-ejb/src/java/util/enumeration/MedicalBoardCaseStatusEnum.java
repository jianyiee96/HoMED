/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

/**
 *
 * @author User
 */
public enum MedicalBoardCaseStatusEnum {
    WAITING, //No date, not assigned medical board entity
    SCHEDULED, //Assigned medical board entity, upcoming
    COMPLETED //Completed
}
