(defproject clink "0.1.0-SNAPSHOT"
  :description "All the examples in Flink Documentation, but in Clojure"
  :url "https://vedang.me/techlog"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.apache.flink/flink-connector-kafka-0.10_2.11 "1.8.0"]
                 [org.apache.flink/flink-core "1.8.0"]
                 [org.apache.flink/flink-streaming-java_2.11 "1.8.0"]
                 [org.clojure/clojure "1.10.0"]]
  :global-vars {*warn-on-reflection* true}
  :java-source-paths ["src/main/java"]
  :aot [clink.streaming.examples.socket.socket-window-wordcount
        clink.streaming.examples.kafka.read-from-kafka
        clink.streaming.examples.kafka.write-into-kafka]
  :main clink.streaming.examples.socket.socket-window-wordcount)
