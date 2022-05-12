package dev.webfx.lib.reusablestream.impl;

import dev.webfx.lib.reusablestream.Spliterable;

import java.util.Spliterator;

/**
 * @author Bruno Salmon
 */
final class ResumableOperator<T> extends Operator<T, T> {

    private Spliterator<T> singleOperation;

    ResumableOperator(Spliterable<T> wrappedSpliterable) {
        super(wrappedSpliterable);
    }

    @Override
    public Spliterator<T> spliterator() {
        if (singleOperation == null)
            singleOperation = newOperation();
        return singleOperation;
    }

    @Override
    ResumeOperation<T> newOperation() {
        return new ResumeOperation<>();
    }

    private final class ResumeOperation<_T extends T> extends Operation<_T, _T> {

        @Override
        void pushBackLastElement() {
            pushBackRequested = true;
        }
    }

}
