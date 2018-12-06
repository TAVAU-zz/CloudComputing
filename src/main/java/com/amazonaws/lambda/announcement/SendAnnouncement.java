package com.amazonaws.lambda.announcement;

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
import com.amazonaws.lambda.datamodel.*;



import java.util.*;

public class SendAnnouncement implements RequestHandler<DynamodbEvent, String> {

	AmazonSNS SNS_CLIENT = AmazonSNSClientBuilder.defaultClient();
	String SNS_PREFFIX = "arn:aws:sns:us-east-2:432346481126:";
	
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
    DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(client);
    
	
    @Override
    public String handleRequest(DynamodbEvent input, Context context) {
    	
     // Read DDB Records
		context.getLogger().log("Input: " + input);
		
		for(DynamodbStreamRecord record : input.getRecords()) {
			context.getLogger().log(record.getEventID());
            context.getLogger().log(record.getEventName());
            context.getLogger().log(record.getDynamodb().toString());
			if(record != null) {
				String event = record.getEventName();
				if(!event.equals("INSERT"))
					continue;
				
				String boardId = getBoardId(record);
				String courseId = getCourseId(boardId);
				String SNSTopic = getSNSTopic(courseId);
				
				String subject = "New Announcement - " + boardId;
				StringBuilder outputBody = new StringBuilder();
	    			outputBody.append("Here is a new announcement:\n");
	    			outputBody.append(getAnnouncementText(record));
	    			String topicArn = SNS_PREFFIX + SNSTopic;
	    			sendEmailNotification(topicArn, subject, outputBody.toString());
			}
		}
		return input.toString();
    }
    
    public void sendEmailNotification(final String topicArn, final String subject, final String message) {
		PublishRequest pr = new PublishRequest(topicArn, message, subject);
		PublishResult publishResult = SNS_CLIENT.publish(pr);
		System.out.println("MessageId - " + publishResult.getMessageId());
	}
	
	public String getBoardId(DynamodbStreamRecord record) {
		Map<String, AttributeValue> map = record.getDynamodb().getNewImage();
		System.out.println(record.toString());
		String boardId = map.get("boardId").getS();
		return boardId;
	}
	
	public String getCourseId(String boardId) {
		Board myBoard= new Board();
        myBoard.setBoardId(boardId);
        DynamoDBQueryExpression<Board> queryExpression = new DynamoDBQueryExpression<Board>();
        queryExpression.setHashKeyValues(myBoard);
        queryExpression.withIndexName("boardId-index");//not necessary
        queryExpression.setConsistentRead(false);
        List<Board> boards = dynamoDBMapper.query(Board.class, queryExpression);
        return boards.get(0).getCourseId();
	}
	
	public String getSNSTopic(String courseId) {
		Course myCourse= new Course();
        myCourse.setCourseId(courseId);
        DynamoDBQueryExpression<Course> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.setHashKeyValues(myCourse);
        queryExpression.withIndexName("courseId-index");
        queryExpression.setConsistentRead(false);
        List<Course> courses = dynamoDBMapper.query(Course.class, queryExpression);
        return courses.get(0).getNotificationTopic();
	}
	
	private String getAnnouncementText(DynamodbStreamRecord record) {
		Map<String, AttributeValue> map = record.getDynamodb().getNewImage();
		String text = map.get("announcementText").getS();
		return text;
	}
}