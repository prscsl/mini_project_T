package com.sparta.mini_project01.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.mini_project01.domain.Image;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Component
@Service
@Getter
@RequiredArgsConstructor
public class S3UploaderService {

    private final AmazonS3 amazonS3;

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public Image uploadFiles(MultipartFile multipartFile , String filepath)throws IOException{
        File uploadFile =convert(multipartFile)
                .orElseThrow(()->new IllegalArgumentException("ERROR :  MultipartFile -> File convert fail"));

        return upload(uploadFile,filepath);
    }

    //(S3 서버에 올리면서 로컬에 만든 파일 지우기) 총괄 함수
    public Image upload(File uploadFile, String filepath){
        String fileName = filepath+"/"+UUID.randomUUID() + uploadFile.getName();

        String uploadImageUrl = putS3(uploadFile,fileName);

        removeNewFile(uploadFile);
        return Image.builder()
                .key(fileName)
                .path(uploadImageUrl)
                .build();
    }

    //S3 업로드
    public String putS3(File uploadFile, String fileName){
        amazonS3.putObject(new PutObjectRequest(bucket,fileName,uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket,fileName).toString();
    }

    //로컬에 삭제
    private void removeNewFile(File targetFile){
        if(targetFile.delete()){
            System.out.println("File delete success");
            return;
        }
        System.out.println("file delete fail");
    }


    //로컬에 파일 업로드하기
    private Optional<File> convert(MultipartFile file) throws IOException{
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File convertFile = new File(System.getProperty("user.dir") + "/" +now+".jpg" );

        if(convertFile.createNewFile()){
            try(FileOutputStream fos = new FileOutputStream(convertFile)){
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        System.out.println("변환실패");
        return Optional.empty();
    }

    // image 삭제
    public void remove(String key) {
        amazonS3.deleteObject(bucket, key);
    }

}
