# Echo solution

### Requirement
* 	Submit only 1 file, named SOLUTION-<YOUR-NAME>.md, written in Markdown format.
* 	Include your name and email.
* 	Include the link to test the REST API endpoint and the link to the public repository for the application.
*	Include the login credentials for the tester’s readonly user account on AWS.
*	Include setup instructions for your solution. The tester will attempt to replicate your solution based on these instructions.

Lixia Yuan <lixiayuan@hotmail.com>

### REST API application

* Git repo: <https://github.com/lixiayuan/echorest>
* REST API endpoint:  <http://5d204b8b-echorestnamespace-3331-1985697944.ap-southeast-1.elb.amazonaws.com/echo>

##### REST API Definition:
* Method: POST /echo  
Example with any query params: 
```
    POST /echo?name=Amy&age=6
```
* Description: 
```
    Echoes the querystring params and request body from the request to the response. 
    Requst IP and user agent will be included in the response body too.
```
* Request header: 
```
    Content-Type:application/json
```
* Request body: Json format string. Example:
```
    {
        "id": 1,
        "content": "Hello World"
    }
```

* Response Body: Json format string includes: "queryStringParams", "requestBody","requestIP" and "userAgent".  Example: 
```
    {
        "queryStringParams": " Parameter 1: (Name - name, Value - Amy)  Parameter 2: (Name - age, Value - 8)  ",  
        "requestBody": {    
            "id": 1,  
            "content": "Hello World"  
        },  
        "requestIP": "111.112.22.11",  
        "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) "
    }
```

### Solution 

REST API application is developed based on Spring Boot framework using IntellJ IDEA IDE.  
Application is deployed on Amazon Elastic Kubernetes Service using Application Load Balancer. Replicas is set to 2 to get 2 copies of application running behind application load balancer.
It is publicly accessible on the Internet through ALB's DNS.  


Jenkins pipeline is created, it automates git checkout, maven build, push to ECR and Deploy to EKS with ALB.

Readonly User Credentials:
* Username: user1
* Password: ylx098@7
* Access key ID: AKIAQJ25XTKZR52Z2FF5
* Secret access key:  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
* Console login link: https://021134547635.signin.aws.amazon.com/console

Jenkins:
* URL: http://54.255.222.100:8080
* Username: admin
* Password: Vl@gBeig35Logi
* Pipeline:[Build-dev-Echo](http://54.255.222.100:8080/job/Build-dev-Echo/)
    
##### Setup Instruction
1. Create a EC2 instance as Build Server(T2.micro).
2. Install Terraform, Docker, AWS CLI version 2, AWS IAM Authenticator, Kubectl, wget on build server.
2. Use Terraform provision an EKS Cluster on AWS ([aws docs](https://docs.aws.amazon.com/eks/latest/userguide/getting-started.html) & [terraform docs](https://learn.hashicorp.com/tutorials/terraform/eks)).
3. Configure kubectl on build server.
4. Deploy an alb-ingress-controller ([aws docs](https://docs.aws.amazon.com/eks/latest/userguide/alb-ingress.html))
5. Create deployments and ingress resources in the cluster([echorest-namespace.yaml](https://github.com/lixiayuan/echorest/blob/master/echorest-namespace.yaml), [echorest-ingress.yaml](https://github.com/lixiayuan/echorest/blob/master/echorest-ingress.yaml), [echorest-service.yaml](https://github.com/lixiayuan/echorest/blob/master/echorest-service.yaml))
6. Create Jenkins CI pipeline to automate the build, push, deploy process ([Pipeline](http://54.255.222.100:8080/job/Build-dev-Echo/), [Jenkinsfile](https://github.com/lixiayuan/echorest/blob/master/Jenkinsfile)).

For further reference, please consider the following sections:
* [Getting started with Amazon EKS](https://docs.aws.amazon.com/eks/latest/userguide/getting-started.html)
* [Provision an EKS Cluster (AWS)](https://learn.hashicorp.com/tutorials/terraform/eks)
* [ALB Ingress Controller on Amazon EKS](https://docs.aws.amazon.com/eks/latest/userguide/alb-ingress.html)
* [AWS ALB Ingress Controller](https://kubernetes-sigs.github.io/aws-alb-ingress-controller/)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.3.2.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)

