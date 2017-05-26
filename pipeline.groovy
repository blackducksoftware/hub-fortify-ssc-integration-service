def javaEnv(){
  tools javaHome = 'jdk8'
  [
    "PATH=${javaHome}/bin:${env.PATH}",
    "JAVA_HOME=${javaHome}"
  ]
}

node('slave'){
  try{
    stage('Pull from GitHub'){
      checkout scm: [$class: 'GitSCM', branches: [[name: "origin/master"]], userRemoteConfigs: [[credentialsId: "$env.SERV_BUILDER_GIT_CREDENTIALS", url: 'ssh://git@github.com/blackducksoftware/hub-fortify-integration.git']], extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'hub-fortify-integration']]]
    }
    stage('Compile'){
      sh 'cd hub-fortify-integration; ./gradlew clean assemble --refresh-dependencies'
    }
    stage('Test'){
      try{
        sh 'cd hub-fortify-integration; ./gradlew test --refresh-dependencies'
      } finally {
        stash name: 'test-results', includes: '**/build/test-results/test/*.xml'
      }
    }
  } finally {
    sh 'find hub-fortify-integration/build/test-results/test -type f -name "*.xml" -exec rm -f {} \\;'
    unstash 'test-results'
    junit 'hub-fortify-integration/build/test-results/test/*.xml'
  }
}
