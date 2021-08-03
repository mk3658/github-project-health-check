# Github project health metric
## Dependencies

All external libraries are included in build.gradle file.
- gson: used to convert Java Objects into their JSON representation and vice versa.

## How to make the project run
- The project is built with Gradle (7.1.1) and Java (1.8). Make sure your development environtment is well-prepared analogously.
    
        git clone https://github.com/mk3658/github-project-health-check.git
        
        cd quod-ai-challenge-minggjse62848@gmail.com
        
- To check health score last hour
        
        gradle run
        
- To check health score for a period
        
        gradle run --args='2021-07-10T02:00:00Z 2021-07-11T02:00:00Z'

## Technical decisions
### 1. What frameworks/libraries did the project use? What are the benefits of those libraries?
- The project is simply utilized the original data structure that Java provides, which is HashMap.
- HashMaps use an array in the background. Each element in the array is another data structure (usually a linked list or binary search tree). The HashMap uses a function on the key to determine where to place the key's value in the array.
- HashMap is faster than HashSet because the values are associated to a unique key.
- The limitation of HashMap for this project that it can just store maximum 2^30 elements.
- Besides, the project uses gson which can handle file structure.

### 2. How would you improve your code for performance?
- We can store data into database, because database can resolve the data storage and data retrieval problems.

### 3. What code would you refactor for readability and maintainability?
- To overcome this issue, we can try use a database to store all the information from file, or may use Google BigQuery API, Spark, etc.
