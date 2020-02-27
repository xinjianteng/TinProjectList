package com.tin.projectlist.app.library.base.frame.http.rx.function;


import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 * @author chenhx
 * @package : com.cliff.libs.http.rx.function
 * @description :
 * @date 2018/9/19
 */
public class CustomRetryFunction implements Function<Observable<? extends Throwable>, Observable<?>> {

    private static final int RECHECK_TIMES = 2;
    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) {
        return observable.zipWith(Observable.range(1, RECHECK_TIMES + 1), Wrapper.createFunction())
                .flatMap(wrapper -> {
                    boolean overRetry = wrapper.index > RECHECK_TIMES;
                    if (overRetry) {
                        return Observable.error(wrapper.throwable);
                    }
                    return Observable.error(wrapper.throwable);
                });
    }

    private static class Wrapper {

        private int index;
        private Throwable throwable;

        public Wrapper(Throwable throwable, int index) {
            this.index = index;
            this.throwable = throwable;
        }

        public static BiFunction<Throwable, Integer, Wrapper> createFunction() {
            return (throwable, integer) -> new Wrapper(throwable, integer);
        }
    }
}
