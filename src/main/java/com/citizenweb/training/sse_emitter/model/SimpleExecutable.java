package com.citizenweb.training.sse_emitter.model;

import com.citizenweb.training.taskslifecyclemanager.model.Executable;

public interface SimpleExecutable<R> extends Executable<R> {

    void waitSomeTime(long waitingMillis);
    void printSomething(String message);

}
