package org.motechproject.whp.migration.v0.exception;


public enum WHPErrorCodeV0 {

    TB_ID_DOES_NOT_MATCH("No such tb id for current treatment"),
    TREATMENT_DETAILS_DO_NOT_MATCH("Transfer In is not supported with change in treatment category"),
    TREATMENT_ALREADY_IN_PROGRESS("Current treatment is already in progress"),
    TREATMENT_ALREADY_PAUSED("Current treatment is already paused"),
    TREATMENT_ALREADY_CLOSED("Current treatment is already closed"),
    TREATMENT_NOT_CLOSED("Current treatment is not closed"),
    NULL_VALUE_IN_SMEAR_TEST_RESULTS("Invalid smear test results : null value"),

    DUPLICATE_CASE_ID("Patient with the same case-id is already registered"),
    DUPLICATE_PROVIDER_ID("Provider with the same provider-id is already registered"),
    CASE_ID_DOES_NOT_EXIST("Invalid case-id. No such patient"),
    NO_EXISTING_TREATMENT_FOR_CASE("Case does not have any current treatment"),
    NULL_VALUE_IN_WEIGHT_STATISTICS("Invalid weight statistics : null value"),
    NULL_VALUE_IN_ADDRESS("Invalid address : null value"),

    FIELD_VALIDATION_FAILED("Field Validation failed"),
    WEB_ACCOUNT_REGISTRATION_ERROR("Error occured while tying to register user");

    private String message;


    WHPErrorCodeV0(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return name() + " | " + getMessage();
    }
}