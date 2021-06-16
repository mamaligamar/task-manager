package com.company.taskmanager.model;

import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Is the class that represents a Process in the system.
 */
@Getter public class Process implements Runnable {
  private static Logger log = LoggerFactory.getLogger(Process.class);

  private final AtomicBoolean isRunning = new AtomicBoolean(false);
  private Thread thread;

  private final long pid;
  private final PriorityType priority;

  public Process(long pid, PriorityType priority) {
    this.pid = pid;
    this.priority = priority;
  }

  public void start() {
    this.thread = new Thread(this);
    this.thread.start();
  }

  public void stop() {
    this.isRunning.set(false);
    this.thread.interrupt();
  }

  public boolean isRunning() {
    return this.isRunning.get();
  }

  @Override
  public void run() {
    this.isRunning.set(true);
    log.info("Running process [pid={}]", this.pid);
    while (this.isRunning.get()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.debug("Thread execution interrupted [pid={}]", this.pid);
        this.thread.interrupt();
      }
    }
  }

  @Override
  public String toString() {
    StringJoiner stringJoiner = new StringJoiner(",", "[", "]");
    stringJoiner.add("pid=" + pid + ",priority=" + priority.getDescription());
    return this.getClass().getSimpleName() + stringJoiner.toString();
  }
}
