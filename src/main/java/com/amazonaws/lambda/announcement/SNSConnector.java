package com.amazonaws.lambda.announcement;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.*;

public class SNSConnector {
    AmazonSNS amazonSNSClient;

    private static SNSConnector snsClientConnector = new SNSConnector();
    public static SNSConnector getInstance() {
        if (snsClientConnector == null) {
            snsClientConnector = new SNSConnector();
        }
        return snsClientConnector;
    }

    public AmazonSNS getAmazonSNSClient() {
        return amazonSNSClient;
    }

    public SNSConnector() {
        //create a new SNS client and set endpoint
    	
        //amazonSNS = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider());
        //AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider();
        //credentialsProvider.getCredentials();
        if (amazonSNSClient == null) {
            amazonSNSClient = AmazonSNSClientBuilder.defaultClient();
        }

        //snsClient = AmazonSNSClientBuilder.standard().
                //withRegion(Regions.US_EAST_2).build();
        
    }
}
