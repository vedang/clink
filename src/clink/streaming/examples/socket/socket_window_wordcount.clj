(ns clink.streaming.examples.socket.socket-window-wordcount
  (:gen-class)
  (:refer-clojure :exclude [reduce])
  (:require [clojure.string :as cs])
  (:import [clink.examples.wordcount IWordWithCount WordWithCount]
           org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
           org.apache.flink.streaming.api.windowing.time.Time))

(defrecord WordCounter
    [^String word ^Long wcount]
  :load-ns true
  IWordWithCount
  (flatMap [_ value collector]
    (doseq [w (cs/split value #"\s")]
      (println "Word: " w)
      (.collect collector
                (WordCounter. w 1))))

  (getKey [_ word-with-count]
    (.word ^WordCounter word-with-count))

  (reduce [_ a b]
    (WordCounter. (.word ^WordCounter a)
                  (+ (.wcount ^WordCounter a)
                     (.wcount ^WordCounter b))))

  Object
  (toString [_]
    (str word ": " wcount)))


(defn -main
  [& args]
  (let [execution-env (StreamExecutionEnvironment/getExecutionEnvironment)
        text (.socketTextStream execution-env
                                "localhost"
                                9000
                                "\n")
        ^IWordWithCount word-counter-helper (WordCounter. "<begin>" 1)
        window-counts (.. text
                          (flatMap word-counter-helper)
                          (keyBy word-counter-helper)
                          (timeWindow (Time/seconds 5))
                          (reduce word-counter-helper))]
    (.. window-counts
        print
        (setParallelism 1))
    (.execute execution-env "Socket Window WordCount Example")))
