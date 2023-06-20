package com.xh.flink;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;

public class TableKafkaSourceDemo {

    public static void main(String[] args) {

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();

        TableEnvironment tEnv = TableEnvironment.create(settings);

        tEnv.executeSql("CREATE TABLE employee_information (\n" +
                "    emp_id INT,\n" +
                "    name VARCHAR,\n" +
                "    dept_id INT\n" +
                ") WITH ( \n" +
                "    'connector' = 'filesystem',\n" +
                "    'path' = 'src\\main\\resources\\something.csv',\n" +
                "    'format' = 'csv'\n" +
                ");");

        Table table = tEnv.sqlQuery("select *　from KafkaTable");

        table.execute().print();

    }
}
