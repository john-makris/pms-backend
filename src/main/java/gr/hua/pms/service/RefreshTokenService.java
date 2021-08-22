package gr.hua.pms.service;

import java.util.Optional;

import gr.hua.pms.model.RefreshToken;

public interface RefreshTokenService {

	  public Optional<RefreshToken> findByToken(String token);

	  public RefreshToken createRefreshToken(Long userId);

	  public RefreshToken verifyExpiration(RefreshToken token);

	  public int deleteByUserId(Long userId);
}