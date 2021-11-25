# 지도 앱 만들어보기

검색어를 통한 구글맵 지도 검색, 내위치를 표시할 수 있는 앱


- 프로젝트에서 코루틴 이용한 비동기 프로그래밍 설정
- API 연결을 위한 okhttp3, retrofit2 라이브러리
- 구글맵 사용
- 인텐트를 통한 지도와 관련된 데이터 넘기기

## 화면
검색화면 (찾고싶은 위치 검색)
지도 위치 화면 (지도, 현재 내 위치 찾기 기능)

### 검색화면
- POI
- Retrofit (이용 시 API를 통해 받아오므로 IO쓰레드 사용 해야함. (코루틴) )
- GSON
- RecyclerView
- Coroutines

### 지도 위치 화면
- Google Map
- Intent

### 현재 내 위치 기능
- Google Map
- POI Geo Reverse (POI API를 통해 내 위치 받아오기)
- Retrofit
- GSON

## 구현 단계
1. 리사이클러뷰 구현 Mocking 데이터 뿌리기
2. Tmap POI 데이터
3. GoogleMap
4. 추가 기능 구현


### POI

- 티맵 라이브러리 POI api 사용해볼것
- 배민에서 사용
- https://openapi.sk.com/api/detailView
- T map API 바로구매
- https://openapi.sk.com/myproject/apiConsole?pjtSeq=1000027886&ordSeq=33324&gdsSeq=1
- https://tmapapi.sktelecom.com/main.html#webservice/docs/tmapPoiSearch

sample json
```json
{
  "searchPoiInfo": {
    "totalCount": "65",
    "count": "2",
    "page": "1",
    "pois": {
      "poi": [
        {
          "id": "4670161",
          "pkey": "37446900",
          "navSeq": "0",
          "collectionType": "poi",
          "name": "빈스빈스마포공덕점",
          "telNo": "",
          "frontLat": "37.543956",
          "frontLon": "126.950107",
          "noorLat": "37.543872",
          "noorLon": "126.950052",
          "upperAddrName": "서울",
          "middleAddrName": "마포구",
          "lowerAddrName": "공덕동",
          "detailAddrname": "",
          "mlClass": "1",
          "lowerAddrName": "공덕동",
          "firstNo": "461",
          "secondNo": "",
          "roadName": "",
          "firstBuildNo": "",
          "secondBuildNo": "",
          "radius": "0",
          "upperBizName": "생활편의",
          "middleBizName": "술집",
          "lowerBizName": "바",
          "detailBizName": "기타",
          "rpFlag": "16",
          "groupSubLists":{
            "groupSub":[
              {
                "subPkey": "113344401",
                "subSeq": "1",
                "subName": "1번출구",
                "subCenterY": "37.55628772",
                "subCenterX": "126.92272084",
                "subNavY": "37.55628772",
                "subNavX": "126.92272084",
                "subRpFlag": "8",
                "subPoiId": "1133444",
                "subNavSeq": "1",
                "subParkYn": "0",
                "subClassCd": "01012804",
                "subClassNmA": "교통편의",
                "subClassNmB": "교통시설",
                "subClassNmC": "지하철출구번호",
                "subClassNmD": "2호선"
              }
            ]
          },
          "newAddressList":{
            "newAddress":[
              {
                "centerLat": "37.54387299",
                "centerLon": "126.95007981",
                "frontLat": "37.54395631",
                "frontLon": "126.95013536",
                "roadName": "백범로",
                "bldNo1": "178",
                "bldNo2": "",
                "roadId": "00232",
                "fullAddressRoad": "서울 마포구 백범로 178"
              }
            ]
          }
        },
        {
          "id": "4778454",
          "pkey": "191276601",
          "navSeq": "1",
          "collectionType": "poi",
          "name": "티롤",
          "frontLat": "37.544289",
          "frontLon": "126.949635",
          "noorLat": "37.544261",
          "noorLon": "126.949607",
          "upperAddrName": "서울",
          "middleAddrName": "마포구",
          "lowerAddrName": "공덕동",
          "detailAddrname": "",
          "mlClass": "1",
          "lowerAddrName": "공덕동",
          "firstNo": "461",
          "secondNo": "",
          "roadName": "",
          "firstBuildNo": "",
          "secondBuildNo": "",
          "radius": "0.06",
          "upperBizName": "생활편의",
          "middleBizName": "술집",
          "lowerBizName": "바",
          "detailBizName": "기타",
          "rpFlag": "16",
          "groupSubLists":{
            "groupSub":[
              {
                "subPkey": "113344401",
                "subSeq": "1",
                "subName": "1번출구",
                "subCenterY": "37.55628772",
                "subCenterX": "126.92272084",
                "subNavY": "37.55628772",
                "subNavX": "126.92272084",
                "subRpFlag": "8",
                "subPoiId": "1133444",
                "subNavSeq": "1",
                "subParkYn": "0",
                "subClassCd": "01012804",
                "subClassNmA": "교통편의",
                "subClassNmB": "교통시설",
                "subClassNmC": "지하철출구번호",
                "subClassNmD": "2호선"
              }
            ]
          },
          "newAddressList":{
            "newAddress":[
              {
                "centerLat": "37.54387299",
                "centerLon": "126.95007981",
                "frontLat": "37.54395631",
                "frontLon": "126.95013536",
                "roadName": "백범로",
                "bldNo1": "178",
                "bldNo2": "",
                "roadId": "00232",
                "fullAddressRoad": "서울 마포구 백범로 178"
              }
            ]
          }
        }
      ]
    }
  }
}   
    
```

### google map

- https://console.cloud.google.com/google/maps-apis/api-list?hl=ko&project=potent-hue-321909
- Maps SDK for Android 추가

```
Last login: Thu Jul 29 20:00:39 on console
isangsu@iMac ~ % keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
별칭 이름: androiddebugkey
생성 날짜: 2021. 6. 3.
항목 유형: PrivateKeyEntry
인증서 체인 길이: 1
인증서[1]:
소유자: C=US, O=Android, CN=Android Debug
발행자: C=US, O=Android, CN=Android Debug
일련 번호: 1
적합한 시작 날짜: Thu Jun 03 10:47:52 KST 2021 종료 날짜: Sat May 27 10:47:52 KST 2051
인증서 지문:
	 SHA1: 40:CF:A6:4A:14:35:39:E5:B2:25:B3:1A:E3:32:F9:B1:46:8E:E2:0D
	 SHA256: 3F:E1:B0:4E:94:D6:57:96:BA:DC:B5:E8:FD:6A:B2:75:CC:F7:FF:DF:2B:B7:39:36:64:B5:3C:CA:3B:3B:9F:26
서명 알고리즘 이름: SHA1withRSA(약함)
주체 공용 키 알고리즘: 2048비트 RSA 키
버전: 1

Warning:
인증서 uses the SHA1withRSA signature algorithm which is considered a security risk. This algorithm will be disabled in a future update.
isangsu@iMac ~ % 
isangsu@iMac ~ % 
```
