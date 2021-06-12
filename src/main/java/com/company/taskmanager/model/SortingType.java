package com.company.taskmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum SortingType {
  CREATION_TIME(1), PRIORITY(2), ID(3);

  private int code;
}
