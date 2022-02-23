package evgeniy.datapack.extension.plugin.recipebook;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RecipePlaceStrategy(boolean canCraft,
                                  int maxStackSize,
                                  @NotNull List<Transaction> transactions) {

    public static class Transaction {

        private final UnlimitedStackedItem stack;
        private final int count;

        private final boolean empty;

        public Transaction() {
            this(null, 0, true);
        }

        public Transaction(UnlimitedStackedItem stack, int count) {
            this(stack, count, false);
        }

        private Transaction(final UnlimitedStackedItem stack, final int count, final boolean empty) {
            this.stack = stack;
            this.count = count;
            this.empty = empty;
        }

        public UnlimitedStackedItem getStack() {
            if (isEmpty()) throw new UnsupportedOperationException();
            return stack;
        }

        public int getCount() {
            return count;
        }

        public boolean isEmpty() {
            return empty;
        }

        @Override
        public String toString() {
            return "Transaction{count=%d, stack=%s}".formatted(count, stack);
        }

    }

}
