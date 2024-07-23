package com.example.user_app.controller;

import com.example.user_app.dto.LoginOauth2Response;
import com.example.user_app.dto.UpdateProfileRequestDto;
import com.example.user_app.dto.UpdateProfileResponseDto;
import com.example.user_app.model.ERole;
import com.example.user_app.model.Role;
import com.example.user_app.model.User;
import com.example.user_app.repository.RoleRepository;
import com.example.user_app.repository.UserRepository;
import com.example.user_app.security.UserDetailsImpl;
import com.example.user_app.service.MailService;
import com.example.user_app.service.OAuth2JwtService;
import com.example.user_app.service.OAuth2Service;
import com.example.user_app.service.OtpGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("oauth")
public class Oauth2Controller {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    OAuth2JwtService oAuth2JwtService;

    @Autowired
    OAuth2Service oAuth2Service;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/gmail/success")
    public ResponseEntity<LoginOauth2Response> gmailLogin(Authentication authentication){
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        Collection<GrantedAuthority> authorities = new ArrayList<>(oidcUser.getAuthorities());

        Optional<User> userOptional = userRepository.findByEmail(oidcUser.getEmail());
        if (userOptional.isPresent()){
            User user = userOptional.get();

            for (Role role : user.getRoles()){
                authorities.add(new SimpleGrantedAuthority(role.getName().toString()));
            }
        }

        UserDetailsImpl modifiedUserDetails = UserDetailsImpl.build(oidcUser);
        OidcUser modifiedOidcUser = new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

        Authentication modifiedAuthenticaion = new UsernamePasswordAuthenticationToken(
                modifiedOidcUser,
                oidcUser.getIdToken(),
                authorities
        );

        String jwt = oAuth2JwtService.oauthTokenGenerate(modifiedAuthenticaion);

        List<String> roles = modifiedAuthenticaion.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();

        LoginOauth2Response loginOauth2Response = new LoginOauth2Response(
                jwt, modifiedUserDetails.getUsername(), roles
        );

        return ResponseEntity.ok(loginOauth2Response);
    }

    @PutMapping("update/profile")
    public User updateProfile(@RequestBody UpdateProfileRequestDto updateProfileRequestDto){
        return oAuth2Service.updateProfile(updateProfileRequestDto);
    }
}
