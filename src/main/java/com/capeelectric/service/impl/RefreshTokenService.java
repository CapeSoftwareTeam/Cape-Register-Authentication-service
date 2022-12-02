package com.capeelectric.service.impl;

import java.time.Instant;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.capeelectric.model.RefreshToken;
import com.capeelectric.repository.RefreshTokenRepository;

/**
 * 
 * @author capeelectricsoftware
 *
 */
@Service
@Transactional
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
		super();
		this.refreshTokenRepository = refreshTokenRepository;
	}

	public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    public void validateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token);
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}