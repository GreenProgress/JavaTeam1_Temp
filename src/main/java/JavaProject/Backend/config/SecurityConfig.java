package JavaProject.Backend.config;

import JavaProject.Backend.security.JwtAuthenticationFilter;
import JavaProject.Backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod; // HttpMethod 사용을 위해 추가
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    /** JWT 필터 스프링 빈 등록 */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    /** 비밀번호 암호화 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** CORS 설정 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "https://transcendent-sorbet-fd5431.netlify.app"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /** SecurityFilterChain 설정 */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 1. 완전 공개 (로그인, 회원가입, 상황/법령 조회)
                        .requestMatchers(
                                "/auth/**",
                                "/api/situations/**",
                                "/api/laws/**",
                                "/api/health"
                        ).permitAll()

                        // 2. [수정] 비로그인 진단을 위해 허용 (답변 제출, 분석 요청, PDF)
                        //    - POST /api/responses: 답변 제출
                        //    - POST /api/AnalysisResult: 결과 분석 요청
                        //    - GET /api/AnalysisResult/*/pdf: PDF 다운로드
                        .requestMatchers("/api/responses/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/AnalysisResult").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/AnalysisResult/*/pdf").permitAll()

                        // 3. 인증 필요 (마이페이지, 내 결과 목록 조회 등)
                        .requestMatchers(
                                "/mypage/**",
                                "/api/AnalysisResult/user/**", // 사용자별 조회는 본인만 가능하도록
                                "/api/AnalysisResult/**"       // 그 외 삭제 등의 기능은 인증 필요
                        ).authenticated()

                        .anyRequest().permitAll()
                )

                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
