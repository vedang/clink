package clink.examples.kafka;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class KafkaRunningSource implements SourceFunction<String> {
    private static final long serialVersionUID = 6369260445318862378L;
    public boolean running = true;

    public void run(SourceContext<String> ctx) throws InterruptedException {
        long i = 0;
        while(this.running) {
            ctx.collect("Element - " + i++);
            Thread.sleep(1000);
        }
    }
    public void cancel() {
        running = false;
    }
}
