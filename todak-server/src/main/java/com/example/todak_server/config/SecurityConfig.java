package com.example.todak_server.config;

import com.example.todak_server.jwt.JwtAuthenticationFilter;
import com.example.todak_server.service.CustomOAuth2UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@OpenAPIDefinition(
        info = @Info(
                title = "TODAK API 명세서",
                version = "v1.0",
                description = "토닥이 서버 API 문서입니다.<br>인증이 필요한 요청은 우측 상단 🔒Authorize 버튼을 클릭하고 JWT(액세스) 토큰을 입력하세요."
        ),
        security = {
                @SecurityRequirement(name = "BearerAuth")
        }
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .defaultSuccessUrl("/token", true) // 로그인 성공 시 /token으로 이동
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login**",
                                "/oauth2/**", "/token",
                                "/api/auth/**",
                                "/v3/**", "/swagger-ui/**",
                                "/swagger/**", "/actuator/**"
                        ).permitAll()
                        .anyRequest().authenticated()
//                        .anyRequest().permitAll()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
