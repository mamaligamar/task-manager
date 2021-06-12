package com.company.taskmanager.service.impl;

import com.company.taskmanager.model.SortingType;
import com.company.taskmanager.model.PriorityType;
import com.company.taskmanager.model.Process;
import com.company.taskmanager.service.TaskManagerService;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskManagerServiceImpl implements TaskManagerService {

  private static final int FIXED_CAPACITY = 4;
  private final static List<Process> tasks;
  private static long pidSeqNumber = 0;

  //TODO Refactor to allow parallel execution
  static {
    tasks = new Vector<>(FIXED_CAPACITY);
  }

  public TaskManagerServiceImpl() {
  }

  public void add(PriorityType priority) throws IndexOutOfBoundsException {
    Objects.requireNonNull(priority);
    if (tasks.size() == FIXED_CAPACITY) {
      log.error("This element will not be processed [capacity={}]", tasks.size());
      throw new IndexOutOfBoundsException("No more capacity in task manager");
    }
    add(new Process(nextPid(), priority));
  }

  public void addToFifo(PriorityType priority) {
    Objects.requireNonNull(priority);
    final Process newProcess = new Process(nextPid(), priority);
    if (tasks.size() < FIXED_CAPACITY) {
      add(newProcess);
    } else {
      log.warn("Maximum Task Manager capacity reached [capacity={}]", tasks.size());
      log.info("Removing [element={}]", newProcess);
      kill(tasks.get(0).getPid());
      add(newProcess);
    }
  }

  public void addWithPriority(PriorityType priority) {
    Objects.requireNonNull(priority);
    final Process newProcess = new Process(nextPid(), priority);

    if (tasks.size() < FIXED_CAPACITY) {
      add(newProcess);
    } else {
      log.warn("Maximum Task Manager capacity reached [capacity={}]", tasks.size());
      AtomicBoolean added = new AtomicBoolean(false);
      Collections.reverse(tasks); // TODO search for a queue??
      tasks.forEach(currentElement -> {
        boolean isLowerPriority = newProcess.getPriority().getPrecedence() > currentElement.getPriority().getPrecedence();
        if (isLowerPriority && !added.get()) {
          kill(currentElement.getPid());
          added.set(true);
          add(newProcess);
        }
      });
    }
  }

  public List<Process> listAll(SortingType type) {
    Objects.requireNonNull(type);
    log.info("List all processes by {}", type.name());

    if (SortingType.PRIORITY == type) {
      tasks.sort(Comparator.comparing(process -> process.getPriority().getPrecedence()));
    }

    if (SortingType.ID == type) {
      tasks.sort(Comparator.comparing(Process::getPid));
    }

    // The tasks queue is already ordered by creation or insertion time
    return tasks;
  }

  public void kill(long pid) {
    log.info("Kill process [pid={}]", pid);
    Optional<Process> processToKill = tasks.stream().filter(process -> process.getPid() == pid)
        .findAny();
    if (processToKill.isPresent()) {
      processToKill.get().stop();
      tasks.remove(processToKill.get());
    } else {
      log.warn("Nothing will be removed, the specified process does not exist [pid={}]", pid);
    }
  }

  public void killAll(PriorityType priority) {
    Objects.requireNonNull(priority);
    log.info("Kill processes with priority " + priority);
    List<Process> processList = tasks.stream()
        .filter(process -> priority.getPrecedence() == process.getPriority().getPrecedence())
        .collect(Collectors.toList());
    log.info("Processes to be killed: " + processList);
    processList.forEach(process -> process.stop());
    tasks.removeAll(processList);
  }

  public void killAll() {
    log.info("Killing all the exsiting processes: " + tasks);
    tasks.forEach(process -> process.stop());
    tasks.clear();
  }

  private static synchronized long nextPid() {
    return ++pidSeqNumber;
  }

  private void add(Process newProcess) {
    tasks.add(newProcess);
    newProcess.start();
  }
}
