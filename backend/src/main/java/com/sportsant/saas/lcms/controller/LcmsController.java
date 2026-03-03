package com.sportsant.saas.lcms.controller;

import com.sportsant.saas.common.response.Result;
import com.sportsant.saas.lcms.entity.LcmsKey;
import com.sportsant.saas.lcms.entity.LcmsProject;
import com.sportsant.saas.lcms.entity.LcmsTranslation;
import com.sportsant.saas.lcms.service.LcmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("lcmsControllerV2")
@RequestMapping("/api/lcms/v2")
@Tag(name = "多语言内容管理", description = "国际化文案、翻译管理")
public class LcmsController {

    @Autowired
    private LcmsService lcmsService;

    @GetMapping("/projects")
    @Operation(summary = "Get all projects")
    public Result<List<LcmsProject>> getProjects() {
        return Result.success(lcmsService.getAllProjects());
    }

    @PostMapping("/projects")
    @Operation(summary = "Create project")
    public Result<LcmsProject> createProject(@RequestBody LcmsProject project) {
        return Result.success(lcmsService.createProject(project));
    }

    @GetMapping("/projects/{code}/keys")
    @Operation(summary = "Get keys by project")
    public Result<List<LcmsKey>> getKeys(@PathVariable String code) {
        return Result.success(lcmsService.getKeysByProject(code));
    }

    @PostMapping("/keys")
    @Operation(summary = "Create translation key")
    public Result<LcmsKey> createKey(@RequestBody LcmsKey key) {
        return Result.success(lcmsService.createKey(key));
    }

    @PostMapping("/translations")
    @Operation(summary = "Update translation")
    public Result<LcmsTranslation> updateTranslation(@RequestParam Long keyId, @RequestParam String locale, @RequestBody Map<String, String> body) {
        return Result.success(lcmsService.updateTranslation(keyId, locale, body.get("value")));
    }

    @GetMapping("/bundle/{projectCode}/{locale}")
    @Operation(summary = "Get language bundle")
    public Result<Map<String, String>> getBundle(@PathVariable String projectCode, @PathVariable String locale) {
        return Result.success(lcmsService.getBundle(projectCode, locale));
    }
}
