# Freedive Chanssem

프리다이빙 훈련을 위한 완전 오프라인 타이머 앱입니다. CO₂, O₂, One Breath 테이블을 통한 체계적인 숨참기 훈련을 지원합니다.

## 📱 주요 기능

### 완전 오프라인 동작
- 인터넷 연결 없이 모든 기능 사용 가능
- 최소 권한 요구 (인터넷 권한 없음)
- 개인정보 수집 없음

### 음성 안내 (TTS)
- 실시간 카운트다운 음성 안내
- 15초 남음 알림
- 10초부터 1초까지 카운트다운
- 세션 완료 알림

### 세션 관리
- 세션 진행 중 다른 탭으로 이동 시 경고 다이얼로그
- 세션 중단 기능
- 실시간 진행 상황 표시

## 🏊‍♂️ 테이블 종류

### 1. CO₂ 테이블

**목적**: 이산화탄소 적응 훈련

**특징**:
- 숨참기 시간: 고정 (기본 1분, 15초 단위 조정)
- 숨쉬기 시간: 라운드마다 자동 계산
  - 마지막 라운드: 15초
  - 위로 올라갈수록 15초씩 증가
  - 예: 8라운드 시 1라운드 = 120초, 8라운드 = 15초
- 라운드 수: 기본 8라운드 (최소 6라운드)
- 목표 STA 계산: 숨참기 시간 ÷ 0.7

**사용 방법**:
1. 상단에서 숨참기 시간 설정 ([-] [+] 버튼 또는 시간 텍스트 클릭)
2. 라운드 추가/삭제 (하단 + 버튼, 7라운드 이상에서 - 버튼 표시)
3. START 버튼으로 세션 시작
4. Breath → Hold 순서로 각 라운드 진행

### 2. O₂ 테이블

**목적**: 산소 효율성 향상 훈련

**특징**:
- 휴식 시간: 고정 (기본 2분, 15초 단위 조정)
- 목표 숨참기 시간: 설정 가능 (기본 3분, 15초 단위 조정)
- 숨참기 시간: 라운드마다 점진적 증가
  - 1라운드: 목표 시간의 50%
  - 마지막 라운드: 목표 시간의 90~100%
  - 중간 라운드: 비례 증가
- 라운드 수: 기본 8라운드 (최소 6라운드)
- 목표 STA 계산: 마지막 라운드 Hold 시간 ÷ 0.9

**사용 방법**:
1. 상단에서 휴식 시간과 목표 숨참기 시간 설정
2. 라운드 추가/삭제
3. START 버튼으로 세션 시작
4. Breath → Hold 순서로 각 라운드 진행

### 3. One Breath 테이블

**목적**: 고강도 CO₂ 적응 훈련

**특징**:
- 숨참기 시간: 모든 라운드 동일 (기본 1분, 15초 단위 조정)
- 원브레스 시간: 모든 라운드 동일 (기본 6초, 1초 단위 조정, 범위 3~10초)
- 라운드 수: 기본 8라운드 (최소 6라운드, 최대 12라운드)
- 타이머 순서: Hold → One-breath 회복 → 다음 Hold
- 고강도 훈련: 짧은 회복 시간으로 빠른 CO₂ 적응

**사용 방법**:
1. 상단에서 숨참기 시간과 원브레스 시간 설정
2. 라운드 추가/삭제
3. START 버튼으로 세션 시작
4. Hold → One-breath 순서로 각 라운드 진행

**주의사항**:
- 고강도 훈련이므로 CO₂/O₂ 테이블에 적응한 후 사용 권장
- 드라이 환경(육상/소파/침대)에서만 사용
- 혼자 물속에서 사용 금지
- 하루 최대 1회, 주 3회 정도 권장

## 🎨 UI/UX 특징

### 디자인
- 깔끔한 흰색 배경 (다크모드: 블랙/화이트 톤)
- MOBA 프로젝트 블루 컬러 적용
- 직관적인 아이콘 사용 (+/- 버튼)

### 레이아웃
- 상단: 시간 설정 (좌우 배치)
- 중간: 라운드 테이블 (스크롤 가능)
- 하단: 라운드 추가 버튼, START/STOP 버튼

### 세션 진행 중
- 상단 시간 설정 영역이 세션 진행 상태로 전환
- 좌우 분할 화면으로 Breath/Hold 시간 동시 표시
- 현재 진행 중인 페이즈 하이라이트

## 🔧 기술 스택

- **언어**: Kotlin
- **UI 프레임워크**: Jetpack Compose
- **아키텍처**: MVVM (Model-View-ViewModel)
- **비동기 처리**: Kotlin Coroutines
- **상태 관리**: StateFlow
- **음성 안내**: Android Text-to-Speech (TTS)
- **최소 SDK**: Android 8.0 (API 26)

## 📦 프로젝트 구조

```
app/src/main/java/com/chanssem/freedive/
├── ui/
│   ├── MainActivity.kt          # 앱 진입점
│   ├── FreediveApp.kt            # 메인 앱 컴포저블
│   ├── SplashScreen.kt          # 스플래시 화면
│   └── table/
│       ├── Co2TableScreen.kt    # CO₂ 테이블 화면
│       ├── O2TableScreen.kt     # O₂ 테이블 화면
│       ├── OneBreathScreen.kt   # One Breath 테이블 화면
│       ├── TimeInputDialog.kt   # 시간 입력 다이얼로그
│       └── TimeFormatter.kt     # 시간 포맷터
├── model/
│   ├── TableType.kt             # 테이블 타입 enum
│   ├── Round.kt                 # 라운드 데이터 클래스
│   ├── SessionPhase.kt          # 세션 페이즈 enum
│   └── SessionState.kt          # 세션 상태 데이터 클래스
├── domain/
│   └── TableGenerator.kt           # 테이블 생성 로직
├── timer/
│   └── SessionTimer.kt           # 세션 타이머 로직
├── tts/
│   └── TtsManager.kt             # TTS 관리자
└── viewmodel/
    ├── Co2ViewModel.kt          # CO₂ 테이블 ViewModel
    ├── O2ViewModel.kt            # O₂ 테이블 ViewModel
    └── OneBreathViewModel.kt    # One Breath 테이블 ViewModel
```

## 🚀 시작하기

### 요구사항
- Android Studio Hedgehog (2023.1.1) 이상
- Android SDK 26 이상
- Gradle 8.0 이상

### 빌드 방법

1. 저장소 클론
```bash
git clone https://github.com/ChangooLee/freedive_chanssem.git
cd freedive_chanssem
```

2. Android Studio에서 프로젝트 열기

3. Gradle 동기화

4. 실행
   - USB 디버깅 활성화된 Android 기기 연결
   - 또는 에뮬레이터 실행
   - Run 버튼 클릭

### 릴리즈 빌드

1. Android Studio: `Build > Generate Signed Bundle / APK`
2. 키스토어 생성 또는 기존 키스토어 선택
3. Release 빌드 변형 선택
4. 빌드 완료 후 `app/build/outputs/bundle/release/app-release.aab` 생성

## 📝 사용 가이드

### 시간 설정
- **+/- 버튼**: 15초 단위로 시간 증감 (One Breath의 원브레스 시간은 1초 단위)
- **시간 텍스트 클릭**: 다이얼로그에서 분/초 직접 입력
- **최소 시간**: 15초

### 라운드 관리
- **라운드 추가**: 하단 "+" 버튼 클릭
- **라운드 삭제**: 7라운드 이상에서 각 라운드 우측 "-" 버튼 표시
- **최소 라운드**: 6라운드 (One Breath는 최대 12라운드)

### 세션 시작/중단
- **START 버튼**: 세션 시작 (상단 시간 설정 영역이 세션 진행 상태로 전환)
- **STOP 버튼**: 세션 중단 (빨간색 버튼)
- **탭 전환**: 세션 진행 중 다른 탭 클릭 시 경고 다이얼로그 표시

### 음성 안내
- 세션 시작 시: "Breath" 또는 "Hold" 안내
- 15초 남음: "15 seconds, ready" 또는 "15 seconds remain"
- 10~1초: 숫자 카운트다운
- 세션 완료: "Session complete"

## 👨‍🏫 About

### 찬쌤 (Chanssem)
- PADI 프리다이빙 강사 트레이너
- 수중 촬영가
- Instagram: [@chanssem](https://instagram.com/chanssem)

### MOBA (Make Ocean Blue Again)
- 프리다이빙과 플로깅을 통한 해양 보전 프로젝트
- 웹사이트: [https://moba-project.org](https://moba-project.org)

## ⚠️ 안전 주의사항

1. **드라이 환경 전용**: 이 앱의 테이블은 육상/소파/침대 등 드라이 환경에서만 사용하세요.
2. **혼자 물속에서 사용 금지**: 절대 혼자 물속에서 이 테이블로 훈련하지 마세요.
3. **컨디션 체크**: 두통, 멍함, 비정상적인 피로가 느껴지면 즉시 중단하세요.
4. **의료 상담**: 심혈관/호흡기 질환자는 전문 의료인과 프리다이빙 강사 상담 후 사용하세요.
5. **One Breath 테이블**: 고강도 훈련이므로 CO₂/O₂ 테이블에 적응한 후 사용하세요.

## 📄 라이선스

이 프로젝트는 개인 프로젝트입니다.

## 🤝 기여

이슈 및 개선 제안은 GitHub Issues를 통해 제출해주세요.

## 📧 문의

프리다이빙 강습, 투어, 수중 촬영 문의:
- Instagram: [@chanssem](https://instagram.com/chanssem)

---

**Made with ❤️ by Chanssem**

