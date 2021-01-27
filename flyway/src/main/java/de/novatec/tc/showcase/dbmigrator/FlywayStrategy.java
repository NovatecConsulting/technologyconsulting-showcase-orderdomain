package de.novatec.tc.showcase.dbmigrator;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FlywayStrategy{
    @Bean
    @Profile("migration")
    public FlywayMigrationStrategy onlyMigrateStrategy(){
        FlywayMigrationStrategy strategy = new FlywayMigrationStrategy() {
            @Override
            public void migrate(Flyway flyway) {
                flyway.migrate();
            }
        };
    return strategy;
    }

    @Bean
    @Profile("clean")
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        FlywayMigrationStrategy strategy = new FlywayMigrationStrategy() {
            @Override
            public void migrate(Flyway flyway) {
                flyway.clean();
            }
        };

    return strategy;
    }

    @Bean
    @Profile("baseline")
    public FlywayMigrationStrategy baselineMigrateStrategy() {
        FlywayMigrationStrategy strategy = new FlywayMigrationStrategy() {
            @Override
            public void migrate(Flyway flyway) {
                flyway.clean();
                flyway.baseline();
                flyway.migrate();
            }
        };

    return strategy;
    }
}
