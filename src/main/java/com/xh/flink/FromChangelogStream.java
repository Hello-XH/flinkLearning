package com.xh.flink;

import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import java.time.Duration;

public class FromChangelogStream {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        DataStream<Row> dataStream  = env.fromElements(
                Row.ofKind(RowKind.INSERT,"Alice",12),
                Row.ofKind(RowKind.INSERT,"Bob",5),
                Row.ofKind(RowKind.UPDATE_BEFORE,"Alice",12),
                Row.ofKind(RowKind.UPDATE_AFTER,"Alice",100)
        );

        Table table = tEnv.fromChangelogStream(dataStream);

        tEnv.createTemporaryView("InputTable",table);

        tEnv
                .executeSql("select f0 as name, SUM(f1) AS score FROM InputTable GROUP BY f0")
        .print();







    }
}
