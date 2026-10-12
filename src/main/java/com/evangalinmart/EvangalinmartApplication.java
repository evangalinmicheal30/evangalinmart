package com.evangalinmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class EvangalinmartApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(EvangalinmartApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EvangalinmartApplication.class);
    }
}
