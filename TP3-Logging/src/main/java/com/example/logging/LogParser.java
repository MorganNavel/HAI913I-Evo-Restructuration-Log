package com.example.logging;

import com.example.logging.models.LogEntry;
import com.example.logging.models.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LogParser {
     static final Map<Long, UserProfile> userProfileList = new HashMap<>();
     public void parse(String logFilename, String outputFilename) {
          ObjectMapper objectMapper = new ObjectMapper();

          try (BufferedReader br = new BufferedReader(new FileReader(logFilename))) {
               String line;
               while ((line = br.readLine()) != null) {
                    int jsonStart = line.indexOf("- {");
                    if (jsonStart != -1) {
                         String json = line.substring(jsonStart + 2).trim();

                         // Validation du JSON (et gestion des lignes suivantes si nécessaire)
                         if (!json.endsWith("}")) {
                              StringBuilder sb = new StringBuilder(json);
                              while ((line = br.readLine()) != null && !line.trim().endsWith("}")) {
                                   sb.append(line.trim());
                              }
                              if (line != null) {
                                   sb.append(line.trim());
                              }
                              json = sb.toString();
                         }

                         try {
                              LogEntry logEntry = objectMapper.readValue(json, LogEntry.class);
                              long userId = logEntry.getUserId();
                              userProfileList.putIfAbsent(userId, new UserProfile(logEntry.getUserDetails()));
                              updateProfile(userId, logEntry);
                         } catch (Exception e) {
                              System.err.println("Failed to parse JSON: " + json);
                              e.printStackTrace();
                         }
                    }
               }
               // Écriture des résultats dans un fichier JSON
               writeProfilesToFile(outputFilename);

          } catch (IOException e) {
               throw new RuntimeException("Error while processing logs", e);
          }
     }
     private void updateProfile(long userId, LogEntry log) {

          UserProfile userProfile = userProfileList.get(userId);
          switch (log.getOperation()){
               case "READ": userProfile.incrementReadCount();break;
               case "WRITE": userProfile.incrementWriteCount();break;
               default: break;
          }
          if(log.getProductDetails() != null){
               userProfile.trackProductPrice(log.getProductDetails().getPrice());
          }

     }
     private void writeProfilesToFile(String outputFilename) {
          ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
          try {
               // Vérifier si le fichier de sortie existe, sinon le créer
               File outputFile = new File(outputFilename);
               if (!outputFile.exists()) {
                    if (outputFile.createNewFile()) {
                         System.out.println("File created: " + outputFilename);
                    } else {
                         System.err.println("Failed to create file: " + outputFilename);
                    }
               }

               // Écriture des données dans le fichier
               try (FileWriter fileWriter = new FileWriter(outputFile)) {
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(fileWriter, userProfileList);
                    System.out.println("Profiles successfully written to " + outputFilename);
               }

          } catch (IOException e) {
               throw new RuntimeException("Error while writing profiles to file", e);
          }
     }
}

