package util.enumeration;

public enum EmployeeRoleEnum {
    ADMIN("Admin"),
    CLERK("Clerk"),
    MEDICAL_OFFICER("Medical Officer");
    
    private String stringVal;
    
    private EmployeeRoleEnum(String stringVal) {
        this.stringVal = stringVal;
    }

    public String getStringVal() {
        return stringVal;
    }
    
}
