# 이미지와 상품 타이틀 등등의 정보를 한번에 받는 방법 3가지 및 장단점

---

## 1. DTO 내부에 `MultipartFile` / `List<MultipartFile>` 필드를 두고 한 번에 받기

```java
// 예시 DTO 클래스
public class ProductRequestDto {
    private String title;                   // 상품명
    private String description;             // 상품 설명
    private Integer price;                  // 가격

    private MultipartFile thumbnail;        // 썸네일 이미지
    private List<MultipartFile> images;     // 상세 이미지 목록
    // + getter, setter 등
}
```

```java
// 예시 컨트롤러 메서드
@PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> createProduct(
        @ModelAttribute ProductRequestDto dto   // ← @ModelAttribute로 DTO 하나에 form-data 바인딩
) {
    log.info("dto.title = {}", dto.getTitle());
    // thumbnail, images 필드도 dto에서 꺼내서 처리 가능
    productService.save(dto);
    return ResponseEntity.ok("상품 등록 성공");
}
```

### 장점

1. **한 곳에 데이터가 모여서 관리 용이**

   * 상품명·가격 등 일반 필드부터, 썸네일·상세 이미지까지 `ProductRequestDto` 하나로 묶여 있어서 파라미터 개수가 줄어들고 코드가 직관적입니다.
   * 서비스 레이어나 Validator에서 DTO 하나만 보면, 그 안에 어떤 필드들이 있는지 곧바로 파악할 수 있습니다.
2. **검증(Validation) 처리 용이**

   * 모든 입력값(문자열, 숫자, 파일 등)을 하나의 객체로 받기 때문에, 예를 들어 `@Valid`나 커스텀 Validator를 붙여서 한 번에 검증할 수 있습니다.
3. **확장성**

   * 나중에 새로운 필드를 추가해야 할 때, DTO에 컬럼만 추가해주면 되므로 유지보수가 쉽습니다.

### 단점

1. **Swagger에서 테스트가 까다로움**

   * `@ModelAttribute`로 받아야 할 DTO가 `MultipartFile` 필드를 포함하면, Swagger UI에서 자동으로 form-data 입력 폼이 제대로 생성되지 않는 경우가 있습니다.
   * 특히, `@RequestBody`가 섞이지 않은 순수 `@ModelAttribute` 방식에서는 JSON 바인딩과 달리 Swagger의 “Try it out” UI가 복잡하게 보일 수 있습니다.
2. **binding 실패 시 디버깅 난이도**

   * 예를 들어 form-data 키 이름이 DTO 필드명과 다르면 바인딩이 되지 않는데, 그 이유가 잘 드러나지 않을 때가 있습니다.
3. **순수 JSON 요청과는 호환되지 않음**

   * `consumes = MULTIPART_FORM_DATA_VALUE`이기 때문에, 일반적인 `application/json` 요청으로는 DTO가 바인딩되지 않습니다.
   * 만약 클라이언트가 “JSON 바디 + 파일”을 동시에 보내야 할 때, 반드시 multipart 형식으로 보내야 하므로 프론트엔드 구현 난이도가 올라갑니다.

---

## 2. DTO와 `MultipartFile` / `List<MultipartFile>`를 메서드 인자에서 분리해서 받기

```java
// 예시 DTO: 파일을 제외한 텍스트 정보만 담음
public class ProductInfoDto {
    private String title;
    private String description;
    private Integer price;
    // + getter, setter 등
}
```

```java
// 컨트롤러 메서드: DTO와 파일을 분리
@PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> createProduct(
        @ModelAttribute ProductInfoDto infoDto,              // ① 상품명·가격 등 텍스트
        @RequestParam("thumbnail") MultipartFile thumbnail,  // ② 썸네일 파일
        @RequestParam("images") List<MultipartFile> images    // ③ 상세 이미지들
) {
    log.info("infoDto.title = {}", infoDto.getTitle());
    // thumbnail, images는 각각 별도 파라미터로 들어옴
    productService.save(infoDto, thumbnail, images);
    return ResponseEntity.ok("상품 등록 성공");
}
```

### 장점

1. **Swagger UI에서 form-data 입력이 명확**

   * 각각 `@RequestParam("thumbnail")` 등으로 지정했기 때문에, Swagger 화면에서 “thumbnail” 필드에 파일 업로드 UI가 뜨고, “images”에는 파일 여러 개를 붙일 수 있게 나타납니다.
   * 일반 텍스트 필드(infoDto의 title 등)도 `@ModelAttribute`로 잡히기 때문에, Swagger “Try it out”이 안정적으로 동작합니다.
2. **메서드 시그니처가 명확**

   * “DTO”와 “파일”이 따로 떨어져 있어서, 코드만 봐도 어떤 파라미터가 어떤 용도인지 직관적으로 알 수 있습니다.
3. **파일 없이 텍스트만 보낼 때 유연성**

   * 예컨대 수정(update) 시 썸네일만 바꿀 수도 있는데, DTO와 파일 파라미터가 분리되어 있으면 일부만 업데이트하기가 상대적으로 편합니다.

### 단점

1. **인자(Parameter)가 많아짐**

   * DTO, Thumbnail, Images 등 파라미터가 3개 이상으로 분리되면 메서드 시그니처가 길어져 가독성이 떨어질 수 있습니다.
2. **검증 로직 분산**

   * DTO 자체에 대한 검증은 `@Valid ProductInfoDto`로 가능하지만, 파일 검증(예: 빈 파일인 경우, 확장자 체크 등)은 별도로 `if (thumbnail.isEmpty())` 같은 코드를 직접 써야 해서 검증이 분산됩니다.

---

## 3. 상품 타이틀·설명 등은 `@RequestParam`으로, 파일은 `@RequestParam`으로 모두 따로 받기

```java
@PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> createProduct(
        @RequestParam("title") String title,                     // ① 상품명
        @RequestParam("description") String description,         // ② 상품 설명
        @RequestParam("price") Integer price,                    // ③ 가격
        @RequestParam("thumbnail") MultipartFile thumbnail,     // ④ 썸네일 이미지
        @RequestParam("images") List<MultipartFile> images       // ⑤ 상세 이미지 목록
) {
    // title, description, price, thumbnail, images를 모두 분리해서 사용
    productService.save(title, description, price, thumbnail, images);
    return ResponseEntity.ok("상품 등록 성공");
}
```

### 장점

1. **매개변수가 구체적으로 드러남**

   * 메서드 시그니처만 보고도 “쟤는 문자열, 얘는 파일”이 분명하게 보여서, 어떤 파라미터를 넘겨야 하는지 아주 명확합니다.
2. **DTO 클래스를 별도로 만들 필요 없음**

   * 간단한 CRUD라면 DTO 클래스 파일을 만들지 않아도 되므로 코드가 약간 덜 복잡해 보일 수 있습니다.
3. **Swagger 동작이 확실함**

   * 모든 필드가 `@RequestParam`이므로 Swagger UI가 누락 없이 form-data 입력필드를 자동 생성해 줍니다.

### 단점

1. **유지보수성 저하**

   * 필드가 점점 늘어날 때마다 메서드 파라미터가 계속 추가돼서, 한 번에 관리하기가 어렵습니다.
   * 예를 들어 10개 이상 텍스트 필드+여러 파일을 받는 상황이라면, 메서드 시그니처가 지나치게 길어집니다.
2. **검증 로직 분산**

   * 텍스트 검증(문자열 길이, null 체크 등)도 컨트롤러 안에 직접 하거나 별도 로직으로 해야 하기 때문에, 비즈니스 로직과 검증 로직이 뒤섞여서 중복 코드가 생기기 쉽습니다.
3. **확장성 떨어짐**

   * 나중에 필드가 추가될 때마다 `@RequestParam("newField") String newField`를 계속 늘려야 하므로, DTO 하나만 수정하면 끝나는 1번 방식보다 비효율적입니다.

---

## “DTO 하나로 받는 것이 좋다”는 말이 맞는 이유

1. **코드 구조가 깔끔해진다**

   * 상품 정보를 하나의 객체로 묶어두면, 컨트롤러-서비스-레포지토리 전반에서 매개변수가 DTO 하나로 통일됩니다.
   * 이 덕분에 메서드 시그니처가 단순해지고, 어떤 서비스에 어떤 데이터가 들어가는지 추적하기 쉽습니다.
2. **재사용성·테스트 용이**

   * 예를 들어 `ProductRequestDto`를 재사용해서 단위 테스트(Unit Test)할 때도, DTO 객체 하나만 만들어서 쓰면 되므로 테스트 코드가 간결해집니다.
3. **유효성(Validation) 처리 일원화**

   * `@Valid`나 커스텀 어노테이션(`@NotEmpty`, `@Size`, `@FileSize`, `@FileExtension` 등)을 DTO 필드에 붙여두면, 컨트롤러 진입 시점에 자동으로 검증됩니다.
   * 반면 파일과 텍스트 파라미터가 따로 떨어져 있으면, 파일 검증 로직과 텍스트 검증 로직이 분리되어 중복 코드가 생길 가능성이 높습니다.

---

## Swagger에서 1번 방식(@ModelAttribute + DTO + MultipartFile) 테스트할 때 주의사항

1. **DTO 필드명과 form-data key가 1:1 매핑되어야 함**

   * 예: DTO에 `private MultipartFile thumbnail;`이 있으면, Swagger에서 “thumbnail”이라는 이름으로 파일을 업로드해야 바인딩됩니다.
2. **`@ModelAttribute`만 쓰면 Swagger UI가 때때로 폼을 제대로 그려주지 않음**

   * 이 경우 컨트롤러 메서드에 아래처럼 `@RequestPart`를 추가해보면 Swagger가 이를 recognize해서 “thumbnail”, “images” 필드를 나눠 그려줍니다.

   ```java
   @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ResponseEntity<?> createProduct(
           @RequestPart ProductRequestDto dto,                   // ← @ModelAttribute 대신 @RequestPart 사용
           @RequestPart("thumbnail") MultipartFile thumbnail,    // ← 명시적으로 나눠주면 swagger에도 뜸
           @RequestPart("images") List<MultipartFile> images     // ← 복수 파일도 잘 잡힘
   ) { … }
   ```

   * `@RequestPart`를 쓰면 Swagger가 multipart/form-data 구조로 인식하기 때문에, UI에서 파일 필드를 깔끔하게 보여줍니다.
   * 단, 이때 DTO 내부에 `MultipartFile` 필드를 두면 중복 바인딩이 발생할 수 있으므로, DTO에는 순수히 텍스트 필드만 두고, 파일은 모두 컨트롤러 인자로 별도 받는 방향이 좋습니다.
3. **Swagger에 DTO 예제가 깨끗하게 보이게 만들려면**

   * DTO에 `@Schema(description="상품 명") private String title;` 같은 어노테이션을 붙여주면, Swagger UI에서 어떤 필드를 입력해야 하는지 친절하게 설명이 뜹니다.
   * 파일 필드에도 `@Schema(type = "string", format = "binary")`를 붙여주면 Swagger가 해당 필드가 “파일 업로드”인 것을 인식합니다.

---

## 3가지 방식의 비교 요약

|  방식 번호 | 장점                                                                    | 단점                                                              |
| :----: | :-------------------------------------------------------------------- | :-------------------------------------------------------------- |
| **1번** | • DTO 하나로 묶어 관리 → 코드 간결<br>• 검증 로직 일원화 가능<br>• 확장성·유지보수성 우수           | • Swagger 테스트 시 form-data 폼 자동 생성이 불투명<br>• 순수 JSON 요청과 호환 어려움  |
| **2번** | • DTO(텍스트)↔파일(이미지) 분리로 Swagger 호환성↑<br>• 메서드 시그니처가 직관적<br>• 파일만 교체 가능 | • 파라미터 개수 증가로 가독성 저하<br>• 검증 로직 분산됨<br>• DTO 하나만 수정하면 끝나는 편의성 ↓ |
| **3번** | • 필드별 파라미터가 모두 분리되어 직관적<br>• DTO 클래스 없이 바로 구현 가능                      | • 필드 개수 많아지면 메서드가 지저분해짐<br>• 유효성 검증 로직이 컨트롤러에 분산됨<br>• 확장성 떨어짐  |

---

## 1번 방식을 사용할 때의 Swagger 예시 코드

```java
// ① DTO: 텍스트 정보만 담고, 파일 필드는 생략
public class ProductRequestDto {
    @NotEmpty @Schema(description="상품명", example="스마트폰")
    private String title;

    @NotNull @Schema(description="가격", example="500000")
    private Integer price;

    @Schema(description="상품 설명", example="최신형 스마트폰입니다.")
    private String description;

    // 파일 필드는 제외하고, 컨트롤러에서 @RequestPart로 받음
    // private MultipartFile thumbnail;
    // private List<MultipartFile> images;
    // + getter, setter
}
```

```java
// ② 컨트롤러: @RequestPart를 활용하여 DTO와 파일을 분리
@Operation(summary = "상품 등록", description = "상품 정보와 이미지를 함께 전송하여 등록합니다.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "등록 성공"),
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
})
@PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> createProduct(
        @RequestPart @Valid ProductRequestDto dto,                  // ← 텍스트 정보(검증 가능)
        @RequestPart @Schema(type = "string", format = "binary", description="썸네일 이미지")
        MultipartFile thumbnail,                                    // ← 단일 파일
        @RequestPart @Schema(type = "string", format = "binary", description="상세 이미지들")
        List<MultipartFile> images                                   // ← 복수 파일
) {
    // 서비스 호출 시 dto, thumbnail, images를 전달
    productService.save(dto, thumbnail, images);
    return ResponseEntity.ok("상품 등록 성공");
}
```

* 위 코드처럼 `@RequestPart`를 사용하면 Swagger UI에 “dto”라는 JSON-like 폼(input), “thumbnail”은 파일 업로드, “images”는 복수 파일 업로드 UI가 자동 생성됩니다.
* **완전한 DTO 하나로 묶어서 @ModelAttribute만 쓰면 Swagger UI에서 form-data 항목이 제대로 표시되지 않을 수 있으므로**, DTO에는 파일 필드를 두지 않고, 파일은 별도의 `@RequestPart` 파라미터로 받는 방식을 추천합니다.
* 다만 기술적으로는 DTO 내부에 `MultipartFile`을 넣고 `@ModelAttribute ProductRequestDto dto`만 받아도 동작합니다. 다만 Swagger 테스트 환경에서 모양이 깨질 수 있으니 주의하세요.

---

## 4. 1\~3번 이외에 고려해볼 수 있는 방법들

1. **이미지 업로드를 별도 API로 분리하고, URL만 DTO로 전달**

   * 프론트엔드에서는 우선 “이미지만 업로드”하는 엔드포인트를 따로 만들어서 S3나 별도 스토리지에 이미지를 저장하고, 반환받은 업로드된 이미지 URL 리스트를 상품 등록 요청의 JSON 바디에 포함시켜 보냅니다.
   * 예:

     1. `POST /api/upload` → `multipart/form-data`로 이미지 파일들 보냄 → 응답으로 `["https://.../img1.jpg", "https://.../img2.jpg"]`
     2. `POST /api/products` → `{ "title": "...", "price": ..., "imageUrls": ["https://.../img1.jpg", ...] }`
   * **장점**: 상품 등록 시점에 이미지는 URL 형태로 넘어오기 때문에, 컨트롤러 메서드는 순수 JSON 바디(`@RequestBody`)만 처리하면 됩니다. Swagger에서도 JSON 바디 예제를 깔끔하게 보여줄 수 있습니다.
   * **단점**: 업로드용 API를 별도 구현해야 하고, 이미지 업로드와 상품 등록 과정을 2단계로 나눠야 하므로 클라이언트 로직이 조금 복잡해집니다.

2. **Base64 인코딩된 이미지 문자열을 JSON으로 전달**

   * 이미지를 Base64 문자열로 인코딩한 뒤, JSON의 필드에 담아 보냅니다.
   * 예:

     ```json
     {
       "title": "스마트폰",
       "price": 500000,
       "thumbnailBase64": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkG…",
       "imagesBase64": [
         "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkG…",
         …
       ]
     }
     ```
   * **장점**: 전송 자체가 순수 JSON이므로, Spring 컨트롤러에서는 `@RequestBody ImageUploadDto dto` 하나만 받아서 처리하면 됩니다. Swagger에서도 JSON 샘플을 그대로 보여줄 수 있습니다.
   * **단점**: Base64 문자열이 매우 길기 때문에, payload 크기가 커지고 네트워크 부하가 늘어납니다. 또한 디코딩 로직을 매번 넣어야 하므로 서버 메모리·CPU 부담이 커질 수 있습니다.

3. **Spring WebFlux + Reactive 방식 (MultipartDataFlux)**

   * WebFlux를 쓰면 `Flux<FilePart>` 형태로 파일 스트림을 비동기로 처리할 수 있습니다.
   * 예:

     ```java
     @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     public Mono<ResponseEntity<?>> createProduct(
             @RequestPart("title") String title,
             @RequestPart("price") Integer price,
             @RequestPart("images") Flux<FilePart> imagesFlux
     ) {
         // imagesFlux를 구독해서 Reactive 방식으로 저장
         return imagesFlux.collectList()
                 .flatMap(list -> {
                     // list: List<FilePart>
                     // 서비스 로직 진행
                     return productService.saveReactive(title, price, list);
                 })
                 .map(saved -> ResponseEntity.ok("등록 성공"));
     }
     ```
   * **장점**: 대용량 이미지 업로드나 논블로킹 I/O 처리를 원할 때 유리합니다.
   * **단점**: 구현 복잡도가 높아지고, 일반 REST API–기반 프로젝트에서는 무겁게 느껴질 수 있습니다.

---

## 정리 및 추천

* **간단한 CRUD 수준**이라면, **1번(한 DTO에 `MultipartFile` / `List<MultipartFile>` 포함해서 `@ModelAttribute`로 받기)** 방식을 추천합니다.

  * 코드가 깔끔하고 검증(Validation) 로직을 DTO 레벨에서 관리할 수 있어서 유지보수가 편합니다.
  * 다만 Swagger에서 테스트가 잘 안 된다면, **DTO에는 순수 텍스트만 두고, 실제 파일은 `@RequestPart MultipartFile thumbnail, @RequestPart List<MultipartFile> images`로 분리**하여 사용하면 Swagger UI가 form-data를 정확히 잡아줍니다.
* 클라이언트 구현 측면에서 “한 번의 요청”으로 모든 데이터를 보내길 원한다면 1번 방식이 가장 편리합니다.
* 하지만 **“Swagger로 미리 시뮬레이션 테스트”를 중요하게 생각한다면**, \*\*2번 방식(텍스트 DTO + 파일 파라미터 각각 분리)\*\*이 사용 편의성이 더 높습니다.
* \*\*3번 방식(모두 `@RequestParam`)\*\*은 필드가 몇 개 없는 간단한 상황에서는 직관적이지만, 필드가 많아질수록 가독성과 유지보수성이 현저히 떨어지므로 가급적 권장하지 않습니다.

---

### 코드 예시: DTO 하나 + 파일 분리 (`@RequestPart`) 방식

```java
// 1) DTO: 순수 텍스트 필드만 존재
public class ProductRequestDto {
    @NotEmpty
    @Schema(description = "상품명", example = "스마트폰")
    private String title;

    @NotNull
    @Schema(description = "가격", example = "500000")
    private Integer price;

    @Schema(description = "상품 설명", example = "최신형 스마트폰입니다.")
    private String description;

    // + getter, setter
}
```

```java
// 2) Controller: DTO + 파일을 분리하여 받기
@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "상품 등록", description = "상품의 텍스트 정보와 이미지를 Multipart로 함께 전송하여 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart @Valid ProductRequestDto dto,                      // 순수 텍스트 정보만 바인딩
            @RequestPart("thumbnail")                                     
            @Schema(type = "string", format = "binary", description = "썸네일 이미지")
            MultipartFile thumbnail,                                      

            @RequestPart("images")
            @Schema(type = "string", format = "binary", description = "상세 이미지 목록")
            List<MultipartFile> images                                     
    ) {
        // → dto.getTitle(), dto.getPrice() 등 텍스트와
        //    thumbnail, images를 서비스로 전달
        productService.save(dto, thumbnail, images);
        return ResponseEntity.ok("상품 등록 성공");
    }
}
```

* 위 예시에서 **DTO 내부에 `MultipartFile` 필드를 두지 않고**, 반드시 `@RequestPart`로 파일을 명시적으로 분리했습니다.
* Swagger UI에서 “dto” 아래에 텍스트 필드( title, price, description )가 뜨고, 아래에 “thumbnail” → 파일 업로드 버튼, “images” → 복수 파일 업로드 버튼이 자동으로 표시됩니다.
* **만약 DTO 내부에 `MultipartFile thumbnail;`을 그대로 넣고 `@ModelAttribute ProductRequestDto dto`로만 받으면**, Swagger UI가 form 필드를 제대로 그려주지 않을 수 있으므로 이 방식을 사용하시는 편이 안전합니다.

---

## 결론

* \*\*“DTO 하나로 받는 게 좋다”\*\*는 말 자체는 부분적으로 맞습니다. 모든 데이터를 한 객체 안으로 묶어두면 **유효성 검증·확장성·유지보수 측면**에서 훨씬 유리하기 때문입니다.
* 다만 **Swagger 테스트를 포함한 실제 개발·디버깅 환경**을 고려할 때, DTO 내부에 `MultipartFile` 필드를 두는 경우 Swagger UI가 제대로 동작하지 않는 문제가 생길 수 있습니다.
  → 따라서 **DTO에는 텍스트 필드만 두고, 파일은 `@RequestPart`나 `@RequestParam`으로 분리하여 받는 방식**을 혼합해서 사용하시면, “한 번에 요청” 형태의 편리함과 “Swagger 호환성” 두 마리 토끼를 모두 잡을 수 있습니다.
* 이 외에도 **“파일 먼저 업로드 → URL만 DTO로 전달”** 또는 **“Base64 인코딩”** 등의 대체 방식을 필요에 따라 검토해 보세요.
