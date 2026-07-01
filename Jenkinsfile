@Library('jenkins-shared-lib') _
pipeline {
    agent any


    parameters {
        string(name: 'APP_NAME', defaultValue: 'ecommerce-app')
        choice(name: 'ENV', choices: ['dev', 'qa', 'uat'], description: 'Select Environment')
    }

    stages {

        stage('Initialize') {
    steps {
        script {

            def config = constant(params.APP_NAME)

            env.ACCOUNT_ID = config.ACCOUNT_ID
            env.AWS_REGION = config.AWS_REGION

            env.ECR_REPO   = config.ECR_REPO
            env.ECR_URL    = config.ECR_URL
            env.GIT_URL    = config.GIT_URL
            env.IMAGE_NAME = config.IMAGE_NAME

            env.EKS_CLUSTER = config.EKS_CLUSTER
            env.TF_DIR      = config.TF_DIR
            env.HELM_DIR    = config.HELM_DIR
            env.NAMESPACE   = config.NAMESPACE
        }
    }
}

        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: "${env.GIT_URL}",
                        credentialsId: 'git-creds'
                    ]]
                ])
            }
        }

       stage('Extract App Info from POM') {
    steps {
        script {
            env.IMAGE_NAME = sh(
                script: "mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout",
                returnStdout: true
            ).trim()

            env.IMAGE_TAG = sh(
                script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                returnStdout: true
            ).trim()
        }
    }
}

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }


        //echo "Image Name: ${env.IMAGE_NAME}"
       // echo "Image Tag: ${env.IMAGE_TAG}"
    

     stage('Build Docker Image') {
    steps {
        sh '''
        docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
        '''
    }
}

        stage('Login to ECR') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'aws-creds'
                ]]) {
                    sh '''
                    aws ecr get-login-password --region ${AWS_REGION} | \
                    docker login --username AWS \
                    --password-stdin ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                    '''
                }
            }
        }

      stage('Tag Docker Image') {
    steps {
        sh '''
        docker tag ${IMAGE_NAME}:${IMAGE_TAG} \
        ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${IMAGE_TAG}
        '''
    }
}

        stage('Push to ECR') {
            steps {
                sh '''
                docker push ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${IMAGE_TAG}
               '''
            }
       }
        stage('Package Helm Chart') {
    steps {
        sh '''
        helm lint ./helm-chart
        helm package ./helm-chart
        '''
    }
}
                stage('Push Helm Chart to ECR') {
            steps {
                // Wrap in withCredentials so the AWS CLI can find your access keys
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'aws-creds'
                ]]) {
                    sh """
                    # 1. Package the chart into a .tgz file
                    helm package ./helm-chart
                    
                    # 2. Authenticate helm CLI with ECR OCI registry
                    aws ecr get-login-password --region ${AWS_REGION} | \
                    helm registry login --username AWS \
                    --password-stdin ${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                    
                    # 3. Push the packaged chart to ECR
                    helm push ecommerce-app-*.tgz oci://${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                    """
                }
            }
        }
stage('Configure kubectl') {
    steps {
        withCredentials([[
            $class: 'AmazonWebServicesCredentialsBinding',
            credentialsId: 'aws-creds'
        ]]) {
            sh '''
            aws eks update-kubeconfig \
              --region ${AWS_REGION} \
              --name ${EKS_CLUSTER}

            kubectl get nodes
            '''
        }
    }
}
        stage('Test EKS') {
    steps {
        withCredentials([[
            $class: 'AmazonWebServicesCredentialsBinding',
            credentialsId: 'aws-creds'
        ]]) {
            sh '''
            aws eks update-kubeconfig \
              --region eu-north-1 \
              --name eks-dev

            kubectl get nodes
            '''
        }
    }
}
    stage('Check AWS Identity') {
    steps {
        sh '''
        aws sts get-caller-identity
        aws eks update-kubeconfig \
          --region ${AWS_REGION} \
          --name ${EKS_CLUSTER}

        kubectl config current-context
        kubectl get nodes
        '''
    }
}   
        stage('Deploy to Kubernetes using Helm') {
            steps {
                sh """
            helm upgrade --install ecommerce-app ./helm-chart \
          --namespace ecommerce \
              --create-namespace \
          -f ./helm-chart/values-${ENV}.yaml \
          --set image.repository=${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO} \
          --set image.tag=${IMAGE_TAG}
            """
            }
        }
        stage('Verify Deployment') {
    steps {
        sh '''
        kubectl get pods -n ecommerce
        kubectl get svc -n ecommerce
        kubectl rollout status deployment/ecommerce-app -n ecommerce
        '''
    }
}
    }
    post {
        always {
            sh 'docker logout || true'
        }
    }

}
