//package com.xh.flink;
//
//import org.apache.flink.connector.datagen.table.DataGenConnectorOptions;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.table.api.*;
//import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
//import org.apache.flink.table.descriptors.TableDescriptor;
//
//public class CreateTableEnvironment {
//
//    public static void main(String[] args) {
//        // 直接构建表运行环境
//        EnvironmentSettings settings = EnvironmentSettings
//                .newInstance()
//                .inStreamingMode()
//                // .inBatchMode()
//                .build();
//
//        TableEnvironment tEnv = TableEnvironment.create(settings);
//
//        // 从已经存在的StreamExecutionEnvironment 构建表环境
//        // StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        // StreamTableEnvironment tEnv2 = StreamTableEnvironment.create(env);
//
//        // 在Flink SQL中，表分为视图、常规表，视图一般由已存在的Table对象创建，常规表描述外部数据，
//        // 例如，文件、数据库表、消息队列
//
//        // 从物理数据源创建一个表
//        tEnv.createTemporaryTable("SourceTable", TableDescriptor.forConnector("datagen")
//                .schema(Schema.newBuilder()
//                        .column("f0", DataTypes.STRIaNG())
//                        .build())
//                .option(DataGenConnectorOptions.ROWS_PER_SECOND, 100L)
//                .build());
//
//        // 使用Table API 创建表对象
//        Table table1 = tEnv.from("SourceTable");
//
//        // 使用SQL查询创建表对象
//        Table table2 = tEnv.sqlQuery("SELECT * FROM SourceTable");
//
//        // 利用表对象注册视图
//        tEnv.createTemporaryView("projectedTable", table1);
//
//    }
//}
