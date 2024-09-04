package winkhant.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.GitHubUserActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String userName;
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter user name: ");
        userName = sc.nextLine();
        while (userName == null || userName.trim().isEmpty()){
            System.out.println("Invalid input! Please enter a valid user name.");
            userName = sc.nextLine();
        }
            String userUrl = "https://api.github.com/users/"+userName+"/events";
            try {
                URL url = new URL(userUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == 404){
                    System.out.println("Username doesn't exist");
                    System.exit(1);
                }
                System.out.println("Response Message:"+ connection.getResponseMessage());

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = br.readLine()) != null){
                    content.append(inputLine);
                }
                br.close();
                connection.disconnect();
                String fetchedJson = content.toString();
                if (fetchedJson.isEmpty()) {
                    System.out.println("The user " + userName + " has no actions yet.");
                } else if (fetchedJson.equals("[]")) {
                    System.out.println("The user " + userName + " has no public events.");
                } else {
                    ObjectMapper objectMapper = new ObjectMapper();
                    GitHubUserActivity[] activities = objectMapper.readValue(fetchedJson, GitHubUserActivity[].class);
                    for (GitHubUserActivity activity : activities) {
                        System.out.println("Type: " + activity.getType());
                        System.out.println("Repo: " + activity.getRepo().getName());
                        System.out.println("Actor: " + activity.getActor().getLogin());
                        System.out.println("----------");
                    }
                }

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }
}