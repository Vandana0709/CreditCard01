package com.bank.portal.web;
import com.bank.portal.service.FeatureService; import com.bank.portal.web.ApiDtos.*; import jakarta.validation.Valid; import java.util.*; import lombok.RequiredArgsConstructor; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/features") @RequiredArgsConstructor public class FeatureController { private final FeatureService service;
 @GetMapping("/score") @PreAuthorize("hasRole('CUSTOMER')") public ScoreView score(Authentication a){return service.score(a.getName());}
 @PostMapping("/limit-requests") @PreAuthorize("hasRole('CUSTOMER')") public LimitRequestView request(@Valid @RequestBody LimitRequestInput r,Authentication a){return service.request(a.getName(),r);}
 @GetMapping("/limit-requests/mine") @PreAuthorize("hasRole('CUSTOMER')") public List<LimitRequestView> mine(Authentication a){return service.mine(a.getName());}
 @GetMapping("/limit-requests/pending") @PreAuthorize("hasRole('STAFF')") public List<LimitRequestView> pending(){return service.pending();}
 @PatchMapping("/limit-requests/{id}") @PreAuthorize("hasRole('STAFF')") public LimitRequestView review(@PathVariable Long id,@Valid @RequestBody ReviewInput r,Authentication a){return service.review(id,r,a.getName());}
 @PostMapping("/late-fees/run") @PreAuthorize("hasRole('STAFF')") public MessageView fees(){return new MessageView(service.applyLateFees()+" late fee(s) applied");}}
