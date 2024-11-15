package com.jw;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.slf4j.Slf4j;

@QuarkusMain
@Slf4j
public class OrderApplication {

    public static void main(String... args) {
        // REVIEW-VINI.MD: [minor] I think we can remove it, we will have other logs that will let us know that the app started
        log.info("Starting Quarkus.Main");
        Quarkus.run(args);
    }
}
