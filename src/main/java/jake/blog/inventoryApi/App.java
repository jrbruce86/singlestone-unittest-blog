package jake.blog.inventoryApi;

import jake.blog.inventoryApi.persist.PurchaseRecordRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("jake.blog.inventoryApi")
@ComponentScan(basePackages = {"persist"})
@EnableJpaRepositories(basePackages = {"jake.blog.inventoryApi.persist"})
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {PurchaseRecordRepository.class})
@EnableJpaAuditing
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}