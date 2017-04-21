(ns clink.socket-window-wordcount
  (:gen-class)
  (:require [clojure.string :as cs])
  (:import [org.apache.flink.api.common.functions FlatMapFunction ReduceFunction]
           org.apache.flink.api.java.functions.KeySelector
           org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
           org.apache.flink.streaming.api.windowing.time.Time))

(defrecord WordWithCount
    [word wcount]
  Object
  (toString [this]
    (str word ": " wcount)))

(defrecord WordKeySelector
    []
  KeySelector
  (getKey [this value]
    (:word value)))

(defn -main
  [& args]
  (let [execution-env (StreamExecutionEnvironment/getExecutionEnvironment)
        text (.socketTextStream execution-env
                                "localhost"
                                9000
                                "\n")
        window-counts (.. text
                          (flatMap (reify
                                     FlatMapFunction
                                     (flatMap [this value out]
                                       (doseq [word (cs/split value #"\s")]
                                         (.collect out
                                                   (WordWithCount. word 1))))))
                          (returns clink.socket_window_wordcount.WordWithCount)
                          (keyBy (WordKeySelector.))
                          (timeWindow (Time/seconds 5))
                          (reduce (reify
                                    ReduceFunction
                                    (reduce [this a b]
                                      (WordWithCount. (:word a)
                                                      (+ (:wcount a)
                                                         (:wcount b)))))))]
    (.. window-counts
        print
        (setParallelism 1))
    (.execute execution-env "Socket Window WordCount")))
