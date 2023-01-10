package com.example.services;

import java.net.URL;

import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.HttpMethod;

public class TemplateSpecService {
    private final HttpPipeline _httpPipeline;

    public TemplateSpecService(HttpPipeline httpPipeline) {
        _httpPipeline = httpPipeline;
    }

    public String saveTemplateSpecApi(
        String apiUrl,
        String spec
    ) throws Exception {    
        HttpRequest request = new HttpRequest(HttpMethod.PUT, new URL(apiUrl))
            .setHeader("Content-Type", "application/json")
            .setBody(spec);
        HttpResponse httpResponse = this._httpPipeline.send(request).block();
        
        return httpResponse.getBodyAsString().block();
    }  
}