package com.amazonaws.lambda.isnewcourse;

import java.util.Map;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.*;
import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;
import com.amazonaws.services.stepfunctions.model.SendTaskFailureRequest;
import com.amazonaws.services.stepfunctions.model.SendTaskSuccessRequest;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;

public class isNewCourse implements RequestHandler<DynamodbEvent, Course> {
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
    DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);
    AWSStepFunctions stepFunctionClient = AWSStepFunctionsClientBuilder.standard()
            .withRegion("us-east-2")
            .build();  
    
    
    @Override
    public Course handleRequest(DynamodbEvent input, Context context) {
    	
    	context.getLogger().log("Input: " + input);
    	Course course = new Course();
		for(DynamodbStreamRecord record : input.getRecords()) {
			context.getLogger().log("eventid----"+record.getEventID());
            context.getLogger().log("eventname----"+record.getEventName());
            context.getLogger().log("dynamo---------"+record.getDynamodb().toString());
			if(record != null) {
				String event = record.getEventName();
				if(!event.equals("INSERT"))
					continue;
				
				Map<String, AttributeValue> map = record.getDynamodb().getNewImage();
				course.setId(map.get("Id").getS());
				course.setCourseId(map.get("courseId").getS());
				course.setProfessorId(map.get("professorId").getS());
				course.setTAId(map.get("TAId").getS());
				course.setDepartment(map.get("department").getS());
				dynamoDBMapper.save(course);
				if (isNewCourse(record)) {
					course.setStatus("NEW");
				} else {
					course.setStatus("OLD");
				}
			}
			
		}
		StartExecutionRequest startExecutionRequest = new StartExecutionRequest()
	    		.withStateMachineArn("arn:aws:states:us-east-2:432346481126:stateMachine:registerCoursesMachine");
		startExecutionRequest.setInput(course.toString());
		stepFunctionClient.startExecution(startExecutionRequest);
		
		return course;
    }
    
    public boolean isNewCourse(DynamodbStreamRecord record) {
    	Map<String, AttributeValue> map = record.getDynamodb().getNewImage();
		System.out.println(record.toString());
		for (String key : map.keySet()) {
			//System.out.println(map.get(key));
		}
		if (!map.containsKey("boardId") && !map.containsKey("enrolledStudent") && !map.containsKey("notificationTopic")) {
			return true;
		} else {
			return false;
		}
    }
}