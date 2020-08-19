# Echo solution

Lixia Yuan <lixiayuan@hotmail.com>

### Solution Summary

This echo solution includes stages : code, build, deploy and monitor. Provide a CI pipeline to automate these stages using Jenkins.

**Code**: REST API application is developed based on Spring Boot framework using IntellJ IDEA IDE.  

**Build**: Build by Maven using docker multistage builds([Dockerfile](https://github.com/lixiayuan/echorest/blob/master/Dockerfile)).  

**Deploy**: Deploy on Amazon Elastic Kubernetes Service using Application Load Balancer. Replicas is set to 2 to get 2 copies of application running behind application load balancer. It is publicly accessible on the Internet through ALB's DNS.  

**Monitor**: Enable ALB access logging to capture detailed information about request sent to REST API. 

**Continuous Integration**:
Create Jenkins pipeline to automate git checkout, maven build, push to ECR and Deploy/Rolling update to EKS with ALB.

### REST API application

* Git repo: <https://github.com/lixiayuan/echorest>
* REST API endpoint:  <http://5d204b8b-echorestnamespace-3331-1985697944.ap-southeast-1.elb.amazonaws.com/echo>

REST API Definition:

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
* Request body: Json format string.   
Example:

```
    {
        "id": 1,
        "content": "Hello World"
    }
```

* Response Body: Json format string includes: "queryStringParams", "requestBody","requestIP" and "userAgent".  
Example: 

```
    {
        "queryStringParams": " Parameter 1: (Name - name, Value - Amy)  Parameter 2: (Name - age, Value - 6)  ",  
        "requestBody": {    
            "id": 1,  
            "content": "Hello World"  
        },  
        "requestIP": "111.112.22.11",  
        "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) "
    }
```

### Solution Setup
 
AWS resources:
* Region: Asia Pacific (Singapore) / ap-southeast-1
* EKS cluster_name: training-eks-YsApwF4k
* ALB name: 5d204b8b-echorestnamespace-3331
* ALB DNS name: 5d204b8b-echorestnamespace-3331-1985697944.ap-southeast-1.elb.amazonaws.com
* ECR repositories name: echorest
* S3: lixiayuan-loadbalancer-logs/echorest-app

Jenkins:
* URL: http://54.255.222.100:8080
* Pipeline: [Build-dev-Echo](http://54.255.222.100:8080/job/Build-dev-Echo/)

Setup Instruction:
1. Develop echorest project using Spring boot, put code into github([Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)).
1. Use Postman test locally.
1. Create a EC2 instance as Build Server(T2.micro).
1. Install Terraform, Docker, Jenkins, AWS CLI version 2, AWS IAM Authenticator, Kubectl, wget on build server.
1. Checkout code from github, build docker image, tag, push to ECR repo: echorest on buildserver ([Jenkinsfile](https://github.com/lixiayuan/echorest/blob/master/Jenkinsfile)).
2. Use Terraform provision an EKS Cluster on AWS ([aws docs](https://docs.aws.amazon.com/eks/latest/userguide/getting-started.html) & [terraform docs](https://learn.hashicorp.com/tutorials/terraform/eks)).
3. Configure kubectl on build server.
4. Deploy an alb-ingress-controller ([aws docs](https://docs.aws.amazon.com/eks/latest/userguide/alb-ingress.html))
5. Create deployments and ingress resources in the cluster([echorest-namespace.yaml](https://github.com/lixiayuan/echorest/blob/master/echorest-namespace.yaml), [echorest-ingress.yaml](https://github.com/lixiayuan/echorest/blob/master/echorest-ingress.yaml), [echorest-service.yaml](https://github.com/lixiayuan/echorest/blob/master/echorest-service.yaml))
6. Create Jenkins CI pipeline to automate the build, push, deploy/update process ([Pipeline](http://54.255.222.100:8080/job/Build-dev-Echo/), [Jenkinsfile](https://github.com/lixiayuan/echorest/blob/master/Jenkinsfile)).
7. Enable ELB access logs for requests to the REST API, stored in S3. ([LB Access logs](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-access-logs.html), [s3](https://s3.console.aws.amazon.com/s3/buckets/lixiayuan-loadbalancer-logs/?region=ap-southeast-1))

ALB Ingress and Service information:

```
    yuans-Air:echorest yuan$ /usr/local/bin/kubectl -n echorest-namespace describe service echorest-service
    Name:                     echorest-service
    Namespace:                echorest-namespace
    Labels:                   app=echorest-app
    Annotations:              Selector:  app=echorest-app
    Type:                     NodePort
    IP:                       172.20.130.99
    Port:                     <unset>  8080/TCP
    TargetPort:               8080/TCP
    NodePort:                 <unset>  31776/TCP
    Endpoints:                10.0.1.72:8080,10.0.3.174:8080
    Session Affinity:         None
    External Traffic Policy:  Local
    Events:                   <none>

    yuans-Air:echorest yuan$ kubectl -n echorest-namespace get all
    NAME                                       READY   STATUS    RESTARTS   AGE
    pod/echorest-deployment-846887fff7-84cv6   1/1     Running   0          9h
    pod/echorest-deployment-846887fff7-vv76k   1/1     Running   0          9h
    
    NAME                       TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
    service/echorest-service   NodePort   172.20.130.99   <none>        8080:31776/TCP   9h
    
    NAME                                  READY   UP-TO-DATE   AVAILABLE   AGE
    deployment.apps/echorest-deployment   2/2     2            2           9h
    
    NAME                                             DESIRED   CURRENT   READY   AGE
    replicaset.apps/echorest-deployment-846887fff7   2         2         2       9h


    yuans-Air:echorest yuan$ kubectl -n echorest-namespace describe ingress echorest-ingress
    Name:             echorest-ingress
    Namespace:        echorest-namespace
    Address:          5d204b8b-echorestnamespace-3331-1985697944.ap-southeast-1.elb.amazonaws.com
    Default backend:  default-http-backend:80 (<error: endpoints "default-http-backend" not found>)
    Rules:
      Host        Path  Backends
      ----        ----  --------
      *           
                  /echo       echorest-service:8080 (10.0.1.72:8080,10.0.3.174:8080)
                  /echo/all   echorest-service:8080 (10.0.1.72:8080,10.0.3.174:8080)
    Annotations:  alb.ingress.kubernetes.io/scheme: internet-facing
                  kubernetes.io/ingress.class: alb
    Events:       <none>
```

Load Balancer access logs:

```
    http 2020-08-19T02:22:44.923271Z app/5d204b8b-echorestnamespace-3331/bad4612c0d3ccdc5 202.45.129.182:34511 10.0.1.229:31776 0.002 0.005 0.000 201 201 761 549 "POST http://5d204b8b-echorestnamespace-3331-1985697944.ap-southeast-1.elb.amazonaws.com:80/echo?name=Amy&age=8&color=red HTTP/1.1" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36" - - arn:aws:elasticloadbalancing:ap-southeast-1:021134547635:targetgroup/5d204b8b-6316ec1a70fdd4e1a99/b9f4e7a2b5d77c25 "Root=1-5f3c8cf4-1c4dd4a3b6282979c93a2381" "-" "-" 1 2020-08-19T02:22:44.916000Z "forward" "-" "-" "10.0.1.229:31776" "201" "-" "-"
```
For further reference, please consider the following sections:
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Getting started with Amazon EKS](https://docs.aws.amazon.com/eks/latest/userguide/getting-started.html)
* [Provision an EKS Cluster (AWS)](https://learn.hashicorp.com/tutorials/terraform/eks)
* [ALB Ingress Controller on Amazon EKS](https://docs.aws.amazon.com/eks/latest/userguide/alb-ingress.html)
* [AWS ALB Ingress Controller](https://kubernetes-sigs.github.io/aws-alb-ingress-controller/)
* [AWS ALB Access logs](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-access-logs.html)

