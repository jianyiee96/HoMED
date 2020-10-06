package util.enumeration;

public enum EmployeeRoleEnum {
    SUPER_USER("Super User"),
    CLERK("Clerk"),
    MB_ADMIN("Medical Board Admin"),
    MEDICAL_OFFICER("Medical Officer");

    private String stringVal;

    private EmployeeRoleEnum(String stringVal) {
        this.stringVal = stringVal;
    }

    public String getStringVal() {
        return stringVal;
    }

}
