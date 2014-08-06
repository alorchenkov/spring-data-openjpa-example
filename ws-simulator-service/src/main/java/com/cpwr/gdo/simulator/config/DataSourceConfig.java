package com.cpwr.gdo.simulator.config;

import com.cpwr.gdo.simulator.service.WsSimulatorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.vendor.Database;

import javax.sql.DataSource;
import java.util.HashMap;


//@Profile("dev")
public class DataSourceConfig {
    /*@Value("#{test.sql.schema.script}")
    private String sqlSchema;

    @Value("#{test.sql.data.script}}")
    private String sqlData; */

    //@Bean
    public Database databaseType() {
        return Database.HSQL;
    }

    //@Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                //.addScript("classpath:/db-script/test-hsqldb-ws-simulator-schema.sql")
                //.addScript("classpath:/db-script/test-ws-simulator-data.sql")
                .build();
    }

}
