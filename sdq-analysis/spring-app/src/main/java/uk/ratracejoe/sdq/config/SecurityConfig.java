package uk.ratracejoe.sdq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, ClientRegistrationRepository clientRegRepo) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/", true)) // enables Cognito login
        .logout(
            logout ->
                logout.logoutSuccessHandler(
                    (req, res, auth) -> {
                      String redirect =
                          "http://localhost:8085/realms/ratracejoe/protocol/openid-connect/logout"
                              + "?post_logout_redirect_uri=http://localhost:8080/"
                              + "&client_id=timesheets-service";
                      res.sendRedirect(redirect);
                    }));

    return http.build();
  }
}
