package com.daelim.yourmind.user.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.daelim.yourmind.user.domain.Role;
import com.daelim.yourmind.user.domain.User;
import com.daelim.yourmind.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserResource {
    private final UserService userService;

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }


    @PostMapping("/user/save")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("/role/save")
    public ResponseEntity<Role> saveUser(@RequestBody Role role) {
        return ResponseEntity.ok().body(userService.saveRole(role));
    }

    @PostMapping("/role/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRolename());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                // local 에서 사용할 GrantType 으로 가정하여 Bearer 만 받음
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                // token 을 생성할 때 사용한 알고리즘과 일치
                Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes());
                // 토큰 검증 객체를 생성한 후 알고리즘 주기
                JWTVerifier verifier = JWT.require(algorithm).build();
                // 토큰 검증 객체에서 검증된 복호화된 토큰 변수 지정
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                // 토큰에서 정보 빼내기
                String username = decodedJWT.getSubject();
                // 빼낸 정보로 DB 조회하기
                User user = userService.getUser(username);
                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles()
                                .stream()
                                .map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                // response header 에 담으면 보안에 취약하므로 바디에 담아서 보내기
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }catch (Exception e) {
                log.error("Error logging in : {}", e.getMessage());
                response.setHeader("error", "you\'re monster");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error", e.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                // fasterxml.json 기능 중 하나로 ServletOutputStream 에 직접 error(Map)를 집어넣을 수 있음
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    @Data
    class RoleToUserForm {
        private String username;
        private String rolename;
    }
}
