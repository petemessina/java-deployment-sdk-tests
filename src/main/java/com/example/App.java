package com.example;

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.resources.fluent.models.WhatIfOperationResultInner;
import com.azure.resourcemanager.resources.fluentcore.utils.HttpPipelineProvider;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.resources.models.WhatIfChange;
import java.util.List;
import com.azure.core.http.HttpPipeline;

import com.example.services.BlobStorageService;
import com.example.services.TemplateSpecService;
import com.example.services.WhatIfDeploymentService;

public class App 
{
    public static void main( String[] args ) throws Exception
    { 
        String subscriptionId = "SUBSCRIPTIONID";
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        TokenCredential defaultCredential = new DefaultAzureCredentialBuilder()
            .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
            .build();
        
        String responseBody = callTemplateSpecApi(defaultCredential, profile, subscriptionId);
        System.out.println(responseBody);

        WhatIfOperationResultInner whatIfResponse = executeWhatIf(defaultCredential, profile, subscriptionId);
        
        if(whatIfResponse.error() != null) {
            System.out.println(whatIfResponse.error().getMessage());
        } else {
            List<WhatIfChange> changes = whatIfResponse.changes();

            for (WhatIfChange change : changes) 
            {
                System.out.println(change.changeType().name());
            }
        } 
    }

    static String callTemplateSpecApi(
        TokenCredential credentials, 
        AzureProfile profile,
        String subscriptionId
    ) throws Exception {
        HttpPipeline pipeline = HttpPipelineProvider.buildHttpPipeline(credentials, profile);
        TemplateSpecService templateSpecService = new TemplateSpecService(pipeline);
        String specName = "myapispec2";
        String apiVersion = "2021-05-01";
        String apiUrl = "https://management.azure.com/subscriptions/" + subscriptionId + "/resourceGroups/RESOURCEGROUPNAME/providers/Microsoft.Resources/templateSpecs/" + specName + "?api-version=" + apiVersion;
        
        String spec = "{"+
            "\"location\": \"eastus\","+
            "\"properties\": {"+
            "\"description\": \"A very simple Template Spec\""+
            "}"+
        "}";  

        return templateSpecService.saveTemplateSpecApi(apiUrl, spec);
    }

    static String getBlobContent() throws Exception {
        String endpoint = "https://BLOBNAME.blob.core.windows.net/";
        String sasToken = "SASTOKEN";
        BlobStorageService blobStorageService = new BlobStorageService(endpoint, sasToken);

        return blobStorageService.getContent("arm", "testarm.json");
    }

    static WhatIfOperationResultInner executeWhatIf(
        TokenCredential credentials,
        AzureProfile profile,
        String subscriptionId
    ) throws Exception {
        WhatIfDeploymentService whatIfDeploymentService = new WhatIfDeploymentService();
        String resourceGroupName = "RESOURCEGROUPNAME";
        String deploymentName = "my-deployment";
        String blobContent = getBlobContent();
        String parameters = "{"+
                "\"storageAccountName\": {"+
                "\"value\": \"BLOBNAME\""+
            "}" +
        "}";

        return whatIfDeploymentService.executeWhatIf(
            credentials,
            profile,
            subscriptionId,
            resourceGroupName,
            deploymentName,
            blobContent,
            parameters
        );
    }
}
