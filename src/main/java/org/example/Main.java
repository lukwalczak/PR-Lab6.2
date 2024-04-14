package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {
    private static class CachingPrimeChecker {
        private final Map<Long, Boolean> cache = new ConcurrentHashMap<>();

        public boolean isPrime(final long x) {
            return cache.computeIfAbsent(x, this::computeIfIsPrime);
        }

        private boolean computeIfIsPrime(long x) {
            System.out.println("\tChecking if " + x + " is a prime number.");
            return computeIfIsPrimeInternal(x);
        }

        private boolean computeIfIsPrimeInternal(long x) {
            final String currentThreadName = Thread.currentThread().getName();
            System.out.printf("\t[%s] Running computation for: %d%n", currentThreadName, x);
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(2));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            if (x < 2) return false;
            for (long i = 2; i * i <= x; i++) {
                if (x % i == 0) return false;
            }
            return true;
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner scanner = new Scanner(System.in);
        CachingPrimeChecker primeChecker = new CachingPrimeChecker();
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        while (true) {
            long[] numbers = new long[4];
            List<Future<Boolean>> results = new ArrayList<>();

            System.out.println("Enter 4 numbers to check:");
            for (int i = 0; i < 4; i++) {
                numbers[i] = scanner.nextLong();
            }

            for (long number : numbers) {
                results.add(executorService.submit(() -> primeChecker.isPrime(number)));
            }

            for (int i = 0; i < 4; i++) {
                boolean isPrime = results.get(i).get();
                System.out.println("Number " + numbers[i] + " is prime: " + isPrime);
            }

            System.out.println("Do you want to continue? (y/n)");
            if (!scanner.next().trim().equalsIgnoreCase("y")) {
                break;
            }
        }

        executorService.shutdown();
        scanner.close();
    }

}