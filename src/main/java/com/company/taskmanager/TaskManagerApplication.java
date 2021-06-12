package com.company.taskmanager;

import com.company.taskmanager.exception.TaskManagerException;
import com.company.taskmanager.model.SortingType;
import com.company.taskmanager.model.PriorityType;
import com.company.taskmanager.model.Process;
import com.company.taskmanager.service.impl.TaskManagerServiceImpl;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class TaskManagerApplication {
  private static final TaskManagerServiceImpl taskqueue = new TaskManagerServiceImpl();
  private static boolean keepRunning = true;

  public static void main(String[] args) throws TaskManagerException {
    System.out.println("Starting the application ...");

    while (keepRunning) {
      Scanner scanner = new Scanner(new InputStreamReader(System.in));
      System.out.println("Available actions");
      System.out.println("1 - Add");
      System.out.println("2 - List");
      System.out.println("3 - Kill");
      Integer operation = scanner.nextInt();

      if (operation == 1) {
        manageAddOperation(scanner);
      }

      if (operation == 2) {
        manageListOperation(scanner);
      }

      if (operation == 3) {
        manageKillOperation(scanner);
      }
      checkAndCloseApp(scanner);
    }
  }

  private static void manageAddOperation(Scanner scanner) throws TaskManagerException {
    System.out.println("Add version:");
    System.out.println("1 - regular");
    System.out.println("2 - fifo");
    System.out.println("3 - priority");
    Integer addVersion = scanner.nextInt();
    System.out.println("Insert the priority of the process:");
    System.out.println("1 - low");
    System.out.println("2 - medium");
    System.out.println("3 - high");
    Integer processPriority = scanner.nextInt();
    PriorityType priorityType = getPriorityType(processPriority);

    Process addedProcess;
    if (addVersion == 1) {
      addedProcess  = taskqueue.add(priorityType);
    } else if (addVersion == 2) {
      addedProcess  = taskqueue.addToFifo(priorityType);
    } else if (addVersion == 3) {
      addedProcess  = taskqueue.addWithPriority(priorityType);
    } else {
      throw new UnsupportedOperationException("The specified add version is not supported");
    }
    System.out.println("Added process: "+addedProcess);
  }

  private static void manageListOperation(Scanner scanner) {
    System.out.println("Specify how to sort the output");
    System.out.println("1 - Creation time");
    System.out.println("2 - Priority");
    System.out.println("3 - Id");

    Integer listVersion = scanner.nextInt();
    SortingType sortingType = SortingType.CREATION_TIME;
    if (listVersion == 2) {
      sortingType = SortingType.PRIORITY;
    } else if (listVersion == 3) {
      sortingType = SortingType.ID;
    }
    List<Process> sortedProcesses = taskqueue.listAll(sortingType);
    System.out.println(sortedProcesses.toString());
  }

  private static void manageKillOperation(Scanner scanner) {
    System.out.println("Kill version:");
    System.out.println("1 - Kill a specific process");
    System.out.println("2 - Kill by priority");
    System.out.println("3 - Kill all");
    Integer killVersion = scanner.nextInt();
    if (killVersion == 1) {
      System.out.println("Insert the process PID");
      Integer pid = scanner.nextInt();
      taskqueue.kill(pid);
    } else if (killVersion == 2) {
      Integer priority = scanner.nextInt();
      taskqueue.killAll(getPriorityType(priority));
    } else if (killVersion == 3) {
      taskqueue.killAll();
    }
  }

  private static PriorityType getPriorityType(Integer processPriority) {
    PriorityType priorityType = PriorityType.LOW;
    if(processPriority == 2) {
      priorityType = PriorityType.MEDIUM;
    } else if(processPriority == 3){
      priorityType = PriorityType.HIGH;
    }
    return priorityType;
  }

  private static void checkAndCloseApp(Scanner scanner) {
    System.out.println("Exit the application?");
    System.out.println("1 - Yes");
    System.out.println("2 - No?");
    Integer exitAppOption = scanner.nextInt();
    if(exitAppOption == 1) {
      keepRunning = false;
    }
  }
}
