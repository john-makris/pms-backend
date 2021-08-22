package gr.hua.pms.payload.response;

import java.time.Instant;

public class TokenRefreshResponse {
  private String accessToken;
  private String refreshToken;
  private Instant accessTokenExpiryDate;
  private Instant refreshTokenExpiryDate;
  private String tokenType = "Bearer";
  
  public TokenRefreshResponse(String accessToken, String refreshToken, Instant refreshTokenExpiryDate, Instant accessTokenExpiryDate) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.refreshTokenExpiryDate = refreshTokenExpiryDate;
    this.setAccessTokenExpiryDate(accessTokenExpiryDate);
  }
  

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String token) {
    this.accessToken = token;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public Instant getRefreshTokenExpiryDate() {
    return refreshTokenExpiryDate;
  }
	
  public void setRefreshTokenExpiryDate(Instant refreshTokenExpiryDate) {
    this.refreshTokenExpiryDate = refreshTokenExpiryDate;
  }


  public Instant getAccessTokenExpiryDate() {
	return accessTokenExpiryDate;
  }
	
	
  public void setAccessTokenExpiryDate(Instant accessTokenExpiryDate) {
	this.accessTokenExpiryDate = accessTokenExpiryDate;
  }

}