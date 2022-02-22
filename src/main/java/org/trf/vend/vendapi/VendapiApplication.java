package org.trf.vend.vendapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.trf.vend.model.IFloat;
import org.trf.vend.model.ISalesTransaction;
import org.trf.vend.model.Kitty;
import org.trf.vend.model.SalesTransactionService;

@SpringBootApplication
public class VendapiApplication {

    @Bean
    IFloat createFloat() {
        return new Kitty();
    }

    @Bean()
    public SalesHolder salesHolder() {
        return new SalesHolder();
    }


    @Bean
    ISalesTransaction initService(IFloat k) {
        return new SalesTransactionService(k);
    }

    public static void main(String[] args) {
        SpringApplication.run(VendapiApplication.class, args);
    }

}
