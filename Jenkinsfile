pipeline {
  agent {
    node {
      label 'maven'
    }

  }
  stages {
    stage('拉取代码并编译') {
      steps {
        container('maven') {
          git(url: 'https://github.com/star574/guli-mall.git', credentialsId: 'github-token', branch: 'dev', changelog: true, poll: false)
          sh 'echo 正在构建:  $PROJECT_NAME  版本号:  $PROJECT_VERSION'
          sh 'echo 开始编译 ! && mvn clean install -gs `pwd`/mvn-settings.xml -Dmaven.test.skip=true'
        }

      }
    }
    stage('构建 & 推送最新镜像') {
      steps {
        container('maven') {
          sh 'mvn -o -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml clean install'
          sh 'cd $PROJECT_NAME && docker build --no-cache -f Dockerfile -t $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER .'
          withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$ALIYUN_CREDENTIAL_ID" ,)]) {
            sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
            sh 'docker tag  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:latest '
            sh 'docker push  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:latest '
          }

        }

      }
    }
    stage('部署到到k8s') {
      steps {
       container('maven') {
        withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$ALIYUN_CREDENTIAL_ID" ,)]) {
            input(id: "deploy-to-dev-$PROJECT_NAME", message: "是否发布$PROJECT_NAME?")
            sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
            sh 'docker pull $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:latest '
            kubernetesDeploy(configs: "$PROJECT_NAME/deploy/**", enableConfigSubstitution: true, kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
        }
        }
      }
    }
    stage('发布当前版本') {
      when {
        expression {
          return params.PROJECT_VERSION =~ /V.*/
        }

      }
      steps {
        container('maven') {
          input(id: 'release-image-with-tag', message: '发布当前版本镜像到git?')
          withCredentials([usernamePassword(credentialsId : 'github-token' ,passwordVariable : 'GIT_PASSWORD' ,usernameVariable : 'GIT_USERNAME' ,)]) {
            sh 'git config --global user.email "shihengluo574@gmail.com" '
            sh 'git config --global user.name "star574" '
            sh 'git tag -a $PROJECT_VERSION -m "$PROJECT_VERSION" '
            sh 'git push http://$GIT_USERNAME:$GIT_PASSWORD@github.com/$GITHUB_ACCOUNT/guli-mall.git --tags --ipv4'
          }

          sh 'docker tag  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION '
          sh 'docker push  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION '
        }

      }
    }
  }
  environment {
    ALIYUN_CREDENTIAL_ID = 'aliyun-hub-id'
    GITHUB_CREDENTIAL_ID = 'github-id'
    KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
    REGISTRY = 'registry.cn-shanghai.aliyuncs.com'
    ALIYUNHUB_NAMESPACE = 'star574'
    DOCKERHUB_NAMESPACE = 'star574'
    GITHUB_ACCOUNT = 'star574'
    SONAR_CREDENTIAL_ID = 'sonar-qube'
    BRANCH_NAME = 'dev'
  }
  parameters {
    string(name: 'PROJECT_VERSION', defaultValue: 'V0.0-BETA', description: '')
    string(name: 'PROJECT_NAME', defaultValue: 'gulimall-gateway', description: '')
  }
}