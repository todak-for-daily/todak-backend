package com.example.todak_server.controller;

import com.example.todak_server.dto.auth.GoogleLoginRequest;
import com.example.todak_server.dto.auth.GoogleUser;
import com.example.todak_server.dto.auth.LoginResponse;
import com.example.todak_server.entity.Member;
import com.example.todak_server.jwt.JwtTokenProvider;
import com.example.todak_server.service.GoogleService;
import com.example.todak_server.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@Tag(name = "로그인 API", description = "구글 소셜 로그인 API")
//@RestController
//@RequestMapping("/api/auth/login")
//public class AuthLoginController {
//
//    private final GoogleService googleService;
//
//    public AuthLoginController(GoogleService googleService) {
//        this.googleService = googleService;
//    }
//
//    //    @Operation(summary = "로그인 창", description = "로그인 창으로 이동 후 액세스 토큰이 json으로 반환됨.")
////    @GetMapping("/google")
////    public ResponseEntity<Void> start(HttpServletRequest req) {
////        String url = ServletUriComponentsBuilder.fromRequest(req)
////                .replacePath("/oauth2/authorization/google")
////                .replaceQuery(null)
////                .build().toUriString();
////        return ResponseEntity.status(302).location(URI.create(url)).build();
////    }
//
//    @PostMapping("/api/auth/google")
//    public ResponseEntity<?> login(@RequestBody GoogleLoginRequest request) {
//
//        GoogleUser googleUser = googleService.verifyIdToken(request.getIdToken());
//
//        Member member = memberService.findOrCreateByGoogle(googleUser);
//
//        String jwt = jwtProvider.createToken(member);
//
//        return ResponseEntity.ok(new LoginResponse(jwt, member));
//    }
//
//
//
//}

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthLoginController {

    private final GoogleService googleService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtProvider;

    @PostMapping("/google")
    public LoginResponse googleLogin(@RequestBody GoogleLoginRequest request) {

        GoogleUser googleUser = googleService.verify(request.getIdToken());

        Member member = memberService.findOrCreateByGoogle(googleUser);

        // subject = provider:providerId
        String subject = "GOOGLE:" + googleUser.getSub();

        String access = jwtProvider.createAccess(subject, member.getId(),
                java.util.List.of("USER"));

        String refresh = jwtProvider.createRefresh(subject, member.getId());

        return new LoginResponse(access, refresh, member);
    }
}
