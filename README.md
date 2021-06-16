# Task Manager
1. [Requirements](#requirements)
2. [Technology choices](#technology-choices)
3. [Implementation decisions](#implementation-decisions)
4. [Testing](#testing)
5. [Improvements](#improvements)
6. [How to run the application](#how-to-run-the-application)

## Requirements
Task Manager is a software component that is designed for handling multiple processes inside an operating system. 
Each process is identified by 2 fields, a unique unmodifiable identifier (PID), and a priority (low, medium, high). 
The process is immutable, it is generated with a priority and will die with this priority – each process has a kill() method that will destroy it.

The Task Manager must expose the following **functionality**:
* Add a process - three different behaviours should be implemented:
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

* Kill/KillGroup/KillAll should perform the following:
  * killing a specific process
  * killing all processes with a specific priority
  * killing all running processes
  
## Technology choices
- **Java** - Java v1.8 was selected as the preferred language as my work experience is based in the last 6 years in Java.
- **Maven** - Maven was selected as a build automation tool.
- Libraries:
    - **Lombok** - was selected in order to avoid the boilerplate getters, setters and constructors code in the DTOs.
    - **Junit** - Junit was selected as unit testing framework.
    - **Awaitility** - Awaitility was selected to ease the unit testing with threads.

## Implementation decisions
### Exceptions
**TaskManagerException** is the application business exception which is composed by two pieces:
* The *external exception* which contains the app code and message that would be sent outside the task manager. 
  This app code and message will allow us to hide/mask sensitive information in productive applications.
* The *internal exception* which contains an internal debug code which is useful mostly for developers.

### Data model
The data model of the application consists of the following elements:
- **PriorityType** - Is an enum which contains all the available processes priorities. 
- **Process** - Is the class that represents a Process in the system and implements the java Runnable interface which requires to implement the run method.
  Also, this class implements some basic methods as start and stop the worker thread that is executed inside.
  
  The *process lifetime is indefinite* until we kill the process. This decision was made in order to allow to test properly the application. However, in a 
  productive environment, the Process class constructor should accept a Callable parameter containing the code that should be executed by that process.
- **SortingType** - Is an enum which contains all the possible ways to sort the list of processes inside the task manager.

### Service
- **TaskManagerService** is the interface that exposes the available Task Manager operations.
- **TaskManagerServiceImpl** contains the whole logic exposed in the requirements. The most notable implementation details of this class are the following:
  * The **maximum capacity** of the Task Manager is determined in build time as required, and it's **4**. It's a low number in order to allow running and test easily the whole functionality.
    In a productive environment this number should be configured depending on the available cores in our hardware, also monitored and tuned in consequence.
  * The collection of **processes** inside the task manager is represented by a Java **CopyOnWriteArrayList** which allows concurrent modifications in the task manager by making a fresh copy of the underlying array.
  The decision of using a list instead of a queue was made because some operations need the *insertion order of the elements*, and java lists preserve that order.
  * The *process unique id* is determined by a *synchronized counter* which is incremented each time we create a new process.
  * **Add operations**: there are *three* different of them because each of them has a very different functionality 
    regarding the processing of elements when the task manager maximum capacity is achieved. 
    Each one of the add methods creates the process id and that's why these three methods return the **Process** instead of void.
  * **List operations**: *one* list *operation* sorts the processes inside the task manager depending on the sorting type specified by parameter. The list by priority operation shows the processes in ascendant order.
  * **Kill operations**: there are *three* different of them because each kill method has a very different functionality
    regarding the processing of elements.
    
## Testing
- **Unit tests** were implemented with **Junit** and **awaitility** for TaskManagerServiceImpl class which holds the logic of the application.
- Another kind of tests, such as integration, contract or api test weren't implemented as the application does not have a database or communication with third parties.

## Improvements
- Try to rethink the structure of the *Process* with the thread inside and maybe separate them achieving to have the process isolated from the thread.
- Provide a log4j2.xml file in order to establish a format for the logs, a rotation strategy and a retention policy.
- Test the algorithm accessing the service in parallel from different threads.
- Obtain more details about business requirements and improve the implementation.
- Provide a proper UI instead of the command line.
- Make Process class to run provided code/task by parameter.

## How to run the application
For running the application it's required a *git* and *jdk* installation and possibly *maven* if you want to regenerate the jar file.

After the installation of the previously mentioned tools you can clone and run the application following the next steps from a **command line**:
1. Clone the project from github:
    ```
    git clone https://github.com/mamaligamar/task-manager.git
    ```

2. Now go inside */task-manager* folder and execute the *task-manager.jar* (last generated jar for this app):
    ```
    java -cp task-manager-app.jar com.company.taskmanager.TaskManagerApplication
    ```
   Then the application will guide you through the console output and will present you the available operations.

   You can also specify the whole path to the jar from another directory in order to execute the application.

3. If you prefer to rebuild the jar, execute inside */task-manager* folder:
    ```
    mvn clean install
    ```
   Then go to */task-manager/target* folder and repeat the step 2 of this section.
