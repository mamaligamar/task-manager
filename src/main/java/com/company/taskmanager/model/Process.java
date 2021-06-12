package com.company.taskmanager.model;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j
public class Process implements Runnable{
  private final AtomicBoolean isRunning = new AtomicBoolean(false);
  private Thread thread;

  private final long pid;
  private final PriorityType priority;

  public Process(long pid, PriorityType priority){
    this.pid = pid;
    this.priority = priority;
  }

  public void start() {
    this.thread = new Thread(this);
    this.thread.start();
  }

  public void stop(){
    this.isRunning.set(false);
    this.thread.interrupt();
  }

  public boolean isRunning(){
    return this.isRunning.get();
  }

  @Override
  public void run() {
    this.isRunning.set(true);
    log.info("Running process [pid={}]", this.pid);
    while (this.isRunning.get()) {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        //Thread.currentThread().interrupt();
        log.error("Thread execution interrupted [pid={}]", this.pid);
        e.printStackTrace();
      }
    }
  }
}
