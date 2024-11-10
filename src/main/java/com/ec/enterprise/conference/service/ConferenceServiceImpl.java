package com.ec.enterprise.conference.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ec.enterprise.conference.model.Session;
import com.ec.enterprise.conference.model.Talk;
import com.ec.enterprise.conference.model.Track;

@Service
public class ConferenceServiceImpl implements IConferenceService{


    private List<Talk> _talks = new ArrayList<>();
    private StringBuilder schedule = new StringBuilder();
    
    public String scheduler(String data) {

        List<Talk> talks = new ArrayList<>();
        List<String> data_lst = Arrays.asList(data.split("\n"));

        // descarto lineas vacías
        List<String> data_lst_filt = data_lst.stream().filter(s -> s.length() > 1).collect(Collectors.toList());

        // Creo las charlas
        talks = create_talks(data_lst_filt);

        // pongo al final las charlas sin valor de duración, pero mantengo el orden
        // original
        List<Talk> talks_ord = talks.stream().filter(t -> t.getDuration_minutes() > 5).collect(Collectors.toList());
        List<Talk> talks_lightning = talks.stream().filter(t -> t.getDuration_minutes() == 5).collect(Collectors.toList());
        talks_ord.addAll(talks_lightning);
        _talks = talks_ord;

        List<Track> tracks = new ArrayList<>();
        while (!_talks.isEmpty()) {
            // Creo las sesiones
            Session moorningSession = new Session();
            moorningSession.setStart(LocalTime.of(9, 0));
            moorningSession.setFinish(LocalTime.of(12, 0));
            Session afternoonSession = new Session();
            afternoonSession.setStart(LocalTime.of(13, 0));
            afternoonSession.setFinish(LocalTime.of(16, 0));
            Session lunch = new Session(null, LocalTime.of(12, 0), LocalTime.of(13, 0), "Lunch");
            Session networking = new Session(null, LocalTime.of(16, 0), LocalTime.of(17, 0), "Networking Event");

            moorningSession = add_talksToSession(_talks, moorningSession);
            afternoonSession = add_talksToSession(_talks, afternoonSession);

            // creo el primer track
            Track track = new Track(moorningSession, afternoonSession, lunch, networking);
            tracks.add(track);
        }

        // Escribiendo la respuesta
        int num_track=1;
        for (Track track : tracks) {
            schedule.append("Track "+num_track+":\r\n");
            schedule.append(track_text(track));
            num_track++;
        }

         //return tracks;
         return schedule.toString();
    }

    public List<Talk> create_talks(List<String> data_lst_filt) {
        //crea una lista de las talks con nombre y duracion
        List<Talk> talks = new ArrayList<>();
        for (String s : data_lst_filt) {

            // Busco la duración en minutos de la charla
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(s);

            int duration = 0;// la duración es 0 si no la tiene
            String num = "";
            
            if (matcher.find()) {
                num = matcher.group();
                duration = Integer.parseInt(num);
            }else{
                //los lightning duran 5 minutos
                duration = 5;
            }

            // obtengo la ubicacion del titulo gracias a la ubicacion de la duración
            String title = duration == 5 ? s.substring(0, s.length() - 1).trim()
                    : s.substring(0, s.indexOf(num)).trim();
            talks.add(new Talk(title, duration));
        }
        return talks;
    }

    public Session add_talksToSession(List<Talk> talks, Session session) {
        //Agrego las talks a las sessions, quitando de la lista los elmentos ya usados
        
        int available_time = 0;
        // Agrego charlas a la sesión de la mañana
        available_time = session.getFinish().getHour() * 60 - session.getStart().getHour() * 60;
        List<Talk> session_talks = new ArrayList<>();
        //Iterator<Talk> iterator = talks.iterator();

         for (Talk t : talks) { 
            if ( available_time >= t.getDuration_minutes())// valido que no sobrepase el tiempo de la sesión
            {
                session_talks.add(t);
                available_time -= t.getDuration_minutes();
            }
        }
        
        _talks.removeAll(session_talks);
        session.setTalks(session_talks);
        return session;

    }

    public String track_text(Track track) {
        //Combina el texto de las 4 sesiones que contiene un track
        String s1 = talk_text(track.getMoorningSession());
        String s2 = talk_text(track.getLunch());
        String s3 = talk_text(track.getAfternoonSession());
        String s4 = talk_text(track.getNetworking());

        return s1 + s2 + s3 + s4;
    }

    public String talk_text(Session session) {
        //Devuelve el texto con formato de cada talk con su respectiva hora
        LocalTime talk_time = null;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("hh:mm a");
        String text="";
        if ((session.getTalks() == null || session.getTalks().isEmpty())
        &&session.getName()!=null) {
            
            return ""+session.getStart().format(formato) + " " + session.getName() + "\r\n";
        }

        for (int index = 0; index < session.getTalks().size() ; index++) {
            Talk t = session.getTalks().get(index);
            if (index == 0) {
                talk_time = session.getStart();
                text+= talk_time.format(formato) + " " + t.getTitle() + "\r\n";
                //schedule.append(talk_time.format(formato) + " " + t.getTitle() + "\r\n");
            } else {
                talk_time = talk_time.plusMinutes(session.getTalks().get(index - 1).getDuration_minutes());
                text+= talk_time.format(formato) + " " + t.getTitle() + "\r\n";
            }
        }
        return text;
    }
}
