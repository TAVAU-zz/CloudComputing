package com.amazonaws.lambda.board;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

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


public class GenerateBoard implements RequestHandler<Course, String> {

	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
    DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);

    @Override
    public String handleRequest(Course course, Context context) {
        context.getLogger().log("Input: " + course);
        //dynamoDBMapper.delete(course);
        Board board = new Board();
        board.setCourseId(course.getCourseId());
        board.setBoardId(course.getCourseId());
        course.setBoardId(course.getCourseId());
        dynamoDBMapper.save(board);
        dynamoDBMapper.save(course);
        // TODO: implement your handler
        return "Yes! We added a new board";
    }

}
