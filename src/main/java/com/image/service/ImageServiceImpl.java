package com.image.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.image.dto.UpdateImageDTO;
import com.image.exceptions.InvalidFileExtensionException;
import com.image.response.MessageResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.image.FileConstant.FileConstant;
import com.image.dto.ImageDTO;
import com.image.entity.Image;
import com.image.repository.ImageRepository;
import com.image.util.FileUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * @author atjkm
 *
 */

@Slf4j
@Service
@Transactional
public class ImageServiceImpl implements ImageService {

	@Value("${gcs.bucket.name}")
    public String bucketName;
	
	private final ModelMapper modelMapper = new ModelMapper();
	
	@PersistenceContext
	private EntityManager entityManager;

	private final ImageRepository imageRepo;

	@Autowired
	public ImageServiceImpl(ImageRepository imageRepo) {
		this.imageRepo = imageRepo;
	}

	@Override
	public MessageResponse saveImage(List<MultipartFile> files) throws IOException, InvalidFileExtensionException {

		Storage storage = StorageOptions.getDefaultInstance().getService();

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			try {
				if (fileName != null && !fileName.isBlank() && fileName.contains(FileConstant.DOT)) {
					final String extension = fileName.substring(fileName.indexOf(FileConstant.DOT) + 1);
					boolean isValidExtension = false;

					for (String s : FileConstant.ALLOWEDEXTENSIONS) {
						if (extension.equals(s)) {
							isValidExtension = true;
							String fileNameWithoutExtension = FileUtils.extractFileNameWithoutExtension(fileName);

							Optional<Image> existingEntityOptional = findImageByName(fileNameWithoutExtension);
							Image existingEntity = existingEntityOptional.orElse(null);

							BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileNameWithoutExtension).build();

							try (WriteChannel writer = storage.writer(blobInfo);
								 InputStream inputStream = file.getInputStream()) {

								byte[] buffer = new byte[8192];
								int limit;
								while ((limit = inputStream.read(buffer)) >= 0) {
									writer.write(ByteBuffer.wrap(buffer, 0, limit));
								}
							}
							URL url = storage.signUrl(blobInfo, 7, TimeUnit.DAYS,
									Storage.SignUrlOption.withV4Signature());

							if (existingEntity != null) {
								existingEntity.setUrl(url.toString());
							} else {
								Image image = new Image();
								image.setName(fileNameWithoutExtension);
								image.setUrl(url.toString());
								entityManager.persist(image);
							}
							log.info("File {} saved successfully!", fileNameWithoutExtension);
							break;
						}
					}
					if (!isValidExtension) {
						String errorMessage = "Wrong file. Allowed extensions: " + Arrays.toString(FileConstant.ALLOWEDEXTENSIONS);
						log.warn(errorMessage);
						throw new InvalidFileExtensionException(new MessageResponse(errorMessage));
					}
				}
			} catch (IOException e) {
				throw new IOException("Error saving file: " + file.getOriginalFilename(), e);
			}
		}
		return new MessageResponse("Files saved to cloud successfully.");
	}

	public Optional<Image> findImageByName(String fileNameWithoutExtension) {
		return imageRepo.findByName(fileNameWithoutExtension);
	}

	@Override
	public Page<ImageDTO> getImages(Pageable pageable) {
		Page<Image> imagePage = imageRepo.findAll(pageable);
		return imagePage.map(image -> modelMapper.map(image, ImageDTO.class));
	}

	@Override
	public ImageDTO updateImage(Long id, UpdateImageDTO dto) {

		Image image = imageRepo.findById(id).orElse(null);

		if (image == null) {
			return null;
		}
		modelMapper.map(dto, image);

		Image savedImage = imageRepo.save(image);

		return modelMapper.map(savedImage, ImageDTO.class);
	}


	@Override
	public void deleteImage(Long id) {

		imageRepo.deleteById(id);
	}

}