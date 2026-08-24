# CoinFolio Backend

## 로컬 일회성 실행

Java 21 환경에서 다음 명령으로 실행합니다.

```powershell
.\gradlew.bat bootRun
```

기본 프로필은 H2 인메모리 DB와 `create-drop`을 사용하므로 서버를 종료하면 데이터가 사라집니다.

## Docker 영구 저장 실행

Docker 사용이 가능한 환경에서는 다음 명령으로 백엔드와 MariaDB를 함께 실행합니다.

```powershell
docker compose up --build
```

`docker` 프로필은 MariaDB를 사용하며 데이터는 `coinfolio-mariadb-data` 볼륨에 유지됩니다. 비밀번호와 JWT 키는 `DB_PASSWORD`, `DB_ROOT_PASSWORD`, `JWT_SECRET` 환경 변수로 교체할 수 있습니다.

