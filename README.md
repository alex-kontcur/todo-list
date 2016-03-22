-------------------------------------------------------------------------------------------------------------

INSTRUCTIONS:

1. To build and start test application you need java 7 and maven-3.0.4 installed on your system.
Java version is a mandatory requirement due to source code contains java 7 features.
Use "mvn clean package" command to create program zip archive

2. While developing I used H2 as database layer. You can switch it to MySQL or PostgreSQL with db/hibernate settings
in todolist.conf property file.

3. To start a program unpack archive and start {win|deb}-run.bat. All configurations are in the "conf" folder.
All logs will appear in the "logs" folder. The UI of the application will be available at system tray icon with
question sign.

4. Hibernate configuration contains property for automatic creating db tables hibernate.hbm2ddl.auto=create
Change to "update" to aviod of deleting your existing data.

5. While first startup there will no login dialog because there no users in db. So admin/admin user 
will be created. Before next login you must change property hbm2ddl.auto to "update" to be able 
login with admin/admin or some later created user.

-------------------------------------------------------------------------------------------------------------

TECHNOLOGIES USED:

 - Java 7
 - Slf4j, Hibernate
 - Spring IOC/Data-JPA
                   
-------------------------------------------------------------------------------------------------------------

IMPLEMENTED FEATURES:

 - managing of users by administrators
 - search task by subject
 - task comments
 - task attachments (saving to DB, running from GUI)
 - observing tasks of another users
 - alerting before task must begin

-------------------------------------------------------------------------------------------------------------

POTENTIAL FEATURES:

 - linked tasks
 - parent/child user relationships with the appointment of tasks for "children"
 - alerting when some child complete task
 - alerting another kinds: email, popup, etc.
 - personal settings for postpone tasks and monitoring interval



