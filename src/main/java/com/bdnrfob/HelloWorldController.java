package com.bdnrfob;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("${hello.controller.path}")
public class HelloWorldController {

    //@Inject poderia ser usado como o @autowired em Java
    private final HelloWorldService service;
    private final HelloWorldConfig config;

    public HelloWorldController(HelloWorldService service, HelloWorldConfig config) {
        this.service = service;
        this.config = config;
    }

    @Get
    public String hello(){
        return service.sayHi();
    }

    @Get("/de")
    public String sayHiInGerman(){
        return config.getDe();
    }

    @Get("/en")
    public String sayHiInEnglish(){
        return config.getEn();
    }

    @Get("/json")
    public Greeting sayHelloAsJson(){
        return new Greeting();
    }


}
