package com.ec.enterprise.conference.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Track {
    
    Session moorningSession;
    Session afternoonSession;
    Session lunch;
    Session networking;
    
}
