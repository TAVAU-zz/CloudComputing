package com.amazonaws.lambda.seminars;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class isSeminars implements RequestHandler<Course, Course> {

    @Override
    public Course handleRequest(Course course, Context context) {
        context.getLogger().log("Input: " + course);
        if (course.getDepartment() == "Seminars") {
        	return course;
        } else {
        	return null;
        }
       
    }

}
