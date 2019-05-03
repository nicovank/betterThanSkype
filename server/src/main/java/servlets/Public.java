package servlets;

import application.Encryption;
import crypto.CryptoException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.util.Base64;

public class Public extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        try {

            PublicKey pub = Encryption.getRSA().getPublicKey();

            Base64.Encoder encoder = Base64.getEncoder();
            String encoded = encoder.encodeToString(pub.getEncoded());

            out.println("-----BEGIN RSA PRIVATE KEY-----");

            for (int i = 0; i < encoded.length(); i += 64) {
                out.println(encoded.substring(i, Math.min(64 + i, encoded.length())));
            }

            out.println("-----END RSA PRIVATE KEY-----");
        } catch (CryptoException e) {
            throw new ServletException(e);
        }

    }
}
