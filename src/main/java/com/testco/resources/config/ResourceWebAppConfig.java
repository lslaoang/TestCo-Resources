package com.testco.resources.config;

import com.azure.spring.aad.webapi.AADResourceServerWebSecurityConfigurerAdapter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceWebAppConfig  extends AADResourceServerWebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        // @formatter:off
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/resource").hasAuthority("SCOPE_Consumer.read")
                .antMatchers("/**").denyAll();
        // @formatter:on
    }
}
