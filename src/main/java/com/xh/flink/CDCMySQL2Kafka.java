package com.xh.flink;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class CDCMySQL2Kafka {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("hadoop")
                .port(3306)
                .databaseList("test") // set captured database
                .tableList("test.runoob_tbl") // set captured table
                .username("root")
                .password("assiduity")
                //.serverTimeZone("Asia/Shanghai")
                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
                .build();

        env
                .fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
                .print().setParallelism(1);


        env.execute("xxxxxxxxx");
    }
}
