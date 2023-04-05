## HelloSign integration with Spring Boot
Code sample in this repository demonstrates how to easily integrate electronic signature
into your Spring Boot application using HelloSign API.
HelloSign is one of the most popular software platforms for electronic signatures.
The company provides developer tools that simplify integration with your application.
### Prerequisites
* Java 8+
* Maven ^3.6.0
* [HelloSign Account](https://app.hellosign.com/account/signUp)
### Configuration
To use this code sample you need to change the **hello-sign-properties.apiKey, hello-sign-properties.clientAppId**
in **__application.properties__** file.
### How to Run
* From IDEA: run the **__HelloSignApplication.class__**
* From CLI: run command `mvn spring-boot:run`
### Usage Guide & Tips
First of all, we need to create an app on the HelloSign portal and fill event callback. <br />
<img src="https://user-images.githubusercontent.com/29039912/229996859-af0204f8-abc0-4366-a2dc-72d052b052d4.png" width="300">
<img src="https://user-images.githubusercontent.com/29039912/229996880-a899e447-a357-4a6f-96ce-e099704e2445.png" width="300"> <br/>
**IMPORTANT**: The route to the callback endpoint should be open.
### Community
* Please send us your suggestions on how we make this code even more useful for the development community or contribute to this repo!
* Check out our [blog](https://oril.co/blog) with more articles!
