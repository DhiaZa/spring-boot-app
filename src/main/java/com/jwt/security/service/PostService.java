package com.jwt.security.service;

import com.jwt.security.entity.Post;
import com.jwt.security.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@AllArgsConstructor
public class PostService {

    private  PostRepository postRepository;



    public Post createPost(Post post, MultipartFile image) {
        String imageName = StringUtils.cleanPath(image.getOriginalFilename());
        post.setImage(imageName);
        // Save the post entity to the repository
        return postRepository.save(post);
    }
}