(defproject clink "0.1.0-SNAPSHOT"
  :description "All the examples in Flink Documentation, but in Clojure"
  :url "https://helpshift.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.flink/flink-java "1.2.0"]
                 [org.apache.flink/flink-streaming-java_2.10 "1.2.0"]
                 [org.apache.flink/flink-clients_2.10 "1.2.0"]]
  :global-vars {*warn-on-reflection* true}
  :aot [clink.socket-window-wordcount]
  :main clink.socket-window-wordcount)
