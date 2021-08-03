# Github project health metric
## Dependencies

All external libraries are included in build.gradle file.

    gson: used to convert Java Objects into their JSON representation and vice versa.

## How to make the project run

    The project is built with Gradle (7.1.1) and Java (1.8). Make sure your development environtment is well-prepared analogously.
    git clone https://github.com/mk3658/github-project-health-check.git
    cd HealthScoreCalculator
    To check health score last hour

        gradle run

    To check health score for a period

        gradle run --args='2021-07-10T02:00:00Z 2021-07-11T02:00:00Z'

## Technical decisions

    The project is simply utilized the pure data structure that Java provides, it is HashMap.
    Basically, the project try to used HashMap as a table in database to store and perform some calculation on the data set.
    
    However, if we run the project with the real file with more than 380 MB, it may be pretty slow.
    To overcome this issue, we can try use a database to store all the information from file, or may use Google BigQuery API, Spark, etc.
    
    Also, gson was used to handle file structure.
