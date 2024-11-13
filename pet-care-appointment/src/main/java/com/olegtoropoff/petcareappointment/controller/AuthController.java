package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.request.LoginRequest;
import com.olegtoropoff.petcareappointment.response.ApiResponse;
import com.olegtoropoff.petcareappointment.response.JwtResponse;
import com.olegtoropoff.petcareappointment.security.jwt.JwtUtils;
import com.olegtoropoff.petcareappointment.security.user.UPCUserDetails;
import com.olegtoropoff.petcareappointment.service.token.VerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@CrossOrigin("http://localhost:5173") //TODO delete
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.AUTH)
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final VerificationTokenService tokenService;

    @PostMapping(UrlMapping.LOGIN)
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try{
            Authentication authentication =
                    authenticationManager
                            .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);
            UPCUserDetails userDetails = (UPCUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), jwt);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.AUTHENTICATION_SUCCESS, jwtResponse));
        } catch (DisabledException e) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(new ApiResponse(FeedBackMessage.ACCOUNT_DISABLED,null));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(new ApiResponse(e.getMessage(), FeedBackMessage.INVALID_PASSWORD));
        }
    }

    @GetMapping(UrlMapping.VERIFY_EMAIL)
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam("token")    String token) {
        String result =   tokenService.validateToken(token);
        return  switch (result){
            case "VALID" -> ResponseEntity.ok(new ApiResponse(FeedBackMessage.VALID_TOKEN, null));
            case "VERIFIED" -> ResponseEntity.ok(new ApiResponse(FeedBackMessage.TOKEN_ALREADY_VERIFIED, null));
            case "EXPIRED" ->
                    ResponseEntity.status(HttpStatus.GONE).body(new ApiResponse(FeedBackMessage.EXPIRED_TOKEN, null));
            case "INVALID" ->
                    ResponseEntity.status(HttpStatus.GONE).body(new ApiResponse(FeedBackMessage.INVALID_VERIFICATION_TOKEN, null));
            default -> ResponseEntity.internalServerError().body(new ApiResponse(FeedBackMessage.ERROR, null));
        } ;
    }
}
