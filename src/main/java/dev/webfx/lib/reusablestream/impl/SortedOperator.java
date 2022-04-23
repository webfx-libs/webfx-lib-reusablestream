package dev.webfx.lib.reusablestream.impl;

import dev.webfx.lib.reusablestream.Spliterable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Bruno Salmon
 */
final class SortedOperator<T> extends Operator<T, T> {

    private final Comparator<? super T> comparator;

    SortedOperator(Spliterable<T> wrappedSpliterable, Comparator<? super T> comparator) {
        super(wrappedSpliterable);
        this.comparator = comparator;
    }

    @Override
    SortedOperation<T> newOperation() {
        return new SortedOperation<>();
    }

    final class SortedOperation<_T extends T> extends Operation<_T, _T> {
        final List<_T> queue = new ArrayList<>();
        int index;

        @Override
        public boolean tryAdvance(Consumer<? super _T> action) {
            if (index == 0)
                while (super.tryAdvance(queue::add));
            if (index >= queue.size())
                return false;
            action.accept(queue.get(index++));
            return true;
        }

        @Override
        void onWrappedSpliteratorFullyTraversed() {
            queue.sort(comparator);
        }
    }
}
