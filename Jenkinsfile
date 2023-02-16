pipeline {
    agent any

    options {
        githubProjectProperty(projectUrlStr: "https://github.com/SirBlobman/Colored-Signs")
    }

    environment {
        DISCORD_URL = credentials('PUBLIC_DISCORD_WEBHOOK')
    }

    triggers {
        githubPush()
    }

    tools {
        maven "Default"
        jdk "JDK 17"
    }

    stages {
        stage("Maven: Package") {
            steps {
                withMaven(options: [artifactsPublisher(disabled: true)]) {
                    sh("mvn clean package -U")
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'plugin/target/ColoredSigns-*.jar', fingerprint: true
        }

        always {
            script {
                discordSend webhookURL: DISCORD_URL,
                        title: "${env.JOB_NAME}",
                        link: "${env.BUILD_URL}",
                        result: currentBuild.currentResult,
                        description: "**Build:** ${env.BUILD_NUMBER}\n**Status:** ${currentBuild.currentResult}",
                        enableArtifactsList: false,
                        showChangeset: true
            }
        }
    }
}
