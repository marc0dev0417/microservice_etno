package com.etno.microservice.configuration.swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.view.InternalResourceViewResolver
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.builders.ResponseBuilder
import springfox.documentation.oas.annotations.EnableOpenApi
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import java.util.*


@Configuration
@EnableOpenApi
class SwaggerDocumentationConfig {
    private fun apiInfo(): ApiInfo{
        return ApiInfo(
            "ETNO MICROSERVICE",
            "API documentation where you be able to see how to operate about ETNO",
            "API ETNO",
            "Terms of service",
            Contact("Ecomputer", "https://www.ecomputer.es/", "central@ecomputer.es"),
            "License of API ETNO", "API license URL", Collections.emptyList()
        )
    }
    @Bean
    fun api(): Docket{
        return Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .globalResponses(HttpMethod.GET, listOf(ResponseBuilder().code("500").description("Error in server").build(),
                                                    ResponseBuilder().code("403").description("Forbidden endpoint").build()))
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.etno.microservice.controller"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo())
    }

    @Bean
    fun defaultViewResolver(): InternalResourceViewResolver? {
        return InternalResourceViewResolver()
    }
}