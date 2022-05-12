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
            // Filling the queue on first call by pulling all underlying elements
            if (index == 0) // first call
                while (super.tryAdvance(queue::add)); // should call onWrappedSpliteratorFullyTraversed() once finished
            // If the queue is empty or if we reached the last element, we exit
            if (index >= queue.size())
                return false;
            // Otherwise, we pass the next element to the passed action
            action.accept(queue.get(index++)); // and increment the index by the way for next call
            return true;
        }

        @Override
        void onWrappedSpliteratorFullyTraversed() {
            // Now that the queue is fully populated, we can sort it
            queue.sort(comparator); // with the provided comparator
        }
    }
}
