package com.image.controller;

import java.io.IOException;
import java.util.List;

import com.image.dto.ImageDTO;
import com.image.dto.UpdateImageDTO;
import com.image.exceptions.InvalidFileExtensionException;
import com.image.response.ApiResponse;
import com.image.response.MessageResponse;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.image.entity.Image;
import com.image.service.ImageServiceImpl;

/**
 * @author atjkm
 *
 */

@Slf4j
@RestController
@RequestMapping("api/file/")
public class ImageController {
	
	@Autowired
	ImageServiceImpl imageService;
	
	@PostMapping("saveFile")
	public ResponseEntity<MessageResponse> saveImage(@RequestParam("files") List<MultipartFile> files) {
		try {
			MessageResponse successResponse = imageService.saveImage(files);
			return ResponseEntity.ok(successResponse);
		} catch (IOException e) {
			log.error("File reading/writing error during upload", e);
			return ResponseEntity.badRequest().body(new MessageResponse("Error uploading files: File reading/writing error."));
		} catch (IllegalArgumentException e) {
			log.error("Invalid file argument during upload", e);
			return ResponseEntity.badRequest().body(new MessageResponse("Error uploading files: Invalid file argument."));
		} catch (InvalidFileExtensionException e) {
			return ResponseEntity.badRequest().body(e.getMessageResponse());
		} catch (Exception e) {
			log.error("Unexpected error during file upload", e);
			return ResponseEntity.internalServerError().body(new MessageResponse("An unexpected error occurred."));
		}
	}

	@PutMapping("{id}")
	public ResponseEntity<ApiResponse<ImageDTO>> updateImageWithUpdateDTO(
			@PathVariable Long id,
			@Valid @RequestBody UpdateImageDTO dto) {

		try {
			ImageDTO updatedImage = imageService.updateImage(id, dto);

			if (updatedImage != null) {
				ApiResponse<ImageDTO> response = new ApiResponse<>("Image updated successfully", updatedImage);
				return ResponseEntity.ok(response);
			} else {
				ApiResponse<ImageDTO> response = new ApiResponse<>("Image not found", null);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

		} catch (EntityNotFoundException ex) {
			ApiResponse<ImageDTO> response = new ApiResponse<>(ex.getMessage(), null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		} catch (Exception ex) {
			ApiResponse<ImageDTO> response = new ApiResponse<>("An error occurred", null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){
		imageService.deleteImage(id);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("getFiles")
	public Page<ImageDTO> getImages(Pageable pageable) {

		return imageService.getImages(pageable);
	}

}

