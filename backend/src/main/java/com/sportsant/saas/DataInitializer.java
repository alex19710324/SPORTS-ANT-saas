package com.sportsant.saas;

import com.sportsant.saas.membership.entity.MemberLevel;
import com.sportsant.saas.membership.repository.MemberLevelRepository;
import com.sportsant.saas.ai.service.AiInferenceService;
import com.sportsant.saas.ai.service.FeatureStoreService;
import com.sportsant.saas.entity.ERole;
import com.sportsant.saas.entity.Role;
import com.sportsant.saas.entity.User;
import com.sportsant.saas.language.entity.LanguagePackage;
import com.sportsant.saas.language.repository.LanguagePackageRepository;
import com.sportsant.saas.store.entity.Store;
import com.sportsant.saas.store.repository.StoreRepository;
import com.sportsant.saas.repository.RoleRepository;
import com.sportsant.saas.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
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
                                    MemberLevelRepository memberLevelRepository,
                                    StoreRepository storeRepository,
                                    LanguagePackageRepository languagePackageRepository) {
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

            // 5. Initialize Stores (Mock Data for HQ Dashboard)
            if (storeRepository.count() == 0) {
                Store s1 = new Store();
                s1.setName("Sports Ant - Beijing Flagship");
                s1.setCity("Beijing");
                s1.setCountry("China");
                s1.setLatitude(39.9042);
                s1.setLongitude(116.4074);
                s1.setTodayRevenue(new BigDecimal("12500.50"));
                s1.setTodayVisitors(320);
                s1.setStatus("NORMAL");
                
                Store s2 = new Store();
                s2.setName("Sports Ant - Shanghai Center");
                s2.setCity("Shanghai");
                s2.setCountry("China");
                s2.setLatitude(31.2304);
                s2.setLongitude(121.4737);
                s2.setTodayRevenue(new BigDecimal("18200.00"));
                s2.setTodayVisitors(450);
                s2.setStatus("WARNING"); // Mock Warning
                
                Store s3 = new Store();
                s3.setName("Sports Ant - Tokyo Shibuya");
                s3.setCity("Tokyo");
                s3.setCountry("Japan");
                s3.setLatitude(35.6895);
                s3.setLongitude(139.6917);
                s3.setTodayRevenue(new BigDecimal("9800.00"));
                s3.setTodayVisitors(210);
                s3.setStatus("NORMAL");
                
                storeRepository.save(s1);
                storeRepository.save(s2);
                storeRepository.save(s3);
                System.out.println(">>> Initialized Mock Stores (3 Stores)");
            }

            // 6. Initialize Language Packages (P0)
            if (languagePackageRepository.count() == 0) {
                // zh-CN
                LanguagePackage zh = new LanguagePackage();
                zh.setCode("zh-CN");
                zh.setName("简体中文");
                zh.setVersion("1.0.0");
                zh.setContent("""
                    {
                      "login": {
                        "title": "SPORTS ANT SaaS 登录",
                        "username": "用户名",
                        "password": "密码",
                        "submit": "登录",
                        "reset": "重置",
                        "placeholder": {
                          "username": "请输入用户名",
                          "password": "请输入密码"
                        }
                      },
                      "home": {
                        "welcome": "欢迎来到 SPORTS ANT SaaS",
                        "logout": "退出登录",
                        "applications": "应用中心",
                        "modules": {
                          "hq": { "title": "总部中心", "desc": "加盟与全球管理" },
                          "manager": { "title": "店长工作台", "desc": "日常运营与员工管理" },
                          "frontdesk": { "title": "前台收银", "desc": "签到与POS" },
                          "technician": { "title": "技术员", "desc": "设备监控与维修" },
                          "data": { "title": "数据中心", "desc": "分析与报表" },
                          "marketing": { "title": "营销中心", "desc": "活动与会员" },
                          "finance": { "title": "财务中心", "desc": "发票与薪资" },
                          "communication": { "title": "通讯中心", "desc": "消息与通知" }
                        }
                      },
                      "technician": {
                        "title": "技术员工作台",
                        "workOrders": "待处理工单",
                        "faultyDevices": "故障设备",
                        "offlineDevices": "离线设备",
                        "inspection": "巡检进度",
                        "scanQr": "扫描设备二维码",
                        "reportFault": "上报故障"
                      },
                      "common": {
                        "loading": "加载中...",
                        "error": "发生错误"
                      }
                    }
                    """);
                languagePackageRepository.save(zh);

                // en-US
                LanguagePackage en = new LanguagePackage();
                en.setCode("en-US");
                en.setName("English (US)");
                en.setVersion("1.0.0");
                en.setContent("""
                    {
                      "login": {
                        "title": "SPORTS ANT SaaS Login",
                        "username": "Username",
                        "password": "Password",
                        "submit": "Login",
                        "reset": "Reset",
                        "placeholder": {
                          "username": "Enter Username",
                          "password": "Enter Password"
                        }
                      },
                      "home": {
                        "welcome": "Welcome to SPORTS ANT SaaS",
                        "logout": "Logout",
                        "applications": "Applications",
                        "modules": {
                          "hq": { "title": "HQ Center", "desc": "Franchise & Global Management" },
                          "manager": { "title": "Store Manager", "desc": "Daily Operations & Staff" },
                          "frontdesk": { "title": "Front Desk", "desc": "Check-in & POS" },
                          "technician": { "title": "Technician", "desc": "Device Monitoring & Repair" },
                          "data": { "title": "Data Center", "desc": "Analytics & Reports" },
                          "marketing": { "title": "Marketing", "desc": "Campaigns & Members" },
                          "finance": { "title": "Finance", "desc": "Invoices & Payroll" },
                          "communication": { "title": "Communication", "desc": "Messages & Notifications" }
                        }
                      },
                      "technician": {
                        "title": "Technician Workbench",
                        "workOrders": "Work Orders",
                        "faultyDevices": "Faulty Devices",
                        "offlineDevices": "Offline Devices",
                        "inspection": "Inspection",
                        "scanQr": "Scan Device QR",
                        "reportFault": "Report Fault"
                      },
                      "common": {
                        "loading": "Loading...",
                        "error": "An error occurred"
                      }
                    }
                    """);
                languagePackageRepository.save(en);
                
                System.out.println(">>> Initialized Language Packages (zh-CN, en-US)");
            }
        };
    }
}
