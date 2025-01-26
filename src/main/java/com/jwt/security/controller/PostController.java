package com.jwt.security.controller;


import com.jwt.security.auth.AuthenticationService;
import com.jwt.security.config.JwtService;
import com.jwt.security.entity.Post;
import com.jwt.security.entity.Task;
import com.jwt.security.entity.User;
import com.jwt.security.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("api/v1/post")
public class PostController {
    private  PostService postService;
    private  AuthenticationService authenticationService;
    private  JwtService jwtService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestParam("image") MultipartFile image,
                                           @RequestParam("title") String title,
                                           @RequestParam("content") String content,
                                           HttpServletRequest request) {
        // Extract the user ID from the JWT token
        String token = extractTokenFromRequest(request);
        String email = jwtService.extractUsername(token);

        User user = authenticationService.getUserByEmail(email);

        if (user != null) {
            Post post = new Post();
            post.setTitle(title);
            post.setContent(content);
            post.setUser(user);

            // Handle the image file, save it or process it as needed
            if (!image.isEmpty()) {
                // Save the image or perform any other operations
                // For example, you can store the image in a separate directory or database
                // and set the image path in the post entity
                String imagePath = saveImage(image);
                post.setImage(imagePath);
            }

            Post createdPost = postService.createPost(post, image);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } else {
            // Handle the case when the user is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    private String saveImage(MultipartFile image) {
        try {
            // Generate a unique filename for the image
            String fileName = generateUniqueFileName(image.getOriginalFilename());

            // Specify the directory where the images will be stored

            String uploadDirectory = "C:\\Users\\user\\Desktop\\Nouveau dossier (2)\\upload";

            // Create the directory if it doesn't exist
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create the file path for saving the image
            String filePath = uploadDirectory + fileName;

            // Save the image to the specified path
            File imageFile = new File(filePath);
            FileCopyUtils.copy(image.getBytes(), imageFile);

            // Return the file path or URL of the saved image
            return filePath;
        } catch (IOException e) {
            // Handle the exception (e.g., log an error, return a default image path)
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to generate a unique filename
    private String generateUniqueFileName(String originalFilename) {
        // Generate a unique filename based on the original filename
        // You can customize this method based on your requirements
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFileName = System.currentTimeMillis() + extension;
        return uniqueFileName;
    }
}
