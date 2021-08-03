package com.bdnrfob;

import io.micronaut.context.annotation.ConfigurationInject;
import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("hello.config.greeting")
public class HelloWorldConfig {

    @Getter
    private final String de;
    @Getter
    private final String en;

    @ConfigurationInject
    public HelloWorldConfig(@NotBlank final String de, @NotBlank final String en){
        this.de = de;
        this.en = en;
    }
}
