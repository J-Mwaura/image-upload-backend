package com.image.service;

import java.io.IOException;
import java.util.List;

import com.image.dto.UpdateImageDTO;
import com.image.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.image.dto.ImageDTO;

/**
 * @author atjkm
 *
 */

public interface ImageService {

	MessageResponse saveImage(List<MultipartFile> files) throws Exception;
	Page<ImageDTO> getImages(Pageable pageable);
	ImageDTO updateImage(Long id, UpdateImageDTO dto);
	void deleteImage(Long id);
}

