package ktb.week4.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ktb.week4.Login.Cookie.CookieUtil;
import ktb.week4.Login.Jwt.JwtFilter;
import ktb.week4.Login.Jwt.JwtUtil;
import ktb.week4.Login.LoginFilter;
import ktb.week4.Login.RefreshToken.RefreshTokenRepository;
import ktb.week4.user.UserRepository;
import ktb.week4.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final CookieUtil cookieUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserService userService) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(AbstractHttpConfigurer::disable)

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/signup", "/users/**", "/files/**", "/legal/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())

                .addFilterBefore(new JwtFilter(jwtUtil, userRepository, cookieUtil), LoginFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, cookieUtil, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class)

                .sessionManagement((session)-> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .logout(logout ->
                        logout.logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .addLogoutHandler(new LogoutHandler() {
                                    @Override
                                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                                        // 쿠키조회
                                        String accessToken = cookieUtil.findCookie("accessToken", request.getCookies());
                                        String refreshToken = cookieUtil.findCookie("refreshToken", request.getCookies());

                                        if (accessToken == null) {
                                            log.info("can not found access token.");
                                            return;
                                        }

                                        if (refreshToken == null) {
                                            log.info("can not found refresh token.");
                                            return;
                                        }

                                        // jwt 처리
                                        try {
                                            String username = jwtUtil.getEmail(accessToken);
                                            log.info("로그아웃 시도: " + username);
                                        } catch (Exception e) {
                                            log.info("jwt에서 사용자정보를 가져올수 없습니다.");

                                        }
                                    }
                                })
                                .clearAuthentication(true)
                                .logoutSuccessHandler((request, response, authentication) -> {
                                    log.info("logout 처리 완료");

                                    response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.deleteCookie("accessToken").toString());
                                    response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.deleteCookie("refreshToken").toString());
                                    response.setStatus(HttpServletResponse.SC_OK);
                                })

                )

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((httpServletRequest, httpServletResponse, exception) -> {
                            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                        .accessDeniedHandler((httpServletRequest, httpServletResponse, exception) -> {
                            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        })
                );



        return http.build();
    }
}
