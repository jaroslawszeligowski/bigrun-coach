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
            steps { sh 'mvn verify -P integration-tests -Dskip.surefire.tests=true' }
        }
        stage('Docker Build') {
            steps { sh 'docker build -f Dockerfile.ci -t bigrun-coach .' }
        }
        stage('Deploy') {
            steps {
                withCredentials([
                    string(credentialsId: 'db-name', variable: 'DB_NAME'),
                    string(credentialsId: 'db-username', variable: 'DB_USERNAME'),
                    string(credentialsId: 'db-password', variable: 'DB_PASSWORD'),
                    string(credentialsId: 'ollama-base-url', variable: 'OLLAMA_BASE_URL')
                ]) {
                    sh '''
                        cat <<EOF > .env
DB_NAME=${DB_NAME}
DB_USERNAME=${DB_USERNAME}
DB_PASSWORD=${DB_PASSWORD}
OLLAMA_BASE_URL=${OLLAMA_BASE_URL}
EOF
                        docker-compose down
                        docker-compose up -d
                    '''
                }
            }
        }
        stage('API Smoke Tests') {
            steps {
                sh 'sleep 10'
                sh """newman run scripts/bigrun-coach.postman_collection.json --env-var baseUrl=http://host.docker.internal:8081 --reporters cli,junit --reporter-junit-export target/newman/results.xml"""
            }
        }
    }
    post {
        always {
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            junit testResults: '**/target/failsafe-reports/*.xml', allowEmptyResults: true
            junit testResults: '**/target/newman/results.xml', allowEmptyResults: true
            allure includeProperties: false, results: [[path: 'target/allure-results']]
            recordCoverage(
                tools: [[parser: 'JACOCO', pattern: '**/target/jacoco.exec, **/target/jacoco-it.exec']],
                sourceCodeRetention: 'EVERY_BUILD'
            )
            sh 'docker-compose down'
            sh 'rm -f .env'
            sh 'docker system prune -f'
        }
        failure {
            echo 'Pipeline failed — check logs'
        }
    }
}