package com.amazonaws.lambda.register;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

import com.amazonaws.services.lambda.runtime.RequestHandler;

public class GenerateRegister implements RequestHandler<Course, Course> {
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
    DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);

    @Override
    public Course handleRequest(Course course, Context context) {
        context.getLogger().log("Input: " + course);
        Registrar registrar = new Registrar();
        registrar.setDepartment(course.getDepartment());
        registrar.setOfferingType("Course");
        registrar.setOfferingId(course.getCourseId());
        registrar.setPerUnitPrice("1540");
        registrar.setRegistrationId("Course_" + course.getCourseId());
        dynamoDBMapper.save(registrar);
        // TODO: implement your handler
        return course;
    }

}
