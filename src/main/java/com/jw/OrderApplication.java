package com.jw;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.quarkus.logging.Log;


@QuarkusMain
public class OrderApplication {

    public static void main(String... args) {
        Log.info("Starting Quarkus.Main");
        Quarkus.run(args);
    }
}
