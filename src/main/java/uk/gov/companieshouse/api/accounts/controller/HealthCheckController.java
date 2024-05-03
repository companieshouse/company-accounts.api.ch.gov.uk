package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthCheckController {

    @GetMapping("/company-accounts/healthcheck")
    public ResponseEntity<String> healthcheck() {
            return ResponseEntity.ok("UP");
    }
}
