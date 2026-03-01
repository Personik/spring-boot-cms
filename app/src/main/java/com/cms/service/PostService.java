package com.cms.service;

import com.cms.domain.Post;
import com.cms.domain.Tenant;
import com.cms.domain.User;
import com.cms.dto.PostRequest;
import com.cms.dto.PostResponse;
import com.cms.repository.PostRepository;
import com.cms.repository.TenantRepository;
import com.cms.repository.UserRepository;
import com.cms.security.CmsUserPrincipal;
import com.cms.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PostResponse> listPosts(Pageable pageable) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return postRepository.findAll(pageable).map(this::toResponse);
        }
        return postRepository.findByTenantId(tenantId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Long tenantId = TenantContext.getTenantId();
        Post post;
        if (tenantId == null) {
            post = postRepository.findById(postId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        } else {
            post = postRepository.findByIdAndTenantId(postId, tenantId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));
        }
        return toResponse(post);
    }

    @Transactional
    public PostResponse createPost(PostRequest request) {
        Tenant tenant = resolveTenantForWrite(request.tenantId());
        User author = requireCurrentUser();

        Post post = new Post();
        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setContent(request.content());
        post.setTenant(tenant);
        post.setAuthor(author);

        return toResponse(postRepository.save(post));
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request) {
        Post post = findPostWithAccessCheck(postId);

        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setContent(request.content());

        return toResponse(postRepository.save(post));
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = findPostWithAccessCheck(postId);
        postRepository.delete(post);
    }

    private Post findPostWithAccessCheck(Long postId) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return postRepository.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        }
        return postRepository.findByIdAndTenantId(postId, tenantId).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));
    }

    private Tenant resolveTenantForWrite(Long requestTenantId) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            return tenantRepository.findById(tenantId) .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        }
        // Superadmin: must supply tenantId in the request body
        if (requestTenantId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "tenantId is required for superadmin post creation");
        }
        return tenantRepository.findById(requestTenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
    }

    private User requireCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CmsUserPrincipal principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private PostResponse toResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getContent(),
                post.getTenant().getId(),
                post.getAuthor().getId()
        );
    }
}
