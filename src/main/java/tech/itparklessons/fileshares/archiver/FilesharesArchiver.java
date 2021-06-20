package tech.itparklessons.fileshares.archiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FilesharesArchiver {
    public static void main(String[] args) {
        SpringApplication.run(FilesharesArchiver.class, args);
    }
}
