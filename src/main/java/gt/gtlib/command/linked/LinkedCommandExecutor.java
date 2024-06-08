package gt.gtlib.command.linked;

import gt.gtlib.command.Arg;
import gt.gtlib.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@FunctionalInterface
@SuppressWarnings({"unchecked", "unused"})
public interface LinkedCommandExecutor extends CommandExecutor {

    default List<Arg<?>> getArgs() {
        return List.of();
    }

    static LinkedCommandExecutor of(BiFunction<CommandSender, Object[], Boolean> handle, Arg<?>... args) {
        return new LinkedCommandExecutor() {
            @Override
            public boolean execute(CommandSender sender, Object... values) {
                return handle.apply(sender, values);
            }

            @Override
            public List<Arg<?>> getArgs() {
                return List.of(args);
            }
        };
    }

    static <U> LinkedCommandExecutor ofDynamic(LinkedCommandExecutorDynamicArg dynamicArg) {
        return ((s, v) -> dynamicArg.apply(new DynamicArguments(s, v)));
    }

    static LinkedCommandExecutor of(LinkedCommandExecutorNoArg executorNoArg) {
        return ((s, v) -> executorNoArg.apply());
    }

    static LinkedCommandExecutor of(LinkedCommandExecutorZeroArg supplier) {
        return ((s, v) -> supplier.apply(s));
    }

    static <U> LinkedCommandExecutor of(Arg<U> c1, LinkedCommandExecutorOneArg<U> supplier) {
        return of((s, v) -> supplier.apply(s, (U) v[0]), c1);
    }

    static <U, V, R> LinkedCommandExecutor of(Arg<U> c1, Arg<V> c2, LinkedCommandExecutorTwoArg<U, V> supplier) {
        return of((s, v) -> supplier.apply(s, (U) v[0], (V) v[1]), c1, c2);
    }

    static <U, V, S, R> LinkedCommandExecutor of(Arg<U> c1, Arg<V> c2, Arg<S> c3, LinkedCommandExecutorThreeArg<U, V, S> supplier) {
        return of((s, v) -> supplier.apply(s, (U) v[0], (V) v[1], (S) v[2]), c1, c2, c3);
    }

    static <U, V, S, K, R> LinkedCommandExecutor of(Arg<U> c1, Arg<V> c2, Arg<S> c3, Arg<K> c4, LinkedCommandExecutorFourArg<U, V, S, K> supplier) {
        return of((s, v) -> supplier.apply(s, (U) v[0], (V) v[1], (S) v[2], (K) v[3]), c1, c2, c3, c4);
    }

    static <U, V, S, K, L, R> LinkedCommandExecutor of(Arg<U> c1, Arg<V> c2, Arg<S> c3, Arg<K> c4, Arg<L> c5, LinkedCommandExecutorFiveArg<U, V, S, K, L> supplier) {
        return of((s, v) -> supplier.apply(s, (U) v[0], (V) v[1], (S) v[2], (K) v[3], (L) v[4]), c1, c2, c3, c4, c5);
    }

    static <U, V, S, K, L, E, R> LinkedCommandExecutor of(Arg<U> c1, Arg<V> c2, Arg<S> c3, Arg<K> c4, Arg<L> c5, Arg<E> c6, LinkedCommandExecutorSixArg<U, V, S, K, L, E> supplier) {
        return of((s, v) -> supplier.apply(s, (U) v[0], (V) v[1], (S) v[2], (K) v[3], (L) v[4], (E) v[5]), c1, c2, c3, c4, c5, c6);
    }

    static <U, V, S, K, L, E, M, R> LinkedCommandExecutor of(Arg<U> c1, Arg<V> c2, Arg<S> c3, Arg<K> c4, Arg<L> c5, Arg<E> c6, Arg<M> c7, LinkedCommandExecutorSevenArg<U, V, S, K, L, E, M> supplier) {
        return of((s, v) -> supplier.apply(s, (U) v[0], (V) v[1], (S) v[2], (K) v[3], (L) v[4], (E) v[5], (M) v[6]), c1, c2, c3, c4, c5, c6, c7);
    }

    static <U, V, S, K, L, E, M, N, R> LinkedCommandExecutor of(Arg<U> c1, Arg<V> c2, Arg<S> c3, Arg<K> c4, Arg<L> c5, Arg<E> c6, Arg<M> c7, Arg<N> c8, LinkedCommandExecutorEightArg<U, V, S, K, L, E, M, N> supplier) {
        return of((s, v) -> supplier.apply(s, (U) v[0], (V) v[1], (S) v[2], (K) v[3], (L) v[4], (E) v[5], (M) v[6], (N) v[7]), c1, c2, c3, c4, c5, c6, c7, c8);
    }

    static <U, V, S, K, L, E, M, N, O, R> LinkedCommandExecutor of(Arg<U> c1, Arg<V> c2, Arg<S> c3, Arg<K> c4, Arg<L> c5, Arg<E> c6, Arg<M> c7, Arg<N> c8, Arg<O> c9, LinkedCommandExecutorNineArg<U, V, S, K, L, E, M, N, O> supplier) {
        return of((s, v) -> supplier.apply(s, (U) v[0], (V) v[1], (S) v[2], (K) v[3], (L) v[4], (E) v[5], (M) v[6], (N) v[7], (O) v[8]), c1, c2, c3, c4, c5, c6, c7, c8, c9);
    }

    class DynamicArguments<S extends CommandSender> {
        private final S sender;
        private final Object[] arguments;

        public DynamicArguments(S sender, Object[] arguments) {
            this.sender = sender;
            this.arguments = arguments;
        }

        public S getSender() {
            return sender;
        }

        public boolean isSenderPlayer() {
            return sender instanceof Player;
        }

        public Player getPlayerSender() {
            if (isSenderPlayer()) {
                return (Player) sender;
            }
            throw new IllegalStateException("Sender is not a player.");
        }

        public Object atIndex(int idx) {
            return arguments[idx];
        }

        public String getString(int idx) {
            return getAt(idx, String.class);
        }

        public double getDouble(int idx) {
            return getAt(idx, Double.class);
        }

        public int getInt(int idx) {
            return getAt(idx, Integer.class);
        }

        public <T> T getAt(int index, Class<T> type) {
            Objects.checkIndex(index, arguments.length);
            return (T) atIndex(index);
        }
    }

    interface LinkedCommandExecutorDynamicArg {
        boolean apply(DynamicArguments args);
    }

    interface LinkedCommandExecutorNoArg {
        boolean apply();
    }

    interface LinkedCommandExecutorZeroArg {
        boolean apply(CommandSender sender);
    }

    interface LinkedCommandExecutorOneArg<U> {
        boolean apply(CommandSender sender, U v1);
    }

    interface LinkedCommandExecutorTwoArg<U, V> {
        boolean apply(CommandSender sender, U v1, V v2);
    }

    interface LinkedCommandExecutorThreeArg<U, V, S> {
        boolean apply(CommandSender sender, U v1, V v2, S v3);
    }

    interface LinkedCommandExecutorFourArg<U, V, S, K> {
        boolean apply(CommandSender sender, U v1, V v2, S v3, K v4);
    }

    interface LinkedCommandExecutorFiveArg<U, V, S, K, L> {
        boolean apply(CommandSender sender, U v1, V v2, S v3, K v4, L v5);
    }

    interface LinkedCommandExecutorSixArg<U, V, S, K, L, E> {
        boolean apply(CommandSender sender, U v1, V v2, S v3, K v4, L v5, E v6);
    }

    interface LinkedCommandExecutorSevenArg<U, V, S, K, L, E, M> {
        boolean apply(CommandSender sender, U v1, V v2, S v3, K v4, L v5, E v6, M v7);
    }

    interface LinkedCommandExecutorEightArg<U, V, S, K, L, E, M, N> {
        boolean apply(CommandSender sender, U v1, V v2, S v3, K v4, L v5, E v6, M v7, N v8);
    }

    interface LinkedCommandExecutorNineArg<U, V, S, K, L, E, M, N, O> {
        boolean apply(CommandSender sender, U v1, V v2, S v3, K v4, L v5, E v6, M v7, N v8, O v9);
    }
}
