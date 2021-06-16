package com.company.taskmanager.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum TaskManagerExternalException {
  INTERNAL_ERROR("INTERNAL_ERROR", "No more processes can be managed");

  private String code;
  private String message;

}
