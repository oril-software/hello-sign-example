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
<img src="/Users/oril/IdeaProjects/hello-sign/src/main/resources/images/create_app.png" style="display: inline-block; margin: 0 auto; max-width: 300px"/> <br />
<img src="/Users/oril/IdeaProjects/hello-sign/src/main/resources/images/event_callback.png" style="display: inline-block; margin: 0 auto; max-width: 300px"/> <br />
**IMPORTANT**: The route to the callback endpoint should be open.
### Community
* Please send us your suggestions on how we make this code even more useful for the development community or contribute to this repo!
* Check out our [blog](https://oril.co/blog) with more articles!
