package gr.hua.pms.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.pms.exception.TokenRefreshException;
import gr.hua.pms.model.RefreshToken;
import gr.hua.pms.repository.RefreshTokenRepository;
import gr.hua.pms.repository.UserRepository;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
	  @Value("${hua.pms.jwtRefreshExpirationMs}")
	  private Long refreshTokenDurationMs;

	  @Autowired
	  private RefreshTokenRepository refreshTokenRepository;

	  @Autowired
	  private UserRepository userRepository;
	  
	@Override
	public Optional<RefreshToken> findByToken(String token) {
	    return refreshTokenRepository.findByToken(token);
	}

	@Override
	public RefreshToken createRefreshToken(Long userId) {
	    RefreshToken refreshToken = new RefreshToken();

	    refreshToken.setUser(userRepository.findById(userId).get());
	    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
	    refreshToken.setToken(UUID.randomUUID().toString());

	    refreshToken = refreshTokenRepository.save(refreshToken);
	    return refreshToken;
	}

	@Override
	public RefreshToken verifyExpiration(RefreshToken token) {
		RefreshToken currentRefreshToken = token;
		RefreshToken newToken = new RefreshToken();
		  
	  if (currentRefreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
	    refreshTokenRepository.delete(currentRefreshToken);
	    throw new TokenRefreshException(currentRefreshToken.getToken(), "Refresh token was expired. Please make a new signin request");
	  }
	    
	  currentRefreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
	  refreshTokenRepository.deleteAll();
	  newToken = refreshTokenRepository.save(currentRefreshToken);
	  System.out.println("Hallo: "+newToken.getExpiryDate());
	  return newToken;
	}

	@Transactional
	@Override
	  public int deleteByUserId(Long userId) {
		System.out.println("DELETE REFRESH TOKEN: "+userId);
	    return refreshTokenRepository.deleteByUserId(userRepository.findById(userId).get().getId());
	  }
}