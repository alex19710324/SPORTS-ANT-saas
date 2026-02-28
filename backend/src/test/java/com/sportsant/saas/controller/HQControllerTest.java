package com.sportsant.saas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class HQControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetGlobalOverview_AsAdmin() throws Exception {
        mockMvc.perform(get("/api/hq/dashboard/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").exists())
                .andExpect(jsonPath("$.storeCount").exists());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"HQ_MANAGER"})
    public void testGetGlobalOverview_AsHqManager() throws Exception {
        mockMvc.perform(get("/api/hq/dashboard/overview"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetGlobalOverview_AsUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/hq/dashboard/overview"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetGlobalOverview_Unauthenticated_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/hq/dashboard/overview"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetStoreMapData() throws Exception {
        mockMvc.perform(get("/api/hq/map/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stores").isArray());
    }
}
