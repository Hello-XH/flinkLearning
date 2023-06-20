package com.xh.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import scala.Int;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;

public class BroadcastTest {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // env.getCheckpointConfig().setCheckpointingMode(111);
        Tuple2<String, Integer> john = new Tuple2<>("john", 23);
        Tuple2<String, Integer> tom = new Tuple2<>("tom", 24);
        Tuple2<String, Integer> shiny = new Tuple2<>("shiny", 22);

        // DataSource<Tuple2<String, Integer>> broadcastData = env.fromElements(john, tom, shiny);
        DataStreamSource<Tuple2<String, Integer>> broadcastData = env.fromElements(john, tom, shiny);

//        broadcastData.assignTimestampsAndWatermarks(WatermarkStrategy.<Tuple2<String, Integer>>forBoundedOutOfOrderness(Duration.ofSeconds(20))
//                .withTimestampAssigner((event, timestamp) -> timestamp));


        DataStreamSource<String> ds1 = env.fromElements("john", "tom", "shiny");

        ds1.map(new RichMapFunction<String, String>() {
            List<Tuple2<String, Integer>> bc;
            HashMap<String, Integer> map = new HashMap<>();

            @Override
            public void open(Configuration parameters) throws Exception {
                this.bc = getRuntimeContext().getBroadcastVariable("broadcastData");
                for (Tuple2<String, Integer> tp : bc) {
                    this.map.put(tp.f0, tp.f1);
                }
            }

            @Override
            public String map(String input) throws Exception {
                Integer age = this.map.get(input);
                return input + "->" + age;
            }
        });
    }
}
