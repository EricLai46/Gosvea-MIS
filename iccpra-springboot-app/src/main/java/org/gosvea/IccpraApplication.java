package org.gosvea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableAsync
@MapperScan("org.gosvea.mapper")
public class IccpraApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(IccpraApplication.class,args);
    }
}
