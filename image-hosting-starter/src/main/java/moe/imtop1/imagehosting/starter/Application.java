package moe.imtop1.imagehosting.starter;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {"moe.imtop1.imagehosting.images","moe.imtop1.imagehosting.system"})
//@ComponentScan(basePackages = {"moe.imtop1.imagehosting"})
@MapperScan(basePackages = {"moe.imtop1.imagehosting.images.mapper","moe.imtop1.imagehosting.system.mapper"})
@EnableAspectJAutoProxy
@Slf4j
public class Application {

    public static void main(String[] args) {
        log.info("server is starting...");

        SpringApplication.run(Application.class, args);
    }

}
