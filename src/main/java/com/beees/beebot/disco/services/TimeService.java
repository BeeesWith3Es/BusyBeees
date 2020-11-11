package com.beees.beebot.disco.services;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class TimeService {

    public ZonedDateTime getTimeFromNow(String timeString) throws Exception{
        ZonedDateTime time = ZonedDateTime.now();
        if(timeString.contains("m")){
            String[] splitString = timeString.split("m");
            int minutes = Integer.parseInt(splitString[0]);
            time = time.plusMinutes(minutes);
            timeString = splitString.length>=2 ? splitString[1] : "";
        }
        if (timeString.contains("h")){
            String[] splitString = timeString.split("h");
            int hours = Integer.parseInt(splitString[0]);
            time = time.plusHours(hours);
            timeString = splitString.length>=2 ? splitString[1] : "";
        }
        if(timeString.contains("d")){
            String[] splitString = timeString.split("d");
            int days = Integer.parseInt(splitString[0]);
            time = time.plusDays(days);
        }
        return time;
    }

    public String getPrettyTime(ZonedDateTime time){
        ZonedDateTime now = ZonedDateTime.now();
        String prettyTime = "";

        if(now.until(time, ChronoUnit.HOURS)<=6){
            String minutes = "";
            if (now.until(time, ChronoUnit.MINUTES)%60 != 0){
                minutes = " "+ now.until(time, ChronoUnit.MINUTES) % 60 +" minutes.";
            }
            String hours = now.until(time, ChronoUnit.HOURS) == 0? "In" : "In "+now.until(time, ChronoUnit.HOURS)+" hours"+(minutes.equals("")?".":" and");
            return hours+minutes;
        }

        if(time.getDayOfYear()-now.getDayOfYear() == 0) {
            prettyTime = "Today";
        }
        else if(time.getDayOfYear()-now.getDayOfYear() == 1){
            prettyTime = "Tomorrow";
        } else if(time.getDayOfYear()-now.getDayOfYear() < 0){
            prettyTime = (time.getDayOfYear()-now.getDayOfYear())+" days ago";
        } else  {
            prettyTime = (time.getDayOfYear()-now.getDayOfYear())+" days from now";
        }

        prettyTime = prettyTime.concat(" at "+time.format(DateTimeFormatter.ofPattern("HH:mm"))+" UTC");

        return prettyTime;
    }
}
