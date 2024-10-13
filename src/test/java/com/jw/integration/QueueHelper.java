package com.jw.integration;

import static com.jw.TestHelper.asString;

import com.jw.dto.processed.ProductReservationResult;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Slf4j
@ApplicationScoped
public class QueueHelper {

    @Channel("processed-products")
    @Broadcast
    Emitter<byte[]> productReservationResultEmitter;

    public void sentToProcessedProducts(ProductReservationResult product) {
        productReservationResultEmitter.send(asString(product).getBytes(StandardCharsets.UTF_8));
    }
}
