package org.cyberpay.crypto;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Component;

/**
 * 扩展ServletContextInitializer ，用于作为war包部署在web容器中
 * @author
 */
@Component
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CyberPayCryptoWebApplication.class);
    }


}
