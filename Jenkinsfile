pipeline {
    agent any

    environment {
        AWS_ACCOUNT_ID   = "423671573887"
        AWS_REGION       = "ap-south-1"
        ECR_REPO         = "java-cicd-app"
        IMAGE_TAG        = "${BUILD_NUMBER}"
        MANIFEST_REPO    = "git@github.com:manusinghdhami/java-app-manifests.git"
    }

    stages {

        stage('Checkout App Code') {
            steps {
                git branch: 'main', url: 'https://github.com/manusinghdhami/java-app.git'
            }
        }

        stage('Maven Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${ECR_REPO}:${IMAGE_TAG} ."
            }
        }

        stage('Push to ECR') {
            steps {
                sh '''
                    aws ecr get-login-password --region $AWS_REGION | \
                    docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

                    docker tag $ECR_REPO:$IMAGE_TAG $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO:$IMAGE_TAG
                    docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO:$IMAGE_TAG
                '''
            }
        }

        stage('Update GitOps Manifest Repo') {
            steps {
                sshagent(['manifests-repo-ssh-key']) {
                    sh '''
		    	export GIT_SSH_COMMAND="ssh -o StrictHostKeyChecking=no"

                        rm -rf manifests-repo
                        git clone $MANIFEST_REPO manifests-repo
                        cd manifests-repo

                        sed -i "s#image: .*#image: $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO:$IMAGE_TAG#" deployment.yaml

                        git config user.email "jenkins@ci.local"
                        git config user.name "Jenkins CI"
                        git add deployment.yaml
                        git commit -m "Update image to tag $IMAGE_TAG"
                        git push origin main
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Image built, pushed, and manifest updated. ArgoCD will sync automatically."
        }
        failure {
            echo "Pipeline failed - check logs."
        }
    }
}
