package com.company.taskmanager.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class TaskManagerException extends Exception {

  private String code;
  private TaskManagerInternalException innerException;

  public TaskManagerException(TaskManagerExternalException externalException, TaskManagerInternalException innerException) {
    super(externalException.getMessage());
    this.code = externalException.getCode();
    this.innerException = innerException;
  }
}
