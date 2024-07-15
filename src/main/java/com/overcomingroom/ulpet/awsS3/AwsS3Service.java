package com.overcomingroom.ulpet.awsS3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsS3Service {


    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 경로를 지정하고, s3에 이미지를 업로드합니다.
     * /장소명/uuidFileNAme
     *
     * @param multipartFile 이미지
     * @param dirName       폴더명(장소명)
     * @return imageURL
     * @throws IOException
     */
    @Transactional
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {

        String uniqueFileName = getUniqueFileName(multipartFile);

        String fileName = dirName + "/" + uniqueFileName;
        File uploadFile = convertMultipartFileToFile(multipartFile, uniqueFileName);

        // 이미지 업로드
        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);
        return uploadImageUrl;
    }


    /**
     * uuid 를 이용해 unique한 fileName을 생성
     *
     * @param multipartFile
     * @return fileName
     */
    private static String getUniqueFileName(MultipartFile multipartFile) {
        // 파일 이름에서 공백을 제거한 새로운 파일 이름 생성
        String originalFileName = multipartFile.getOriginalFilename();

        // UUID를 파일명에 추가
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");
        return uniqueFileName;
    }

    /**
     * 로컬에 임시 저장된 업로드 파일을 삭제
     *
     * @param targetFile 삭제할 파일
     */
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    /**
     * MultipartFile에서 File 로 변경합니다.
     *
     * @param file
     * @param uniqueFileName
     * @return
     * @throws IOException
     */
    private File convertMultipartFileToFile(MultipartFile file, String uniqueFileName) throws IOException {

        File convertFile = new File(uniqueFileName);
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                log.error("파일 변환 중 오류 발생: {}", e.getMessage());
                throw e;
            }
            return convertFile;
        }
        throw new IllegalArgumentException(String.format("파일 변환에 실패했습니다. %s", file.getOriginalFilename()));
    }

    /**
     * Uploads Amazon S3 bucket
     *
     * @param uploadFile
     * @param fileName
     * @return url
     */
    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    /**
     * S3에서 image file 삭제
     *
     * @param fileName
     */
    public void deleteFile(String fileName) throws AmazonServiceException, UnsupportedEncodingException {

        // URL 디코딩을 통해 원래의 파일 이름을 가져옴
        String decodedFileName = URLDecoder.decode(fileName, "UTF-8");

        String[] splitUrl = decodedFileName.split("/");
        String imageFilename = splitUrl[splitUrl.length - 1];
        String dirName = splitUrl[splitUrl.length - 2];
        String setKey = dirName + "/" + imageFilename;

        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, setKey);
        try {
            // 버킷에서 이미지 삭제
            amazonS3.deleteObject(deleteObjectRequest);
        } catch (AmazonServiceException e) {
            log.error("Error while decoding the file name: {}", e.getMessage());
        }
    }
}
