package dev.mikeintoch;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class ReviewRoute extends RouteBuilder{

    @Inject
    ReviewAgent aiAgent;

    @Override
    public void configure() throws Exception {
        // Receive reviews from kafka.
        from("kafka:income-reviews")
          .to("direct:aiAgent");
        
        //Quarkus AI Agent receives review and process message
        from("direct:aiAgent")
           .unmarshal().json(JsonLibrary.Gson)
           .log("${body}")
           .bean(aiAgent)
           .log("Agent Answer: ${body}")
           .to("direct:checkEvaluation");

        // Compare response Agent and send to correct NEGATIVE or POSITIVE topic.
        from("direct:checkEvaluation")
          // Convert to Text.
         .marshal().json(JsonLibrary.Gson)
         // Convert to JSon to manipualte
         .unmarshal().json(JsonLibrary.Gson)
         .setHeader("evaluation").simple("${body[evaluation]}")
         .choice()
           .when(header("evaluation").isEqualTo(Evaluation.POSITIVE))
                .to("kafka:positive")
           .otherwise()
               .to("kafka:negative");
    }
    
}