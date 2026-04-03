package uk.ratracejoe.sdq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/*
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.web.SecurityFilterChain;

 */

@Configuration
// @EnableWebSecurity
@Slf4j
public class SecurityConfig {
  /*

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, OAuth2AuthorizedClientService clientService) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/", true)) // enables Cognito login
        .logout(
            logout ->
                logout.logoutSuccessHandler(
                    (req, res, auth) -> {
                      OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) auth;

                      OAuth2AuthorizedClient client =
                          clientService.loadAuthorizedClient(
                              oauth.getAuthorizedClientRegistrationId(), oauth.getName());

                      ClientRegistration reg = client.getClientRegistration();

                      String logoutUri =
                          (String)
                              reg.getProviderDetails()
                                  .getConfigurationMetadata()
                                  .get("end_session_endpoint");

                      String baseUrl =
                          UriComponentsBuilder.fromUriString(req.getRequestURL().toString())
                              .replacePath(req.getContextPath())
                              .replaceQuery(null)
                              .build()
                              .toUriString();

                      String redirect =
                          logoutUri
                              + "?post_logout_redirect_uri="
                              + baseUrl
                              + "&client_id="
                              + reg.getClientId();

                      log.info("Logout Redirect " + redirect);
                      res.sendRedirect(redirect);
                    }));

    return http.build();
  }

     */
}
