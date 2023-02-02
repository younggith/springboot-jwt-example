package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.filter.MyFilter1;
import com.cos.jwt.filter.MyFilter3;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CorsFilter corsFilter;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws
		Exception {
		http.addFilterBefore(new MyFilter3(), SecurityContextHolderFilter.class);
		http.csrf().disable();	// csrf 토큰 비활성화 (테스트시엔 걸어두는게 좋음)
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)	// stateless서버로 만들겠다는 뜻
		.and()
		.addFilter(corsFilter)	// @Crossorigin(컨트롤러에 걸경우 인증없을경우 다 거부), 시큐리티 필터에 등록 인증이 없어도(O)
		.formLogin().disable()	// 위의 csrf부터 여기까지 4줄은 jwt쓸때는 고정! 바로위 addFilter 빼고..
		.httpBasic().disable()	// 기본적으로 httpBasic방식 id, pw같은 중요데이터가 암호화되서 날라감 하지만 토큰은 노출되도 상관없으니 disable!
		.addFilter(new JwtAuthenticationFilter(authenticationManager))	// AuthenticationManager
		.authorizeHttpRequests()
		.requestMatchers("/api/v1/user/**") .hasAnyRole("USER", "ADMIN", "MANAGER")
		.requestMatchers("/api/v1/manager/**") .hasAnyRole("ADMIN", "MANAGER")
		.requestMatchers("/api/v1/admin/**") .hasRole("ADMIN")
		.anyRequest().permitAll();
			
		return http.build();
	}
	
}
