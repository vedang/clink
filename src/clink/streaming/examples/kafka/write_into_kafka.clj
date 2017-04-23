(ns clink.streaming.examples.kafka.write-into-kafka
  (:gen-class)
  (:import java.util.Properties
           clink.examples.kafka.KafkaRunningSource
           org.apache.flink.api.common.restartstrategy.RestartStrategies
           org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
           org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010
           org.apache.flink.streaming.util.serialization.SimpleStringSchema))

;; (defrecord RunningSource [running-atom]
;;   IStringSourceFunction
;;   (run [this ctx]
;;     (loop [i 0]
;;       (when @running-atom
;;         (.collect ctx (str "Element: " i))
;;         (Thread/sleep 1000)
;;         (recur (inc i)))))
;;   (cancel [this]
;;     (swap! running-atom false)))

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
                     (.setProperty "bootstrap.servers" "localhost:9092"))
        ;; source (RunningSource. (atom true))
        source (KafkaRunningSource.)
        sink (FlinkKafkaProducer010. "flinky"
                                     (SimpleStringSchema.)
                                     properties)
        message-stream (.addSource env source)]
    (.addSink message-stream sink)
    (.execute env "Write Into Kafka Example")))
