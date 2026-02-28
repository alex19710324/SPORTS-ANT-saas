package com.sportsant.saas;

import com.sportsant.saas.membership.entity.MemberLevel;
import com.sportsant.saas.membership.repository.MemberLevelRepository;
import com.sportsant.saas.ai.service.AiInferenceService;
import com.sportsant.saas.ai.service.FeatureStoreService;
import com.sportsant.saas.entity.ERole;
import com.sportsant.saas.entity.Role;
import com.sportsant.saas.entity.User;
import com.sportsant.saas.repository.RoleRepository;
import com.sportsant.saas.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, 
                                    UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    AiInferenceService aiInferenceService,
                                    FeatureStoreService featureStoreService,
                                    MemberLevelRepository memberLevelRepository) {
        return args -> {
            // ... (Existing Init Logic) ...
            // 1. Initialize Roles if not exist
            for (ERole role : ERole.values()) {
                if (roleRepository.findByName(role).isEmpty()) {
                    roleRepository.save(new Role(role));
                }
            }

            // 2. Initialize Admin User if not exist
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@sportsant.com", 
                                    passwordEncoder.encode("admin123"));
                Set<Role> roles = new HashSet<>();
                roleRepository.findByName(ERole.ROLE_ADMIN).ifPresent(roles::add);
                roleRepository.findByName(ERole.ROLE_USER).ifPresent(roles::add);
                admin.setRoles(roles);
                userRepository.save(admin);
                System.out.println(">>> Initialized Admin User: admin / admin123");
            }
            
            // 3. Initialize AI Models & Features
            if (aiInferenceService.listModels().isEmpty()) {
                aiInferenceService.registerModel("churn-prediction-v1", "CLASSIFICATION", "1.0", "internal");
                aiInferenceService.registerModel("marketing-copy-v1", "GENERATION", "1.0", "internal");
                System.out.println(">>> Initialized AI Models");
            }

            if (featureStoreService.listFeatures().isEmpty()) {
                featureStoreService.registerFeature("user_total_purchase_30d", "NUMERIC", "Total purchase amount in last 30 days", "mysql");
                featureStoreService.registerFeature("user_last_active_days", "NUMERIC", "Days since last login", "redis");
                System.out.println(">>> Initialized AI Features");
            }

            // 4. Initialize Member Levels (PRD-016)
            if (memberLevelRepository.count() == 0) {
                MemberLevel l1 = new MemberLevel(); l1.setName("工蚁先锋"); l1.setLevelOrder(1); l1.setRequiredGrowth(0);
                MemberLevel l2 = new MemberLevel(); l2.setName("兵蚁卫士"); l2.setLevelOrder(2); l2.setRequiredGrowth(1000);
                MemberLevel l3 = new MemberLevel(); l3.setName("飞蚁骑士"); l3.setLevelOrder(3); l3.setRequiredGrowth(5000);
                MemberLevel l4 = new MemberLevel(); l4.setName("蚁后亲卫"); l4.setLevelOrder(4); l4.setRequiredGrowth(20000);
                MemberLevel l5 = new MemberLevel(); l5.setName("圣蚁尊者"); l5.setLevelOrder(5); l5.setRequiredGrowth(50000);
                
                memberLevelRepository.save(l1);
                memberLevelRepository.save(l2);
                memberLevelRepository.save(l3);
                memberLevelRepository.save(l4);
                memberLevelRepository.save(l5);
                System.out.println(">>> Initialized Member Levels (5 Levels)");
            }
        };
    }
}
