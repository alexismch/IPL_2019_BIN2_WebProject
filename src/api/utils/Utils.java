package api.utils;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

public class Utils {
    private static final String JWTSECRET = "JSWebProjet2019";

    public static String encodeToken(Map<String, Object> claims) {
        return new JWTSigner(JWTSECRET).sign(claims);
    }

    private static Map<String, Object> decodeToken(String token) throws SignatureException, NoSuchAlgorithmException, JWTVerifyException, InvalidKeyException, IOException {
        return new JWTVerifier(JWTSECRET).verify(token);
    }

    public static boolean verifyToken(String token, HttpServletRequest req) throws NoSuchAlgorithmException, SignatureException, JWTVerifyException, InvalidKeyException, IOException {
        if (token == null || "".equals(token) || "null".equals(token))
            return false;
        Map<String, Object> decodedToken = decodeToken(token);
        System.out.println(req.getRemoteAddr());
        System.out.println(decodedToken.get("ip"));
        if (!req.getRemoteAddr().equals(decodedToken.get("ip")))
            return false;
        if (decodedToken.get("pseudo") == null || "".equals(decodedToken.get("pseudo")))
            return false;
        if (decodedToken.get("email") == null || "".equals(decodedToken.get("email")))
            return false;
        if (decodedToken.get("fullname") == null || "".equals(decodedToken.get("fullname")))
            return false;
        if (decodedToken.get("descript") == null || "".equals(decodedToken.get("descript")))
            return false;
        return true;
    }

    public static void replyWithError(HttpServletResponse resp, int errorCode, String respErrorMsg, String terminalErrorMsg) throws IOException {
        resp.setStatus(errorCode);
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        String return_json = "{\"success\":\"false\", \"message\":\"" + respErrorMsg + "\"}";
        resp.getWriter().write(return_json);
        System.out.println(terminalErrorMsg);
    }

    public static void replyWithWrongTokenError(HttpServletResponse resp, String token) throws IOException {
        replyWithError(resp, 403, "Wrong or non-provided token.", "Access denied to token : " + token);
    }
}
