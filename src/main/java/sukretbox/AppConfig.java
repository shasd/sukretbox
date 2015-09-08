package sukretbox;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.util.Scanner;

/**
 * Created by sukret on 9/7/15.
 */
@Configuration
public class AppConfig {

    @Bean
    public DriverManagerDataSource dataSource() throws Exception {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        driverManagerDataSource.setUrl("jdbc:mysql://sukretdb.c3mw4dj2edji.us-west-2.rds.amazonaws.com:3306/sukretbox");
        driverManagerDataSource.setUsername("sukreth");
        Scanner sc = new Scanner(new FileInputStream(new java.io.File("credentials")));
        sc.next(); sc.next();
        driverManagerDataSource.setPassword(sc.next());
        return driverManagerDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public UserDao userDao(){
        return new UserDao();
    }

    @Bean
    public FileDao FileDao(){
        return new FileDao();
    }

    @Bean
    public S3Store s3Store(){
        return new S3Store();
    }

    @Bean
    public DataStore dataStore () {
        return s3Store();
    }

    @Bean
    public StorageManager storageManager() {
        return new StorageManager();
    }
}
