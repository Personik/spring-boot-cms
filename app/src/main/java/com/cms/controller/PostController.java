package com.cms.controller;

import com.cms.dto.PostRequest;
import com.cms.dto.PostResponse;
import com.cms.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public Page<PostResponse> list(Pageable pageable) {
        return postService.listPosts(pageable);
    }

    @GetMapping("/{postId}")
    public PostResponse getOne(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(@Valid @RequestBody PostRequest request) {
        return postService.createPost(request);
    }

    @PutMapping("/{postId}")
    public PostResponse update(@PathVariable Long postId, @Valid @RequestBody PostRequest request) {
        return postService.updatePost(postId, request);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long postId) {
        postService.deletePost(postId);
    }
}

