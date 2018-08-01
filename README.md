# Slack Insults

Pass in your slack workspace, along with your username and password, and see random slackbot responses get generated

To run, simply have java and maven installed. Checkout the project, and run
```bash
mvn clean verify -Dbrowser=chrome -Dslack.workspace=[WORKSPACE] -Dslack.username=[USERNAME] -Dslack.password=[PASSWORD]
```