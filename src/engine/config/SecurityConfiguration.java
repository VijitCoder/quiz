package engine.config;

import engine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableConfigurationProperties
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userDetailsService;

    /**
     * Access with HTTP Basic Auth.
     *
     * No CSRF protection cause the whole API protected with user auth.
     *
     * No saving of the user session cause we don't need it in API.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            // Why: if your browser does not open H2 console frames, just add a string below
            .headers().frameOptions().disable()
            .and().authorizeRequests().anyRequest().authenticated()
            .and().httpBasic()
            .and().sessionManagement().disable();
    }

    /**
     * Access without authorization.
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers(HttpMethod.POST, "/api/register", "/actuator/shutdown")
            .antMatchers("/h2/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService);
    }
}
