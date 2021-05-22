과제
----
###구현 내용
- 주기적으로 테스트 코드 수행 및 수행 결과 Push 알림
- 설정된 이메일 사용자에게 설정된 시간 간격으로 테스트 결과를 전송합니다.
- Bot의 App Key, 사용자 이메일, 시간 간격은 Docker Image JAVA_TOOL_OPTIONS 환경 변수로 설정합니다. 

###Docker Image 빌드
- 빌드 후, 데몬에 Docker Image 등록
```bash
$ gradlew clean jibDockerBuild
```
- Tar ball 빌드 후, 데몬에 Docker Image 로드
```bash
$ gradlew clean jibBuildTar
$ docker load --input build/jib-image.tar
```

###Docker Image 실행
- Docker Image 실행 시, 환경 변수 JAVA_TOOL_OPTIONS 내 필요한 Property들을 설정한다.
    - com.example.botAppKey : Bot의 App Key (필수)
    - com.example.userEmail : 사용자 이메일 (필수)
    - com.example.testrun.fixedDelayString : 테스트 코드 실행 주기 (선택, 기본 30초, millisecond)
    - logging.level.root : 로그 레벨 조정 (선택, 기본 DEBUG)  
```bash
$ docker run \
-e JAVA_TOOL_OPTIONS="-Dcom.example.botAppKey=<<BOT_APP_KEY>> \
-Dcom.example.userEmail=<<USER_EMAIL>>
-Dcom.example.testrun.fixedDelayString=<<MILLISECONDS>>" \
kakaowork-exam:0.0.1-SNAPSHOT
```

###Default Properties
```
#로그 레벨 조정
logging.level.root=DEBUG
#실행 주기 설정(default: 30초)
com.example.testrun.fixedDelayString=30000
```