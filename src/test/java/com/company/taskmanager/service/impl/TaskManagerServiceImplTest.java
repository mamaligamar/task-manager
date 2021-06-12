package com.company.taskmanager.service.impl;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

import com.company.taskmanager.exception.TaskManagerException;
import com.company.taskmanager.model.PriorityType;
import com.company.taskmanager.model.Process;
import com.company.taskmanager.model.SortingType;
import java.util.List;
import org.awaitility.Durations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskManagerServiceImplTest {

  private static final TaskManagerServiceImpl underTest = new TaskManagerServiceImpl();

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
    underTest.killAll();
    assertTrue(underTest.listAll(SortingType.CREATION_TIME).size() == 0);
  }

  @Test(expected = NullPointerException.class)
  public void addWithNullInputThrowsException() throws TaskManagerException {
    underTest.add(null);
  }

  @Test(expected = TaskManagerException.class)
  public void addAtMaxCapacityThrowsException() throws TaskManagerException {
    // Given the Task Manager at full capacity
    underTest.add(PriorityType.HIGH);
    underTest.add(PriorityType.MEDIUM);
    underTest.add(PriorityType.LOW);
    underTest.add(PriorityType.HIGH);

    //When adding a new element out of capacity a IndexOutOfBoundsException is thrown
    underTest.add(PriorityType.MEDIUM);
  }

  @Test
  public void add() throws TaskManagerException {
    Process process = underTest.add(PriorityType.HIGH);
    await().atMost(Durations.ONE_MINUTE).until(() -> process.isRunning());

    List<Process> processes = underTest.listAll(SortingType.CREATION_TIME);

    assertTrue(processes.size() == 1);
    assertEquals(processes.get(0).getPid(), process.getPid());
    assertEquals(processes.get(0).getPriority(), process.getPriority());
    assertTrue(process.isRunning());
  }

  @Test(expected = NullPointerException.class)
  public void addFifoWithNullInputThrowsException() {
    underTest.addToFifo(null);
  }

  @Test
  public void addToFifo() {
    Process process = underTest.addToFifo(PriorityType.HIGH);
    await().atMost(Durations.ONE_MINUTE).until(() -> process.isRunning());

    List<Process> processes = underTest.listAll(SortingType.CREATION_TIME);

    assertEquals(processes.get(0).getPid(), process.getPid());
    assertEquals(processes.get(0).getPriority(), process.getPriority());
    assertTrue(process.isRunning());
  }

  @Test
  public void addToFifoAtMaxCapacityRemovesLatestAddsNew() {
    // Given the task manager at full capacity
    underTest.addToFifo(PriorityType.HIGH);
    underTest.addToFifo(PriorityType.LOW);
    underTest.addToFifo(PriorityType.MEDIUM);
    underTest.addToFifo(PriorityType.HIGH);
    List<Process> processes = underTest.listAll(SortingType.CREATION_TIME);

    // The candidate to be removed is the first process that entered the task manager and it is running
    Process processToBeRemoved = processes.get(0);
    assertTrue(processToBeRemoved.isRunning());

    // When add to fifo at max capacity is executed the last element will be removed and the new one
    // added
    Process resultProcess = underTest.addToFifo(PriorityType.LOW);
    await().atMost(Durations.ONE_MINUTE).until(() -> resultProcess.isRunning());

    //ensu
    List<Process> newProcesses = underTest.listAll(SortingType.CREATION_TIME);
    assertFalse(newProcesses.contains(processToBeRemoved));
    assertFalse(processToBeRemoved.isRunning());

    assertTrue(processes.size() == 4);
    // The new element is added as the last element of the list
    assertEquals(processes.get(processes.size() - 1).getPid(), resultProcess.getPid());
    assertEquals(processes.get(processes.size() - 1).getPriority(), resultProcess.getPriority());
    assertTrue(resultProcess.isRunning());
  }


  @Test(expected = NullPointerException.class)
  public void addWithPriorityWithNullInputThrowsException() {
    underTest.addWithPriority(null);
  }

  @Test
  public void addWithPriority() {
    Process process = underTest.addWithPriority(PriorityType.HIGH);
    await().atMost(Durations.ONE_MINUTE).until(() -> process.isRunning());

    List<Process> processes = underTest.listAll(SortingType.CREATION_TIME);

    assertEquals(processes.get(0).getPid(), process.getPid());
    assertEquals(processes.get(0).getPriority(), process.getPriority());
    assertTrue(process.isRunning());
  }

  @Test
  public void addWithPriorityAtMaxCapacityRemovesLatestWithLowPriorityAddsNew() {
    // Given the task manager at full capacity
    underTest.addWithPriority(PriorityType.HIGH);
    Process candidateToBeRemoved = underTest.addWithPriority(PriorityType.LOW);
    underTest.addWithPriority(PriorityType.MEDIUM);
    underTest.addWithPriority(PriorityType.HIGH);

    List<Process> processes = underTest.listAll(SortingType.CREATION_TIME);
    processes.contains(candidateToBeRemoved);
    assertTrue(candidateToBeRemoved.isRunning());

    // The candidate to be removed is the oldest process that entered the task manager with lowest priority
    // When add to fifo at max capacity is executed the last element will be removed and the new one
    // added
    Process resultProcess = underTest.addWithPriority(PriorityType.HIGH);
    await().atMost(Durations.ONE_MINUTE).until(() -> resultProcess.isRunning());

    List<Process> newProcesses = underTest.listAll(SortingType.CREATION_TIME);
    assertFalse(newProcesses.contains(candidateToBeRemoved));
    assertFalse(candidateToBeRemoved.isRunning());

    assertTrue(processes.size() == 4);
    // The new element is added as the last element of the list
    assertEquals(processes.get(processes.size() - 1).getPid(), resultProcess.getPid());
    assertEquals(processes.get(processes.size() - 1).getPriority(), resultProcess.getPriority());
    assertTrue(resultProcess.isRunning());
  }

  @Test
  public void addWithPriorityAtMaxCapacityNoOldestWithLowPrioritySkipAdd() {
    // Given the task manager at full capacity
    underTest.addWithPriority(PriorityType.HIGH);
    underTest.addWithPriority(PriorityType.HIGH);
    underTest.addWithPriority(PriorityType.HIGH);
    underTest.addWithPriority(PriorityType.HIGH);
    List<Process> oldProcesses = underTest.listAll(SortingType.CREATION_TIME);

    // The candidate to be removed is the oldest process that entered the task manager with lowest priority
    // When add to fifo at max capacity is executed the last element will be removed and the new one
    // added
    Process resultProcess = underTest.addWithPriority(PriorityType.HIGH);

    List<Process> newProcesses = underTest.listAll(SortingType.CREATION_TIME);
    assertTrue(newProcesses.size() == 4);
    assertNull(resultProcess);

    assertArrayEquals(oldProcesses.toArray(), newProcesses.toArray());
  }


  @Test(expected = NullPointerException.class)
  public void listAllNullSortingTypeThrowsNullPointerException() {
    underTest.listAll(null);
  }

  @Test
  public void listAllEmptyTaskManagerReturnsAnEmptyList() {
    List<Process> processes = underTest.listAll(SortingType.CREATION_TIME);
    assertTrue(processes.size() == 0);
  }

  @Test
  public void listAllByCreationTimeReturnsTheSameList() throws TaskManagerException {
    Process firstElement = underTest.add(PriorityType.HIGH);
    Process secondElement = underTest.add(PriorityType.MEDIUM);

    List<Process> processes = underTest.listAll(SortingType.CREATION_TIME);
    assertTrue(processes.size() == 2);
    assertEquals(firstElement, processes.get(0));
    assertEquals(secondElement, processes.get(1));
  }

  @Test
  public void listAllByPriority() throws TaskManagerException {
    Process firstElement = underTest.add(PriorityType.LOW);
    Process secondElement = underTest.add(PriorityType.HIGH);
    Process thirdElement = underTest.add(PriorityType.MEDIUM);

    List<Process> processes = underTest.listAll(SortingType.PRIORITY);
    assertTrue(processes.size() == 3);
    assertEquals(secondElement, processes.get(0));
    assertEquals(thirdElement, processes.get(1));
    assertEquals(firstElement, processes.get(2));
  }

  @Test
  public void listAllById() throws TaskManagerException {
    underTest.add(PriorityType.LOW);
    underTest.add(PriorityType.HIGH);
    underTest.add(PriorityType.MEDIUM);

    List<Process> processes = underTest.listAll(SortingType.ID);
    assertTrue(processes.size() == 3);
    assertTrue((int) processes.get(0).getPid() < (int) processes.get(1).getPid());
    assertTrue((int) processes.get(1).getPid() < (int) processes.get(2).getPid());
  }

  @Test
  public void killPidDoesNotExistNoException() {
    underTest.kill(-1);
  }

  @Test
  public void killPid() throws TaskManagerException, InterruptedException {
    // Given one running process in the task manager
    Process process = underTest.add(PriorityType.LOW);
    await().atMost(Durations.ONE_MINUTE).until(() -> process.isRunning());
    assertTrue(process.isRunning());

    List<Process> processes = underTest.listAll(SortingType.CREATION_TIME);
    assertTrue(processes.size() == 1);

    // When
    underTest.kill(process.getPid());

    // Then
    List<Process> newProcesses = underTest.listAll(SortingType.CREATION_TIME);
    assertTrue(newProcesses.size() == 0);
  }

  @Test(expected = NullPointerException.class)
  public void testKillAllWithNullPriority() {
    underTest.killAll(null);
  }

  @Test
  public void testKillAllWithPriority() throws TaskManagerException {
    Process firstProcess = underTest.add(PriorityType.LOW);
    await().atMost(Durations.ONE_MINUTE).until(() -> firstProcess.isRunning());
    assertTrue(firstProcess.isRunning());

    Process secondProcess = underTest.add(PriorityType.HIGH);
    await().atMost(Durations.ONE_MINUTE).until(() -> secondProcess.isRunning());
    assertTrue(secondProcess.isRunning());

    Process thirdProcess = underTest.add(PriorityType.HIGH);
    await().atMost(Durations.ONE_MINUTE).until(() -> thirdProcess.isRunning());
    assertTrue(thirdProcess.isRunning());

    List<Process> processes = underTest.listAll(SortingType.CREATION_TIME);
    assertTrue(processes.size() == 3);

    underTest.killAll(PriorityType.HIGH);

    List<Process> newProcesses = underTest.listAll(SortingType.CREATION_TIME);
    assertTrue(newProcesses.size() == 1);
    assertEquals(newProcesses.get(0), firstProcess);
    assertTrue(firstProcess.isRunning());
    assertFalse(secondProcess.isRunning());
    assertFalse(thirdProcess.isRunning());
  }

  @Test
  public void testKillAll() throws TaskManagerException {
    Process firstProcess = underTest.add(PriorityType.LOW);
    await().atMost(Durations.ONE_MINUTE).until(() -> firstProcess.isRunning());
    assertTrue(firstProcess.isRunning());

    Process secondProcess = underTest.add(PriorityType.HIGH);
    await().atMost(Durations.ONE_MINUTE).until(() -> secondProcess.isRunning());
    assertTrue(secondProcess.isRunning());

    Process thirdProcess = underTest.add(PriorityType.HIGH);
    await().atMost(Durations.ONE_MINUTE).until(() -> thirdProcess.isRunning());
    assertTrue(thirdProcess.isRunning());

    List<Process> processes = underTest.listAll(SortingType.CREATION_TIME);
    assertTrue(processes.size() == 3);

    underTest.killAll();

    List<Process> newProcesses = underTest.listAll(SortingType.CREATION_TIME);
    assertTrue(newProcesses.size() == 0);
    assertFalse(firstProcess.isRunning());
    assertFalse(secondProcess.isRunning());
    assertFalse(thirdProcess.isRunning());
  }
}