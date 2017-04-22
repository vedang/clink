(ns clink.socket-window-wordcount
  (:gen-class)
  (:require [clojure.string :as cs])
  (:import [clink.examples.wordcount IWordWithCount WordWithCount]
           org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
           org.apache.flink.streaming.api.windowing.time.Time))

(defn -main
  [& args]
  (let [execution-env (StreamExecutionEnvironment/getExecutionEnvironment)
        text (.socketTextStream execution-env
                                "localhost"
                                9000
                                "\n")
        window-counts (.. text
                          (flatMap (reify
                                     IWordWithCount
                                     (flatMap [this value out]
                                       (doseq [word (cs/split value #"\s")]
                                         (println "Word: " word)
                                         (.collect out
                                                   (WordWithCount. word 1))))))
                          (keyBy (reify
                                   IWordWithCount
                                   (getKey [this word-with-count]
                                     (.word ^WordWithCount word-with-count))))
                          (timeWindow (Time/seconds 5))
                          (reduce (reify
                                    IWordWithCount
                                    (reduce [this a b]
                                      (WordWithCount. (.word ^WordWithCount a)
                                                      (+ (.wcount ^WordWithCount a)
                                                         (.wcount ^WordWithCount b)))))))]
    (.. window-counts
        print
        (setParallelism 1))
    (.execute execution-env "Socket Window WordCount")))
