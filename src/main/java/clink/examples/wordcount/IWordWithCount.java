package clink.examples.wordcount;

import clink.examples.wordcount.WordWithCount;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;

public interface IWordWithCount extends FlatMapFunction<String,WordWithCount>, ReduceFunction<WordWithCount>, KeySelector<WordWithCount,String> {
}
