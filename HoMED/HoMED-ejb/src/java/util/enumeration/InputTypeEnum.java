/*
 * Project Title: Home Team Medical Board
 * Project Application: HoMED-ejb
 */
package util.enumeration;

public enum InputTypeEnum {
    TEXT("Text"),
    HEADER("Header"),
    NUMBER("Number"),
    DATE("Date"),
    TIME("Time"),
    RADIO_BUTTON("Radio Button"),
    CHECK_BOX("Check Box"),
    SINGLE_DROPDOWN("Single Dropdown"),
    MULTI_DROPDOWN("Multi Dropdown"),
    FILE_UPLOAD("File Upload"),
    IMAGE_UPLOAD("Image Upload");

    private String inputTypeString;

    private InputTypeEnum(String inputTypeString) {
        this.inputTypeString = inputTypeString;
    }

    public String getInputTypeString() {
        return inputTypeString;
    }
}
