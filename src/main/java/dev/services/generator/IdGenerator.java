package dev.services.generator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class IdGenerator {
    private static IdGenerator instance;

    private IdGenerator() {
    }


    public synchronized static IdGenerator getInstance() {
        if (instance == null) {
            instance = new IdGenerator();
        }
        return instance;
    }

    ReentrantLock lock = new ReentrantLock();
    int count = 1;


    public int getAndIncrement() {

        lock.lock();

        try {
            return count++;
        } finally {
            lock.unlock();
        }

    }


}
