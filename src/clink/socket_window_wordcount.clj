(ns clink.socket-window-wordcount
  (:gen-class)
  (:require [clojure.string :as cs])
  (:import [clink.examples.wordcount IWordWithCount WordWithCount]
           org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
           org.apache.flink.streaming.api.windowing.time.Time))

(defn- get-flinky-word-counter
  []
  (reify
    IWordWithCount
    (flatMap [this value out]
      (doseq [word (cs/split value #"\s")]
        (println "Word: " word)
        (.collect out
                  (WordWithCount. word 1))))

    (getKey [this word-with-count]
      (.word ^WordWithCount word-with-count))

    (reduce [this a b]
      (WordWithCount. (.word ^WordWithCount a)
                      (+ (.wcount ^WordWithCount a)
                         (.wcount ^WordWithCount b))))))
(defn -main
  [& args]
  (let [execution-env (StreamExecutionEnvironment/getExecutionEnvironment)
        text (.socketTextStream execution-env
                                "localhost"
                                9000
                                "\n")
        ^IWordWithCount word-counter-helper (get-flinky-word-counter)
        window-counts (.. text
                          (flatMap word-counter-helper)
                          (keyBy word-counter-helper)
                          (timeWindow (Time/seconds 5))
                          (reduce word-counter-helper))]
    (.. window-counts
        print
        (setParallelism 1))
    (.execute execution-env "Socket Window WordCount")))
