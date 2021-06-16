package com.company.taskmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Contains process available priorities.
 */
@AllArgsConstructor
@Getter
public enum PriorityType {
  LOW("low", 1), MEDIUM("medium", 2), HIGH("high", 3);

  private String description;
  private int precedence;

  @Override
  public String toString() {
    return this.getDescription();
  }
}
