package dev.services.generator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class IdGenerator {

    ReentrantLock lock = new ReentrantLock();
    int count = 0;


    public int getAndIncrement(){

        lock.lock();

        try{
            return count++;
        } finally {
            lock.unlock();
        }

    }



}
