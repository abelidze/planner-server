package com.skillmasters.server;

import java.io.InputStream;
import java.io.IOException;
import biweekly.ICalVersion;
import biweekly.io.ParseContext;
import biweekly.io.scribe.property.RecurrenceRuleScribe;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.skillmasters.server.http.middleware.security.FirebaseAuthenticationTokenFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.skillmasters.server" } )
public class ServerApplication
{
  public static void main(String[] args)
  {
    SpringApplication.run(ServerApplication.class, args);
  }

  @Bean
  public FirebaseAuth firebaseAuth() throws IOException
  {
    // TODO: change before push
    if (FirebaseApp.getApps().size() == 0) {
      ClassLoader loader = ServerApplication.class.getClassLoader();
      InputStream serviceAccount = loader.getResourceAsStream("service_account.json");

      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .setDatabaseUrl("https://test-calendar-241815.firebaseio.com")
          .build();

      FirebaseApp.initializeApp(options);
    }
    return FirebaseAuth.getInstance();
  }

  @Bean
  public RecurrenceRuleScribe rruleScribe()
  {
    return new RecurrenceRuleScribe();
  }

  @Bean
  public ParseContext beweeklyContext()
  {
    ParseContext ctx = new ParseContext();
    ctx.setVersion(ICalVersion.V2_0);
    return ctx;
  }
}