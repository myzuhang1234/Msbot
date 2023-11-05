package com.badeling.msbot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.badeling.msbot.entity.ImageSecurity;

public interface ImageSecurityRepository extends CrudRepository<ImageSecurity, Long>{
	
	@Query(value = "select * from image_security where image_hash_code = ?1 limit 0,1",nativeQuery=true)
	ImageSecurity findImageByHash(String sha256Hex);
	
	@Query(value = "select * from image_security where image_name = ?1 limit 0,1",nativeQuery=true)
	ImageSecurity findImageByName(String imageName);

}
