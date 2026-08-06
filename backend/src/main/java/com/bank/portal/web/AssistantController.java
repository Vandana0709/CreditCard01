package com.bank.portal.web;

import com.bank.portal.service.MosaicAssistantService;
import com.bank.portal.web.ApiDtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AssistantController {
 private final MosaicAssistantService service;
 @PostMapping("/chat") @org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')") public AssistantChatView chat(@Valid @RequestBody AssistantChatRequest request,Authentication authentication){return service.chat(authentication.getName(),request);}
}