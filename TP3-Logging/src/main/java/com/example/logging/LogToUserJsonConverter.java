package com.example.logging;

public class LogToUserJsonConverter {
     public static void main(String[] args) throws Exception {
          LogParser logParser = new LogParser();
          logParser.parse("logs/app.log","logs/user-profiles.json");
     }
}
