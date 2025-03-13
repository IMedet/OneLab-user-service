package kz.medet.userservice.controller;

import jakarta.validation.Valid;
import kz.medet.userservice.payload.request.LoginRequest;
import kz.medet.userservice.payload.request.SignUpRequest;
import kz.medet.userservice.payload.response.JWTTokenSuccessResponse;
import kz.medet.userservice.payload.response.MessageResponse;
import kz.medet.userservice.security.JWTTokenProvider;
import kz.medet.userservice.security.SecurityConstants;
import kz.medet.userservice.service.impl.UserService;
import kz.medet.userservice.validations.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;
    private final ResponseErrorValidation responseErrorValidation;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider,
                          ResponseErrorValidation responseErrorValidation, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.responseErrorValidation = responseErrorValidation;
        this.userService = userService;
    }


    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                                   BindingResult bindingResult){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);

        if(!ObjectUtils.isEmpty(errors)) return errors;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
    }


    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequest signUpRequest,
                                               BindingResult bindingResult){
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);

        if(!ObjectUtils.isEmpty(errors)) return errors;

        userService.createUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }
}
