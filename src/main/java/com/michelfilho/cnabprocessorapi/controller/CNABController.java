package com.michelfilho.cnabprocessorapi.controller;

import com.michelfilho.cnabprocessorapi.service.CNABService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/cnab")
public class CNABController {

    CNABService service;

    public CNABController(CNABService service) {
        this.service = service;
    }

    @PostMapping("upload")
    @CrossOrigin(origins = {"http://localhost:9090"})
    public String upload(@RequestParam("file")MultipartFile file) throws Exception {
        service.process(file);
        return "Process initialized!";
    }
}
