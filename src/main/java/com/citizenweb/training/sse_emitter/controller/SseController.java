package com.citizenweb.training.sse_emitter.controller;

import com.citizenweb.training.sse_emitter.model.SimpleExecutable;
import com.citizenweb.training.taskslifecyclemanager.exceptions.TaskExecutionException;
import com.citizenweb.training.taskslifecyclemanager.model.*;
import com.citizenweb.training.taskslifecyclemanager.services.DataStream;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@RestController
@RequestMapping(value = "sse")
@Log4j2
public class SseController {

    private DataStream dataStream;

    public SseController() {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2);
        executorService.scheduleWithFixedDelay( () -> {
            Executable<String> executable1 = new SimpleExecutable<>() {
                @Override
                public void waitSomeTime(long waitingMillis) {
                    try {
                        Thread.sleep(waitingMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void printSomething(String message) {
                    log.info(message);
                }

                @Override
                public String execute() {
                    waitSomeTime(2000);
                    printSomething("Actually building a set of tasks in controller");
                    return "TASK #1 RESULT";
                }
            };
            Task<String> task1 = new Task<>("TASK 1", executable1);

            Executable<String> executable2 = new SimpleExecutable<>() {
                @Override
                public void waitSomeTime(long waitingMillis) {
                    try {
                        Thread.sleep(waitingMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void printSomething(String message) {
                    log.info(message);
                }

                @Override
                public String execute() {
                    waitSomeTime(1000);
                    printSomething("Actually building a set of tasks in controller");
                    return "TASK #2 RESULT";
                }
            };
            Task<String> task2 = new Task<>("TASK 2", executable2);

            Executable<String> executable3 = new SimpleExecutable<>() {
                @Override
                public void waitSomeTime(long waitingMillis) {
                    try {
                        Thread.sleep(waitingMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void printSomething(String message) {
                    log.info(message);
                }

                @Override
                public String execute() {
                    waitSomeTime(3000);
                    printSomething("Actually building a set of tasks in controller");
                    return "TASK #3 RESULT";
                }
            };
            Task<String> task3 = new Task<>("TASK 3", executable3);

            Executable<String> executable4 = new SimpleExecutable<>() {
                @Override
                public void waitSomeTime(long waitingMillis) {
                    try {
                        Thread.sleep(waitingMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void printSomething(String message) {
                    log.info(message);
                }

                @Override
                public String execute() {
                    waitSomeTime(2000);
                    printSomething("Actually building a set of tasks in controller");
                    return "TASK #4 RESULT";
                }
            };
            Task<String> task4 = new Task<>("TASK 4", executable4);

            Executable<String> executable5 = new SimpleExecutable<>() {
                @Override
                public void waitSomeTime(long waitingMillis) {
                    try {
                        Thread.sleep(waitingMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void printSomething(String message) {
                    log.info(message);
                }

                @Override
                public String execute() {
                    waitSomeTime(1500);
                    printSomething("Actually building a set of tasks in controller");
                    return "TASK #5 RESULT";
                }
            };
            Task<String> task5 = new Task<>("TASK 5", executable5);

            Set<Task<?>> tasks = new HashSet<>();
            task2.withPredecessor(task1);
            task3.withPredecessor(task1);
            task4.withPredecessor(task2);
            task4.withPredecessor(task3);
            task5.withPredecessor(task3);
            tasks.add(task1);
            tasks.add(task2);
            tasks.add(task3);
            tasks.add(task4);
            tasks.add(task5);

            LifecycleManager lifeCycle;
            try {
                lifeCycle = TasksLifecycleManager.createLifecycle("Lifecycle 1", tasks, AsapTaskPlanner.class);
                this.dataStream = lifeCycle.getDataStream();
                lifeCycle.execute();
            } catch (TaskExecutionException e) {
                e.printStackTrace();
            }
        },1,10, TimeUnit.SECONDS);
    }

    @GetMapping(value = "lifecycle", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> getLifeCycleSSE() {
        if (this.dataStream != null) {
            log.info("SENDING DATA TO UI");
            return dataStream.streamData();
        }
        return null;
    }

    @GetMapping(value = "single-event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<ServerSentEvent<?>> getSingleLifeCycleSSE() {
        return new ResponseEntity<>(this.dataStream.streamSingleData(), HttpStatus.OK);
    }

}
