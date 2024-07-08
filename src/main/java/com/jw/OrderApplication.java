package com.jw;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class OrderApplication {

    public static void main(String... args) {
        Log.info("Starting Quarkus.Main");
        Quarkus.run(args);
    }
}
