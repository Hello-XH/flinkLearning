package com.xh.flink;

import org.apache.flink.table.api.*;

import static org.apache.flink.table.api.Expressions.*;


public class TableApiDemo01 {

    public static void main(String[] args) {
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();

        TableEnvironment tEnv = TableEnvironment.create(settings);
        tEnv.executeSql("CREATE TABLE employee_information (\n" +
                "    SOURCE VARCHAR,\n" +
                "    TOPIC VARCHAR,\n" +
                "    `COUNT` INT\n" +
                ") WITH ( \n" +
                "    'connector' = 'kafka',\n" +
                "    'topic' = 'o-coll-resolving-stat',\n" +
                "    'properties.bootstrap.servers' = '172.30.61.57:21005,172.30.61.58:21005,172.30.61.59:21005,172.30.61.60:21005',\n" +
                "    'properties.group.id' = 'testGroup',\n" +
                "    'scan.startup.mode' = 'earliest-offset',\n" +
                "    'format' = 'json'\n" +
                ");");



        tEnv.executeSql("CREATE TABLE employee_information_sink (\n" +
                "    SOURCE VARCHAR,\n" +
                "    TOPIC VARCHAR,\n" +
                "    `COUNT` INT,\n" +
                "     PRIMARY KEY (SOURCE,TOPIC) NOT ENFORCED\n" +
                ") WITH ( \n" +
                "    'connector' = 'upsert-kafka',\n" +
                "    'properties.bootstrap.servers' = '172.30.61.57:21005,172.30.61.58:21005,172.30.61.59:21005,172.30.61.60:21005',\n" +
                "    'topic' = 'test02',\n" +
                "    'key.format' = 'json',\n" +
                "    'value.format' = 'json'\n" +
               // "    'format' = 'json'\n" +
                ");");

//        Table table = tEnv.fromValues(
//                row(1, "A", "a")
//                , row(2, "B", "b")
//                , row(3, "C", "c")
//                , row(3, "C", "c")
//        ).as("a", "b", "c");
//
//        // Table table2 = table.groupBy($("a")).select($("a"), $("b").count().as("cnt"));
//        Table table2 = table
//                .addColumns(concat($("c"), "sunny").as("d"))
//                .addOrReplaceColumns(concat($("c"), "sunny").as("c"))
//                .dropColumns($("d"));

        tEnv.executeSql("INSERT INTO employee_information_sink select SOURCE,TOPIC,sum(`COUNT`) as `COUNT` from employee_information group by SOURCE,TOPIC");
        //tEnv.executeSql("INSERT INTO employee_information_sink select SOURCE,TOPIC,`COUNT` from employee_information");


        //table.execute().print();


    }

}
