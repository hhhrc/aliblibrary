package cn.hbjx.alib.encryption;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class RSA {

    private static Cipher cipher;
    public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCe7ylt5je2WZu2NvFcNuMeZcToZTCjAXPnEEEW\nmYrX+LTnvIStMFMdtXFVQQR6G7xH2htTLYOwD7S/4X3r5F74l/QqNO9pqUfu96bSxsPHq7PVEflx\nZqQ7sePZN3ccu7VkwdGGy7eKkMbzjWk83xZ2ZlkY0b/nt7br4PrF4WamGwIDAQAB";
    public static String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJ7vKW3mN7ZZm7Y28Vw24x5lxOhl\nMKMBc+cQQRaZitf4tOe8hK0wUx21cVVBBHobvEfaG1Mtg7APtL/hfevkXviX9Co072mpR+73ptLG\nw8ers9UR+XFmpDux49k3dxy7tWTB0YbLt4qQxvONaTzfFnZmWRjRv+e3tuvg+sXhZqYbAgMBAAEC\ngYAoGgBCEoyMHiAD4ekUc2TrDpKYcK/M8VjlPFyv7x3xUHeU4SQ47rCKFnX6JOWUSds/5fBvFFTd\n35ijsamsE3tCp1Na9Z7zb6LDX9HTTDRBMXE9wwnzkRjtxwPrxZA+8XqvI3CPQSjRvnzIVpYEGxvJ\nItaMfQjnaal6yXk11PAnwQJBAOtcaZ5MZQWp8BVTgndCouI/JlZPAh01GKE8AA4pjQmCeh3U+Cm5\nfg0vnNAOzu3hZti01LZmGQQAYx5Z/oeCKPcCQQCs3w4aoeMY7pN3HSR/cRVXWfFsKGmqlmpHvjjg\nC3qdXeIft+bpdAivdQzfUFsZdeSZKijN3EcLoideH30xbKb9AkEAsShET+UM/XBmyIatY3uDA22p\nO7oIy1dWDLcPC5n3ETtnE+FkUnPPD7nQ/ULIO4I4WdHzcr/zAHISKZxv3Cv7DQJBAIs4qHcyYV62\n5PRM+BPa0sEiopfkhBTqRnW48L0fAYSzE2VQChuBY21K6y793CBJHe1sUqhX+q+Xy/S1j3G6e1EC\nQQDc3cDsTFg2QUmH7Re1YE8mZu6FlGwFmdDuFJSc1ocsrjmV8Uf9BNcDe8RxsAsuS9fbaaTpresJ\n8T2z9cHoE4yf";
    public static int base64flag = 2;

    public RSA() {
    }

    public static Map<String, String> generateKeyPair(String filePath) {
        try {
            KeyPairGenerator e = KeyPairGenerator.getInstance("RSA");
            e.initialize(1024);
            KeyPair keyPair = e.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
            String publicKeyString = getKeyString(publicKey);
            String privateKeyString = getKeyString(privateKey);
            FileWriter pubfw = new FileWriter(filePath + "/publicKey.keystore");
            FileWriter prifw = new FileWriter(filePath + "/privateKey.keystore");
            BufferedWriter pubbw = new BufferedWriter(pubfw);
            BufferedWriter pribw = new BufferedWriter(prifw);
            pubbw.write(publicKeyString);
            pribw.write(privateKeyString);
            pubbw.flush();
            pubbw.close();
            pubfw.close();
            pribw.flush();
            pribw.close();
            prifw.close();
            HashMap map = new HashMap();
            map.put("publicKey", publicKeyString);
            map.put("privateKey", privateKeyString);
            return map;
        } catch (Exception var12) {
            var12.printStackTrace();
            return null;
        }
    }

    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes = android.util.Base64.decode(key, base64flag);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = android.util.Base64.decode(key, base64flag);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static String getKeyString(Key key) throws Exception {
        byte[] keyBytes = key.getEncoded();
        String s = android.util.Base64.encodeToString(keyBytes, base64flag);
        return s;
    }

    public static String encrypt(PublicKey publicKey, String plainText) {
        try {
            cipher.init(1, publicKey);
            byte[] e = cipher.doFinal(plainText.getBytes("UTF-8"));
            return android.util.Base64.encodeToString(e, base64flag);
        } catch (InvalidKeyException var3) {
            var3.printStackTrace();
        } catch (IllegalBlockSizeException var4) {
            var4.printStackTrace();
        } catch (BadPaddingException var5) {
            var5.printStackTrace();
        } catch (UnsupportedEncodingException var6) {
            var6.printStackTrace();
        }

        return null;
    }

    public static String en(String publicKeyString, String text) {
        try {
            cipher.init(1, getPublicKey(publicKeyString));
            byte[] e = cipher.doFinal(text.getBytes("UTF-8"));
            return android.util.Base64.encodeToString(e, base64flag);
        } catch (InvalidKeyException var3) {
            var3.printStackTrace();
        } catch (IllegalBlockSizeException var4) {
            var4.printStackTrace();
        } catch (BadPaddingException var5) {
            var5.printStackTrace();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return null;
    }

    public static String encryptByFile(String publicKeystore, String plainText) {
        try {
            FileReader e = new FileReader(publicKeystore);
            BufferedReader br = new BufferedReader(e);

            String publicKeyString;
            String str;
            for(publicKeyString = ""; (str = br.readLine()) != null; publicKeyString = publicKeyString + str) {
                ;
            }

            br.close();
            e.close();
            cipher.init(1, getPublicKey(publicKeyString));
            byte[] enBytes = cipher.doFinal(plainText.getBytes());
            return android.util.Base64.encodeToString(enBytes, base64flag);
        } catch (InvalidKeyException var7) {
            var7.printStackTrace();
        } catch (IllegalBlockSizeException var8) {
            var8.printStackTrace();
        } catch (BadPaddingException var9) {
            var9.printStackTrace();
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        return null;
    }

    public static String en(String text) {
        return encrypt(text, publicKey);
    }

    public static String encrypt(String plainText, String publicKeyString) {
        try {
            cipher.init(1, getPublicKey(publicKeyString));
            byte[] e = cipher.doFinal(plainText.getBytes("UTF-8"));
            byte[] encode = android.util.Base64.encode(e, base64flag);
            return new String(encode, "UTF-8");
        } catch (InvalidKeyException var4) {
            var4.printStackTrace();
        } catch (IllegalBlockSizeException var5) {
            var5.printStackTrace();
        } catch (BadPaddingException var6) {
            var6.printStackTrace();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return null;
    }

    public static String decrypt(PrivateKey privateKey, String enStr) {
        try {
            cipher.init(2, privateKey);
            byte[] e = cipher.doFinal(android.util.Base64.decode(enStr, base64flag));
            return new String(e);
        } catch (InvalidKeyException var3) {
            var3.printStackTrace();
        } catch (IllegalBlockSizeException var4) {
            var4.printStackTrace();
        } catch (BadPaddingException var5) {
            var5.printStackTrace();
        }

        return null;
    }

    public static String decryptByFile(String privateKeystore, String enStr) {
        try {
            FileReader e = new FileReader(privateKeystore);
            BufferedReader br = new BufferedReader(e);

            String privateKeyString;
            String str;
            for(privateKeyString = ""; (str = br.readLine()) != null; privateKeyString = privateKeyString + str) {
                ;
            }

            br.close();
            e.close();
            cipher.init(2, getPrivateKey(privateKeyString));
            byte[] deBytes = cipher.doFinal(android.util.Base64.decode(enStr, base64flag));
            return new String(deBytes);
        } catch (InvalidKeyException var7) {
            var7.printStackTrace();
        } catch (IllegalBlockSizeException var8) {
            var8.printStackTrace();
        } catch (BadPaddingException var9) {
            var9.printStackTrace();
        } catch (IOException var10) {
            var10.printStackTrace();
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return null;
    }

    public static String de(String privateKeyString, String enStr) {
        try {
            cipher.init(2, getPrivateKey(privateKeyString));
            byte[] e = cipher.doFinal(android.util.Base64.decode(enStr, base64flag));
            return new String(e);
        } catch (InvalidKeyException var3) {
            var3.printStackTrace();
        } catch (IllegalBlockSizeException var4) {
            var4.printStackTrace();
        } catch (BadPaddingException var5) {
            var5.printStackTrace();
        } catch (IOException var6) {
            var6.printStackTrace();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String privateKeyString, String enStr) {
        try {
            cipher.init(2, getPrivateKey(privateKeyString));
            byte[] e = cipher.doFinal(android.util.Base64.decode(enStr, base64flag));
            return new String(e);
        } catch (InvalidKeyException var3) {
            var3.printStackTrace();
        } catch (IllegalBlockSizeException var4) {
            var4.printStackTrace();
        } catch (BadPaddingException var5) {
            var5.printStackTrace();
        } catch (IOException var6) {
            var6.printStackTrace();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        String encrypt = "E6TbjuwLjVu1A1rhBBu6L0KEI1c127ttIGtMgvA22YGbdKUp+dulMkEfLRdOEGv1F3vJMdLNQt+38mwJd6DcI34DaZQeAmBKT5x221E5qMfR9NKC/BL0a6pw21S6s1mdnNUFNbOI2pPljzlpy2CSRBvFhg3E+fp7CDFxwfRMY78=\n";
        String decrypt = de(privateKey, encrypt);
        System.out.println("解密后:" + decrypt);
    }

    static {
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException var1) {
            var1.printStackTrace();
        } catch (NoSuchPaddingException var2) {
            var2.printStackTrace();
        }

    }

}
