/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

public enum InputTypeEnum {
    TEXT("Text", "Requires text input from user"),
    HEADER("Header", "Header desciption. No input required"),
    NUMBER("Number", "Requires number input from user. Up to 2 decimal points"),
    DATE("Date", "Requires date input from user. Date format: dd/mm/yyyy"),
    TIME("Time", "Requires time input from user. Time format: HH:MM"),
    RADIO_BUTTON("Radio Button", "Requires user to select up to one radio option"),
    CHECK_BOX("Check Box", "Requires user to select multiple check box options"),
    SINGLE_DROPDOWN("Single Dropdown", "Requires user to select up to one dropdown option"),
    MULTI_DROPDOWN("Multi Dropdown", "Requires user to select multiple dropdown options");
//    FILE_UPLOAD("File Upload", "Requires user to upload file documents. File types will be specified in the future"),
//    IMAGE_UPLOAD("Image Upload", "Requires user to upload image documents. File types will be specified in the future");

    private String inputTypeString;
    private String description;

    private InputTypeEnum(String inputTypeString, String description) {
        this.inputTypeString = inputTypeString;
        this.description = description;
    }

    public String getInputTypeString() {
        return inputTypeString;
    }

    public String getDescription() {
        return this.description;
    }
}
