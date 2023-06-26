package com.xh.flink;

import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.runtime.util.ExecutorThreadFactory;

import java.time.Instant;

public class BatchJdbc2Jdbc {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        tEnv.executeSql("CREATE TABLE IF NOT EXISTS `runoob_tbl`(\n" +
                "   `runoob_id` INT,\n" +
                "   `runoob_title` VARCHAR(100) NOT NULL,\n" +
                "   `runoob_author` VARCHAR(40) NOT NULL,\n" +
                "   `submission_date` VARCHAR(40),\n" +
                "   PRIMARY KEY ( `runoob_id` ) NOT ENFORCED\n" +
                ") WITH (\n" +
                "   'connector' = 'jdbc',\n" +
                "   'username' = 'root',\n" +
                "   'password' = 'assiduity',\n" +
                "   'url' = 'jdbc:mysql://hadoop:3306/test',\n" +
                "   'table-name' = 'runoob_tbl'\n" +
                ")");

//        tEnv.executeSql("CREATE TABLE IF NOT EXISTS `runoob_tbl2`(\n" +
//                "   `runoob_id` INT,\n" +
//                "   `runoob_title` VARCHAR(100) NOT NULL,\n" +
//                "   `runoob_author` VARCHAR(40) NOT NULL,\n" +
//                "   `submission_date` VARCHAR(40),\n" +
//                "   PRIMARY KEY ( `runoob_id` ) NOT ENFORCED\n" +
//                ") WITH (\n" +
//                "   'connector' = 'jdbc',\n" +
//                "   'driver' = 'com.mysql.cj.jdbc.Driver',\n" +
//                "   'username' = 'root',\n" +
//                "   'password' = 'assiduity',\n" +
//                "   'url' = 'jdbc:mysql://hadoop:3306/test?rewriteBatchedStatements=true',\n" +
//                "   'table-name' = 'runoob_tbl2'\n" +
//                ")");

        tEnv.sqlQuery("select * from runoob_tbl").execute().print();
        Table resultTable = tEnv.sqlQuery("select * from runoob_tbl");

        //tEnv.createTemporaryView("result",table);
        //tEnv.executeSql("insert into runoob_tbl2 select runoob_id,runoob_title,runoob_author,submission_date from runoob_tbl");

        DataStream<User> ds = tEnv.toDataStream(resultTable, DataTypes.STRUCTURED(
                User.class,
                DataTypes.FIELD("runoob_id", DataTypes.INT()),
                DataTypes.FIELD("runoob_title", DataTypes.STRING()),
                DataTypes.FIELD("runoob_author", DataTypes.STRING()),
                DataTypes.FIELD("submission_date", DataTypes.STRING())));

        ds.addSink(
                JdbcSink.sink(
                        "insert into runoob_tbl2 (runoob_id, runoob_title, runoob_author, submission_date) values (?, ?, ?, ?)",
                        (statement, user) -> {
                            statement.setInt(1, user.runoob_id);
                            statement.setString(2, user.runoob_title);
                            statement.setString(3, user.runoob_author);
                            statement.setString(4, user.submission_date);
                        },
                        JdbcExecutionOptions.builder()
                                .withBatchSize(1000)
                                .withBatchIntervalMs(200)
                                .withMaxRetries(5)
                                .build(),
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl("jdbc:mysql://hadoop:3306/test?rewriteBatchedStatements=true")
                                .withDriverName("com.mysql.cj.jdbc.Driver")
                                .withUsername("root")
                                .withPassword("assiduity")
                                .build()
                ));

        env.execute("xxxxxxx");
    }

    public static class User {
        public User(int runoob_id, String runoob_title, String runoob_author, String submission_date) {
            this.runoob_id = runoob_id;
            this.runoob_title = runoob_title;
            this.runoob_author = runoob_author;
            this.submission_date = submission_date;
        }

        public final int runoob_id;
        public final String runoob_title;
        public final String runoob_author;
        public final String submission_date;
    }
}


