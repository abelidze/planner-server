package com.skillmasters.server;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import com.fasterxml.classmate.TypeResolver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import static springfox.documentation.schema.AlternateTypeRules.newRule;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = { "com.skillmasters.server" } )
public class ServerApplication
{
  public static void main(String[] args)
  {
    SpringApplication.run(ServerApplication.class, args);
  }

  @Autowired
  private TypeResolver typeResolver;

  @Bean
  public Docket apiVersion1()
  {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("api-v1")
        .select()
          .apis(RequestHandlerSelectors.basePackage("com.skillmasters.server.http"))
          .paths(PathSelectors.regex("/api/v1.*"))
          .build()
        .apiInfo(new ApiInfoBuilder()
            .title("Planer API Service")
            .version("1.0")
            .contact(new Contact(
                "Abelidze",
                "https://abelidze.github.io",
                "abel_9@mail.ru"
              ))
            // .description("\"Spring Boot REST API\"")
            // .termsOfServiceUrl("...")
            // .license("MIT License")
            // .licenseUrl("https://raw.githubusercontent.com/abelidze/planer-server/master/LICENSE")
            .build())
        .pathMapping("/")
        .genericModelSubstitutes(ResponseEntity.class)
        .produces(new HashSet(Arrays.asList("application/json")))
        .consumes(new HashSet(Arrays.asList("application/json")))
        .alternateTypeRules(
            newRule(
                typeResolver.resolve(DeferredResult.class,
                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                typeResolver.resolve(WildcardType.class)))
        .useDefaultResponseMessages(false)
        .globalResponseMessage(RequestMethod.GET, globalResponses())
        .globalResponseMessage(RequestMethod.POST, globalResponses())
        .globalResponseMessage(RequestMethod.PUT, globalResponses())
        .globalResponseMessage(RequestMethod.PATCH, globalResponses())
        .globalResponseMessage(RequestMethod.DELETE, globalResponses())
        .securitySchemes(Arrays.asList(apiKey()))
        .securityContexts(Arrays.asList(securityContext()))
        .enableUrlTemplating(false);
        // .tags(new Tag("Service", "All apis relating to ..."));
  }

  private List<ResponseMessage> globalResponses()
  {
    return Arrays.asList(
        new ResponseMessageBuilder()
          .code(200)
          .message("OK")
          .build(),
        new ResponseMessageBuilder()
          .code(204)
          .message("No content")
          .build(),
        new ResponseMessageBuilder()
          .code(400)
          .message("Bad request")
          .build(),
        new ResponseMessageBuilder()
          .code(401)
          .message("You are not authorized to view the resource")
          .build(),
        new ResponseMessageBuilder() 
          .code(403)
          .message("Accessing the resource you were trying to reach is forbidden")
          .build(),
        new ResponseMessageBuilder() 
          .code(404)
          .message("The resource you were trying to reach is not found")
          .build(),
        new ResponseMessageBuilder()
          .code(500)
          .message("Internal error")
          .build()
      );
  }

  private ApiKey apiKey()
  {
    return new ApiKey("access_token", "api_key", "header");
  }

  private SecurityContext securityContext()
  {
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(PathSelectors.regex("/anyPath.*"))
        .build();
  }

  List<SecurityReference> defaultAuth()
  {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Arrays.asList(
        new SecurityReference("mykey", authorizationScopes)
      );
  }

  @Bean
  SecurityConfiguration security()
  {
    return SecurityConfigurationBuilder.builder()
        .clientId("test-app-client-id")
        .clientSecret("test-app-client-secret")
        .realm("test-app-realm")
        .appName("test-app")
        .scopeSeparator(",")
        .additionalQueryStringParams(null)
        .useBasicAuthenticationWithAccessCodeGrant(false)
        .build();
  }

  @Bean
  UiConfiguration uiConfig()
  {
    return UiConfigurationBuilder.builder()
        .deepLinking(true)
        .displayOperationId(false)
        .defaultModelsExpandDepth(1)
        .defaultModelExpandDepth(1)
        .defaultModelRendering(ModelRendering.EXAMPLE)
        .displayRequestDuration(false)
        .docExpansion(DocExpansion.NONE)
        .filter(false)
        .maxDisplayedTags(null)
        .operationsSorter(OperationsSorter.ALPHA)
        .showExtensions(false)
        .tagsSorter(TagsSorter.ALPHA)
        .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
        .validatorUrl(null)
        .build();
  }
}
