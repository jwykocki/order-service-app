Few comments:
- Rename the package to exception. In java we have 2 types of exceptions: Checked and unchecked. Inside unchecked we have Error exceptions which are mostly thrown by the JVM and it's not recommend for us to "catch" it - I even not sure if it's possible, specially because if you get an Error exception, your app is crashed :D  
  - https://www.oracle.com/technical-resources/articles/enterprise-architecture/effective-exceptions-part1.html
  - https://www.geeksforgeeks.org/exceptions-in-java/
  - https://www.geeksforgeeks.org/errors-v-s-exceptions-in-java/
- Do we really need all these exceptions? I'm not against create some specifics exceptions, but we should take care to do not create so many exception that it can end-up with more exception than service/mappers/entities classes on our service :)