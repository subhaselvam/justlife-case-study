package com.justlife.cleaning.exception;

public class BusinessRuleViolationException extends RuntimeException {
  private String errorCode;
  private String fieldName;

  public BusinessRuleViolationException(String message) {
    super(message);
    this.errorCode = "BUSINESS_RULE_VIOLATION";
  }

  public BusinessRuleViolationException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public BusinessRuleViolationException(String errorCode, String message, String fieldName) {
    super(message);
    this.errorCode = errorCode;
    this.fieldName = fieldName;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getFieldName() {
    return fieldName;
  }
}
