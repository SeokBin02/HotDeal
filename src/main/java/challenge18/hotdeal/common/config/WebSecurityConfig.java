package challenge18.hotdeal.common.config;

import challenge18.hotdeal.common.security.JwtAuthenticationFilter;
import challenge18.hotdeal.common.security.UserDetailsServiceImpl;
import challenge18.hotdeal.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests()
                .antMatchers("/users/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers(HttpMethod.GET,"/products**").permitAll()
                .antMatchers(HttpMethod.GET,"/products/**").permitAll()
                .antMatchers(HttpMethod.GET, "/limited-products**").permitAll()
                .antMatchers(HttpMethod.GET, "/limited-products/**").permitAll()
                .antMatchers(HttpMethod.GET, "/css/**").permitAll()
                .antMatchers(HttpMethod.GET, "/js/**").permitAll()
                .antMatchers(HttpMethod.GET, "/webfonts/**").permitAll()
                .antMatchers(HttpMethod.GET, "/images/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
