pipeline {
    agent any
    // 按需修改您想要的流水线工程
    stages {
        stage('clone') {
            steps {
                // 将这里替换成您的仓库地址。credentialsId是您在Jenkins中配置好的git访问凭据id
                git branch: 'master', credentialsId: '93c5637e-2121-4d8d-ab3e-0d5584fe5184', url: 'git@github.com:MuShanYu/apply-room-record.git'
            }
        }
        // 这里是将代码中的配置替换成您的配置文件
        stage('rewriteConfig') {
            steps {
                sh 'rm -rf ./src/main/resources/application-prod.yml'
                // 这一步请确保jenkins有对该目录的读写权限
                sh 'cp ../../apr-config/server/* ./src/main/resources/'
            }
        }
        stage ('package') {
            steps {
                // 这里在服务器和Jenkins配置好maven！
                sh 'mvn -s $MAVEN_HOME/conf/settings.xml -f ./pom.xml clean package -P prod'
            }
        }
        stage ('build') {
            steps {
                script {
                    // 在这里您可以自定义您想要的镜像名称规则
                    def currentDateTime = new Date().format("yyyy-MM-dd_HH-mm-ss")
                    def imageName = "server-apr-${currentDateTime}"
                    env.IMAGE_NAME = imageName
                    sh "docker build -t ${imageName} ."
                }
            }
        }
        stage('cleanup') {
            steps {
                script {
                    // 我这里保留最新的两个镜像，其他删除，当然您也可以保存更多版本的镜像。
                    def oldContainerId = sh(script: "docker ps -q --filter name=server-apr*", returnStdout: true).trim()
                    if (oldContainerId) {
                        sh "docker stop ${oldContainerId}"
                        sh "docker rm ${oldContainerId}"
                    }
                    def allImages = sh(script: "docker images --format '{{.ID}} {{.CreatedAt}}' --filter 'reference=server-apr-*' | sort -k2,2r -k3,3r | awk '{print \$1}'", returnStdout: true).split('\n')                    // 保留最后两个镜像（当前和上一个）
                    if (allImages.size() > 2) {
                        def imagesToRemove = allImages[2..-1]
                        for (image in imagesToRemove) {
                            sh "docker rmi ${image}"
                        }
                    }
                }
            }
        }
        stage ('deploy') {
            steps {
            // 运行容器是您可以自定义这里的端口
                sh 'docker run -it -p 8500:8500 -p 9500:9500 --name $IMAGE_NAME --log-opt max-size=25m --log-opt max-file=3 -d $IMAGE_NAME'
            }
        }
    }
}