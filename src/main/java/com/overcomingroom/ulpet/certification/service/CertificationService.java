package com.overcomingroom.ulpet.certification.service;

import com.overcomingroom.ulpet.awsS3.AwsS3Service;
import com.overcomingroom.ulpet.certification.domain.dto.CertificationRequestDto;
import com.overcomingroom.ulpet.certification.domain.dto.CertificationResponseDto;
import com.overcomingroom.ulpet.certification.domain.entity.Certification;
import com.overcomingroom.ulpet.certification.repository.CertificationRepository;
import com.overcomingroom.ulpet.exception.CustomException;
import com.overcomingroom.ulpet.exception.ErrorCode;
import com.overcomingroom.ulpet.member.domain.entity.MemberEntity;
import com.overcomingroom.ulpet.member.repository.MemberRepository;
import com.overcomingroom.ulpet.member.service.MemberService;
import com.overcomingroom.ulpet.place.domain.entity.Feature;
import com.overcomingroom.ulpet.place.domain.entity.Place;
import com.overcomingroom.ulpet.place.domain.entity.PlaceImage;
import com.overcomingroom.ulpet.place.repository.FeatureRepository;
import com.overcomingroom.ulpet.place.repository.PlaceImageRepository;
import com.overcomingroom.ulpet.place.repository.PlaceRepository;
import com.overcomingroom.ulpet.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final PlaceRepository placeRepository;
    private final FeatureRepository featureRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PlaceImageRepository placeImageRepository;
    private final PlaceService placeService;
    private final AwsS3Service awsS3Service;
    private final String CERTIFICATION_PATH = "certification/";

    /**
     * 장소에 대한 인증 하기
     *
     * @param username                login username
     * @param multipartFile           인증 이미지
     * @param certificationRequestDto requestDto
     * @return CertificationResponseDto
     * @throws IOException
     */
    @Transactional
    public CertificationResponseDto certification(String username, MultipartFile multipartFile, CertificationRequestDto certificationRequestDto) throws IOException {

        // 이미지 파일이 없을 때
        if (multipartFile.isEmpty()) {
            throw new CustomException(ErrorCode.NO_IMAGE);
        }

        // member 정보
        Long memberId = certificationRequestDto.getMemberId();
        // member 검증 및 반환
        MemberEntity member = memberService.verifyMemberAccessAndRetrieve(memberId, username);

        // 장소 정보
        Place place = null;
        Long placeId = certificationRequestDto.getPlaceId();
        String address = certificationRequestDto.getAddress();
        String placeName = certificationRequestDto.getPlaceName();
        String categoryName = certificationRequestDto.getCategoryName();

        // 1. 장소 ID 가 들어옴
        if (placeId != null) {
            place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
            log.info("1. findByPlaceId = {}", place.getPlaceName());
        }

        // 2. 주소와 이름, 카테고리 정보가 들어옴.
        else if (placeId == null &&                                 // placeId 정보 없음
                (address != null || !address.isEmpty())             // 주소 정보 존재
                && (placeName != null || !placeName.isEmpty())      // 장소에 대한 이름 존재
                && (categoryName != null || !categoryName.isEmpty())// 카테고리 정보 존재
        ) {
            Optional<Place> findByPlaceNameAndAddress = placeRepository.findByPlaceNameAndAddress(placeName, address);
            if (!findByPlaceNameAndAddress.isPresent()) {
                // 사용자에 의해 새로운 장소가 등록됨.
                place = placeService.userRegistersPlace(placeName, address, categoryName, memberId);
                log.info("2-1. register = {}", place.getPlaceName());

            } else {
                place = findByPlaceNameAndAddress.get();
                log.info("2-2. findByPlaceNameAndAddress = {}", place.getPlaceName());
            }
        }
        // 입력이 잘못 됨.
        else {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 인증 가능 여부 확인
        if (!isCertificationPossibleToday(member, place)) {
            // 자정 전이라면 인증 불가능 처리
            throw new CustomException(ErrorCode.CERTIFICATION_BEFORE_MIDNIGHT);
        }

        log.info("인증 가능 여부 확인 완료");

        // 특징 추가 목줄, 입마개등등 list 로 들어옴.
        List<Feature> featureList = certificationRequestDto.getFeatureList();

        // 인증 이미지 S3에 업로드 ( certification / 장소명 / 인증 사진명 )

        String uploadImageUrl = awsS3Service.upload(multipartFile, CERTIFICATION_PATH + place.getPlaceName());

        place.getFeatures().addAll(featureRepository.saveAll(featureList));

        Place savePlace = placeRepository.save(place);

        // 이미지 사용 여부
        boolean useImage = certificationRequestDto.isUseImage();

        // 만약 이미지 사용여부가 true일때 placeImage 가 null이라면 image 를 대표 사진으로 사용함.
        if (useImage) {
            PlaceImage placeImage = placeImageRepository.findByPlaceId(place.getId());

            if (placeImage.getImageUrl() == null || placeImage.getImageUrl().isEmpty()) {
                placeImage.modifyPlaceImageUrl(uploadImageUrl);
                placeImageRepository.save(placeImage);
            }
        }

        // 인증 저장
        Certification certification = certificationRepository.save(Certification.builder()
                .certificationImageUrl(uploadImageUrl)
                .member(member)
                .place(savePlace)
                .features(featureList)
                .useImage(useImage)
                .build());

        return CertificationResponseDto.of(certification);
    }


    /**
     * 인증 상세 정보를 반환 합니다.
     *
     * @param certificationId 인증 장소 Id
     * @param username        login username
     * @return CertificationResponseDto
     */
    public CertificationResponseDto certificationDetail(Long certificationId, String username) {
        // 인증 정보와 멤버 이름으로 본인이 맞는지 검증을 함.
        // 인증 정보 조회
        Certification certification = certificationRepository.findById(certificationId).orElseThrow(() -> new CustomException(ErrorCode.CERTIFICATION_NOT_FOUND));

        // member 검증 및 반환
        MemberEntity member = memberService.verifyMemberAccessAndRetrieve(certification.getMember().getMemberId(), username);

        return CertificationResponseDto.of(certification);
    }

    /**
     * 장소에 대한 인증 List 를 반환 합니다.
     *
     * @param placeId 장소 Id
     * @return List<CertificationResponseDto>
     */
    public List<CertificationResponseDto> certificationListForPlace(Long placeId) {

        Place place = placeRepository.findById(placeId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));

        List<Certification> allByPlace = certificationRepository.findAllByPlace(place);

        return allByPlace.stream().map(o -> CertificationResponseDto.of(o)).toList();
    }

    /**
     * 멤버의 인증 리스트를 반환 합니다.
     *
     * @param userId   멤버 Id
     * @param username login username
     * @return List<CertificationResponseDto>
     */
    public List<CertificationResponseDto> userCertificationList(Long userId, String username) {

        MemberEntity findById = memberRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // member 검증 및 반환
        MemberEntity member = memberService.verifyMemberAccessAndRetrieve(findById.getMemberId(), username);

        List<Certification> certifications = certificationRepository.findAllByMember(member).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_CERTIFICATION_LIST_IS_EMPTY));

        return certifications.stream().map(o -> CertificationResponseDto.of(o)).toList();
    }

    /**
     * 유저가 인증을 삭제합니다.
     *
     * @param certificationId 삭제할 id
     * @param username        login username
     */
    public void certificationDelete(Long certificationId, String username, boolean deletedByMember) throws UnsupportedEncodingException {

        Certification certification = certificationRepository.findById(certificationId).orElseThrow(() -> new CustomException(ErrorCode.CERTIFICATION_NOT_FOUND));

        Long memberId = certification.getMember().getMemberId();

        // member 검증 및 반환
        memberService.verifyMemberAccessAndRetrieve(memberId, username);

        Place place = certification.getPlace();
        String certificationImageUrl = certification.getCertificationImageUrl();

        // 만약 place 이미지가 user의 인증 사진인 경우, 대표사진을 null값으로 변경함.
        PlaceImage placeImage = placeImageRepository.findByPlaceId(place.getId());
        String imageUrl = placeImage.getImageUrl();
        if (imageUrl.equals(certificationImageUrl)) {
            placeImage.modifyPlaceImageUrl(null);
            placeImageRepository.save(placeImage);
        }

        // 멤버가 인증을 직접 삭제하는 경우
        // 특징 삭제, S3에서 이미지 삭제
        if (deletedByMember) {
            List<Feature> features = certification.getFeatures();
            // 특정 장소에 매핑된 특징 삭제
            List<Feature> placeAllFeatures = place.getFeatures();
            for (Feature allFeature : features) {
                for (Feature placeAllFeature : placeAllFeatures) {
                    if (allFeature.equals(placeAllFeature)) {
                        placeAllFeatures.remove(allFeature);
                        break;
                    }
                }
            }
            placeRepository.save(place);
            // S3 이미지 삭제
            awsS3Service.deleteFile(certificationImageUrl);
            // 인증 삭제
            certificationRepository.delete(certification);
        }
    }

    /**
     * 금일 인증 가능한지 확인합니다. (같은 장소에 대해 하루 한번 인증 가능.)
     *
     * @param member
     * @param place
     * @return
     */
    private boolean isCertificationPossibleToday(MemberEntity member, Place place) {
        // 장소와 멤버 정보로 인증 리스트를 찾음.
        log.info("인증 가능 여부 확인 시작");

        Optional<List<Certification>> optionalCertifications = certificationRepository.findAllByPlaceAndMember(place, member);

        if (optionalCertifications.get().isEmpty()) {
            return true;
        }

        // 오늘 날짜
        LocalDate today = LocalDate.now();
        // 내일 자정
        LocalDateTime nextDayMidnight = today.atStartOfDay().plusDays(1);

        // 만약 인증 리스트에 정보가 있다면
        if (optionalCertifications.isPresent()) {
            List<Certification> certifications = optionalCertifications.get();

            // 인증 리스트를 createAt 내림차순으로 정렬
            certifications.sort((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()) > 0 ? -1 : 1);
            Certification certification = certifications.get(0);

            // 인증 날짜
            LocalDateTime createdAt = certification.getCreatedAt();
            LocalDate certificationLocalDate = createdAt.toLocalDate();

            // 다음날 자정 전인지 확인
            if (createdAt.isBefore(nextDayMidnight) && certificationLocalDate.isEqual(today)) {
                return false;
            }
        }
        return true;
    }
}
