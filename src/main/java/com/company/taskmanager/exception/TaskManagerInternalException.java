package com.company.taskmanager.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskManagerInternalException {
  MAX_CAPACITY_EXCEPTION(1001, "MAX_CAPACITY_EXCEPTION");

  private int code;
  private String exception;

  @Override
  public String toString() {
    return Integer.toString(code);
  }
}
