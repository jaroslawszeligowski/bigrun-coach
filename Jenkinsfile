pipeline {
    agent any
    tools { maven 'Maven 3.9' }

    stages {
        stage('Build') {
            steps { sh 'mvn clean package -DskipTests' }
        }
        stage('Unit Tests') {
            steps { sh 'mvn test' }
        }
        stage('Integration Tests') {
            steps { sh 'mvn verify -P integration-tests' }
        }
        stage('Docker Build') {
            steps { sh 'docker build -f Dockerfile.ci -t bigrun-coach .' }
        }
        stage('Deploy') {
            steps {
                sh 'docker compose down'
                sh 'docker compose up -d'
            }
        }
        stage('API Smoke Tests') {
            steps {
                sh """newman run scripts/bigrun-coach.postman_collection.json --env-var baseUrl=http://localhost:8081 --reporters cli,junit --reporter-junit-export target/newman/results.xml"""
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            junit '**/target/failsafe-reports/*.xml'
        }
        failure {
            echo 'Pipeline failed — check logs'
        }
    }
}
