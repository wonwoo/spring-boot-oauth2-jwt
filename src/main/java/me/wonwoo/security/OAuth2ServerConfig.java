package me.wonwoo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * Created by wonwoo on 2016. 7. 4..
 */
@Configuration
public class OAuth2ServerConfig {

  @Configuration
  @EnableResourceServer
  protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Value("${resource.id:spring-boot-application}")
    private String resourceId;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
      resources.resourceId(resourceId);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

      http.authorizeRequests()
        .antMatchers("/simple/**").hasRole("USER");
    }
  }


  @Configuration
  @RequiredArgsConstructor
  @EnableAuthorizationServer
  public static class JwtOAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;

    @Value("${resource.id:spring-boot-application}")
    private String resourceId;

    @Value("${access_token.validity_period:3600}")
    private int accessTokenValiditySeconds = 3600;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
      throws Exception {
      endpoints.accessTokenConverter(jwtAccessTokenConverter())
        .authenticationManager(this.authenticationManager);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      clients.inMemory()
        .withClient("bar")
        .authorizedGrantTypes("password")
        .authorities("ROLE_USER")
        .scopes("read", "write")
        .resourceIds(resourceId)
        .accessTokenValiditySeconds(accessTokenValiditySeconds)
        .secret("foo");
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
      JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
      KeyPair keyPair = new KeyStoreKeyFactory(
        new ClassPathResource("server.jks"), "qweqwe".toCharArray())
        .getKeyPair("hello", "zaqwsx".toCharArray());
      converter.setKeyPair(keyPair);
      return converter;
    }
  }
}