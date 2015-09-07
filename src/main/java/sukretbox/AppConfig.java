package sukretbox;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Created by sukret on 9/7/15.
 */
@Configuration
public class AppConfig {

    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        driverManagerDataSource.setUrl("jdbc:mysql://sukretdb.c3mw4dj2edji.us-west-2.rds.amazonaws.com:3306/sukretbox");
        driverManagerDataSource.setUsername("sukreth");
        driverManagerDataSource.setPassword("**");
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
    @Scope("request")
    public MainController mainController (S3Store s3Store) {
        MainController ret = new MainController();
        ret.setDataStore(s3Store);
        return ret;
    }
}
