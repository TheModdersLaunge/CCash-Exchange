package variant;

import java.util.Optional;

public class Variant<T1, T2> {
    private Optional<T1> value1;
    private Optional<T2> value2;

    private Variant(T1 a, T2 b) {
        value1 = Optional.of(a);
        value2 = Optional.of(b);
    }

    public static <T1, T2> Variant<T1, T2> ofT1(T1 value) {
        return new Variant(value, null);
    }

    public static <T1, T2> Variant<T1, T2> ofT2(T2 value) {
        return new Variant(null, value);
    }

    public boolean isValue1() {
        return value1.isPresent();
    }

    public boolean isValue2() {
        return value2.isPresent();
    }

    public T1 getValue1() {
        if (value1.isEmpty()) {
            throw new IllegalStateException("No value of type T1 present");
        }
        return value1.get();
    }

    public T2 getValue2() {
        if (value2.isEmpty()) {
            throw new IllegalStateException("No value of type T2 present");
        }
        return value2.get();
    }
}