package com.example.rxjava;

import com.example.rxjava.scheduler.IOThreadScheduler;
import com.example.rxjava.scheduler.SingleThreadScheduler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RxJavaTest {
    private static final String TEST_START = "🚀 Starting test: ";
    private static final String TEST_PASSED = "✅ Test passed: ";
    private static final String TEST_FAILED = "❌ Test failed: ";

    @BeforeEach
    void setUp() {
        System.out.println("\n--- New Test ---");
    }

    @Test
    @DisplayName("Basic Observable Creation")
    void testCreateAndSubscribe() {
        System.out.println(TEST_START + "Basic Observable Creation");

        List<Integer> results = new ArrayList<>();
        AtomicBoolean completed = new AtomicBoolean(false);

        Observable.<Integer>create(observer -> {
            System.out.println("Emitting values: 1, 2, 3");
            observer.onNext(1);
            observer.onNext(2);
            observer.onNext(3);
            observer.onComplete();
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onNext(Integer item) {
                System.out.println("Received value: " + item);
                results.add(item);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error occurred: " + t.getMessage());
                fail("Unexpected error");
            }

            @Override
            public void onComplete() {
                System.out.println("Stream completed");
                completed.set(true);
            }
        });

        assertEquals(3, results.size());
        assertTrue(completed.get());
        System.out.println(TEST_PASSED + "Basic Observable Creation");
    }

    @Test
    @DisplayName("Map Operator Test")
    void testMapOperator() {
        System.out.println(TEST_START + "Map Operator Test");

        List<Integer> results = new ArrayList<>();

        Observable.<Integer>create(observer -> {
                    System.out.println("Emitting original values: 1, 2");
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onComplete();
                })
                .map(x -> {
                    int result = x * 2;
                    System.out.println("Mapping " + x + " to " + result);
                    return result;
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        System.out.println("Received mapped value: " + item);
                        results.add(item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("Error in map: " + t.getMessage());
                        fail("Unexpected error");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Map operation completed");
                        assertEquals(List.of(2, 4), results);
                    }
                });

        System.out.println(TEST_PASSED + "Map Operator Test");
    }

    @Test
    @DisplayName("Filter Operator Test")
    void testFilterOperator() {
        System.out.println(TEST_START + "Filter Operator Test");

        List<Integer> results = new ArrayList<>();

        Observable.<Integer>create(observer -> {
                    System.out.println("Emitting values: 1, 2, 3, 4, 5");
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onNext(3);
                    observer.onNext(4);
                    observer.onNext(5);
                    observer.onComplete();
                })
                .filter(x -> {
                    boolean keep = x % 2 == 0;
                    System.out.println("Filtering " + x + " - " + (keep ? "kept" : "filtered out"));
                    return keep;
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        System.out.println("Received filtered value: " + item);
                        results.add(item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("Error in filter: " + t.getMessage());
                        fail("Unexpected error");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Filter operation completed");
                        assertEquals(List.of(2, 4), results);
                    }
                });

        System.out.println(TEST_PASSED + "Filter Operator Test");
    }

    @Test
    @DisplayName("SubscribeOn Thread Test")
    void testSubscribeOn() {
        System.out.println(TEST_START + "SubscribeOn Thread Test");
        String mainThread = Thread.currentThread().getName();
        System.out.println("Main thread: " + mainThread);

        Observable.<Integer>create(observer -> {
                    String currentThread = Thread.currentThread().getName();
                    System.out.println("Emission thread: " + currentThread);
                    assertNotEquals(mainThread, currentThread);
                    observer.onNext(1);
                    observer.onComplete();
                })
                .subscribeOn(new IOThreadScheduler())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        System.out.println("Received onNext on thread: " + Thread.currentThread().getName());
                        assertEquals(1, item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("Error in subscribeOn: " + t.getMessage());
                        fail("Unexpected error");
                    }

                    @Override
                    public void onComplete() {
                        String currentThread = Thread.currentThread().getName();
                        System.out.println("Completed on thread: " + currentThread);
                        assertNotEquals(mainThread, currentThread);
                        assertTrue(currentThread.contains("pool-"));
                        System.out.println(TEST_PASSED + "SubscribeOn Thread Test");
                    }
                });
    }

    @Test
    @DisplayName("ObserveOn Thread Test")
    void testObserveOn() {
        System.out.println(TEST_START + "ObserveOn Thread Test");
        String mainThread = Thread.currentThread().getName();
        System.out.println("Main thread: " + mainThread);

        Observable.<Integer>create(observer -> {
                    System.out.println("Emitting on thread: " + Thread.currentThread().getName());
                    observer.onNext(1);
                    observer.onComplete();
                })
                .observeOn(new SingleThreadScheduler())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        String currentThread = Thread.currentThread().getName();
                        System.out.println("Processing onNext on thread: " + currentThread);
                        assertEquals(1, item);
                        assertNotEquals(mainThread, currentThread);
                        assertTrue(currentThread.contains("thread"));
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("Error in observeOn: " + t.getMessage());
                        fail("Unexpected error");
                    }

                    @Override
                    public void onComplete() {
                        String currentThread = Thread.currentThread().getName();
                        System.out.println("Processing complete on thread: " + currentThread);
                        assertNotEquals(mainThread, currentThread);
                        assertTrue(currentThread.contains("thread"));
                        System.out.println(TEST_PASSED + "ObserveOn Thread Test");
                    }
                });
    }

    @Test
    @DisplayName("FlatMap Operator Test")
    void testFlatMapOperator() {
        System.out.println(TEST_START + "FlatMap Operator Test");

        List<String> results = new ArrayList<>();

        Observable.<Integer>create(observer -> {
                    System.out.println("Emitting numbers: 1, 2");
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onComplete();
                })
                .flatMap(number -> {
                    System.out.println("FlatMapping number: " + number);
                    return Observable.<String>create(emitter -> {
                        emitter.onNext("A" + number);
                        emitter.onNext("B" + number);
                        emitter.onComplete();
                    });
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String item) {
                        System.out.println("Received flatMapped value: " + item);
                        results.add(item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("Error in flatMap: " + t.getMessage());
                        fail("Unexpected error");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("FlatMap operation completed");
                        assertEquals(List.of("A1", "B1", "A2", "B2"), results);
                        System.out.println(TEST_PASSED + "FlatMap Operator Test");
                    }
                });
    }

    @Test
    @DisplayName("Error Handling Test")
    void testErrorHandling() {
        System.out.println(TEST_START + "Error Handling Test");

        AtomicBoolean errorReceived = new AtomicBoolean(false);

        Observable.<Integer>create(observer -> {
                    System.out.println("Emitting value then error");
                    observer.onNext(1);
                    observer.onError(new RuntimeException("Test error"));
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        System.out.println("Received value before error: " + item);
                        assertEquals(1, item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("Received error: " + t.getMessage());
                        assertEquals("Test error", t.getMessage());
                        errorReceived.set(true);
                    }

                    @Override
                    public void onComplete() {
                        fail("Should not complete on error");
                    }
                });

        assertTrue(errorReceived.get());
        System.out.println(TEST_PASSED + "Error Handling Test");
    }
}