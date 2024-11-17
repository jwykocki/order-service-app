package com.jw;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.slf4j.Slf4j;

@QuarkusMain
@Slf4j
public class OrderApplication {

    public static void main(String... args) {
        Quarkus.run(args);
    }
}
