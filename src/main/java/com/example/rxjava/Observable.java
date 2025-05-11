package com.example.rxjava;

import java.util.function.Function;
import java.util.function.Predicate;

public class Observable<T> {
    private final ObservableOnSubscribe<T> source;

    private Observable(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
        return new Observable<>(source);
    }

    public void subscribe(Observer<? super T> observer) {
        source.subscribe(observer);
    }

    public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        return new Observable<>(observer ->
                subscribe(new Observer<T>() {
                    @Override public void onNext(T item) {
                        try {
                            observer.onNext(mapper.apply(item));
                        } catch (Exception e) {
                            observer.onError(e);
                        }
                    }
                    @Override public void onError(Throwable t) {
                        observer.onError(t);
                    }
                    @Override public void onComplete() {
                        observer.onComplete();
                    }
                })
        );
    }

    public Observable<T> filter(Predicate<? super T> predicate) {
        return new Observable<>(observer ->
                subscribe(new Observer<T>() {
                    @Override public void onNext(T item) {
                        try {
                            if (predicate.test(item)) {
                                observer.onNext(item);
                            }
                        } catch (Exception e) {
                            observer.onError(e);
                        }
                    }
                    @Override public void onError(Throwable t) {
                        observer.onError(t);
                    }
                    @Override public void onComplete() {
                        observer.onComplete();
                    }
                })
        );
    }

    public <R> Observable<R> flatMap(Function<? super T, ? extends Observable<? extends R>> mapper) {
        return new Observable<>(observer ->
                subscribe(new Observer<T>() {
                    @Override public void onNext(T item) {
                        try {
                            mapper.apply(item).subscribe(new Observer<R>() {
                                @Override public void onNext(R rItem) {
                                    observer.onNext(rItem);
                                }
                                @Override public void onError(Throwable t) {
                                    observer.onError(t);
                                }
                                @Override public void onComplete() {}
                            });
                        } catch (Exception e) {
                            observer.onError(e);
                        }
                    }
                    @Override public void onError(Throwable t) {
                        observer.onError(t);
                    }
                    @Override public void onComplete() {
                        observer.onComplete();
                    }
                })
        );
    }

    public Observable<T> subscribeOn(com.example.rxjava.scheduler.Scheduler scheduler) {
        return new Observable<>(observer ->
                scheduler.execute(() -> source.subscribe(observer))
        );
    }

    public Observable<T> observeOn(com.example.rxjava.scheduler.Scheduler scheduler) {
        return new Observable<>(observer ->
                subscribe(new Observer<T>() {
                    @Override public void onNext(T item) {
                        scheduler.execute(() -> observer.onNext(item));
                    }
                    @Override public void onError(Throwable t) {
                        scheduler.execute(() -> observer.onError(t));
                    }
                    @Override public void onComplete() {
                        scheduler.execute(() -> observer.onComplete());
                    }
                })
        );
    }
}