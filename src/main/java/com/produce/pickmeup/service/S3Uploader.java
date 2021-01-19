package com.produce.pickmeup.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.produce.pickmeup.common.ErrorCase;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-aws.yml")
public class S3Uploader {
	private final static List<String> IMAGE_EXTENSIONS = Arrays
		.asList(".jpg", ".jpeg", ".gif", ".png", ".img", ".tiff", ".heif");
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
		String extension = getExtension(uploadFile.getName()).toLowerCase();
		if (!IMAGE_EXTENSIONS.contains(extension)) {
			deleteLocalFile(uploadFile);
			return ErrorCase.INVALID_FILE_TYPE;
		}
		String fileName = dirName + "/" + id + extension;
		String uploadImageUrl = putS3(uploadFile, fileName);
		deleteLocalFile(uploadFile);
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

	private void deleteLocalFile(File uploadFile) {
		if (!uploadFile.delete()) {
			System.out.println(ErrorCase.FAIL_FILE_DELETE_ERROR);
		}
	}
}
