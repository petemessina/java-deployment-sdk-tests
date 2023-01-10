package com.example.services;

import com.azure.core.credential.TokenCredential;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.fluent.models.WhatIfOperationResultInner;
import com.azure.resourcemanager.resources.models.DeploymentMode;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.resources.models.DeploymentWhatIf;
import com.azure.resourcemanager.resources.models.DeploymentWhatIfProperties;
import com.azure.core.util.Context;
import com.azure.core.management.serializer.SerializerFactory;
import com.azure.core.util.serializer.SerializerEncoding;

public class WhatIfDeploymentService {
    public  WhatIfOperationResultInner executeWhatIf(
        TokenCredential credentials,
        AzureProfile profile,
        String subscriptionId,
        String resourceGroupName,
        String deploymentName,
        String armContents,
        String parameters
    ) throws Exception {
        AzureResourceManager azureResourceManager = AzureResourceManager.authenticate(credentials, profile)
            .withSubscription(subscriptionId);

        return azureResourceManager
            .genericResources()
            .manager()
            .serviceClient()
            .getDeployments()
            .whatIf(
                resourceGroupName,
                deploymentName,
                new DeploymentWhatIf()
                    .withProperties(
                        new DeploymentWhatIfProperties()
                            .withTemplate(SerializerFactory
                            .createDefaultManagementSerializerAdapter()
                            .deserialize(armContents, Object.class, SerializerEncoding.JSON))
                            .withParameters(SerializerFactory
                                    .createDefaultManagementSerializerAdapter()
                                    .deserialize(parameters, Object.class, SerializerEncoding.JSON))
                            .withMode(DeploymentMode.INCREMENTAL)),
                Context.NONE);
    }
}
