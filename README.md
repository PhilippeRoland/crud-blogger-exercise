# How to use

With Gradle installed, run *./gradlew clean bootRun* from the main project repository.
Once you can see "Started BloggerApplication" in the console, an in-memory database will be running with the schema and a local server running on port 8080, awaiting commands.

## Sending commands
If you use Postman, you can import the collection provided in the repository to easily test the application.
Otherwise, here are some commands you can try:

### Post creation
*Creation without tags*\
curl -X POST -H "Content-Type: application/json" localhost:8080/posts -d '{"title":"Test title", "description":"Test description", "author":"Philippe"}}'

*Creation with tags*\
curl -X POST -H "Content-Type: application/json" localhost:8080/posts -d '{"title":"My First Post", "description":"Blog post content", "author":"Philippe Roland", "tags": [{"name":"my Tag"}, {"name":"testTag"}]}'


### Post retrieval
*Plain GET, no filters or sorting*\
curl -X GET -H "Content-Type: application/json" localhost:8080/posts

*Sorting by date DESC*\
curl -X GET -H "Content-Type: application/json" localhost:8080/posts?order=desc&sort_by=creationDate

*Sorting by author name ASC*\
curl -X GET -H "Content-Type: application/json" localhost:8080/posts?order=asc&sort_by=author

*Sorting by date ASC AND filtering by Tag*\
curl -X GET -H "Content-Type: application/json" localhost:8080/posts?tag_filter=testTag&order=asc&sort_by=creationDate

## Database Access
The H2 in-memory database can be accessed at any time by visiting http://localhost:8080/h2-console using the default username / password ("sa", "")

# What's missing for a production-ready app

## CI/CD
A proper testing and integration chain is mandatory.

Additionally, I ran out of time before I could introduce unit tests, but they would be needed for this to pass code reviews

## Deployment considerations
A few things are also missing here. Logging is still very rudimentary and would have to be configured to not just use the console but save to a file.

Database URL and configuration, username and password will obviously have to be set per environment (and possibly encrypted) for security reasons. We used Ansible (+vault) over at Lyra for our deployments and it worked quite well.

Should a Docker / Kubernetes deployment be desired, the relevant container configuration should also be created

## REST and API design
Pagination on GETs was not added but would be quite simple to introduce given that my repository objects both extend ListPagingAndSortingRepository. Not paginating would lead to massive over-fetching in most use cases, such as a human person browsing a few recent posts.

A proper documentation for ease of implementation (of particular note: listing acceptable parameters for the sorting operations) would be great. I've had good experiences using Swagger (now OpenAPI) and would look to that first.

Character limits were not added (I was unsure as to how to do it for H2) but are a must, if only so as to avoid the database being overloaded by accident.

# Other possible improvements / added functionality
It was mentioned in the email with the test instructions, but user log-in / authentication is a must (for functional and possibly legal reasons). Other interesting features this would allow include deletion or editing of posts, plus having different user levels to allow for effective moderation.
One way to do this would be to use sessionIds in cookies and use those to restrict access to certain operations and/or resources

# Other remarks
I used an H2 in-memory database for ease of use and testing, but the actual database to be used in production would have to be different. Which one to use will depend heavily on the usecases and expected issues / performance bottlenecks

I'm not certain the provided indexes will work well in case of a multi-sort / filter GET request, this would warrant double-checking

