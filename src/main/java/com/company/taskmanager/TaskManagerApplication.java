package com.company.taskmanager;

import com.company.taskmanager.exception.TaskManagerException;
import com.company.taskmanager.model.SortingType;
import com.company.taskmanager.model.PriorityType;
import com.company.taskmanager.model.Process;
import com.company.taskmanager.service.impl.TaskManagerServiceImpl;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TaskManagerApplication {

  private static final TaskManagerServiceImpl taskqueue = new TaskManagerServiceImpl();
  private static boolean keepRunning = true;

  public static void main(String[] args) {
    System.out.println("Welcome to Task Manager");
    System.out.println(
        "To interact with the app introduce the NUMBER of the LISTED ACTIONS and press ENTER");

    while (keepRunning) {
      try {
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.println("Available actions");
        System.out.println("1 - Add");
        System.out.println("2 - List");
        System.out.println("3 - Kill");
        System.out.println("4 - Exit the application");
        Integer operation = scanner.nextInt();

        if (operation == 1) {
          manageAddOperation(scanner);
        } else if (operation == 2) {
          manageListOperation(scanner);
        } else if (operation == 3) {
          manageKillOperation(scanner);
        } else if (operation == 4) {
          manageKillAppOperation();
        } else {
          System.out.println("This operation is not available");
        }

      } catch (InputMismatchException exception) {
        System.out.println("The provided option is not available");
      }
    }
  }

  private static void manageAddOperation(Scanner scanner) {
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
      try {
        addedProcess = taskqueue.add(priorityType);
        System.out.println("Added process: " + addedProcess);
      } catch (TaskManagerException exception) {
        System.out
            .println("MAX CAPACITY achieved - no more elements can be added to the task manager");
      }
    } else if (addVersion == 2) {
      addedProcess = taskqueue.addToFifo(priorityType);
      System.out.println("Added process: " + addedProcess);
    } else if (addVersion == 3) {
      addedProcess = taskqueue.addWithPriority(priorityType);
      if (addedProcess == null) {
        System.out.println("The process can not be inserted");
      } else {
        System.out.println("Added process: " + addedProcess);
      }
    } else {
      System.out.println("The specified add version is not supported");
    }
  }

  private static void manageListOperation(Scanner scanner) {
    System.out.println("Specify how to sort the output");
    System.out.println("1 - Creation time");
    System.out.println("2 - Priority");
    System.out.println("3 - Id");

    Integer listVersion = scanner.nextInt();
    SortingType sortingType;

    if (listVersion == 1) {
      sortingType = SortingType.CREATION_TIME;
      System.out.println(taskqueue.listAll(sortingType).toString());
    } else if (listVersion == 2) {
      sortingType = SortingType.PRIORITY;
      System.out.println(taskqueue.listAll(sortingType).toString());
    } else if (listVersion == 3) {
      sortingType = SortingType.ID;
      System.out.println(taskqueue.listAll(sortingType).toString());
    } else {
      System.out.println("The specified list version is not supported");
    }
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
      System.out.println("Insert a priority");
      System.out.println("1 - low");
      System.out.println("2 - medium");
      System.out.println("3 - high");
      Integer priority = scanner.nextInt();
      taskqueue.killAll(getPriorityType(priority));
    } else if (killVersion == 3) {
      taskqueue.killAll();
    } else {
      System.out.println("The specified kill version is not supported");
    }
  }

  private static PriorityType getPriorityType(Integer processPriority) {
    PriorityType priorityType = PriorityType.LOW;
    if (processPriority == 2) {
      priorityType = PriorityType.MEDIUM;
    } else if (processPriority == 3) {
      priorityType = PriorityType.HIGH;
    }
    return priorityType;
  }

  private static void manageKillAppOperation() {
    keepRunning = false;
    System.exit(0);
  }
}
