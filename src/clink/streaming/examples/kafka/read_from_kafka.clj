(ns clink.streaming.examples.kafka.read-from-kafka
  (:gen-class)
  (:import java.util.Properties
           org.apache.flink.api.common.restartstrategy.RestartStrategies
           org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
           org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
           org.apache.flink.streaming.util.serialization.SimpleStringSchema))

(defn -main
  [& args]
  (let [env (doto (StreamExecutionEnvironment/getExecutionEnvironment)
              ;; checkpoint every 5s
              (.enableCheckpointing 5000))
        config (doto (.getConfig env)
                 (.disableSysoutLogging)
                 (.setRestartStrategy
                  ;; 4 retries, 10s between retries
                  (RestartStrategies/fixedDelayRestart 4 10000)))
        properties (doto (Properties.)
                     (.setProperty "bootstrap.servers" "localhost:9092")
                     (.setProperty "group.id" "clink_consumers"))
        consumer (FlinkKafkaConsumer010. "flinkyclinky"
                                         (SimpleStringSchema.)
                                         properties)
        message-stream (.addSource env consumer)]
    (.print message-stream)
    (.execute env "Read From Kafka Example")))
