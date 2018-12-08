This project is just for lambda,  
For other parts of the assignment please check https://github.com/TAVAU/ColoudComputing1

Assignment 3, part 4
Using lambda publish subscribed topics.
For example:
For the student who have subscribed topic"cs700"
POST in http://csye6225-env.25auac3tzp.us-east-2.elasticbeanstalk.com/webapi/announcements/
with body
{
    "announcementId": "001",
    "announcementText": "Introduction to Classes, objects and methods",
    "boardId": "cs700"
}

students will get notification email about the new posted announcement of related topic.
