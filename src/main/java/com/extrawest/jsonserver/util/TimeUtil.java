package com.extrawest.jsonserver.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeUtil {

    @SneakyThrows(InterruptedException.class)
    public static void sleep(long l) {
        Thread.sleep(l);
    }

    public static void waitOneSecond() {
        sleep(1000);
    }

    public static void waitHalfSecond() {
        sleep(500);
    }

}
