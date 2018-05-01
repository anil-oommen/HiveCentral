package com.oom.hive.central;

import com.google.common.collect.ImmutableList;
import io.swagger.models.HttpMethod;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    private static String SPRING_SECURITY_BASE_CONTEXT = "/app/security";
    private static String API_SESSIONCNTR_BASE_CONTEXT = "/api/session";
    private static String DEF_SECURE_AREA_REDIRECT_URL = API_SESSIONCNTR_BASE_CONTEXT
                                                        + "/secure/check.access";
    private static String STATIC_PAGE_ALTERNATE_LOGIN_FORM = "/alternate.loginform.html";

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("guest")
                        .password("pass123")
                        .roles("USER");


    }


    /* HTTP Basic Authentication without Form , Working Example
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                authorizeRequests()
                .antMatchers("/api/* * /secure / * *").hasAuthority("USER").anyRequest() # remova spaces in expr
                .authenticated().and().csrf().disable()
                .httpBasic()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/session/public/logout"))
                .logoutSuccessUrl("/").and().exceptionHandling()
                .accessDeniedPage("/access-denied");
    }
     */



    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.
                authorizeRequests()
                .antMatchers("/api/**/public/**").permitAll()
                .antMatchers("/api/**/secure/**").hasAnyRole("USER").anyRequest()
                .authenticated()
                .and()
                .formLogin()
                    // page 302 Alternate Form Login as default see Exception handling
                    // this will be overidden by exceptionHandling().authenticationEntryPoint
                    .loginPage(STATIC_PAGE_ALTERNATE_LOGIN_FORM)

                    .loginProcessingUrl(SPRING_SECURITY_BASE_CONTEXT +"/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                    //.failureUrl(STATIC_PAGE_ALTERNATE_LOGIN_FORM)
                    .defaultSuccessUrl(DEF_SECURE_AREA_REDIRECT_URL)
                    .successHandler((request,response,authentication)->{
                        logger.info("User Authenticated " + authentication.getPrincipal()
                                + " Respond With NO Forms : " +
                                "true".equalsIgnoreCase(request.getParameter("noforms"))
                        );
                        if("true".equalsIgnoreCase(request.getParameter("noforms"))){
                            response.setStatus(response.SC_OK,"User Authenticated.");
                        }else{
                            //TODO we dont have Post Form Logon Secure area to redirect yet.
                            response.sendRedirect(DEF_SECURE_AREA_REDIRECT_URL );
                        }
                    })
                    .failureHandler((request,response,e)->{
                        logger.info("User Authentication Failure " + e.getMessage() + " Respond With NO Forms : " +
                                "true".equalsIgnoreCase(request.getParameter("noforms"))
                        );
                        if("true".equalsIgnoreCase(request.getParameter("noforms"))){
                            response.sendError(response.SC_UNAUTHORIZED,"User Authentication failure");
                        }else{
                            response.sendRedirect(STATIC_PAGE_ALTERNATE_LOGIN_FORM );
                        }
                    })
                    .permitAll()
                .and()
                .logout()
                    .logoutUrl(SPRING_SECURITY_BASE_CONTEXT +"/logout")
                    .logoutSuccessHandler((request,response,auth)->{
                        logger.info("User Logout Successfull " +  " Respond With NO Forms : " +
                                "true".equalsIgnoreCase(request.getParameter("noforms"))
                        );
                        if("true".equalsIgnoreCase(request.getParameter("noforms"))){
                            response.setStatus(response.SC_ACCEPTED);
                        }else{
                            response.sendRedirect(STATIC_PAGE_ALTERNATE_LOGIN_FORM );
                        }
                    })
                    .permitAll()
                .and()
                .exceptionHandling().authenticationEntryPoint((request,response,authException)->{
                    if(authException!=null){
                        logger.warn("Attempted Access on Secure resource." +request.getRequestURI());
                        /* For  API Access Request dont sent to relogin Form */
                        if(request.getRequestURI().contains("/api/")){
                            response.sendError(response.SC_UNAUTHORIZED,"User Unauthorized Access");
                        }else{
                            response.sendRedirect(STATIC_PAGE_ALTERNATE_LOGIN_FORM);
                        }
                    }
                })
                .and()
                .cors().and()
                .csrf().disable();

    }

    /*
    * CORS for Security Config
    * https://stackoverflow.com/questions/40418441/spring-security-cors-filter?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ImmutableList.of("*"));
        configuration.setAllowedMethods(ImmutableList.of("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(
                        "/resources/**",
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",

                        "/api/**/public/**",

                        /* Swagger Resources */
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v2/api-docs/**",

                        /* MQTT Client Public for Now */
                        "/hivemq-mqtt-web-client/**",

                        "/index.html",
                        "/ng/**",
                        "/"
                );
    }
}
