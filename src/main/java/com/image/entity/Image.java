package com.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * @author atjkm
 *
 */

@Entity
@Getter
@Setter
public class Image {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private String name;
	@Column(name = "imageurl", columnDefinition = "TEXT", nullable = false)
	private String url;
	
	public Image() {}
	
	public Image(Long id, String name, String url) {
		this.id = id;
		this.name = name;
		this.url = url;
	}

}

