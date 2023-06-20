package com.xh.flink;

import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.connector.sink.Sink;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.datagen.table.DataGenConnectorOptions;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.time.Duration;

public class Converting {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        DataStream<String> dataStream = env.fromElements("Alice", "Bob", "John");
        Table inputTable = tEnv.fromDataStream(dataStream);

        tEnv.createTemporaryView("InputTable", inputTable);
        Table resultTable = tEnv.sqlQuery("select upper(f0) from InputTable");
        DataStream<Row> resultStream = tEnv.toDataStream(resultTable);
        resultStream.print();

        tEnv.createTemporaryTable("SourceTable", TableDescriptor.forConnector("datagen")
                .schema(Schema.newBuilder()
                        .column("f0", DataTypes.INT())
                        .column("f1",DataTypes.TIME())
                        .build())
                .option(DataGenConnectorOptions.ROWS_PER_SECOND, 1l)
                .build());

        Table resultTable2 = tEnv.sqlQuery("select f0, f1 from SourceTable");

        SingleOutputStreamOperator<String> resultStream2 = tEnv.toDataStream(resultTable2).map(record ->
        {
            Object f0 = record.getField(0);
            Object f1 = record.getField(1);
            return  f0 + " "+ f1;
        });
        resultStream2.print();

        final FileSink<String> sink = FileSink
                .forRowFormat(new Path("src\\main\\resources\\"), new SimpleStringEncoder<String>("UTF-8"))
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(Duration.ofMinutes(1))
                                .withInactivityInterval(Duration.ofMinutes(5))
                                .withMaxPartSize(MemorySize.ofMebiBytes(1024))
                                .build())
                .build();

        resultStream2.sinkTo(sink);

        env.execute();
    }
}
