package com.produce.pickmeup.controller;

import com.produce.pickmeup.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping("/tags")
    public ResponseEntity<Object> getHotTagsList() {
        return ResponseEntity.ok().body(tagService.getHotTagsList());
    }
}
