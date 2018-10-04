node {
    stage('Check out') {
	    retry(3){
        	checkout scm
	    }
    }  
    stage('Build') {
	// FIXME: Use full mvn patch due to mvn command not found issue - no idea why
	// Start neccessary services to prepare required libraries if needed
	lock(resource: "lock_${env.NODE_NAME}_${env.BRANCH_NAME}", inversePrecedence: true) {
	      milestone 1
	      sh "fastlane build_release"
        }
	build job: 'StartServices'
    	if (env.BRANCH_NAME.findAll(/^[Release]+/)) {
    		sh '''
		    cd source
		    /usr/local/bin/mvn clean verify -Pprod
	        '''
    	} else {
    		sh '''
		    cd source
		    /usr/local/bin/mvn clean verify -Pstag
	        '''
    	}       
    }
    stage('Package') {
        sh '''
            sudo ./package.sh ${JOB_BASE_NAME}
        '''

        if (env.BRANCH_NAME == 'release') {
                sh '''
                    sudo ./verify.sh ${JOB_BASE_NAME}
                '''
        }
    }
}
