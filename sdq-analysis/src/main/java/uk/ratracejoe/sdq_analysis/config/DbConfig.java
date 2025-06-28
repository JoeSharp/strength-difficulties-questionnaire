package uk.ratracejoe.sdq_analysis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

@Configuration
public class DbConfig {
    @Value("${database.file}")
    private String databaseFile;

    @Bean
    public DataSource dataSource() {
        var dataSource = new SQLiteDataSource();
        dataSource.setUrl(String.format("jdbc:sqlite:%s", databaseFile)); // Path to your SQLite DB
        return dataSource;
    }

}
