package com.company.taskmanager.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskManagerExternalException {
  VALIDATION_EXCEPTION("INVALID_INPUT", "Unable to process the input"),
  INTERNAL_ERROR("INTERNAL_ERROR", "No more processes can be managed");

  private String code;
  private String message;

}
