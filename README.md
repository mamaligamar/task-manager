# IptiQ Challenge
1. [Requirements](#requirements)
2. [Technology choices](#technology-choices)
3. [Implementation decisions](#implementation-decisions)
4. [Technical debt](#technical-debt)
5. [How to run the application](#how-to-run-the-application)

## Requirements
Task Manager is a software component that is designed for handling multiple processes inside an operating system. 
Each process is identified by 2 fields, a unique unmodifiable identifier (PID), and a priority (low, medium, high). 
The process is immutable, it is generated with a priority and will die with this priority – each process has a kill() method that will destroy it.

The Task Manager must expose the following functionality:
* Add a process
    * Add a process (1/3) 

        The task manager should have a prefixed maximum capacity, so it can not have more than a certain number of running processes within itself. 
        This value is defined at build time. The add(process) method in TM is used for it. 
        The default behaviour is that we can accept new processes till when there is capacity inside the Task Manager, otherwise we won’t accept any new process.
    * Add a process – FIFO approach (2/3)
      
        A different customer wants a different behaviour: he’s asking to accept all new processes through the add() method, 
        killing and removing from the TM list the oldest one (First-In, First-Out) when the max size is reached.
    * Add a process – Priority based (3/3)
      
        A new customer is asking something different again, every call to the add() method, when the max size is reached, 
        should result into an evaluation: if the new process passed in the add() call has a higher priority compared to any of the existing one, 
        we remove the lowest priority that is the oldest, otherwise we skip it.
      
* List running processes
  
    The task manager offers the possibility to list() all the running processes, sorting them by time of creation (implicitly we can consider it the time in which has been added to the TM), priority or id.

* Kill/KillGroup/KillAll
  
  Model one or more methods capable of:
  * killing a specific process
  * killing all processes with a specific priority
  * killing all running processes
  

Process implements java Runnable interface because the idea of a task manager is to run some code. The intention is to make possible to specify the code to run.
Design decissions - several adds, one list
As it is requested to fix the queue capacity at build time it was selected a capacity of 4 processes just for make it easier for you to test the program.
Of course, in the real world this number dependes on the cores of our hardware.
The process lifetime was determined in build time too and its 2 minutes (2 minutes of sleeping), for the same reason, make it possible to run and test.
In the real world we would execute real tasks and the Process will execute the according code but not sleep.

If we want to run actual tasks we will need to enable a new callable parameter to the process in order to enable to add the code we want
to run in that process.

Why 8? How many processes may run in the machine