package com.produce.pickmeup.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.produce.pickmeup.common.ErrorCase;
import java.io.File;
import java.io.FileOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-aws.yml")
public class S3Uploader {
	private final static String TEMP_FILE_PATH = "src/main/resources/";
	private final AmazonS3Client amazonS3Client;
	@Value("${cloud.aws.s3.bucket}")
	public String bucket;

	public String upload(MultipartFile multipartFile, String dirName, String id) {
		try {
			File convertedFile = convert(multipartFile);
			if (convertedFile == null) {
				return ErrorCase.FAIL_FILE_CONVERT_ERROR;
			}
			return upload(convertedFile, dirName, id);
		} catch (Exception e) {
			return ErrorCase.FAIL_FILE_SAVE_ERROR;
		}
	}

	private File convert(MultipartFile file) {
		File convertFile = new File(TEMP_FILE_PATH + file.getOriginalFilename());
		try {
			if (convertFile.createNewFile()) {
				try (FileOutputStream fos = new FileOutputStream(convertFile)) {
					fos.write(file.getBytes());
				}
				return convertFile;
			}
		} catch (Exception e) {
			e.printStackTrace(); //for logging
		}
		return null;
	}

	private String upload(File uploadFile, String dirName, String id) {
		String fileName = dirName + "/" + id + getExtension(uploadFile.getName());
		String uploadImageUrl = putS3(uploadFile, fileName);
		if (!uploadFile.delete()) {
			System.out.println(ErrorCase.FAIL_FILE_DELETE_ERROR);
		}
		return uploadImageUrl;
	}

	private String getExtension(String fileName) {
		return "." + fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	private String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(
			new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(
				CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}
}
