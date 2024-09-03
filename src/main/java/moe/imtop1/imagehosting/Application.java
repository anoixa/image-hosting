package moe.imtop1.imagehosting;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("moe.imtop1.imagehosting.project.mapper")
@EnableAspectJAutoProxy
@Slf4j
public class Application {

    public static void main(String[] args) {
        log.info("server is starting...");

        SpringApplication.run(Application.class, args);
    }

}
