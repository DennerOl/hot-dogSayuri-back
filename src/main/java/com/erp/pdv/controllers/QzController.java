package com.erp.pdv.controllers;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.pdv.service.QzSignatureService;

@RestController

@RequestMapping("/api/qz")
public class QzController {

    private final QzSignatureService signatureService;

    public QzController(QzSignatureService signatureService) {
        this.signatureService = signatureService;
    }

@PostMapping(
    value = "/sign",
    consumes = MediaType.TEXT_PLAIN_VALUE,
    produces = MediaType.TEXT_PLAIN_VALUE
)
public ResponseEntity<String> sign(@RequestBody String payload) {
    return ResponseEntity.ok(signatureService.sign(payload));
}



 @PostMapping(
    value = "/test-sign",
    consumes = MediaType.TEXT_PLAIN_VALUE
)
public ResponseEntity<String> testSign(@RequestBody String payload) {
    return ResponseEntity.ok(signatureService.sign(payload));
}

}
