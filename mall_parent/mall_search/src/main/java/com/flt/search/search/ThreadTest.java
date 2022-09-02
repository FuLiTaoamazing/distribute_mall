package com.flt.search.search;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest {
    static final ExecutorService service = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        System.out.println("main start ");
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            System.out.println("当前线程:" + Thread.currentThread().getName());
            int i = 10 / 2;
            System.out.println("运行结果:" + i);
        }, service);
        System.out.println("mian end");

    }
}
