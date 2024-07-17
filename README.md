# Logic Link -Sudoku

![logo.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/f58fce9b-5435-4512-983c-9c9ce63a8403/logo.png)

Logic Link -Sudoku는 친구와 소통하며 logic 게임인 스도쿠를 즐길 수 있는 게임입니다.

# 팀원

- 장세일
- 박윤서

# 개발 환경

FE

- OS: Android
- IDE: Android Studio
- Language: Kotlin

BE

- Nest js
- MoongoDB

# 서비스 소개

## 1. 로그인

- 카카오 로그인을 통해 sign up
- 재로그인시 저장된 카카오 아이디를 활용하여 서버에 저장된 유저 정보 로드

![로그인.gif](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/730cbf8a-9dcb-4e8b-a88e-2aebfa0957af/%EB%A1%9C%EA%B7%B8%EC%9D%B8.gif)

![로그인2.jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/f8dcbd56-a4d1-43b3-a837-832a26bb837d/%EB%A1%9C%EA%B7%B8%EC%9D%B82.jpg)

## 2. Main Page

- 진행중인 게임 목록 나열
- 게임 클릭시 해당 게임으로 resume
- + 버튼을 통해 새로운 무작위 게임을 생성할 수 있음
- 게임을 길게 눌러 삭제 가능
- 좌측의 메뉴를 통해 다른 탭으로 이동 가능

[메인1.mp4](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/daa4b2ca-f004-490b-80db-2bcbed38138a/%EB%A9%94%EC%9D%B81.mp4)

[메인2.mp4](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/09b60058-b758-410d-a307-b666759c48c5/%EB%A9%94%EC%9D%B82.mp4)

## 3. Ranking Page

- 현재까지의 누적 점수를 바탕으로 유저 중 상위 랭킹을 확인

[랭킹.mp4](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/7aba656d-9da8-4756-8b4f-833621d6184d/%EB%9E%AD%ED%82%B9.mp4)

## 4. Profile Edit Page

- 서버에 저장된 프로필 정보 변경 가능
- 초기 정보는 카카오 로그인시 이용한 정보와 동일
- game 정보를 백업하고 불러올 수 있음

[프로필.mp4](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/5cab19ad-aa8e-4c09-8e44-f3e09b0bf229/%ED%94%84%EB%A1%9C%ED%95%84.mp4)

## 5. Friends Page

- 친구 추가 요청, 친구 추가 수락 및 친구 삭제가 가능한 탭
- 친구들 사이 게임 공유 가능

[KakaoTalk_20240710_204452299.mp4](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/d9c9c32d-74cc-4a90-a98b-f33dfcae7ac5/KakaoTalk_20240710_204452299.mp4)

[KakaoTalk_20240710_204500059.mp4](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/0d4833b7-76d1-4534-88b8-7588e4c2150b/KakaoTalk_20240710_204500059.mp4)

## 6. Now, Play Sudoku!

- 메모 기능이 있는 스도쿠
- 오답을 입력하면 감점되며, 오답 횟수 초과시 게임 오버
- 플레이타임 측정 가능
- 로컬에 저장되어있는 게임 상태를 서버에 백업하면, 동일 정보로 다른 기기에 로그인하여 게임 정보를 불러올 수 있음
- 

[KakaoTalk_20240710_205316167.mp4](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/3ed744f8-f457-4876-92a2-67f939877458/KakaoTalk_20240710_205316167.mp4)

[KakaoTalk_20240710_205328791.mp4](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/b949d4cf-2031-4f85-9290-c90336b42daa/KakaoTalk_20240710_205328791.mp4)

# 기술 설명

## Back-end

**userdb**

- users collection에서 각 user의 id, nickname, score, ranking을 저장
- user의 id는 불변하며, 카카오톡 id를 활용하므로 unique하게 관리됨
- relationships collection에서 user들 간의 관계를 저장.
- 친구 요청을 전송한 사람과 수신한 사람, 그리고 수락 여부가 저장됨
- 수락된 경우 서로 친구인 것으로 간주되며, 반대방향의 친구 요청은 삭제

**gamedb**

- games collection에서 게임을 생성한 사용자 아이디, 게임의 이름, 게임 진행 상황을 저장
- shared games collection을 통해 친구 관계의 여러 사용자가 게임을 공유할 수 있도록 지원

**server**

- KCLOUD VM을 활용하여 DB 및 서버 구축
- KCLOUD VPN에 접속된 모바일에서 어플 실행시 VM의 저장소의 DB 이용

## Front-end

- navigation을 사용해 프래그먼트간 이동 조율, 로그인 프래그먼트와 다른 프래그먼트 분리
- 뷰모델을 이용하여 데이터 변경이 있을 때 마다 즉각적으로 뷰를 업데이트
- retrofit을 이용해 서버와 통신
- 카카오 로그인 구현, shared preference와 서버db를 조합해 유저 정보 저장

# 프로젝트 진행 소감

## 장세일

서버랑 db를 과제에서만 만져보다 처음으로 직접 이용해 코드를 짜는 과정이 매우 험난했다. 

그래도 힘들었던 만큼 결과물이 나오니 뿌듯한 것 같다.

서버 관련 디버깅이 특히 힘들었던 것 같은데, nestjs에서 변수명의 중요성을 알게 되었다.

## 박윤서

생소한 개념이 많아 해결해야 할 문제를 정의하는 것부터 난항을 겪었습니다.

제한된 시간동안 정신없이 프로젝트를 진행하면서 아쉬움도 많이 남았지만, 한 단계 나아갈 때마다 신기함도 배가 되었던 것 같습니다.

플메 및 우리반 모두 수고하셨습니다🙂
