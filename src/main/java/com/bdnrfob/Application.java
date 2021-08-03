package com.bdnrfob;

import io.micronaut.runtime.Micronaut;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
            LOG.info("Iniciando a aplicação!");
            LOG.debug("Iniciando a aplicação!");
            Micronaut.run(Application.class, args);
    }


}
