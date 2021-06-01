#!/bin/bash
sudo su
yum update -y
yum install httpd -y
service httpd start
cd /var/www/html
aws s3 sync s3://aliaksei-ryzhkou-site /var/www/html
cd /var/www
sudo aws s3api get-object --bucket aliaksei-ryzhkou-task2 --key awsinfo-0.0.1-SNAPSHOT.jar awsinfo-0.0.1-SNAPSHOT.jar
sudo amazon-linux-extras install java-openjdk11 -y
java -jar /var/www/awsinfo-0.0.1-SNAPSHOT.jar --server.port=8081 --sql.host=db-instance-private.cf34fj10bovf.eu-west-2.rds.amazonaws.com &

# install mysql client https://techviewleo.com/how-to-install-mysql-8-on-amazon-linux-2/