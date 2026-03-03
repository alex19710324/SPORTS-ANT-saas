package com.sportsant.saas.controller;

import com.sportsant.saas.dto.ConsentDto;
import com.sportsant.saas.compliance.service.ConsentService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.sportsant.saas.dto.JwtResponse;
import com.sportsant.saas.dto.LoginRequest;
import com.sportsant.saas.dto.MessageResponse;
import com.sportsant.saas.dto.SignupRequest;
import com.sportsant.saas.entity.ERole;
import com.sportsant.saas.entity.Role;
import com.sportsant.saas.entity.User;
import com.sportsant.saas.repository.RoleRepository;
import com.sportsant.saas.repository.UserRepository;
import com.sportsant.saas.security.JwtUtils;
import com.sportsant.saas.security.UserDetailsImpl;
import com.sportsant.saas.communication.service.NotificationService;
import com.sportsant.saas.membership.service.MembershipService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证中心", description = "用户注册、登录、Token管理")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  NotificationService notificationService;

  @Autowired
  MembershipService membershipService;

  @Autowired
  ConsentService consentService;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken((UserDetailsImpl) authentication.getPrincipal());
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt, 
                         userDetails.getId(), 
                         userDetails.getUsername(), 
                         userDetails.getEmail(), 
                         roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(), 
               signUpRequest.getEmail(),
               encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "mod":
          Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    // Create Member Profile if phone is provided
    if (signUpRequest.getPhoneNumber() != null) {
        try {
            membershipService.createMember(
                user.getId(), 
                user.getUsername(), 
                signUpRequest.getPhoneNumber(),
                signUpRequest.getIdCard(),
                signUpRequest.getLocale()
            );
        } catch (Exception e) {
            // Rollback user creation or just log error? 
            // Ideally should be transactional, but for now we delete user if member creation fails to ensure consistency
            userRepository.delete(user);
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Send Welcome Notification
    try {
        notificationService.sendLocalizedToUser(
            user,
            "notification.welcome.title",
            "notification.welcome.message",
            new Object[]{user.getUsername()},
            "SYSTEM",
            "/profile"
        );
    } catch (Exception e) {
        System.err.println("Failed to send welcome notification: " + e.getMessage());
    }

    // Record Consents
    if (signUpRequest.getConsents() != null) {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        for (ConsentDto c : signUpRequest.getConsents()) {
            try {
                consentService.recordConsent(user.getId(), c.getAgreementType(), c.getVersion(), c.isAgreed(), ip, ua);
            } catch (Exception e) {
                System.err.println("Failed to record consent: " + e.getMessage());
            }
        }
    }

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
