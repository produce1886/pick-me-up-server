# pick-me-up-server [![Build Status](https://www.travis-ci.org/produce1886/pick-me-up-server.svg?branch=main)](https://www.travis-ci.org/produce1886/pick-me-up-server)

## commit message convention
서로 다른 부분 코드만을 보니까 서로의 코드를 볼 때 힘드니 작게 커밋을 해주는 게 좋을 것 같아요  
그리고 대충 이 커밋이 어떤 내용인지를 알 수 있게 커밋 메시지 컨벤션이 통일되었으면 좋겠어요  

### AngularJS Git Commit Message Convention
`feat() : ` 새로운 기능 추가할 때  
`fix() : ` 버그 수정할 때  
`docs() : ` 문서 추가 및 변경할 때  
`style()` 코드 포맷팅, 로직의 변화는 없이 띄어쓰기나 탭 문자 등의 사소한 변화가 있을 때  
`refactor() : ` 리팩토링할 때  
`test() : ` 테스트 코드 수정 및 변경할 때  
`chore() : ` 빌드 및 패키지 매니저 수정 등 maintain할 떄  

`()`괄호 안에는 클래스명등 어디를 고쳤는지 정도를 적고, 뒤에 무엇이 달라졌고 왜 수정했는지 현재형으로 적는다

#### example
`feat(Location): add location class`  
`fix(Location): fix bug for addLocation method`  
`docs(README.md) : check plans`
