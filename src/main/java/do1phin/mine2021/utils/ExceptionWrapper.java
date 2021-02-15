package do1phin.mine2021.utils;

import java.util.function.Supplier;

public class ExceptionWrapper {

    public static <R> R getOrDefault(Supplier<R> f, R defaultValue) {
        try {
            return f.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
