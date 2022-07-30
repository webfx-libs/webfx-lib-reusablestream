package dev.webfx.lib.reusablestream.impl;

import dev.webfx.lib.reusablestream.ReusableStream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Bruno Salmon
 */
public final class ReusableStreamImpl<T> implements ReusableStream<T> {

    private final Iterable<T> iterable;
    private String name; // Used only to ease debugging (the name helps the developer to know what this stream is)

    public ReusableStreamImpl(Iterable<T> iterable) {
        this.iterable = iterable;
    }

    @Override
    public Spliterator<T> spliterator() {
        return iterable.spliterator();
    }

    @Override
    public Iterator<T> iterator() {
        return iterable.iterator();
    }

    @Override
    public ReusableStream<T> filter(Predicate<? super T> predicate) {
        return ReusableStream.create(new FilterOperator<>(this, predicate));
    }

    @Override
    public <R> ReusableStream<R> map(Function<? super T, ? extends R> mapper) {
        return ReusableStream.create(new MapOperator<>(this, mapper));
    }

    @Override
    public <R> ReusableStream<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return ReusableStream.create(new FlatMapOperator<>(this, mapper));
    }

    @Override
    public ReusableStream<T> takeWhile(Predicate<? super T> predicate) {
        return ReusableStream.create(new TakeWhileOperator<>(this, predicate));
    }

    @Override
    public ReusableStream<T> sorted(Comparator<? super T> comparator) {
        return ReusableStream.create(new SortedOperator<>(this, comparator));
    }

    @Override
    public ReusableStream<T> distinct() {
        return ReusableStream.create(new DistinctOperator<>(this));
    }

    @Override
    public ReusableStream<T> resumable() {
        return ReusableStream.create(new ResumableOperator<>(this));
    }

    @Override
    public ReusableStream<T> cache() {
        return ReusableStream.create(new CacheOperator<>(this));
    }

    @Override
    @SafeVarargs
    public final ReusableStream<T> concat(Iterable<? extends T>... iterables) {
        return ReusableStream.create(new ConcatOperator<>(this, iterables));
    }

    @Override
    public ReusableStream<T> name(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public String nextName() {
        String nextName = name;
        if (nextName != null && iterable instanceof ReusableStreamImpl)
            nextName = ((ReusableStreamImpl) iterable).nextName();
        if (nextName == null && iterable instanceof Operator)
            nextName = ((Operator) iterable).nextName();
        return nextName;
    }
}
