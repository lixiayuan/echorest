node {
    try{
	    
	    stage ('GIT Checkout'){
	    	 echo "clear workspace"
		     cleanWs()
		     checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/lixiayuan/echorest.git']]])
	    }


	}catch(error){
	    echo error
	    throw error
	}finally{
	    	    //clean containers
		       // sh "sudo docker rm \$(sudo docker ps -a -f status=exited -q)"
				//sh "sudo docker rm \$(sudo docker ps -a -f status=created -q)"
               // sh "sudo docker rmi \$(sudo docker images -f dangling=true -q)"
	}
}