package com.company.taskmanager.service;

import com.company.taskmanager.model.PriorityType;
import com.company.taskmanager.model.Process;
import com.company.taskmanager.model.SortingType;
import java.util.List;

public interface TaskManagerService {
  /**
   * Add a process - adds new processes till when there is capacity inside the Task Manager,
   * otherwise we won’t accept any new process and will through a IndexOutOfBoundsException.
   *
   * @param priority {@link PriorityType} the priority of the process that will be added.
   * @throws IndexOutOfBoundsException
   */
  public void add(PriorityType priority);
  /**
   * Add a process - accepts all new processes killing and removing from the TM list the oldest one
   * (First-In, First-Out) when the max size is reached.
   *
   * @param priority {@link PriorityType} the priority of the process that will be added.
   */
  public void addToFifo(PriorityType priority);

  /**
   * Adds a process – If the max size is reached it is evaluated if the new process passed in the
   * add() call has a higher priority compared to any of the existing one, we remove the lowest
   * priority that is the oldest, otherwise we skip it.
   *
   * @param priority {@link PriorityType} the priority of the process that will be added.
   */
  public void addWithPriority(PriorityType priority);

  /**
   * List running processes sorting them by time of creation (implicitly we can consider it the time
   * in which has been added to the TM), priority or id.
   *
   * @param type {@link SortingType} the type of sorting we want to apply.
   * @return
   */
  public List<Process> listAll(SortingType type);

  /**
   * Kills a specific process.
   *
   * @param pid Process id.
   */
  public void kill(long pid);

  /**
   * Kills all processes with a specific priority.
   *
   * @param priority {@link PriorityType} the priority of the process that will be added.
   */
  public void killAll(PriorityType priority);

  /**
   * Kills all running processes
   */
  public void killAll();
}
