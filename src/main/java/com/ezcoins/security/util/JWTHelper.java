package com.ezcoins.security.util;

import com.ezcoins.constant.Constants;
import com.ezcoins.constant.interf.JWTConstans;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.exception.jwt.TokenException;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.security.configuration.TokenProperties;
import com.ezcoins.utils.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by wx on 2017/9/10.
 */
@Component
public class JWTHelper {

    private static RsaKeyHelper rsaKeyHelper = new RsaKeyHelper();

    @Autowired
    private TokenProperties tokenProperties;


    @Autowired
    private RedisCache redisCache;

    public String getToken(HttpServletRequest request){
        String token = request.getHeader(tokenProperties.getHeader());
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)){
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }


    public IJWTInfo jwtInfo(HttpServletRequest request) throws Exception {
        String token = request.getHeader(tokenProperties.getHeader());
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)){
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return getInfoFromToken(token);
    }



    /**
     * 密钥加密token
     *
     * @param jwtInfo
     * @param priKeyPath
     * @return
     * @throws Exception
     */
    private String generateToken(IJWTInfo jwtInfo, String priKeyPath) {
        String compactJws = Jwts.builder()
                .setSubject(jwtInfo.getUserName())
                .claim(JWTConstans.JWT_KEY_USER_ID, jwtInfo.getUserId())
                .claim(JWTConstans.JWT_KEY_USER_TYPE, jwtInfo.getUserType())
                .setExpiration(new Date(System.currentTimeMillis() + (long) tokenProperties.getExpireTime() * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, tokenProperties.getSecret())
                .compact();
        return compactJws;
    }

    /**
     * 将用户数据存入数据库
     */
     public String createToken(IJWTInfo jwtInfo){
         redisCache.setCacheObject(RedisConstants.LOGIN_USER_KEY+jwtInfo.getUserId()+"_"+jwtInfo.getUserType(),
                 jwtInfo,tokenProperties.getExpireTime(),
                 TimeUnit.MINUTES);
        return generateToken(jwtInfo,null);
     }

    /**
     * 验证token是否过期
     */
    public boolean verifyToken(IJWTInfo jwtInfo){
        IJWTInfo redisCacheCacheObject = redisCache.getCacheObject(RedisConstants.LOGIN_USER_KEY + jwtInfo.getUserId() + "_" + jwtInfo.getUserType());
        if (StringUtils.isNull(redisCacheCacheObject)){
            return false;
        }
        return jwtInfo.equals(redisCacheCacheObject);
    }



    /**
     * 密钥加密token
     *
     * @param jwtInfo
     * @param priKey
     * @param expire
     * @return
     * @throws Exception
     */
    public static String generateToken(IJWTInfo jwtInfo, byte priKey[], int expire) throws Exception {
        String compactJws = Jwts.builder()
                .setSubject(jwtInfo.getUserName())
                .claim(JWTConstans.JWT_KEY_USER_ID, jwtInfo.getUserId())
                .claim(JWTConstans.JWT_KEY_USER_TYPE, jwtInfo.getUserType())
                .setExpiration(DateTime.now().plusSeconds(expire).toDate())
                .signWith(SignatureAlgorithm.RS256, rsaKeyHelper.getPrivateKey(priKey))
                .compact();
        return compactJws;
    }

    /**
     * 公钥解析token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public  Jws<Claims> parserToken(String token) throws Exception {
        Jws<Claims> claimsJws =null;
        try {
           claimsJws = Jwts.parser().setSigningKey(tokenProperties.getSecret()).parseClaimsJws(token);
        }catch (Exception e){
            throw new TokenException();
        }
        return claimsJws;
    }
    /**
     * 公钥解析token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Jws<Claims> parserToken(String token, byte[] pubKey) throws Exception {
        return Jwts.parser().setSigningKey(rsaKeyHelper.getPublicKey(pubKey)).parseClaimsJws(token);
    }
    /**
     * 获取token中的用户信息
     *
     * @param token
     * @return
     * @throws Exception
     */
    public  IJWTInfo getInfoFromToken(String token) throws Exception {
        Jws<Claims> claimsJws = parserToken(token);
        if (null==claimsJws){
            return null;
        }
        Claims body = claimsJws.getBody();
        return new JWTInfo(body.getSubject(), StringHelper.getObjectValue(body.get(JWTConstans.JWT_KEY_USER_ID)), StringHelper.getObjectValue(body.get(JWTConstans.JWT_KEY_USER_TYPE)));
    }
    /**
     * 获取token中的用户信息
     *
     * @param token
     * @param pubKey
     * @return
     * @throws Exception
     */
    public static IJWTInfo getInfoFromToken(String token, byte[] pubKey) throws Exception {
        Jws<Claims> claimsJws = parserToken(token, pubKey);
        Claims body = claimsJws.getBody();
        return new JWTInfo(body.getSubject(), StringHelper.getObjectValue(body.get(JWTConstans.JWT_KEY_USER_ID)), StringHelper.getObjectValue(body.get(JWTConstans.JWT_KEY_USER_TYPE)));
    }

    public static void main(String[] args) throws Exception {


    }
}
