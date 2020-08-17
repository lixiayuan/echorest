node {
    try{
	    
	    stage ('GIT Checkout'){
	    	 echo "clear workspace"
		     cleanWs()
		     checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/lixiayuan/echorest.git']]])
	    }

	    stage ('Maven Build'){
	        sh "sudo docker build --rm=false -t echorest ."

	    }
        stage ('Push to ECR'){
	    	 sh """
                 sudo docker login -u AWS -p \$(sudo /usr/local/bin/aws ecr get-login-password --region ap-southeast-1) 021134547635.dkr.ecr.ap-southeast-1.amazonaws.com

                 sudo docker tag echorest:latest 021134547635.dkr.ecr.ap-southeast-1.amazonaws.com/echorest:latest
                 sudo docker push 021134547635.dkr.ecr.ap-southeast-1.amazonaws.com/echorest:latest
         	 """
         	 sh "sudo docker tag echorest:latest 021134547635.dkr.ecr.ap-southeast-1.amazonaws.com/echorest:${currentBuild.number};sudo docker push 021134547635.dkr.ecr.ap-southeast-1.amazonaws.com/echorest:${currentBuild.number}"

             sh "sudo docker image prune -f"

        }
	    stage ('Deploy to EKS'){
            try{
                echo "Attempting image rolling update..."
                sh "sudo /usr/local/bin/kubectl -n echorest-namespace set image deployment.apps/echorest-deployment  echorest=021134547635.dkr.ecr.ap-southeast-1.amazonaws.com/echorest:${currentBuild.number}"
                sh "sudo /usr/local/bin/kubectl -n echorest-namespace rollout status deployment.apps/echorest-deployment"

            }catch(Exception e){
                echo "echorest deployment/service do not exist"
                sh "sudo /usr/local/bin/kubectl create namespace echorest-namespace"
                sh "sudo /usr/local/bin/kubectl apply -f echorest-service.yaml"
            }
            sh "sudo /usr/local/bin/kubectl get all -n echorest-namespace"

	    }

	}catch(error){
	    echo error
	    throw error
	}finally{
	   //clean containers
	    sh "sudo docker rm \$(sudo docker ps -a -f status=exited -q)"
		sh "sudo docker rm \$(sudo docker ps -a -f status=created -q)"
        sh "sudo docker rmi \$(sudo docker images -f dangling=true -q)"
	}
}