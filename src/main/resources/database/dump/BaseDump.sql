-- 더미 데이터 INSERT 쿼리
USE daangn;

-- 1. Region(지역) 더미 데이터
INSERT INTO
    region (name)
VALUES ('서울특별시'),
       ('부산광역시'),
       ('대구광역시'),
       ('인천광역시'),
       ('광주광역시'),
       ('대전광역시'),
       ('울산광역시'),
       ('세종특별자치시'),
       ('경기도'),
       ('강원도');

-- 2. Category(카테고리) 더미 데이터
INSERT INTO
    category (type)
VALUES ('전자기기'),
       ('가구/인테리어'),
       ('유아동'),
       ('생활/가공식품'),
       ('스포츠/레저'),
       ('여성의류'),
       ('남성의류'),
       ('게임/취미'),
       ('뷰티/미용'),
       ('반려동물용품'),
       ('도서/티켓/음반'),
       ('식물'),
       ('기타 중고물품');

-- 3. User(사용자) 더미 데이터
INSERT INTO
    users (
    region_id,
    email,
    password,
    nickname,
    phone,
    temperature,
    profile_url
)
VALUES (
           1,
           'user1@example.com',
           'password123',
           '당근이',
           '010-1234-5678',
           36.5,
           'profile1.jpg'
       ),
       (
           1,
           'user2@example.com',
           'password123',
           '토마토',
           '010-2345-6789',
           37.2,
           'profile2.jpg'
       ),
       (
           2,
           'user3@example.com',
           'password123',
           '오이',
           '010-3456-7890',
           36.8,
           'profile3.jpg'
       ),
       (
           2,
           'user4@example.com',
           'password123',
           '상추',
           '010-4567-8901',
           36.9,
           'profile4.jpg'
       ),
       (
           3,
           'user5@example.com',
           'password123',
           '배추',
           '010-5678-9012',
           36.7,
           'profile5.jpg'
       );

-- 4. MannerDetail(매너 평가 항목) 더미 데이터
INSERT INTO
    manner_detail (content)
VALUES ('친절하고 매너가 좋아요'),
       ('시간 약속을 잘 지켜요'),
       ('응답이 빨라요'),
       ('상품 상태가 설명한 것과 같아요'),
       ('좋은 상품을 합리적인 가격에 판매해요'),
       ('나쁜 매너를 보여요'),
       ('시간 약속을 자주 안 지켜요'),
       ('응답이 느려요');

-- 확인 쿼리 (삽입된 데이터 확인용)

SELECT * FROM region;

SELECT * FROM category;


SELECT id, nickname, email, region_id, temperature FROM users;

SELECT * FROM manner_detail;