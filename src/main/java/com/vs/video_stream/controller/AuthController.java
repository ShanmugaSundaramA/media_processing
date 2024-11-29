package com.vs.video_stream.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vs.video_stream.request.AuthDTO;
import com.vs.video_stream.service.JWTService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AuthController {

  // private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;

  @PostMapping("/authenticate")
  public String authenticate(@RequestBody AuthDTO authDTO) {

    // authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
    //     authDTO.email(),
    //     authDTO.password()));

    return jwtService.generateToken(authDTO.email());
  }

}
