package com.ec.enterprise.conference.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ec.enterprise.conference.model.*;
import com.ec.enterprise.conference.service.ConferenceServiceImpl;

@RestController
@RequestMapping("/conference")
public class ConferenceController {

    @Autowired
    ConferenceServiceImpl conferenceService;

    @PostMapping("/schedule")
    public ResponseEntity<String> schedule(@RequestBody String data) {
        if(data==null||data.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El texto de entrada está vacío");
        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(conferenceService.scheduler(data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El texto de entrada está vacío");
        }
        
    }

    
}
