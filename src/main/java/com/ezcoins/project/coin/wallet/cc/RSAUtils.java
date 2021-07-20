package com.ezcoins.project.coin.wallet.cc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ezcoins.utils.HttpUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


/**
 * RSA非对称加密算法工具类
 *
 * @author lixk
 */

public class RSAUtils {
    //非对称密钥算法
    private static final String KEY_ALGORITHM = "RSA";
    //密钥长度，在512到65536位之间，建议不要太长，否则速度很慢，生成的加密数据很长
    private static final int KEY_SIZE = 512;
    //字符编码
    private static final String CHARSET = "UTF-8";

    /**
     * 生成密钥对
     *
     * @return KeyPair 密钥对
     */
    public static KeyPair getKeyPair() throws Exception {
        return getKeyPair(null);
    }

    /**
     * 生成密钥对
     *
     * @param password 生成密钥对的密码
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair(String password) throws Exception {
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器
        if (password == null) {
            keyPairGenerator.initialize(KEY_SIZE);
        } else {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes(CHARSET));
            keyPairGenerator.initialize(KEY_SIZE, secureRandom);
        }
        //生成密钥对
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 取得私钥
     *
     * @param keyPair 密钥对
     * @return byte[] 私钥
     */
    public static byte[] getPrivateKeyBytes(KeyPair keyPair) {
        return keyPair.getPrivate().getEncoded();
    }

    /**
     * 取得Base64编码的私钥
     *
     * @param keyPair 密钥对
     * @return String Base64编码的私钥
     */
    public static String getPrivateKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(getPrivateKeyBytes(keyPair));
    }

    /**
     * 取得公钥
     *
     * @param keyPair 密钥对
     * @return byte[] 公钥
     */
    public static byte[] getPublicKeyBytes(KeyPair keyPair) {
        return keyPair.getPublic().getEncoded();
    }

    /**
     * 取得Base64编码的公钥
     *
     * @param keyPair 密钥对
     * @return String Base64编码的公钥
     */
    public static String getPublicKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(getPublicKeyBytes(keyPair));
    }

    /**
     * 私钥加密
     *
     * @param data       待加密数据
     * @param privateKey 私钥字节数组
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] privateKey) throws Exception {
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey key = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        //数据加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     *
     * @param data       待加密数据
     * @param privateKey Base64编码的私钥
     * @return String Base64编码的加密数据
     */
    public static String encryptByPrivateKey(String data, String privateKey) throws Exception {
        byte[] key = Base64.getDecoder().decode(privateKey);
        return Base64.getEncoder().encodeToString(encryptByPrivateKey(data.getBytes(CHARSET), key));
    }

    /**
     * 公钥加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥字节数组
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成公钥
        PublicKey key = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
        //数据加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     *
     * @param data      待加密数据
     * @param publicKey Base64编码的公钥
     * @return String Base64编码的加密数据
     */
    public static String encryptByPublicKey(String data, String publicKey) throws Exception {
        byte[] key = Base64.getDecoder().decode(publicKey);
        return Base64.getEncoder().encodeToString(encryptByPublicKey(data.getBytes(CHARSET), key));
    }

    /**
     * 私钥解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥字节数组
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] privateKey) throws Exception {
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey key = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        //数据解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data       Base64编码的待解密数据
     * @param privateKey Base64编码的私钥
     * @return String 解密数据
     */
    public static String decryptByPrivateKey(String data, String privateKey) throws Exception {
        byte[] key = Base64.getDecoder().decode(privateKey);
        return new String(decryptByPrivateKey(Base64.getDecoder().decode(data), key), CHARSET);
    }

    /**
     * 公钥解密
     *
     * @param data      待解密数据
     * @param publicKey 公钥字节数组
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //产生公钥
        PublicKey key = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
        //数据解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 公钥解密
     *
     * @param data      Base64编码的待解密数据
     * @param publicKey Base64编码的公钥
     * @return String 解密数据
     */
    public static String decryptByPublicKey(String data, String publicKey) throws Exception {
        byte[] key = Base64.getDecoder().decode(publicKey);
        return new String(decryptByPublicKey(Base64.getDecoder().decode(data), key), CHARSET);
    }

    /**
     * 测试加解密方法
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //公钥
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA07L/9eSyDhmPpUg2jBw1SmgNaGHnWglzj25rgLI4vsmoULjLYk81QBrvP1STDnrgRgR0wK8AmfnzA7BU2+73jduU0xpkZzlY05sjL4dFBX0UIvNoCKFP0DE+weG4Aqk1wFV2/vx1Q8yctb/kWxNS/NUKi+Ab9hpSEm8x9CQ2Q+VgLc9cWRQ9bQliGp4db9O8pfPdTHHi+UgYXavPtOrADREzunbaRdClyTb9774v0kYuKOFEsvYHbOpFa2L+WoyJ+I1yTEJ+1FyIJu1tcsaaW7AIo8N1h2CybYr7RRJwV9BAjFexXyr6b+0sBiM2ZTcXC4L5qya3JW2iN5R0l5GO8QIDAQAB";
        //私钥
        String privateKey = "MIIEowIBAAKCAQEA07L/9eSyDhmPpUg2jBw1SmgNaGHnWglzj25rgLI4vsmoULjL" +
                "Yk81QBrvP1STDnrgRgR0wK8AmfnzA7BU2+73jduU0xpkZzlY05sjL4dFBX0UIvNo" +
                "CKFP0DE+weG4Aqk1wFV2/vx1Q8yctb/kWxNS/NUKi+Ab9hpSEm8x9CQ2Q+VgLc9c" +
                "WRQ9bQliGp4db9O8pfPdTHHi+UgYXavPtOrADREzunbaRdClyTb9774v0kYuKOFE" +
                "svYHbOpFa2L+WoyJ+I1yTEJ+1FyIJu1tcsaaW7AIo8N1h2CybYr7RRJwV9BAjFex" +
                "Xyr6b+0sBiM2ZTcXC4L5qya3JW2iN5R0l5GO8QIDAQABAoIBAQCFpomAFQzwZ+TP" +
                "RULiu+BNBFoKn1yu9C6Xy+9kxZ+bm7Z+l9wXSchlm1uWEO/ygVLcAW4wQ4vOqgVp" +
                "O8anPzvcvFc923itzn+swFG7bNv/dhLjaeYxJJ494wkq4DUKwQclUQ+aNYpW7nO1" +
                "LtudQBrxchKBclBU1BMifj78UmxL+fsG67zdoi1d2qC7w1pibaBJf/jQnwXklwCs" +
                "59PTsoB5YXejwnAM1hfEEJqTmU7z8PeE6DGOSIqSe44DxiL/1Rl9/pj0S2F2QWHi" +
                "4G9lKbKZntDfDIIxVwKkrCYqNBvN9++1PR3HzRKuUfMA2Hea0Ss7OxZ9BpBVv5uG" +
                "MvHobG4NAoGBAOmaGFyFq0CHXP0lBNANgPv2czRcvrP/+E6jCU8UHzG/2qu+TvA5" +
                "abtEgPY47zzB9VM1W35293ocB24rdMOlH5OtJdIpis3xFejRNhunhzbBB2ZzdPAr" +
                "He8P6Vh+cdemxf6y1Ou/oNlH5GE2/69IZJxODNL+3aeOMeFFTZ6zxLAzAoGBAOf/" +
                "SfSZiKLPSap/AyXPtwBSPu4eggjU3OTpFG5ajgQL/8X6/gI+zY9SmQUh+sImyE+T" +
                "oJADEgr+EwfchKNZQ9hr7+bAh25zYRpscKo4IzUUbCSy0DYwQWCWWHc4og59xgtL" +
                "iByLBfpac2qJLQqEVlls1SvdCoRvqnlUV7nollBLAoGAHwZYNfGKzQctUnGYMgmZ" +
                "F92jb1DiAwX79HWyxs3lVrYkVKrx0PXPFRAEEK9/FpKsAmETxbgHJjCSzA75cwJ7" +
                "LyHP5BmgVbmvLzyZ8xJM4vjAAttSrJMNZrK2SwqAFCQSJ8GYhbbtpe3mZwLm8mCq" +
                "/BdhnKQ8zAQWm9SYentlpAECgYALC2xJ6MVIevXW2BUeR7U+nqvASlOGeBvMCgye" +
                "9FCugKO8mSZelP7UQ+plBCkb9Ry0LSMHuB2zyES/qw/ldupz8y+InMHV0EhfKapz" +
                "cBi8/ivn01t2OPXj00SC6H9OkU5MW1wvf67ZLFUyTTWHuqUhvLypSeymQWqsPAex" +
                "IhGF4QKBgFb6lnPShsWX9C5C7xm+y8SyCDvQ2X6LhlzGxWBWCM0kyLqII+R9FyAN" +
                "9qh6fXxQsnMVh3N3TVCpVuWgWcUgeZ/D/72Tg+6AHmjzJLpuWncIQa8B8MHTJrcP" +
                "VHe/kHbYnFUX+qScaoBBFOaAJac/PkWB6i+7hG5E9MfsfsIdk1UU";

        System.out.println("公钥：\n" + publicKey);
        System.out.println("私钥：\n" + privateKey);

        Security.addProvider(
                new BouncyCastleProvider()
        );

        String data = "{\"account\":\"account_name\",\"timestamp\":\"1625577020\"}";
        {
            System.out.println("\n===========公钥加密，私钥解密==============");
            String s1 = RSAUtils.encryptByPublicKey(data, publicKey);
            System.out.println("加密后的数据:" + s1);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", s1);
            HttpUtils.sendPost("https://wallet.ezcoins.cc/Wallet/v2/index", JSON.toJSONString(jsonObject));
            String s2 = RSAUtils.decryptByPrivateKey(s1, privateKey);
            System.out.println("解密后的数据:" + s2 + "\n\n");
        }
    }
}